package dk.kb.tvsubtitleocr.common.messagebroker;

import java.util.Map;

/**
 * Created by jacob on 2018-03-24 (YYYY-MM-DD).
 */
public class QueueSettings {
    /**
     * The name of the queue
     */
    private String queueName;

    /**
     * Should the message survive a restart of the message Broker?
     */
    private boolean durable;

    /**
     * Only allow this connection ( This MessageBrokerHandler instance and all workers ) to access this queue.
     * Will also delete the queue the moment the connection is terminated.
     */
    private boolean exclusive = false;

    /**
     * Should the queue be deleted when there's none to consume from it?
     */
    private boolean autoDelete = false;

    /**
     * Message Broker Arguments
     */
    private Map<String, Object> arguments = null;

    /**
     * Settings that define how a MessageConsumer and the channels it creates should act.
     * @param queueName The name of the queue
     * @param durable Should the message survive a restart of the message Broker?
     * @param exclusive Only allow this connection, that created the queue access to it.
     * @param autoDelete Should the queue be deleted when there's none to consume from it?
     * @param arguments Various Message Broker Arguments
     */
    public QueueSettings(String queueName, boolean durable, boolean exclusive, boolean autoDelete, Map<String, Object> arguments) {
        this.queueName = queueName;
        this.durable = durable;
        this.exclusive = exclusive;
        this.autoDelete = autoDelete;
        this.arguments = arguments;
    }

    public QueueSettings(String queueName, boolean durable, Map<String, String> arguments) {
        this.queueName = queueName;
        this.durable = durable;
    }

    public QueueSettings(String queueName, boolean durable) {
        this.queueName = queueName;
        this.durable = durable;
    }

    public String getQueueName() {
        return queueName;
    }

    public boolean isDurable() {
        return durable;
    }

    public boolean isExclusive() {
        return exclusive;
    }

    public boolean isAutoDelete() {
        return autoDelete;
    }

    public Map<String, Object> getArguments() {
        return arguments;
    }
}
