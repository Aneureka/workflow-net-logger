package cn.aneureka;

import cn.aneureka.model.WorkflowNet;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.FileNotFoundException;
import java.io.IOException;

/**
 * @author Aneureka
 * @createdAt 2019-12-19 20:38
 * @description
 **/
public class Main {

    public static void getLogOfModel(String modelFile, String logFile) {
        Parser parser = new Parser();
        try {
            WorkflowNet net = parser.parse(modelFile);
//            System.out.println(net);
            net.getLogOfGraph(logFile);
        } catch (FileNotFoundException e) {
            System.err.println("model file not found: " + modelFile);
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        String modelFile = "src/main/resources/models/Model3.pnml";
        String logFile = "src/main/resources/logs/log3.txt";
        Main.getLogOfModel(modelFile, logFile);
    }
}
