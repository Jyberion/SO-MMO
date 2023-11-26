package provider.SS;

public class ImgSound {
    private int dataLength, offset;

    public ImgSound(int dataLength, int offset) {
        this.dataLength = dataLength;
        this.offset = offset;
    }

    public int getDataLength() {
        return dataLength;
    }

    public int getOffset() {
        return offset;
    }
}