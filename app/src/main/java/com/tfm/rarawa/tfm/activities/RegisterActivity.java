package com.tfm.rarawa.tfm.activities;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.tfm.rarawa.tfm.credentials.rabbitMQ;
import com.tfm.rarawa.tfm.rabbitMQ.ExecuteThreadRabbitMQ;

import com.tfm.rarawa.tfm.R;
import com.tfm.rarawa.tfm.rabbitMQ.SettingsBean;
import com.tfm.rarawa.tfm.sqlite.SqliteHandler;

import org.json.JSONException;
import org.json.JSONObject;

public class RegisterActivity extends BaseActivity implements View.OnClickListener {

    private static final String TAG = "RegisterActivity";

    private SqliteHandler db;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        setContentView(R.layout.prev_activity_register);
        displayDrawer(); // Displays the toolbar and the drawer
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        super.onCreate(savedInstanceState);

        db = new SqliteHandler(this);

        Button btnSend = findViewById(R.id.btnSend);
        btnSend.setOnClickListener((View.OnClickListener) this);

    }

    //There is only one button listener
    public void onClick(View v) {
        EditText inputToken = findViewById(R.id.inputText);
        String token = inputToken.getText().toString();

        //Connection settings
        SettingsBean settingsBean = new SettingsBean();
        settingsBean.setHost(rabbitMQ.HOST);
        settingsBean.setVirtualHost(rabbitMQ.VIRTUALHOST);
        settingsBean.setUsername(rabbitMQ.USER);
        settingsBean.setPassword(rabbitMQ.PASSWORD);

        //Information sent
        String topic = "plain";

        String plainMessage = String.format("{\"operation\":\"syncDevice\", \"token\":\"%s\"}", token);

        ExecuteThreadRabbitMQ thread = new ExecuteThreadRabbitMQ();
        JSONObject response = thread.execute(settingsBean, topic, plainMessage);

        try {
            if (Boolean.parseBoolean(response.get("queryResult").toString())) {

                db.insertUserInfo(response.get("name").toString(),
                        response.get("surname1").toString(),
                        response.get("surname2").toString(),
                        Integer.parseInt(response.get("age").toString()),
                        response.get("sex").toString(),
                        response.get("communicationToken").toString());

                Toast toast = Toast.makeText(getApplicationContext(),
                        "Paciente emparejado correctamente", Toast.LENGTH_SHORT);
                toast.show();
            } else {
                Toast toast =
                        Toast.makeText(getApplicationContext(),
                                "Error: por favor, verifique el código introducido", Toast.LENGTH_LONG);
                toast.show();
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }


    }

}
