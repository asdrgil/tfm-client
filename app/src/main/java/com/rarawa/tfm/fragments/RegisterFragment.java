package com.rarawa.tfm.fragments;

import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.android.volley.Request;
import com.rarawa.tfm.MainActivity;
import com.rarawa.tfm.R;
import com.rarawa.tfm.sqlite.SqliteHandler;
import com.rarawa.tfm.utils.ApiRest;
import com.rarawa.tfm.utils.Constants;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Map;

import java.sql.Timestamp;
import java.util.Date;


public class RegisterFragment extends Fragment implements View.OnClickListener {
    TextInputLayout layoutToken;
    EditText editToken;

    SqliteHandler db;
    boolean registered;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        db = new SqliteHandler(getContext());
        registered = db.userInfoExists();
        View rootView = null;

        if(!registered) {
            rootView = inflater.inflate(R.layout.fragment_register, container, false);
            editToken = rootView.findViewById(R.id.inputTokenText);
            layoutToken = rootView.findViewById(R.id.inputTokenLayout);
            Button btnSend = rootView.findViewById(R.id.btnSendToken);
            btnSend.setOnClickListener(this);
        } else {
            rootView = inflater.inflate(R.layout.fragment_register_done, container, false);

            Timestamp ts=new Timestamp((long) db.getUserInfo().getRegisterTimestamp());

            Date date = new Date(ts.getTime());
            String strTimeFormat = "hh:mm";
            DateFormat timeFormat = new SimpleDateFormat(strTimeFormat);
            String formattedTime= timeFormat.format(date);

            String strDateFormat = "dd/MM/yyyy";
            DateFormat dateFormat = new SimpleDateFormat(strDateFormat);
            String formattedDate= dateFormat.format(date);

            TextView textInfo = rootView.findViewById(R.id.textinfo);

            String dateTimeText = getResources().getText(R.string.register_done).toString();
            dateTimeText = dateTimeText.replace("[fecha]", formattedDate)
                    .replace("[hora]", formattedTime);

            textInfo.setText(dateTimeText);
        }

        return rootView;
    }

    @Override
    public void onClick(View view) {
        String token = editToken.getText().toString();

        //Invalid token length
        if (token.length() < Constants.API_TOKEN_LENGTH) {
            layoutToken.setError(getResources().getText(R.string.register_token_length));
        } else {
            //HTTP GET

            final ApiRest jsonParserVolley = new ApiRest(getContext(), Constants.SYNC_DEVICE_URL.concat(token));
            //TODO
            /*jsonParserVolley.executeRequest(Request.Method.GET, new ApiRest.VolleyCallback() {
                @Override
                public void getResponse(Map response) {
                    //Error
                    if(Integer.parseInt(response.get("code").toString()) < 0){
                        layoutToken.setError(response.get("message").toString());

                    //Register user in the local database
                    } else {

                        db.insertUserInfo(response.get("name").toString(),
                                response.get("surname1").toString(),
                                response.get("surname2").toString(),
                                Integer.parseInt(response.get("age").toString()),
                                response.get("gender").toString(),
                                response.get("communicationToken").toString());



                        ((MainActivity) getActivity()).setDrawableRegister(
                                response.get("name").toString(),
                                response.get("surname1").toString(),
                                response.get("surname2").toString());

                        ((MainActivity) getActivity()).setFragment(Constants.FRAGMENT_INDEX,
                                "Paciente registrado correctamente.", Snackbar.LENGTH_SHORT);
                    }
                }
            });*/
        }
    }
}
