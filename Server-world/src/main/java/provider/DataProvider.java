package provider;

public interface DataProvider {
    Data getData(String path);
    DataDirectoryEntry getRoot();
}