package provider.SS;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import provider.Data;
import provider.DataDirectoryEntry;
import provider.DataFileEntry;
import provider.DataProvider;
import tools.data.input.GenericLittleEndianAccessor;
import tools.data.input.GenericSeekableLittleEndianAccessor;
import tools.data.input.InputStreamByteStream;
import tools.data.input.LittleEndianAccessor;
import tools.data.input.RandomAccessByteStream;
import tools.data.input.SeekableLittleEndianAccessor;

public class SSFile implements DataProvider {
    static {
        ListSSFile.init();
    }
    private File SSfile;
    private LittleEndianAccessor lea;
    private SeekableLittleEndianAccessor slea;
    private int headerSize;
    private SSDirectoryEntry root;
    private boolean provideImages;
    private int cOffset;

    public SSFile(File SSfile, boolean provideImages) throws IOException {
        this.SSfile = SSfile;
        lea = new GenericLittleEndianAccessor(new InputStreamByteStream(new BufferedInputStream(new FileInputStream(SSfile))));
        RandomAccessFile raf = new RandomAccessFile(SSfile, "r");
        slea = new GenericSeekableLittleEndianAccessor(new RandomAccessByteStream(raf));
        root = new SSDirectoryEntry(SSfile.getName(), 0, 0, null);
        this.provideImages = provideImages;
        load();
    }

    private void load() throws IOException {
        lea.readAsciiString(4);
        lea.readInt();
        lea.readInt();
        headerSize = lea.readInt();
        lea.readNullTerminatedAsciiString();
        lea.readShort();
        parseDirectory(root);
        cOffset = (int) lea.getBytesRead();
        getOffsets(root);
    }

    private void getOffsets(DataDirectoryEntry dir) {
        for (DataFileEntry file : dir.getFiles()) {
            file.setOffset(cOffset);
            cOffset += file.getSize();
        }
        for (DataDirectoryEntry sdir : dir.getSubdirectories()) {
            getOffsets(sdir);
        }
    }

    private void parseDirectory(SSDirectoryEntry dir) {
        int entries = SSTool.readValue(lea);
        for (int i = 0; i < entries; i++) {
            byte marker = lea.readByte();
            String name = null;
            int size, checksum;
            switch (marker) {
                case 0x02:
                    name = SSTool.readDecodedStringAtOffsetAndReset(slea, lea.readInt() + this.headerSize + 1);
                    size = SSTool.readValue(lea);
                    checksum = SSTool.readValue(lea);
                    lea.readInt(); //dummy int
                    dir.addFile(new SSFileEntry(name, size, checksum, dir));
                    break;
                case 0x03:
                case 0x04:
                    name = SSTool.readDecodedString(lea);
                    size = SSTool.readValue(lea);
                    checksum = SSTool.readValue(lea);
                    lea.readInt(); //dummy int
                    if (marker == 3) {
                        dir.addDirectory(new SSDirectoryEntry(name, size, checksum, dir));
                    } else {
                        dir.addFile(new SSFileEntry(name, size, checksum, dir));
                    }
                    break;
                default:
            }
        }
        for (DataDirectoryEntry idir : dir.getSubdirectories()) {
            parseDirectory((SSDirectoryEntry) idir);
        }
    }

    public SSIMGFile getImgFile(String path) throws IOException {
        String segments[] = path.split("/");
        SSDirectoryEntry dir = root;
        for (int x = 0; x < segments.length - 1; x++) {
            dir = (SSDirectoryEntry) dir.getEntry(segments[x]);
            if (dir == null) {
                return null;
            }
        }
        SSFileEntry entry = (SSFileEntry) dir.getEntry(segments[segments.length - 1]);
        if (entry == null) {
            return null;
        }
        String fullPath = SSfile.getName().substring(0, SSfile.getName().length() - 3).toLowerCase() + "/" + path;
        return new SSIMGFile(this.SSfile, entry, provideImages, ListSSFile.isModernImgFile(fullPath));
    }

    @Override
    public synchronized Data getData(String path) {
        try {
            SSIMGFile imgFile = getImgFile(path);
            if (imgFile == null) {
                return null;
            }
            Data ret = imgFile.getRoot();
            return ret;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public DataDirectoryEntry getRoot() {
        return root;
    }
}