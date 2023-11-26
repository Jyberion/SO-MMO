package provider.SS;

import provider.DataEntity;
import provider.DataFileEntry;

public class SSFileEntry extends SSEntry implements DataFileEntry {
    private int offset;

    public SSFileEntry(String name, int size, int checksum, DataEntity parent) {
        super(name, size, checksum, parent);
    }

    @Override
    public int getOffset() {
        return offset;
    }

    public void setOffset(int offset) {
        this.offset = offset;
    }
}