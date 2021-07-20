package com.pora.stempl.ui.home;

import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.ToggleButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.andrognito.pinlockview.IndicatorDots;
import com.andrognito.pinlockview.PinLockListener;
import com.andrognito.pinlockview.PinLockView;
import com.pora.lib.PeopleEditModel;
import com.pora.stempl.AddPersonActivity;
import com.pora.stempl.ApplicationMy;
import com.pora.stempl.InfoPersonActivity;
import com.pora.stempl.NavigationActivity;
import com.pora.stempl.PeopleAdapter;
import com.pora.stempl.R;

import java.time.LocalDateTime;

public class HomeFragment extends Fragment implements PeopleAdapter.OnItemClickListener {
    public static final String TAG = HomeFragment.class.getSimpleName();

    private ApplicationMy app;
    private PeopleAdapter adapter;
    private RecyclerView recyclerView;
    private AlertDialog.Builder dialogBuilder;
    private AlertDialog dialog;

    // Pin lock popup
    private PinLockView mPinLockView;
    private IndicatorDots mIndicatorDots;

    private View currentToggleRow;

    private HomeViewModel homeViewModel;
    private View root;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        homeViewModel =
                new ViewModelProvider(this).get(HomeViewModel.class);
        root = inflater.inflate(R.layout.fragment_home, container, false);

        bindGUI();
        initData();

        return root;
    }
    private void bindGUI() {
    }
    private void initData(){
        app = (ApplicationMy) getActivity().getApplication();
        adapter = new PeopleAdapter(app);

        recyclerView = root.findViewById(R.id.rv_person);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        adapter.setOnItemClickListener(this);
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

    @Override
    public void onInfoButton(View itemView, int position){
        View currentRow = recyclerView.findViewHolderForAdapterPosition(position).itemView;

        Intent intent = new Intent(getActivity(), InfoPersonActivity.class);
        TextView tvName = (TextView) currentRow.findViewById(R.id.tv_name);
        intent.putExtra("personName", tvName.getText().toString());
        startActivity(intent);

    }


    public void createNewPinLockDialog(){
        dialogBuilder = new AlertDialog.Builder(getActivity());
        final View pinLockPopupView = getLayoutInflater().inflate(R.layout.popup_pin_lock, null);

        mPinLockView = (PinLockView) pinLockPopupView.findViewById(R.id.pin_lock_view);
        mPinLockView.setPinLockListener(mPinLockListener);
        mIndicatorDots = (IndicatorDots) pinLockPopupView.findViewById(R.id.indicator_dots);
        mPinLockView.attachIndicatorDots(mIndicatorDots);

        TextView tvPinCode = (TextView) pinLockPopupView.findViewById(R.id.tvPinLock);
        tvPinCode.setText(R.string.enter_user_pin);

        ImageButton btExit = (ImageButton) pinLockPopupView.findViewById(R.id.btExit);
        btExit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
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