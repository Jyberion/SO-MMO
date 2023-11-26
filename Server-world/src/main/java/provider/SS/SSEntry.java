package provider.SS;

import provider.DataEntity;
import provider.DataEntry;

public class SSEntry implements DataEntry {
    private String name;
    private int size;
    private int checksum;
    private int offset;
    private DataEntity parent;

    public SSEntry(String name, int size, int checksum, DataEntity parent) {
        super();
        this.name = name;
        this.size = size;
        this.checksum = checksum;
        this.parent = parent;
    }

    public String getName() {
        return name;
    }

    public int getSize() {
        return size;
    }

    public int getChecksum() {
        return checksum;
    }

    public int getOffset() {
        return offset;
    }

    public DataEntity getParent() {
        return parent;
    }
}