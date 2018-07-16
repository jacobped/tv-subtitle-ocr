package dk.kb.tvsubtitleocr.videoProcessor;

import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Envelope;
import dk.kb.tvsubtitleocr.common.MachineLearningProperties;
import dk.kb.tvsubtitleocr.common.RuntimeProperties;
import dk.kb.tvsubtitleocr.common.Utility;
import dk.kb.tvsubtitleocr.common.messagebroker.MessageConsumer;
import dk.kb.tvsubtitleocr.common.messagebroker.QueueSettings;
import dk.kb.tvsubtitleocr.common.messagebroker.SerializeBase64;
import dk.kb.tvsubtitleocr.common.messagebroker.message.VideoResultMessage;
import dk.kb.tvsubtitleocr.common.model.VideoInfo;
import dk.kb.tvsubtitleocr.videoProcessor.controller.VideoProcessor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Created by jacob on 2018-03-25 (YYYY-MM-DD).
 */
public class ProcessVideoConsumer extends MessageConsumer {
    private final static Logger log = LoggerFactory.getLogger(ProcessVideoConsumer.class);
    private final static String videoFileExtension = "mp4";
    private final VideoProcessor videoProcessor;
    private final File videoSourceDir;
    private final String queueNameResult;

    public ProcessVideoConsumer(String queueNameResult, QueueSettings queueSettings, RuntimeProperties properties, File workDir, File videoSourceDir, MachineLearningProperties machinegProps) {
        super(queueSettings);
        this.videoProcessor = new VideoProcessor(properties, workDir, machinegProps.getModelfile(), machinegProps.getLabelfile());
        this.videoSourceDir = videoSourceDir;
        this.queueNameResult = queueNameResult;
    }

    @Override
    public void handleMessageReceived(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] message) {
        VideoInfo resultMessage = null;
//        try {
        String stuff = new String(message);
            resultMessage = SerializeBase64.deserialize(message, VideoInfo.class);
//        } catch (IOException | ClassNotFoundException e) {
//            log.error("Error deserializing content into object resultType {}.", VideoInfo.class.getName(), e);
//        }

        if(resultMessage != null) {
            Exception exception = null;
            List<String> srtResult = null;

            File videoFile = null;
            try {
                videoFile = Utility.getFileFromVideoUUID(resultMessage.getUuid(), videoSourceDir, videoFileExtension);
            } catch (IOException e) {
                log.error("Couldn't find video-file for UUID: {}", resultMessage.getUuid(), e);
                exception = e;
            }

            if(exception == null) {
                try {
                    srtResult = videoProcessor.processVideo(videoFile);
                } catch (IOException e) {
                    log.error("Couldn't process video UUID {}", resultMessage.getUuid(), e);
                    exception = e;
                }
            }

            sendResultMessage(resultMessage, srtResult, exception);

            try {
                channel.basicAck(envelope.getDeliveryTag(), false);
            } catch (IOException e) {
                log.error("Something went wrong, when acknowledging that processing the following UUID is done: {}",
                        resultMessage.getUuid(), e);
            }

        } else {
            try {
                log.error("Received message that couldn't be understood. Rejecting without requeue..");
                channel.basicReject(envelope.getDeliveryTag(), false);
            } catch (IOException e) {
                log.error("Something went wrong when rejecting a message that couldn't be understood.", e);
            }
        }
    }

    protected boolean sendResultMessage(VideoInfo videoInfo, List<String> srtResult, Exception exception) {
        boolean success;

        VideoResultMessage resultMessage = new VideoResultMessage(videoInfo, srtResult);
        if(exception != null) {
            resultMessage.addError(exception);
        }

        Map<String, Object> headers = new HashMap<>();
        headers.put("videoUUID", videoInfo.getUuid().toString());

        AMQP.BasicProperties messageProperties = new AMQP.BasicProperties.Builder()
                .headers(headers)
                .build();

        try {
            sendMessage(resultMessage, null, queueNameResult, messageProperties);
            success = true;
        } catch (IOException e) {
            log.error("Failed sending result message for video UUID: {}, to queue.", videoInfo.getUuid().toString(), e);
            success = false;
        }
        return success;
    }
}
