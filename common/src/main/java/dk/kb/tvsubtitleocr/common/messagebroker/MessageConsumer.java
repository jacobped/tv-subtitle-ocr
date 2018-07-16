package dk.kb.tvsubtitleocr.common.messagebroker;

import com.rabbitmq.client.*;

import java.io.IOException;
import java.io.Serializable;
import java.util.concurrent.TimeoutException;

/**
 * Created by jacob on 2018-03-24 (YYYY-MM-DD).
 */
public abstract class MessageConsumer implements Consumer {
    private final int identifier = this.hashCode(); // Not sure this is unique enough.
    private QueueSettings queueSettings;
    private Connection connection;
    private MessageBrokerHandler messageBroker;
    protected Channel channel;
    private String consumerTag;

    public MessageConsumer(QueueSettings queueSettings) {
        this.queueSettings = queueSettings;
    }

    void setConnection(Connection connection) {
        this.connection = connection;
    }

    void setMessageBroker(MessageBrokerHandler messageBroker) {
        this.messageBroker = messageBroker;
    }

    public void start() throws IOException {
        channel = connection.createChannel();
        channel.queueDeclare(
                queueSettings.getQueueName(),
                queueSettings.isDurable(),
                queueSettings.isExclusive(),
                queueSettings.isAutoDelete(),
                queueSettings.getArguments()
        );
        channel.basicQos(1); // Only process one message at a time.
        channel.basicConsume(queueSettings.getQueueName(), false, this);
    }

    public void stop() throws IOException, TimeoutException {
        channel.close();
    }

    public void restart() throws IOException, TimeoutException {
        stop();
        start();
    }

    protected void sendMessage(Serializable message, String exchangeName, String queueName, AMQP.BasicProperties properties) throws IOException {
        messageBroker.sendMessage(this.channel, message, exchangeName, queueName, properties);
    }

    /**
     * Stores the most recently passed-in consumerTag - semantically, there should be only one.
     * @param consumerTag
     */
    @Override
    public void handleConsumeOk(String consumerTag) {
        this.consumerTag = consumerTag;
    }

    @Override
    public void handleCancelOk(String consumerTag) {

    }

    @Override
    public void handleCancel(String consumerTag) {

    }

    @Override
    public void handleShutdownSignal(String consumerTag, ShutdownSignalException sig) {

    }

    @Override
    public void handleRecoverOk(String consumerTag) {

    }

    @Override
    final public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) {
//        byte[] messageDecoded = java.util.Base64.getDecoder().decode(body);
//        String message = new String(messageDecoded, StandardCharsets.UTF_8);
        handleMessageReceived(consumerTag, envelope, properties, body);
    }

    public abstract void handleMessageReceived(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] message);

    public int getIdentifier() {
        return identifier;
    }
}
