package cn.aneureka;

/**
 * @author Aneureka
 * @createdAt 2019-12-19 20:38
 * @description
 **/
public class Logger {

    public void getLogOfModel(String modelFile, String logFile) {

    }

    public static void main(String[] args) {
        Logger logger = new Logger();
        String modelFile = "src/main/resources/models/Model1.pnml";
        String logFile = "src/main/resources/logs/log1.txt";
        logger.getLogOfModel(modelFile, logFile);
    }
}
