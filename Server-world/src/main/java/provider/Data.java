package provider;

import java.util.List;
import provider.SSFiles.DataType;

public interface Data extends DataEntity, Iterable<Data> {
    @Override
    public String getName();
    public DataType getType();
    public List<Data> getChildren();
    public Data getChildByPath(String path);
    public Object getData();
}
