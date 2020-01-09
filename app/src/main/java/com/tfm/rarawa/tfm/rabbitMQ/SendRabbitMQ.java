package com.tfm.rarawa.tfm.rabbitMQ;

import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.DefaultConsumer;
import com.rabbitmq.client.Envelope;

import java.io.IOException;
import java.util.UUID;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeoutException;

public class SendRabbitMQ {
    private Connection connection;
    private Channel channel;

    public SendRabbitMQ(SettingsBean settingsBean) throws IOException, TimeoutException {
        ConnectionFactory factory = new ConnectionFactory();

        factory.setHost(settingsBean.getHost());
        factory.setVirtualHost(settingsBean.getVirtualHost());
        factory.setUsername(settingsBean.getUsername());
        factory.setPassword(settingsBean.getPassword());

        connection = factory.newConnection();
        channel = connection.createChannel();
    }

    public String call(String queue, byte[] message) throws IOException,
            InterruptedException {
        final String corrId = UUID.randomUUID().toString();

        String replyQueueName = channel.queueDeclare().getQueue();
        AMQP.BasicProperties props = new AMQP.BasicProperties.Builder()
                .correlationId(corrId).replyTo(replyQueueName).build();

        channel.basicPublish("", queue, props, message);

        final BlockingQueue<String> response = new ArrayBlockingQueue<String>(1);

        String ctag = channel.basicConsume(replyQueueName, true,
                new DefaultConsumer(channel) {
                    @Override
                    public void handleDelivery(String consumerTag,
                                               Envelope envelope, AMQP.BasicProperties properties,
                                               byte[] body) throws IOException {
                        if (properties.getCorrelationId().equals(corrId)) {
                            response.offer(new String(body, "UTF-8"));
                        }
                    }
                });

        String result = response.take();
        channel.basicCancel(ctag);
        return result;
    }

    public void close() throws IOException {
        connection.close();
    }

    public static String sendElement(SettingsBean settingsBean, String topic, String message) {
        SendRabbitMQ sendRabbitMQ = null;
        String response = "";
        try {
            sendRabbitMQ = new SendRabbitMQ(settingsBean);
            response = sendRabbitMQ.call(topic, message.getBytes());
        } catch (IOException | TimeoutException | InterruptedException e) {
            e.printStackTrace();

        } finally {
            if (sendRabbitMQ != null) {
                try {
                    sendRabbitMQ.close();
                } catch (IOException _ignore) {
                }
            }
        }

        return response;
    }
}