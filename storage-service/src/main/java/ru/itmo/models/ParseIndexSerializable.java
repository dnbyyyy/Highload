package ru.itmo.models;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class ParseIndexSerializable implements Serializable {
    private final List<Pair<String, Long>> segmentOffsets;

    public ParseIndexSerializable(SortedPairList<String, Long> segmentOffsets) {
        this.segmentOffsets = new ArrayList<>(segmentOffsets);
    }

    public List<Pair<String, Long>> getSegmentOffsets() {
        return segmentOffsets;
    }
}