package com.tfm.rarawa.tfm.rabbitMQ;

import org.json.JSONException;
import org.json.JSONObject;

public class ExecuteThreadRabbitMQ {

    public JSONObject execute(SettingsBean settingsBean, String topic, String message){

        ThreadRabbitMQ thread = new ThreadRabbitMQ(settingsBean, topic, message);
        thread.start();
        try {
            thread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        String responseStr = thread.getResponse();
        JSONObject responseJson = null;

        try {
            responseJson = new JSONObject(responseStr);
        }catch
                (JSONException e){
            e.printStackTrace();
            return null;
        }

        return responseJson;
    }
}

