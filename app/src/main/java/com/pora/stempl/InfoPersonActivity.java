package com.pora.stempl;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.pora.stempl.ui.home.HomeFragment;

public class InfoPersonActivity extends AppCompatActivity implements PersonInfoAdapter.OnItemClickListener {
    public static final String TAG = InfoPersonActivity.class.getSimpleName();

    private ApplicationMy app;
    private PersonInfoAdapter adapter;
    private RecyclerView recyclerView;

    private TextView tvName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info_person);

        bindGUI();
        initData();
    }

    private void initData() {
        Intent intent = getIntent();
        String personName = intent.getStringExtra("personName");
        tvName.setText(personName);

        app = (ApplicationMy) getApplication();
        adapter = new PersonInfoAdapter(app, personName);

        recyclerView = findViewById(R.id.rv_check_in_outs);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        adapter.setOnItemClickListener(this);
    }
    private void bindGUI() {
        tvName = (TextView) findViewById(R.id.tvName);
    }

    @Override
    public void onItemClick(View itemView, int position) {

    }

    public void goBack(View view) {
        finish();
    }
}