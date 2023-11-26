package provider.SS;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import provider.DataDirectoryEntry;
import provider.DataEntity;
import provider.DataEntry;
import provider.DataFileEntry;

public class SSDirectoryEntry extends SSEntry implements DataDirectoryEntry {
    private List<DataDirectoryEntry> subdirs = new ArrayList<DataDirectoryEntry>();
    private List<DataFileEntry> files = new ArrayList<DataFileEntry>();
    private Map<String, DataEntry> entries = new HashMap<String, DataEntry>();

    public SSDirectoryEntry(String name, int size, int checksum, DataEntity parent) {
        super(name, size, checksum, parent);
    }

    public SSDirectoryEntry() {
        super(null, 0, 0, null);
    }

    public void addDirectory(DataDirectoryEntry dir) {
        subdirs.add(dir);
        entries.put(dir.getName(), dir);
    }

    public void addFile(DataFileEntry fileEntry) {
        files.add(fileEntry);
        entries.put(fileEntry.getName(), fileEntry);
    }

    public List<DataDirectoryEntry> getSubdirectories() {
        return Collections.unmodifiableList(subdirs);
    }

    public List<DataFileEntry> getFiles() {
        return Collections.unmodifiableList(files);
    }

    public DataEntry getEntry(String name) {
        return entries.get(name);
    }
}