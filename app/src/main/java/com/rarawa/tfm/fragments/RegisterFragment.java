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
import com.rarawa.tfm.sqlite.models.UserInfo;
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
    Button btnSend;

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

        Log.d(Constants.LOG_TAG, "onClick register");

        String token = editToken.getText().toString();

        //Invalid token length
        if (token.length() < Constants.API_TOKEN_LENGTH) {
            layoutToken.setError(getResources().getText(R.string.register_token_length));
        } else {
            //HTTP GET

            int result = ApiRest.registerPatient(getContext(), token);

            Log.d(Constants.LOG_TAG, "result: " + result);

            //OK
            if(result > 0){
                Log.d(Constants.LOG_TAG, "db.userInfoExists(): True");

                ((MainActivity) getActivity()).setFragment(Constants.FRAGMENT_INDEX_NOT_CALIBRATED,
                        "Paciente registrado correctamente.", Snackbar.LENGTH_LONG);

            //ERROR
            } else {
                Log.d(Constants.LOG_TAG, "db.userInfoExists(): False");

                layoutToken.setError(getResources().getText(R.string.register_token_invalid));
                btnSend.setEnabled(true);
            }
        }
    }
}
