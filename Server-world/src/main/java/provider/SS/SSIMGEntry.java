package provider.SS;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import provider.Data;
import provider.DataEntity;

public class SSIMGEntry implements Data {
    private String name;
    private DataType type;
    private List<Data> children = new ArrayList<Data>(10);
    private Object data;
    private DataEntity parent;

    public SSIMGEntry(DataEntity parent) {
        this.parent = parent;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public DataType getType() {
        return type;
    }

    @Override
    public List<Data> getChildren() {
        return Collections.unmodifiableList(children);
    }

    @Override
    public Data getChildByPath(String path) {
        String segments[] = path.split("/");
        if (segments[0].equals("..")) {
            return ((Data) getParent()).getChildByPath(path.substring(path.indexOf("/") + 1));
        }
        Data ret = this;
        for (int x = 0; x < segments.length; x++) {
            boolean foundChild = false;
            for (Data child : ret.getChildren()) {
                if (child.getName().equals(segments[x])) {
                    ret = child;
                    foundChild = true;
                    break;
                }
            }
            if (!foundChild) {
                return null;
            }
        }
        return ret;
    }

    @Override
    public Object getData() {
        return data;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setType(DataType type) {
        this.type = type;
    }

    public void setData(Object data) {
        this.data = data;
    }

    public void addChild(SSIMGEntry entry) {
        children.add(entry);
    }

    @Override
    public Iterator<Data> iterator() {
        return getChildren().iterator();
    }

    @Override
    public String toString() {
        return getName() + ":" + getData();
    }

    public DataEntity getParent() {
        return parent;
    }

    public void finish() {
        ((ArrayList<Data>) children).trimToSize();
    }
}