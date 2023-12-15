package ru.itmo.models;

import ru.itmo.keyValueStorage.ParseIndex;

import java.io.*;
import java.nio.file.Path;
import java.util.Comparator;


public class ParseIndexUtil {
    public static void createDump(ParseIndex parseIndex, Path filePath) {
        ParseIndexSerializable parseIndexSerializable = parseIndex.toSerializable();
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(filePath.toFile()))) {
            oos.writeObject(parseIndexSerializable);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void deleteDump(Path filePath) {
        var ignored = filePath.toFile().delete();
    }

    public static SortedPairList<String, ParseIndex> loadSparseIndexes(Path indexesDirPath) {
        SortedPairList<String, ParseIndex> parseIndices = new SortedPairList<>(Comparator.comparing(Pair::getKey));
        var listFiles = indexesDirPath.toFile().listFiles();
        if (listFiles == null)
            return parseIndices;
        for (File file : listFiles) {
            if (!file.isFile()) {
                continue;
            }
            try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
                ParseIndexSerializable parseIndexSerializable = (ParseIndexSerializable) ois.readObject();
                ParseIndex parseIndex = ParseIndex.ofSerializable(parseIndexSerializable);
                parseIndices.insertSorted(new Pair<>(file.getName(), parseIndex));
            } catch (IOException | ClassNotFoundException e) {
                throw new RuntimeException(e);
            }
        }
        return parseIndices;
    }
}