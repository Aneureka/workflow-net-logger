package cn.aneureka;

import cn.aneureka.model.WorkflowNet;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Iterator;

/**
 * @author Aneureka
 * @createdAt 2019-12-19 20:33
 * @description
 **/
@SuppressWarnings("ALL")
public class Parser {

    public WorkflowNet parse(String filePath) throws FileNotFoundException, DocumentException {
        File file = new File(filePath);
        if (!file.exists()) {
            throw new FileNotFoundException();
        }
        WorkflowNet workflowNet = new WorkflowNet();
        NodeFactory factory = new NodeFactory();
        Document document = new SAXReader().read(file);
        Element root = document.getRootElement();
        Element net = root.element("net");
        for (Iterator<Element> it = net.elementIterator("place"); it.hasNext();) {
            Element e = it.next();
            String id = e.attributeValue("id");
            workflowNet.addNode(factory.createNode(id));
        }
        for (Iterator<Element> it = net.elementIterator("transition"); it.hasNext();) {
            Element e = it.next();
            String id = e.attributeValue("id");
            workflowNet.addNode(factory.createNode(id));
        }
        for (Iterator<Element> it = net.elementIterator("arc"); it.hasNext();) {
            Element e = it.next();
            String sourceId = e.attributeValue("source");
            String targetId = e.attributeValue("target");
            workflowNet.addEdge(factory.createNode(sourceId), factory.createNode(targetId));
        }
        return workflowNet;
    }

    public static void main(String[] args) {
        Parser parser = new Parser();
        String modelFile = "src/main/resources/models/Model1.pnml";
        try {
            WorkflowNet net = parser.parse(modelFile);
            System.out.println(net);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (DocumentException e) {
            e.printStackTrace();
        }
    }

}
