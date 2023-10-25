package ru.itmo.keyValueStorage;

public class MemTable<K, V> implements KeyValueStorage<K, V>{
    private final LSMTree<K, V> lsmTree;

    private final long maxMemTableSize;

    public MemTable(long maxMemTableSize) {
        this.lsmTree = new LSMTree<>("D:\\GitClone\\Highload\\storage-service\\memTableFiles");
        this.maxMemTableSize = maxMemTableSize;
    }

    public void set(K key, V value) {
        lsmTree.put(key, value);
        if (lsmTree.getSizeInBytes() >= maxMemTableSize) {
            saveMemTableAsSSTable();
        }
    }

    public V get(K key) {
        return lsmTree.get(key);
    }

    public void saveMemTableAsSSTable() {
        lsmTree.saveAsSSTable();
    }

}

