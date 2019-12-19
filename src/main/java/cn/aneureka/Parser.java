package cn.aneureka;

import cn.aneureka.model.Net;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import java.io.File;
import java.io.FileNotFoundException;

/**
 * @author Aneureka
 * @createdAt 2019-12-19 20:33
 * @description
 **/
public class Parser {

    public static Net parse(String filePath) throws FileNotFoundException {
        // read workflow net pnml file
        File file = new File(filePath);
        if (!file.exists()) {
            throw new FileNotFoundException();
        }

        SAXParserFactory factory = SAXParserFactory.newInstance();
        factory.setValidating(true);
        try {
            SAXParser parser = factory.newSAXParser();

        } catch (ParserConfigurationException | SAXException e) {
            e.printStackTrace();
        }
        return null;
    }
}
