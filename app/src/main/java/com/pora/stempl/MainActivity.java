package com.pora.stempl;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.andrognito.pinlockview.IndicatorDots;
import com.andrognito.pinlockview.PinLockListener;
import com.andrognito.pinlockview.PinLockView;

import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.ToggleButton;

import java.time.LocalDateTime;

public class MainActivity extends AppCompatActivity implements PeopleAdapter.OnItemClickListener {
    public static final String TAG = MainActivity.class.getSimpleName();

    static int LAUNCH_PIN_LOCK_ACTIVITY = 1;

    private ApplicationMy app;
    private PeopleAdapter adapter;
    private RecyclerView recyclerView;
    private AlertDialog.Builder dialogBuilder;
    private AlertDialog dialog;

    // Pin lock popup
    private PinLockView mPinLockView;
    private IndicatorDots mIndicatorDots;

    private View currentToggleRow;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        bindGUI();
        initData();
    }


    private void bindGUI() {
    }
    private void initData(){
        app = (ApplicationMy) getApplication();
        adapter = new PeopleAdapter(app);
        recyclerView = findViewById(R.id.rv_person);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        adapter.setOnItemClickListener(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.menu_add, menu);
        return true;
    }

    @Override
    public void onItemClick(View itemView, int position) {

    }

    @Override
    public void onToggleButton(View itemView, int position) {
        currentToggleRow = recyclerView.findViewHolderForAdapterPosition(position).itemView;
        // We reverse the toggle on click, because we have to wait for the PinLockActivity result
        ToggleButton tbToggleCheckInOut = (ToggleButton)currentToggleRow.findViewById(R.id.tbCheckInOut);
        tbToggleCheckInOut.setChecked(!tbToggleCheckInOut.isChecked());

        createNewPinLockDialog();
    }

    public void goAddPerson(MenuItem item){
        Intent intent = new Intent(getBaseContext(), AddPersonActivity.class);
        startActivity(intent);
    }

    public void createNewPinLockDialog(){
        dialogBuilder = new AlertDialog.Builder(this);
        final View pinLockPopupView = getLayoutInflater().inflate(R.layout.popup_pin_lock, null);

        mPinLockView = (PinLockView) pinLockPopupView.findViewById(R.id.pin_lock_view);
        mPinLockView.setPinLockListener(mPinLockListener);
        mIndicatorDots = (IndicatorDots) pinLockPopupView.findViewById(R.id.indicator_dots);
        mPinLockView.attachIndicatorDots(mIndicatorDots);

        dialogBuilder.setView(pinLockPopupView);
        dialog = dialogBuilder.create();
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();
    }
    private PinLockListener mPinLockListener = new PinLockListener() {
        @Override
        public void onComplete(String pin) {
            Log.d(TAG, "Pin complete: " + pin);
            TextView tvName =  (TextView)currentToggleRow.findViewById(R.id.tv_name);
            String name = tvName.getText().toString();

            String currentPin = app.findPersonByName(name).getPin();
            if (pin.equals(currentPin)) {
                ToggleButton tbToggleCheckInOut = (ToggleButton)currentToggleRow.findViewById(R.id.tbCheckInOut);
                // We toggle the button back programmatically
                tbToggleCheckInOut.setChecked(!tbToggleCheckInOut.isChecked());

                LocalDateTime ltNow = LocalDateTime.now();
                boolean isCheckingIn = tbToggleCheckInOut.isChecked();

                // We add to firebase
                if(isCheckingIn){
                    app.checkInPerson(name, ltNow);
                }
                else{
                    app.checkOutPerson(name, ltNow);
                }

                // We add to adapter People array
                for (PeopleEditModel person : PeopleAdapter.peopleEditModelArrayList){
                    if(person.getName().equals(name)){
                        if(isCheckingIn){
                            person.addCheckIn(ltNow);
                        }
                        else{
                            person.addCheckOut(ltNow);
                        }
                    }
                }
                adapter.notifyDataSetChanged();
                dialog.dismiss();
            }
            else {
                dialog.dismiss();
            }
        }

        @Override
        public void onEmpty() {
            Log.d(TAG, "Pin empty");
        }

        @Override
        public void onPinChange(int pinLength, String intermediatePin) {
            Log.d(TAG, "Pin changed, new length " + pinLength + " with intermediate pin " + intermediatePin);
        }
    };
}