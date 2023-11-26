package provider;

import java.util.List;

public interface DataDirectoryEntry extends DataEntry {
    public List<DataDirectoryEntry> getSubdirectories();
    public List<DataFileEntry> getFiles();
    public DataEntry getEntry(String name);
}