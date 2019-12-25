package cn.aneureka;

import cn.aneureka.model.Place;
import cn.aneureka.model.Transition;
import cn.aneureka.model.WorkflowNet;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;


/**
 * @author Aneureka
 * @createdAt 2019-12-19 20:33
 * @description
 **/
@SuppressWarnings("ALL")
public class Parser {

    public WorkflowNet parse(String filePath) throws FileNotFoundException {
        File file = new File(filePath);
        if (!file.exists()) {
            throw new FileNotFoundException();
        }
        WorkflowNet workflowNet = new WorkflowNet();
        DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder documentBuilder = null;
        try {
            documentBuilder = documentBuilderFactory.newDocumentBuilder();
            Document document = documentBuilder.parse(file);
            Element root = document.getDocumentElement();
            Element net = (Element) root.getElementsByTagName("net").item(0);
            NodeList placeNodes = net.getElementsByTagName("place");
            for (int i = 0; i < placeNodes.getLength(); i++) {
                Element element = (Element) placeNodes.item(i);
                String id = element.getAttribute("id");
                Element nameNode = (Element) element.getElementsByTagName("name").item(0);
                Element textNode = (Element) element.getElementsByTagName("text").item(0);
                String name = textNode.getTextContent();
                workflowNet.addNode(new Place(id, name));
            }
            NodeList transitionNodes = net.getElementsByTagName("transition");
            for (int i = 0; i < transitionNodes.getLength(); i++) {
                Element element = (Element) transitionNodes.item(i);
                String id = element.getAttribute("id");
                Element nameNode = (Element) element.getElementsByTagName("name").item(0);
                Element textNode = (Element) element.getElementsByTagName("text").item(0);
                String name = textNode.getTextContent();
                workflowNet.addNode(new Transition(id, name));
            }
            NodeList arcNodes = net.getElementsByTagName("arc");
            for (int i = 0; i < arcNodes.getLength(); i++) {
                Element element = (Element) arcNodes.item(i);
                String sourceId = element.getAttribute("source");
                String targetId = element.getAttribute("target");
                workflowNet.addEdge(sourceId, targetId);
            }
            workflowNet.init();
            return workflowNet;
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        } catch (SAXException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static void main(String[] args) {
        Parser parser = new Parser();
        String modelFile = "src/main/resources/models/1575897789165.pnml";
        try {
            WorkflowNet net = parser.parse(modelFile);
//            System.out.println(net);
            net.getLogOfGraph(null);
        } catch (FileNotFoundException e) {
            System.err.println("model file not found: " + modelFile);
        }
    }

}
