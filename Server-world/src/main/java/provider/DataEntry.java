package provider;

public interface DataEntry extends DataEntity {
    public String getName();
    public int getSize();
    public int getChecksum();
    public int getOffset();
}