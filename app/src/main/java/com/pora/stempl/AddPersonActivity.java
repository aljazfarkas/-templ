package com.pora.stempl;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.pora.lib.Person;

public class AddPersonActivity extends AppCompatActivity {
    private ApplicationMy app;
    private EditText etPersonName;
    private EditText etPin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_person);
        bindData();
        initData();
    }
    private void initData() {
        app = (ApplicationMy) getApplication();
    }
    private void bindData() {
        this.etPersonName = (EditText) findViewById(R.id.etPersonName);
        this.etPin = (EditText) findViewById(R.id.etPin);
    }
    public void addPerson(View view) {
        Person newPerson = new Person(etPersonName.getText().toString(), etPin.getText().toString());
        app.writePerson(newPerson);

        Intent intent = new Intent(getBaseContext(), NavigationActivity.class);
        startActivity(intent);
    }
}