package provider.SS;

import constants.game.GameConstants;
import java.awt.Point;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import provider.Data;
import provider.DataEntity;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class XMLDomData implements Data {
	private Node node;
	private File imageDataDir;

	public XMLDomData(FileInputStream fis, File imageDataDir) {
		try {
			DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
			Document document = documentBuilder.parse(fis);
			this.node = document.getFirstChild();
		} catch (ParserConfigurationException e) {
			throw new RuntimeException(e);
		} catch (SAXException e) {
			throw new RuntimeException(e);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		this.imageDataDir = imageDataDir;
	}

	private XMLDomData(Node node) {
		this.node = node;
	}

	@Override
	public synchronized Data getChildByPath(String path) {  // the whole XML reading system seems susceptible to give nulls on strenuous read scenarios
		String segments[] = path.split("/");
		if (segments[0].equals("..")) {
			return ((Data) getParent()).getChildByPath(path.substring(path.indexOf("/") + 1));
		}

                Node myNode;
                myNode = node;
                for (String s : segments) {
                        NodeList childNodes = myNode.getChildNodes();
                        boolean foundChild = false;
                        for (int i = 0; i < childNodes.getLength(); i++) {
                                Node childNode = childNodes.item(i);
                                if (childNode.getNodeType() == Node.ELEMENT_NODE && childNode.getAttributes().getNamedItem("name").getNodeValue().equals(s)) {
                                        myNode = childNode;
                                        foundChild = true;
                                        break;
                                }
                        }
                        if (!foundChild) {
                                return null;
                        }
                }
                
		XMLDomData ret = new XMLDomData(myNode);
		ret.imageDataDir = new File(imageDataDir, getName() + "/" + path).getParentFile();
		return ret;
	}

	@Override
	public synchronized List<Data> getChildren() {
		List<Data> ret = new ArrayList<>();
                
                NodeList childNodes = node.getChildNodes();
                for (int i = 0; i < childNodes.getLength(); i++) {
                        Node childNode = childNodes.item(i);
                        if (childNode.getNodeType() == Node.ELEMENT_NODE) {
                                XMLDomData child = new XMLDomData(childNode);
                                child.imageDataDir = new File(imageDataDir, getName());
                                ret.add(child);
                        }
                }
                
		return ret;
	}

	@Override
	public synchronized Object getData() {
                NamedNodeMap attributes = node.getAttributes();
                DataType type = getType();
                switch (type) {
                        case DOUBLE:
                        case FLOAT:
                        case INT:
                        case SHORT: {
                                String value = attributes.getNamedItem("value").getNodeValue();
                                Number nval = GameConstants.parseNumber(value);

                                switch (type) {
                                        case DOUBLE:
                                                return nval.doubleValue();
                                        case FLOAT:
                                                return nval.floatValue();
                                        case INT:
                                                return nval.intValue();
                                        case SHORT:
                                                return nval.shortValue();
                                        default:
                                                return null;
                                }
                        }
                        case STRING:
                        case UOL: {
                                String value = attributes.getNamedItem("value").getNodeValue();
                                return value;
                        }
                        case VECTOR: {
                                String x = attributes.getNamedItem("x").getNodeValue();
                                String y = attributes.getNamedItem("y").getNodeValue();
                                return new Point(Integer.parseInt(x), Integer.parseInt(y));
                        }
                        case CANVAS: {
                                String width = attributes.getNamedItem("width").getNodeValue();
                                String height = attributes.getNamedItem("height").getNodeValue();
                                return new FileStoredPngCanvas(Integer.parseInt(width), Integer.parseInt(height), new File(
                                                imageDataDir, getName() + ".png"));
                        }
                        default:
                                return null;
                }
	}

	@Override
	public synchronized DataType getType() {
                String nodeName = node.getNodeName();
                
                switch (nodeName) {
                    case "imgdir":
                        return DataType.PROPERTY;
                    case "canvas":
                        return DataType.CANVAS;
                    case "convex":
                        return DataType.CONVEX;
                    case "sound":
                        return DataType.SOUND;
                    case "uol":
                        return DataType.UOL;
                    case "double":
                        return DataType.DOUBLE;
                    case "float":
                        return DataType.FLOAT;
                    case "int":
                        return DataType.INT;
                    case "short":
                        return DataType.SHORT;
                    case "string":
                        return DataType.STRING;
                    case "vector":
                        return DataType.VECTOR;
                    case "null":
                        return DataType.IMG_0x00;
                }
		return null;
	}

	@Override
	public synchronized DataEntity getParent() {
                Node parentNode;
                parentNode = node.getParentNode();
                if (parentNode.getNodeType() == Node.DOCUMENT_NODE) {
                        return null;
                }
		XMLDomData parentData = new XMLDomData(parentNode);
		parentData.imageDataDir = imageDataDir.getParentFile();
		return parentData;
	}

	@Override
	public synchronized String getName() {
                return node.getAttributes().getNamedItem("name").getNodeValue();
	}

	@Override
	public synchronized Iterator<Data> iterator() {
		return getChildren().iterator();
	}
}