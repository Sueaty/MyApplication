package com.example.sm_pc.myapplication;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.RadioButton;
import android.widget.TextView;

import com.example.sm_pc.myapplication.account.LoginActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Calendar;

public class SettingsActivity extends AppCompatActivity {

    private FirebaseAuth auth;
    private Button signOut, dateButton, saveButton;
    private RadioButton boyButton, girlButton, undecidedButton;
    private TextView showEmail, ddayText, babyName;

    private int tYear, tMonth, tDay;
    private int dYear = 1, dMonth = 1, dDay = 1;
    static final int DATE_DIALOG_ID = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        auth = FirebaseAuth.getInstance();
        final DatabaseReference mRootRef = FirebaseDatabase.getInstance().getReference();

        signOut = (Button) findViewById(R.id.buttonSignOut);
        dateButton = (Button) findViewById(R.id.dateButton);
        saveButton = (Button) findViewById(R.id.saveButton);
        boyButton = (RadioButton) findViewById(R.id.buttonBoy);
        girlButton = (RadioButton) findViewById(R.id.buttonGirl);
        undecidedButton = (RadioButton) findViewById(R.id.buttonUndecided);
        showEmail = (TextView) findViewById(R.id.showEmail);
        ddayText = (TextView) findViewById(R.id.ddaydate);
        babyName = (TextView) findViewById(R.id.textBabyName);

        //get current user
        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String ddaydate = ddayText.getText().toString().trim();
                final String textBabyName = babyName.getText().toString().trim();
                final String genderB = "Boy";
                final String genderG = "Girl";
                final String genderU = "Undecided";
                DatabaseReference Baby = mRootRef.child("Baby");
                DatabaseReference user = Baby.child(FirebaseAuth.getInstance().getCurrentUser().getUid());
                DatabaseReference name = user.child("name");
                name.setValue(textBabyName);
                DatabaseReference date = user.child("date");
                date.setValue(ddaydate);
                DatabaseReference gender = user.child("gender");
                if(boyButton.isChecked()){gender.setValue(genderB);}
                else if(girlButton.isChecked()){gender.setValue(genderG);}
                else{gender.setValue(genderU);}
            }
        });



        setDataToView(user);


        signOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signOut();
            }
        });

        dateButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                showDialog(0);
            }
        });



        Calendar calendar = Calendar.getInstance();
        tYear = calendar.get(Calendar.YEAR);
        tMonth = calendar.get(Calendar.MONTH);
        tDay = calendar.get(Calendar.DAY_OF_MONTH);

        Calendar dCalendar = Calendar.getInstance();
        dCalendar.set(dYear, dMonth, dDay);
        updateDisplay();
    }

    public void signOut() {
        auth.signOut();
        FirebaseAuth.AuthStateListener authListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user == null) {
                    // user auth state is changed - user is null
                    // launch login activity
                    startActivity(new Intent(SettingsActivity.this, LoginActivity.class));
                    finish();
                }
            }
        };
    }

    private void setDataToView(FirebaseUser user) {

        showEmail.setText(user.getEmail());

    }


    private void updateDisplay() {
        if(dYear == 1){
            ddayText.setText("출산예정일");
        }else{
            ddayText.setText(String.format("%d년 %d월 %d일", dYear, dMonth + 1, dDay));
        }
    }

    private DatePickerDialog.OnDateSetListener dDateSetListener = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
            dYear = year;
            dMonth = monthOfYear;
            dDay = dayOfMonth;
            final Calendar dCalendar = Calendar.getInstance();
            dCalendar.set(dYear, dMonth, dDay);
            updateDisplay();
        }

    };

    @Override
    protected Dialog onCreateDialog(int id){
        if(id==DATE_DIALOG_ID){
            return new DatePickerDialog(this, dDateSetListener,tYear,tMonth, tDay);
        }
        return null;
    }

}