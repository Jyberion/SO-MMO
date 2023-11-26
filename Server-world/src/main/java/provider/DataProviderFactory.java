package provider;

import java.io.File;
import java.io.IOException;
import provider.SS.SSFile;
import provider.SS.XMLSSFile;

public class DataProviderFactory {
    private final static String SSPath = System.getProperty("SSpath");

    private static DataProvider getSS(File in, boolean provideImages) {
        if (in.getName().toLowerCase().endsWith("SS") && !in.isDirectory()) {
            try {
                return new SSFile(in, provideImages);
            } catch (IOException e) {
                throw new RuntimeException("Loading SS File failed", e);
            }
        } else {
            return new XMLSSFile(in);
        }
    }

    public static DataProvider getDataProvider(File in) {
        return getSS(in, false);
    }

    public static DataProvider getImageProvidingDataProvider(File in) {
        return getSS(in, true);
    }

    public static File fileInSSPath(String filename) {
        return new File(SSPath, filename);
    }
}