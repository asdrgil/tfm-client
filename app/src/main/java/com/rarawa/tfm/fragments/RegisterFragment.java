package com.rarawa.tfm.fragments;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
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
import com.rarawa.tfm.sqlite.models.UserInfo;
import com.rarawa.tfm.utils.ApiRest;
import com.rarawa.tfm.utils.Constants;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Map;

import java.sql.Timestamp;
import java.util.Date;

import static com.rarawa.tfm.utils.Constants.LOG_TAG;


public class RegisterFragment extends Fragment implements View.OnClickListener {
    TextInputLayout layoutToken;
    EditText editToken;
    Button btnSend;

    SqliteHandler db;
    boolean registered;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        db = new SqliteHandler(getContext());
        registered = db.userInfoExists();
        View rootView;

        if(!registered) {
            rootView = inflater.inflate(R.layout.fragment_register, container, false);

            TextView fragmentTitle = rootView.findViewById(R.id.fragmentTitle);

            editToken = rootView.findViewById(R.id.inputTokenText);
            layoutToken = rootView.findViewById(R.id.inputTokenLayout);
            btnSend = rootView.findViewById(R.id.btnSendToken);
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

        //Avoids that the button is clicked several times before receiving the result
        btnSend.setEnabled(false);

        String token = editToken.getText().toString();

        //Invalid token length
        if (token.length() < Constants.API_TOKEN_LENGTH) {
            layoutToken.setError(getResources().getText(R.string.register_token_length));
        } else {
            //HTTP GET
            int result = ApiRest.registerPatient(getContext(), token);

            Log.d(LOG_TAG, "result: " + result);

            //OK
            if(result > 0){

                SharedPreferences sharedPref = getContext().getSharedPreferences(
                        Constants.SHAREDPREFERENCES_FILE, Context.MODE_PRIVATE);
                SharedPreferences.Editor sharedPrefEditor = sharedPref.edit();
                sharedPrefEditor.putInt(Constants.SHAREDPREFERENCES_REGISTERED, 1);
                sharedPrefEditor.commit();

                ((MainActivity) getActivity()).setFragment(Constants.FRAGMENT_INDEX_NOT_CALIBRATED,
                        "Paciente registrado correctamente.", Snackbar.LENGTH_LONG);

            //ERROR
            } else {

                layoutToken.setError(getResources().getText(R.string.register_token_invalid));
                btnSend.setEnabled(true);
            }
        }
    }
}
