package com.tfm.rarawa.tfm.rabbitMQ;

public class ThreadRabbitMQ extends Thread {
    private String topic;
    private String message;
    public String response;
    public SettingsBean settingsBean;

    public ThreadRabbitMQ(SettingsBean settingsBean, String topic, String message) {
        this.topic = topic;
        this.message = message;
        this.settingsBean = settingsBean;
    }

    @Override
    public void run() {
        try  {
            this.response = SendRabbitMQ.sendElement(settingsBean,topic, message);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String getResponse() {
        return this.response;
    }

}