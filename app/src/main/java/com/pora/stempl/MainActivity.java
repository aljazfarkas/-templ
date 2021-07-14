package com.pora.stempl;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.pora.stempl.PeopleAdapter;

import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.time.LocalDateTime;

public class MainActivity extends AppCompatActivity implements PeopleAdapter.OnItemClickListener {
    static int LAUNCH_PIN_LOCK_ACTIVITY = 1;

    private ApplicationMy app;
    private PeopleAdapter adapter;
    private RecyclerView recyclerView;

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

        // We require the correct PIN
        Intent intent = new Intent(getBaseContext(), PinLockActivity.class);
        intent.putExtra("pin", "1234");
        startActivityForResult(intent, LAUNCH_PIN_LOCK_ACTIVITY);

    }
    // We wait for activity result of the PIN Lock activity
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == LAUNCH_PIN_LOCK_ACTIVITY) {
            if(resultCode == Activity.RESULT_OK){
                TextView tvName =  (TextView)currentToggleRow.findViewById(R.id.tv_name);
                ToggleButton tbToggleCheckInOut = (ToggleButton)currentToggleRow.findViewById(R.id.tbCheckInOut);
                // We toggle the button back programmatically
                tbToggleCheckInOut.setChecked(!tbToggleCheckInOut.isChecked());

                String name = tvName.getText().toString();
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
            }
            if (resultCode == Activity.RESULT_CANCELED) {
                // Write your code if there's no result
            }
        }
    }

    public void goAddPerson(MenuItem item){
        Intent intent = new Intent(getBaseContext(), AddPersonActivity.class);
        startActivity(intent);
    }
}