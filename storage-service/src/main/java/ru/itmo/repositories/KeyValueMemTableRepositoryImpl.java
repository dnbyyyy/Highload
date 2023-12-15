package ru.itmo.repositories;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Repository;
import ru.itmo.keyValueStorage.MemTable;
import ru.itmo.keyValueStorage.ParseIndex;
import ru.itmo.keyValueStorage.WriteAheadLog;
import ru.itmo.models.*;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;


@Profile("lsm")
@Repository
public class KeyValueMemTableRepositoryImpl implements KeyValueMemTableRepository {
    private static final Logger log = LogManager.getLogger();
    private static final long MIB_TO_BYTES = 1048576L;
    private static final long KIB_TO_BYTES = 1024L;
    private final WriteAheadLog writeAheadLog;
    private MemTable memTable;
    private final SortedPairList<String, ParseIndex> parseIndices;
    private final long memTableSizeBytes;
    private final long segmentSizeBytes;
    private final long mergedSizeBytes;
    private final Path ssTablesDirPath;
    private final Path indexDirPath;

    public KeyValueMemTableRepositoryImpl(
            @Value("${mem-table.size.mib:10}") int memTableSizeMib,
            @Value("${storage.path:~/storage}") String storagePathString,
            @Value("${ss-table.segment.size.kib:4}") int segmentSizeKib,
            @Value("${ss-table.merged.size.mib:1}") int mergedSizeMib) {
        Path storagePath = Path.of(storagePathString);
        this.ssTablesDirPath = storagePath.resolve("ss-tables");
        this.indexDirPath = storagePath.resolve("sparse-indexes");
        this.writeAheadLog = new WriteAheadLog(storagePath.resolve("mem-table-wal"));
        this.memTable = writeAheadLog.loadFromFile();
        this.parseIndices = ParseIndexUtil.loadSparseIndexes(indexDirPath);
        this.memTableSizeBytes = memTableSizeMib * MIB_TO_BYTES;
        this.segmentSizeBytes = segmentSizeKib * KIB_TO_BYTES;
        this.mergedSizeBytes = mergedSizeMib * MIB_TO_BYTES;
        if (!ssTablesDirPath.toFile().exists()) {
            var ignored = ssTablesDirPath.toFile().mkdirs();
        }
        if (!indexDirPath.toFile().exists()) {
            var ignored = indexDirPath.toFile().mkdirs();
        }
    }

    public double getMemUsage() {
        return ((double) memTable.getMemSize()) / memTableSizeBytes * 100;
    }

    public Optional<KeyValue> getKeyValue(String key) {
        String value = memTable.get(key);
        if (value != null) {
            return Optional.of(new KeyValue(key, value));
        }
        return Optional.of(new KeyValue(key, findValueOnDisk(key)));
    }

    public void setKeyValue(KeyValue keyValue) {
        if (memTable.getMemSize() >= memTableSizeBytes) {
            dumpMemTableToSsTable();
            writeAheadLog.clearWal();
        }
        String key = keyValue.getKey();
        String value = keyValue.getValue();
        writeAheadLog.writeLine(new Pair<>(key, value));
        memTable.put(key, value);
    }

    @Scheduled(
            fixedDelayString = "${ss-table.compaction.timeout.millis:60000}",
            initialDelayString = "${ss-table.compaction.timeout.millis:60000}"
    )
    public void scheduleFixedDelayTask() {
        log.info("Starting merge and compress operations on SS-tables..");

        List<Pair<String, ParseIndex>> dumpingSparseIndexes = new ArrayList<>(parseIndices);
        if (dumpingSparseIndexes.size() < 2) {
            log.info("Nothing to merge");
            return;
        }

        List<Pair<MemTable, Map.Entry<String, String>>> mergingMemTables = new ArrayList<>();
        for (Pair<String, ParseIndex> sparseIndexPair : dumpingSparseIndexes) {
            String fileName = sparseIndexPair.getKey();
            ParseIndex parseIndex = sparseIndexPair.getValue();
            for (Pair<String, Long> pair : parseIndex) {
                MemTable tmpMemTable = SsTableUtil.readMemTable(ssTablesDirPath.resolve(fileName), pair.getValue());
                if (tmpMemTable == null) {
                    continue;
                }
                mergingMemTables.add(new Pair<>(tmpMemTable, tmpMemTable.pollFirstEntry()));
            }
        }

        List<MemTable> mergedTablesList = new ArrayList<>();
        MemTable mergedTables = new MemTable();
        while (true) {
            if (mergedTables.getMemSize() >= mergedSizeBytes) {
                mergedTablesList.add(mergedTables);
                mergedTables = new MemTable();
            }

            Pair<MemTable, Map.Entry<String, String>> mergingMemTableWithMinKey = mergingMemTables.get(0);
            Map.Entry<String, String> minEntryByKey = mergingMemTableWithMinKey.getValue();
            for (Pair<MemTable, Map.Entry<String, String>> mergingMemTable : mergingMemTables) {
                if (mergingMemTable.getValue() == null) {
                    continue;
                }

                Map.Entry<String, String> entry = mergingMemTable.getValue();

                if (minEntryByKey == null) {
                    minEntryByKey = entry;
                    mergingMemTableWithMinKey = mergingMemTable;
                    continue;
                }

                int keyDiff = entry.getKey().compareTo(minEntryByKey.getKey());
                if (keyDiff < 0) {
                    minEntryByKey = entry;
                    mergingMemTableWithMinKey = mergingMemTable;
                    continue;
                }

                if (keyDiff == 0) {
                    mergingMemTable.setValue(mergingMemTable.getKey().pollFirstEntry());
                }
            }

            if (minEntryByKey == null) {
                break;
            }

            mergedTables.put(minEntryByKey.getKey(), minEntryByKey.getValue());
            mergingMemTableWithMinKey.setValue(mergingMemTableWithMinKey.getKey().pollFirstEntry());
        }
        if (!mergedTables.isEmpty()) {
            mergedTablesList.add(mergedTables);
        }

        List<Pair<String, ParseIndex>> mergedParsedIndexes = new ArrayList<>(mergedTablesList.size());
        long timestamp = System.currentTimeMillis();
        for (MemTable table : mergedTablesList) {
            ParseIndex parsedIndex = new ParseIndex();
            String endStamp = String.valueOf(timestamp++);

            while (!table.isEmpty()) {
                MemTable segmentMemTable = new MemTable();
                while (segmentMemTable.getMemSize() < segmentSizeBytes && !table.isEmpty()) {
                    String key = table.firstKey();
                    String value = table.get(key);
                    segmentMemTable.put(key, value);
                    table.remove(key);
                }
                long offset = SsTableUtil.dumpMemTable(segmentMemTable, ssTablesDirPath.resolve(endStamp));
                parsedIndex.setIndex(segmentMemTable, offset);
            }
            log.info("Merged in file with name={}", endStamp);

            mergedParsedIndexes.add(new Pair<>(endStamp, parsedIndex));
        }

        for (Pair<String, ParseIndex> stringParseIndexPair : mergedParsedIndexes) {
            parseIndices.insertSorted(stringParseIndexPair);
            ParseIndexUtil.createDump(stringParseIndexPair.getValue(), indexDirPath.resolve(stringParseIndexPair.getKey()));
        }
        for (Pair<String, ParseIndex> dumpingSparseIndex : dumpingSparseIndexes) {
            parseIndices.remove(dumpingSparseIndex);
            ParseIndexUtil.deleteDump(indexDirPath.resolve(dumpingSparseIndex.getKey()));
        }
        for (Pair<String, ParseIndex> dumpingSparseIndex : dumpingSparseIndexes) {
            String fileName = dumpingSparseIndex.getKey();
            var ignored = ssTablesDirPath.resolve(fileName).toFile().delete();
        }

        log.info("Merge and compress operations on SS-tables finished");
    }

    private void dumpMemTableToSsTable() {
        String timestamp = String.valueOf(System.currentTimeMillis());
        ParseIndex parseIndex = new ParseIndex();

        long offset = SsTableUtil.dumpMemTable(memTable, ssTablesDirPath.resolve(timestamp));
        parseIndex.setIndex(memTable, offset);
        log.info("Memtable dumped on disk with name={}", timestamp);

        ParseIndexUtil.createDump(parseIndex, indexDirPath.resolve(timestamp));
        parseIndices.insertSorted(new Pair<>(timestamp, parseIndex));
        log.info("Sparse index for memtable dumped on disk with name={}", timestamp);

        memTable = new MemTable();
    }

    private String findValueOnDisk(String key) {
        if (parseIndices.isEmpty()) {
            return null;
        }

        for (Pair<String, ParseIndex> sparseIndexPair : parseIndices) {
            String fileName = sparseIndexPair.getKey();
            ParseIndex parseIndex = sparseIndexPair.getValue();
            var valueOptional = SsTableUtil.findValueInSegment(ssTablesDirPath.resolve(fileName), parseIndex, key);
            if (valueOptional.isPresent()) {
                return valueOptional.get();
            }
        }
        return null;
    }
}
