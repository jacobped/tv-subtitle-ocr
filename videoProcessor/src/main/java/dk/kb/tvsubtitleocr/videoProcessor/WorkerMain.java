package dk.kb.tvsubtitleocr.videoProcessor;

import dk.kb.tvsubtitleocr.videoProcessor.controller.MessageQueueProcessor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by jacob on 2018-03-25 (YYYY-MM-DD).
 */
public class WorkerMain {
    final static Logger log = LoggerFactory.getLogger(WorkerMain.class);

    public static void main(String[] args) throws InterruptedException {
        log.info("Setting up program.");
        MessageQueueProcessor messageQueueProcessor = new MessageQueueProcessor();

        log.info("Running program.");
        messageQueueProcessor.startProcessing();

        log.info("Program is processing tasks from queue.");

        while(true){Thread.sleep(100);}

//        log.info("Program has shut down.");
    }
}
