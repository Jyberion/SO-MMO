package provider.SS;

import java.awt.Point;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.io.RandomAccessFile;
import tools.data.input.GenericSeekableLittleEndianAccessor;
import tools.data.input.RandomAccessByteStream;
import tools.data.input.SeekableLittleEndianAccessor;

public class SSIMGFile {
    private SSFileEntry file;
    private SSIMGEntry root;
    private boolean provideImages;
    @SuppressWarnings ("unused")
    private boolean modernImg;

    public SSIMGFile(File SSfile, SSFileEntry file, boolean provideImages, boolean modernImg) throws IOException {
        RandomAccessFile raf = new RandomAccessFile(SSfile, "r");
        SeekableLittleEndianAccessor slea = new GenericSeekableLittleEndianAccessor(new RandomAccessByteStream(raf));
        slea.seek(file.getOffset());
        this.file = file;
        this.provideImages = provideImages;
        root = new SSIMGEntry(file.getParent());
        root.setName(file.getName());
        root.setType(DataType.EXTENDED);
        this.modernImg = modernImg;
        parseExtended(root, slea, 0);
        root.finish();
        raf.close();
    }

    protected void dumpImg(OutputStream out, SeekableLittleEndianAccessor slea) throws IOException {
        DataOutputStream os = new DataOutputStream(out);
        long oldPos = slea.getPosition();
        slea.seek(file.getOffset());
        for (int x = 0; x < file.getSize(); x++) {
            os.write(slea.readByte());
        }
        slea.seek(oldPos);
    }

    public SSIMGEntry getRoot() {
        return root;
    }

    private void parse(SSIMGEntry entry, SeekableLittleEndianAccessor slea) {
        byte marker = slea.readByte();
        switch (marker) {
            case 0: {
                String name = SSTool.readDecodedString(slea);
                entry.setName(name);
                break;
            }
            case 1: {
                String name = SSTool.readDecodedStringAtOffsetAndReset(slea, file.getOffset() + slea.readInt());
                entry.setName(name);
                break;
            }
            default:
                System.out.println("Unknown Image identifier: " + marker + " at offset " + (slea.getPosition() - file.getOffset()));
        }
        marker = slea.readByte();
        switch (marker) {
            case 0:
                entry.setType(DataType.IMG_0x00);
                break;
            case 2:
            case 11: //??? no idea, since 0.49
                entry.setType(DataType.SHORT);
                entry.setData(Short.valueOf(slea.readShort()));
                break;
            case 3:
                entry.setType(DataType.INT);
                entry.setData(Integer.valueOf(SSTool.readValue(slea)));
                break;
            case 4:
                entry.setType(DataType.FLOAT);
                entry.setData(Float.valueOf(SSTool.readFloatValue(slea)));
                break;
            case 5:
                entry.setType(DataType.DOUBLE);
                entry.setData(Double.valueOf(slea.readDouble()));
                break;
            case 8:
                entry.setType(DataType.STRING);
                byte iMarker = slea.readByte();
                if (iMarker == 0) {
                    entry.setData(SSTool.readDecodedString(slea));
                } else if (iMarker == 1) {
                    entry.setData(SSTool.readDecodedStringAtOffsetAndReset(slea, slea.readInt() + file.getOffset()));
                } else {
                    System.out.println("Unknown String type " + iMarker);
                }
                break;
            case 9:
                entry.setType(DataType.EXTENDED);
                long endOfExtendedBlock = slea.readInt();
                endOfExtendedBlock += slea.getPosition();
                parseExtended(entry, slea, endOfExtendedBlock);
                break;
            default:
                System.out.println("Unknown Image type " + marker);
        }
    }

    private void parseExtended(SSIMGEntry entry, SeekableLittleEndianAccessor slea, long endOfExtendedBlock) {
        byte marker = slea.readByte();
        String type;
        switch (marker) {
            case 0x73:
                type = SSTool.readDecodedString(slea);
                break;
            case 0x1B:
                type = SSTool.readDecodedStringAtOffsetAndReset(slea, file.getOffset() + slea.readInt());
                break;
            default:
                throw new RuntimeException("Unknown extended image identifier: " + marker + " at offset " +
                        (slea.getPosition() - file.getOffset()));
        }
        if (type.equals("Property")) {
            entry.setType(DataType.PROPERTY);
            slea.readByte();
            slea.readByte();
            int children = SSTool.readValue(slea);
            for (int i = 0; i < children; i++) {
                SSIMGEntry cEntry = new SSIMGEntry(entry);
                parse(cEntry, slea);
                cEntry.finish();
                entry.addChild(cEntry);
            }
        } else if (type.equals("Canvas")) {
            entry.setType(DataType.CANVAS);
            slea.readByte();
            marker = slea.readByte();
            if (marker == 0) {
                // do nothing
            } else if (marker == 1) {
                slea.readByte();
                slea.readByte();
                int children = SSTool.readValue(slea);
                for (int i = 0; i < children; i++) {
                    SSIMGEntry child = new SSIMGEntry(entry);
                    parse(child, slea);
                    child.finish();
                    entry.addChild(child);
                }
            } else {
                System.out.println("Canvas marker != 1 (" + marker + ")");
            }
            int width = SSTool.readValue(slea);
            int height = SSTool.readValue(slea);
            int format = SSTool.readValue(slea);
            int format2 = slea.readByte();
            slea.readInt();
            int dataLength = slea.readInt() - 1;
            slea.readByte();
            if (provideImages) {
                byte[] pngdata = slea.read(dataLength);
                entry.setData(new PNGCanvas(width, height, dataLength, format + format2, pngdata));
            } else {
                entry.setData(new PNGCanvas(width, height, dataLength, format + format2, null));
                slea.skip(dataLength);
            }
        } else if (type.equals("Shape2D#Vector2D")) {
            entry.setType(DataType.VECTOR);
            int x = SSTool.readValue(slea);
            int y = SSTool.readValue(slea);
            entry.setData(new Point(x, y));
        } else if (type.equals("Shape2D#Convex2D")) {
            int children = SSTool.readValue(slea);
            for (int i = 0; i < children; i++) {
                SSIMGEntry cEntry = new SSIMGEntry(entry);
                parseExtended(cEntry, slea, 0);
                cEntry.finish();
                entry.addChild(cEntry);
            }
        } else if (type.equals("Sound_DX8")) {
            entry.setType(DataType.SOUND);
            slea.readByte();
            int dataLength = SSTool.readValue(slea);
            SSTool.readValue(slea); // no clue what this is
            int offset = (int) slea.getPosition();
            entry.setData(new ImgSound(dataLength, offset - file.getOffset()));
            slea.seek(endOfExtendedBlock);
        } else if (type.equals("UOL")) {
            entry.setType(DataType.UOL);
            slea.readByte();
            byte uolmarker = slea.readByte();
            switch (uolmarker) {
                case 0:
                    entry.setData(SSTool.readDecodedString(slea));
                    break;
                case 1:
                    entry.setData(SSTool.readDecodedStringAtOffsetAndReset(slea, file.getOffset() + slea.readInt()));
                    break;
                default:
                    System.out.println("Unknown UOL marker: " + uolmarker + " " + entry.getName());
            }
        } else {
            throw new RuntimeException("Unhandled extended type: " + type);
        }
    }
}