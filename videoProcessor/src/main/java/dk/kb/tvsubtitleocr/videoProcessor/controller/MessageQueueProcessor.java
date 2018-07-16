package dk.kb.tvsubtitleocr.videoProcessor.controller;

import dk.kb.tvsubtitleocr.common.PropertiesFactory;
import dk.kb.tvsubtitleocr.common.RuntimeProperties;
import dk.kb.tvsubtitleocr.common.messagebroker.MessageBrokerHandler;
import dk.kb.tvsubtitleocr.common.messagebroker.QueueSettings;
import dk.kb.tvsubtitleocr.videoProcessor.ProcessVideoConsumer;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.concurrent.TimeoutException;

/**
 * Created by jacob on 2018-03-25 (YYYY-MM-DD).
 */
public class MessageQueueProcessor {

    private String queueNameResult = "videosResult";
    private String queueNameToProcess = "videosToProcess";
    private boolean durable = true;
    private final RuntimeProperties properties;
    private final File workDir;
    private final File videoSourceDir;
    private final MessageBrokerHandler messageBroker;
//    private final VideoProcessor videoProcessor;

    public MessageQueueProcessor() {
        this(PropertiesFactory.getProperties());
    }

    public MessageQueueProcessor(RuntimeProperties properties) {
        this.properties = properties;
        this.workDir = new File(properties.getProperty(RuntimeProperties.ResourceName.sharedWorkDir));
        this.videoSourceDir = Paths.get(properties.getProperty(RuntimeProperties.ResourceName.videoSourceDir)).toFile();

//        String serverAddress = "localhost";
        String serverAddress = "pc740.sb";
        int port = 5672;
        String username = "guest";
        String password = "guest";
        try {
            this.messageBroker = new MessageBrokerHandler(serverAddress, port, username, password);
        } catch (IOException e) {
            throw new RuntimeException("Error setting up connection with messageBroker.\n" +
                    "ServerAddress: " + serverAddress + ", port: " + port, e);
        } catch (TimeoutException e) {
            throw new RuntimeException("Timeout when setting up connection with messageBroker.\n" +
                    "ServerAddress: " + serverAddress + ", port: " + port, e);
        }

        try {
            setupQueues(messageBroker);
        } catch (IOException e) {
            throw new RuntimeException("Error creating queues: " + queueNameResult + ", and " + queueNameToProcess, e);
        }

//        this.videoProcessor = new VideoProcessor(properties, workDir);
    }

    protected void setupQueues(MessageBrokerHandler messageBroker) throws IOException {
        messageBroker.createQueue(queueNameResult, durable);
        messageBroker.createQueue(queueNameToProcess, durable);
    }

    public void startProcessing() {
        QueueSettings queueSettings = new QueueSettings(queueNameToProcess, durable);
        messageBroker.addConsumer(new ProcessVideoConsumer(queueSettings, properties, workDir, videoSourceDir, queueNameResult));
        messageBroker.startConsumers();
    }
}
