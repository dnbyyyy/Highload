package ru.itmo.keyValueStorage;

import org.rocksdb.*;

import java.io.*;

public class LSMTree<K, V> {
    private RocksDB db;
    private Options options;
    private ReadOptions readOptions;
    private WriteOptions writeOptions;

    public LSMTree(String dbPath) {
        options = new Options().setCreateIfMissing(true);
        readOptions = new ReadOptions();
        writeOptions = new WriteOptions();

        try {
            db = RocksDB.open(options, dbPath);
        } catch (RocksDBException e) {
            e.printStackTrace();
        }
    }

    public void put(K key, V value) {
        try {
            db.put(writeOptions, serialize(key), serialize(value));
        } catch (RocksDBException e) {
            e.printStackTrace();
        }
    }

    public V get(K key) {
        try {
            byte[] valueBytes = db.get(readOptions, serialize(key));
            if (valueBytes != null) {
                return deserialize(valueBytes);
            }
        } catch (RocksDBException e) {
            e.printStackTrace();
        }

        return searchInSSTables(key);
    }


    public long getSizeInBytes() {
        try {
            String stats = db.getProperty("rocksdb.stats");
            String[] lines = stats.split("\n");
            for (String line : lines) {
                if (line.contains("MemTable")) {
                    String[] parts = line.split(":");
                    if (parts.length >= 2) {
                        try {
                            return Long.parseLong(parts[1].trim());
                        } catch (NumberFormatException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        } catch (RocksDBException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public void close() {
        if (db != null) {
            db.close();
        }
    }

    private byte[] serialize(Object obj) {
        ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
        try (ObjectOutputStream objectStream = new ObjectOutputStream(byteStream)) {
            objectStream.writeObject(obj);
            return byteStream.toByteArray();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    @SuppressWarnings("unchecked")
    private V deserialize(byte[] bytes) {
        try (ObjectInputStream objectStream = new ObjectInputStream(new ByteArrayInputStream(bytes))) {
            return (V) objectStream.readObject();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }

    public void saveAsSSTable() {
        try {
            Snapshot snapshot = db.getSnapshot();
            ReadOptions readOptions = new ReadOptions();
            readOptions.setSnapshot(snapshot);
            WriteOptions writeOptions = new WriteOptions();
            writeOptions.setDisableWAL(true);

            try (WriteBatch batch = new WriteBatch()) {
                RocksIterator iterator = db.newIterator(readOptions);
                for (iterator.seekToFirst(); iterator.isValid(); iterator.next()) {
                    batch.put(iterator.key(), iterator.value());
                }
                db.write(writeOptions, batch);
            }


            db.releaseSnapshot(snapshot);
            db.compactRange();
        } catch (RocksDBException e) {
            e.printStackTrace();
        }
    }

    private V searchInSSTables(K key) {
        ReadOptions readOptions = new ReadOptions();
        RocksIterator iterator = db.newIterator(readOptions);

        for (iterator.seekToLast(); iterator.isValid(); iterator.prev()) {
            K savedKey = (K) deserialize(iterator.key());
            if (savedKey != null && savedKey.equals(key)) {
                byte[] savedValue = iterator.value();
                if (savedValue != null) {
                    return deserialize(savedValue);
                }
            }
        }

        return null;
    }
}

