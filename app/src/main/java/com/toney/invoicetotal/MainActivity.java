package com.toney.invoicetotal;

import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import android.view.View.OnClickListener;

import java.text.NumberFormat;

public class MainActivity extends AppCompatActivity implements OnEditorActionListener, OnClickListener{

    // define our widget variables
    private EditText subtotalET;
    private TextView percentTV;
    private TextView amountTV;
    private TextView totalTV;
    private Button resetButton;

    // define instance variable
    private String subtotalString = "";
    private SharedPreferences savedValues;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // get reference to the widgets
        subtotalET = (EditText) findViewById(R.id.subtotalET);
        percentTV = (TextView) findViewById(R.id.percentTV);
        amountTV = (TextView) findViewById(R.id.amountTV);
        totalTV = (TextView) findViewById(R.id.totalTV);
        resetButton = (Button) findViewById(R.id.resetButton);

        // set the listener for the event
        subtotalET.setOnEditorActionListener(this);
        resetButton.setOnClickListener(this);

        savedValues = getSharedPreferences("SavedValues", MODE_PRIVATE);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.resetButton:
                subtotalET.setText("");
                percentTV.setText("00%");
                amountTV.setText("$0.00");
                totalTV.setText("$0.00");

                break;
        }
    }

    @Override
    public boolean onEditorAction(TextView textView, int actionId, KeyEvent keyEvent) {
        if(actionId == EditorInfo.IME_ACTION_DONE || actionId == EditorInfo.IME_ACTION_UNSPECIFIED) {
            calculateAndDisplay();
        }
        // hide soft keyboard
        return false;
    }

    private void calculateAndDisplay() {
        // get subtotal from user
        subtotalString = subtotalET.getText().toString();
        float subtotal;
        if(subtotalString.equals("")) {
            subtotal = 0;
        } else {
            subtotal = Float.parseFloat(subtotalString);
        }

        // get discount percent
        float percent = 0;
        if(subtotal >= 200) {
            percent = .2f;
        } else if(subtotal >= 100) {
            percent = .1f;
        } else {
            percent = 0;
        }

        // calculate discount
        float amount = subtotal * percent;
        float total = subtotal - amount;

        // format and display
        NumberFormat per = NumberFormat.getPercentInstance();
        percentTV.setText(per.format(percent));

        NumberFormat currency = NumberFormat.getCurrencyInstance();
        amountTV.setText(currency.format(amount));
        totalTV.setText(currency.format(total));
    }

    @Override
    protected void onPause() {
        SharedPreferences.Editor editor = savedValues.edit();
        editor.putString("subtotalString", subtotalString);
        editor.commit();

        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();

        subtotalString = savedValues.getString("subtotalString", "");
        subtotalET.setText(subtotalString);
        calculateAndDisplay();
    }
}