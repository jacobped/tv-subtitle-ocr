package dk.kb.tvsubtitleocr.common.messagebroker;

import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import dk.kb.tvsubtitleocr.common.messagebroker.SerializeBase64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeoutException;

/**
 * Created by jacob on 2018-03-24 (YYYY-MM-DD).
 */
public class MessageBrokerHandler implements AutoCloseable {
    private final static Logger log = LoggerFactory.getLogger(MessageBrokerHandler.class);
    private Connection connection;
    private Channel channel;
    private Map<Integer, MessageConsumer> consumers = new LinkedHashMap<>();

    public MessageBrokerHandler(String serverAddress, int port, String userName, String password) throws IOException, TimeoutException {
        ConnectionFactory connectionFactory = new ConnectionFactory();
        connectionFactory.setHost(serverAddress);
        connectionFactory.setPort(port);
        connectionFactory.setUsername(userName);
        connectionFactory.setPassword(password);
        connectionFactory.setAutomaticRecoveryEnabled(true);
        this.connection = connectionFactory.newConnection();
        this.channel = connection.createChannel();
    }

    public void createQueue(String queueName, boolean durable, boolean exclusive, boolean autoDelete, Map<String, Object> arguments) throws IOException {
        this.channel.queueDeclare(queueName, durable, exclusive, autoDelete, arguments);

    }

    public void createQueue(String queueName, boolean durable) throws IOException {
        createQueue(queueName, durable, false, false, null);

    }

    protected void sendMessage(Channel channel, Serializable message, String exchangeName, String queueName, AMQP.BasicProperties properties) throws IOException {
        byte[] serializedMessage = SerializeBase64.serialize(message);
        if(exchangeName == null) {
            exchangeName = "";
        }

        channel.basicPublish(exchangeName, queueName, properties, serializedMessage);
    }

    public void sendMessage(Serializable message, String exchangeName, String queueName, AMQP.BasicProperties properties) throws IOException {
        this.sendMessage(this.channel, message, exchangeName, queueName, properties);
    }

    public int addConsumer(MessageConsumer consumer) {
        consumer.setConnection(this.connection);
        consumer.setMessageBroker(this);
        int identifier = consumer.getIdentifier();
        consumers.put(identifier, consumer);
        return identifier;
    }

    public MessageConsumer getConsumer(int identifier) {
        return consumers.get(identifier);
    }

    public void removeConsumer(int identifier) {
        consumers.remove(identifier);
    }

    public List<Integer> getConsumers() {
        List<Integer> keys = new ArrayList<>(consumers.keySet());
        return keys;
    }

    public boolean existConsumer(int identifier) {
        return consumers.get(identifier) != null;
    }

    public void startConsumers() {
        consumers.forEach((k, v) -> {
            try {
                v.start();
            } catch (IOException e) {
                log.error("Failed starting consumer {}", v.getIdentifier(), e);
            }
        });
    }

    public void stopConsumers() {
        consumers.forEach((k, v) -> {
            try {
                v.stop();
            } catch (IOException e) {
                log.error("Failed stopping consumer {}", v.getIdentifier(), e);
            } catch (TimeoutException e) {
                log.error("Timeout when stopping consumer {}", v.getIdentifier(), e);;
            }
        });
    }

//    @Override
//    public void start() {
//        startConsumers();
//    }

//    @Override
//    public void stop() {
//        stopConsumers();
//
//    }

//    @Override
//    public void restart() {
//        stop();
//        start();
//    }

    @Override
    public void close() throws Exception {
        stopConsumers();
        this.channel.close();
        this.connection.close();
    }
}
