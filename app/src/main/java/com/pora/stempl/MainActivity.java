package com.pora.stempl;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.pora.stempl.PeopleAdapter;

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

public class MainActivity extends AppCompatActivity implements PeopleAdapter.OnItemClickListener {
    private ApplicationMy app;
    private PeopleAdapter adapter;
    private RecyclerView recyclerView;

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
        View row = recyclerView.findViewHolderForAdapterPosition(position).itemView;

        TextView tvName =  (TextView)row.findViewById(R.id.tv_name);
        ToggleButton tbToggleCheckInOut = (ToggleButton)row.findViewById(R.id.tbCheckInOut);

        String name = tvName.getText().toString();
        boolean isCheckingIn = tbToggleCheckInOut.isChecked();

        if(isCheckingIn){
            app.checkInPerson(name);
        }
        else{
            app.checkOutPerson(name);
        }
        
        //refreshamo people array
        adapter.getFromFirebase(adapter);
    }

    public void goAddPerson(MenuItem item){
        Intent intent = new Intent(getBaseContext(), AddPersonActivity.class);
        startActivity(intent);
    }
}