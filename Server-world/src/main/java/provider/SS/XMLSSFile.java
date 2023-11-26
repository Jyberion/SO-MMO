package provider.SS;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import provider.Data;
import provider.DataDirectoryEntry;
import provider.DataProvider;

public class XMLSSFile implements DataProvider {
    private File root;
    private SSDirectoryEntry rootForNavigation;

    public XMLSSFile(File fileIn) {
        root = fileIn;
        rootForNavigation = new SSDirectoryEntry(fileIn.getName(), 0, 0, null);
        fillDataEntitys(root, rootForNavigation);
    }

    private void fillDataEntitys(File lroot, SSDirectoryEntry SSdir) {
        for (File file : lroot.listFiles()) {
            String fileName = file.getName();
            if (file.isDirectory() && !fileName.endsWith(".img")) {
                SSDirectoryEntry newDir = new SSDirectoryEntry(fileName, 0, 0, SSdir);
                SSdir.addDirectory(newDir);
                fillDataEntitys(file, newDir);
            } else if (fileName.endsWith(".xml")) {
                SSdir.addFile(new SSFileEntry(fileName.substring(0, fileName.length() - 4), 0, 0, SSdir));
            }
        }
    }

    @Override
    public synchronized Data getData(String path) {
        File dataFile = new File(root, path + ".xml");
        File imageDataDir = new File(root, path);
        if (!dataFile.exists()) {
            return null;//bitches
        }
        FileInputStream fis;
        try {
            fis = new FileInputStream(dataFile);
        } catch (FileNotFoundException e) {
            throw new RuntimeException("Datafile " + path + " does not exist in " + root.getAbsolutePath());
        }
        final XMLDomData domData;
        try {
            domData = new XMLDomData(fis, imageDataDir.getParentFile());
        } finally {
            try {
                fis.close();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        return domData;
    }

    @Override
    public DataDirectoryEntry getRoot() {
        return rootForNavigation;
    }
}