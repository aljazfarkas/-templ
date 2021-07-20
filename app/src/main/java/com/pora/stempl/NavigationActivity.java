package com.pora.stempl;

import android.annotation.SuppressLint;
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
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.andrognito.pinlockview.IndicatorDots;
import com.andrognito.pinlockview.PinLockListener;
import com.andrognito.pinlockview.PinLockView;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.pora.lib.PeopleEditModel;
import com.pora.stempl.ui.home.HomeFragment;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.time.LocalDateTime;

public class NavigationActivity extends AppCompatActivity {
    public static final String TAG = NavigationActivity.class.getSimpleName();
    private ApplicationMy app;


    private AlertDialog.Builder dialogBuilder;
    private AlertDialog dialog;

    // Pin lock popup
    private PinLockView mPinLockView;
    private IndicatorDots mIndicatorDots;
    private int navMenuToNavigate;

    //Nav controller
    NavController navController;

    private void initData(){
        app = (ApplicationMy) getApplication();
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initData();

        setContentView(R.layout.activity_navigation);
        BottomNavigationView navView = (BottomNavigationView) findViewById(R.id.nav_view);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_home, R.id.navigation_dashboard, R.id.navigation_settings)
                .build();
        navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupWithNavController(navView, navController);

        navView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                Fragment selectedFragment = null;

                switch (item.getItemId()) {
                    case R.id.navigation_home:
                        navController.navigate(R.id.navigation_home);
                        return true;
                    case R.id.navigation_dashboard:
                        navMenuToNavigate = R.id.navigation_dashboard;
                        createNewPinLockDialog();
                        return true;
                    case R.id.navigation_settings:
                        navMenuToNavigate = R.id.navigation_settings;
                        createNewPinLockDialog();
                        return true;
                }
                return true;
            }
        });
    }

    public void createNewPinLockDialog() {
        dialogBuilder = new AlertDialog.Builder(this);
        final View pinLockPopupView = getLayoutInflater().inflate(R.layout.popup_pin_lock, null);

        mPinLockView = (PinLockView) pinLockPopupView.findViewById(R.id.pin_lock_view);
        mPinLockView.setPinLockListener(mPinLockListener);
        mIndicatorDots = (IndicatorDots) pinLockPopupView.findViewById(R.id.indicator_dots);
        mPinLockView.attachIndicatorDots(mIndicatorDots);

        TextView tvPinCode = (TextView) pinLockPopupView.findViewById(R.id.tvPinLock);
        tvPinCode.setText(R.string.enter_admin_pin);

        ImageButton btExit = (ImageButton) pinLockPopupView.findViewById(R.id.btExit);
        btExit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                navController.navigate(R.id.navigation_home);
                dialog.dismiss();
            }
        });

        dialogBuilder.setView(pinLockPopupView);
        dialog = dialogBuilder.create();
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();
    }

    private PinLockListener mPinLockListener = new PinLockListener() {
        @Override
        public void onComplete(String pin) {
            Log.d(TAG, "Pin complete: " + pin);

            String adminPin = app.ADMIN_PIN;
            if (pin.equals(adminPin)) {
                navController.navigate(navMenuToNavigate);
                dialog.dismiss();
            } else {
                navController.navigate(R.id.navigation_home);
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