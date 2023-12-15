package ru.itmo.models;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.file.Path;
import java.util.Optional;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ru.itmo.keyValueStorage.MemTable;
import ru.itmo.keyValueStorage.ParseIndex;

public class SsTableUtil {
    private static final Logger log = LogManager.getLogger();
    private static final long FILE_START_OFFSET = 10L;

    public static long dumpMemTable(MemTable memTable, Path ssTableFilePath) {
        try (FileOutputStream fileOutputStream = new FileOutputStream(ssTableFilePath.toFile(), true);
             GZIPOutputStream gzipOutputStream = new GZIPOutputStream(fileOutputStream);
             ObjectOutputStream objectOutputStream = new ObjectOutputStream(gzipOutputStream)) {

            long offset = fileOutputStream.getChannel().position() - FILE_START_OFFSET;
            objectOutputStream.writeObject(memTable);
            fileOutputStream.getFD().sync();

            return offset;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static Optional<String> findValueInSegment(Path ssTableFilePath, ParseIndex parseIndex, String key) {
        long offset = parseIndex.getNearestIndexPair(key).getValue();
        MemTable segmentTable = readMemTable(ssTableFilePath, offset);
        if (segmentTable == null) {
            return Optional.empty();
        }
        return Optional.ofNullable(segmentTable.get(key));
    }

    public static MemTable readMemTable(Path ssTableFilePath, long offset) {
        try (FileInputStream fileInputStream = new FileInputStream(ssTableFilePath.toFile())) {
            var ignored = fileInputStream.skip(offset);

            try (GZIPInputStream gzipInputStream = new GZIPInputStream(fileInputStream);
                 ObjectInputStream objectInputStream = new ObjectInputStream(gzipInputStream)) {
                return (MemTable) objectInputStream.readObject();
            }

        } catch (IOException | ClassNotFoundException e) {
            log.error("Error reading segment from file", e);
            return null;
        }
    }
}