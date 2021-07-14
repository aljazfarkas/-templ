package com.pora.stempl;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.andrognito.pinlockview.IndicatorDots;
import com.andrognito.pinlockview.PinLockListener;
import com.andrognito.pinlockview.PinLockView;

public class PinLockActivity extends AppCompatActivity {
    public static final String TAG = PinLockActivity.class.getSimpleName();
    private PinLockView mPinLockView;
    private IndicatorDots mIndicatorDots;
    private String correctPin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pin_lock);

        Bundle extras = getIntent().getExtras();
        correctPin = extras.getString("pin");

        mPinLockView = (PinLockView) findViewById(R.id.pin_lock_view);
        mPinLockView.setPinLockListener(mPinLockListener);
        mIndicatorDots = (IndicatorDots) findViewById(R.id.indicator_dots);
        mPinLockView.attachIndicatorDots(mIndicatorDots);
    }

    private PinLockListener mPinLockListener = new PinLockListener() {
        @Override
        public void onComplete(String pin) {
            Log.d(TAG, "Pin complete: " + pin);

            if (pin.equals(correctPin)) {
                Intent returnIntent = new Intent();
                setResult(Activity.RESULT_OK, returnIntent);
                finish();
            }
        }

        @Override
        public void onEmpty() {
            Log.d(TAG, "Pin empty");

            Intent returnIntent = new Intent();
            setResult(Activity.RESULT_CANCELED, returnIntent);
            finish();
        }

        @Override
        public void onPinChange(int pinLength, String intermediatePin) {
            Log.d(TAG, "Pin changed, new length " + pinLength + " with intermediate pin " + intermediatePin);
        }
    };
}