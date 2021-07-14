package com.pora.stempl;

import android.app.Application;
import android.content.SharedPreferences;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Build;
import android.preference.PreferenceManager;
import android.renderscript.Sampler;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.pora.lib.People;
import com.pora.lib.PeopleEditModel;
import com.pora.lib.Person;


import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

public class ApplicationMy extends Application {
    public static final String TAG = ApplicationMy.class.getSimpleName();
    private FirebaseDatabase database;
    private DatabaseReference peopleRef;

    public static final String APP_ID = "APP_ID_KEY";
    public static SharedPreferences sp;
    String idAPP;

    @Override
    public void onCreate() {
        super.onCreate();
        setAppId();

        FirebaseApp.initializeApp(this);
        database = FirebaseDatabase.getInstance();
        peopleRef = database.getReference("people");
    }

    public void setAppId() {
        sp = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        if (sp.contains(APP_ID)) //READ IT FROM FILE
            idAPP = sp.getString(APP_ID, "DEFAULT VALUE ERR");
        else { //FIRST TIME GENERATE ID AND SAVE IT
            idAPP = UUID.randomUUID().toString().replace("-", "");
            SharedPreferences.Editor editor = sp.edit();
            editor.putString(APP_ID, idAPP);
            editor.apply();
        }
        //list.setUserID(idAPP);
        Log.d(TAG, "id:" + idAPP);
    }

    public PeopleEditModel findPersonByName(String name){
        for(PeopleEditModel person : PeopleAdapter.peopleEditModelArrayList){
            if(person.getName().equals(name)){
                return person;
            }
        }
        return null;
    }

    public void checkInPerson(String name, LocalDateTime lt) {
        DatabaseReference checkTimesRef = peopleRef.child(idAPP).child(name).child("checkTimes");
        ValueEventListener valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                int childrenCount = (int) dataSnapshot.getChildrenCount();
                peopleRef.child(idAPP).child(name).child("checkTimes").child(String.valueOf(childrenCount)).child("checkIn").setValue(lt.toString());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.d(TAG, databaseError.getMessage()); //Don't ignore errors!
            }
        };
        checkTimesRef.addListenerForSingleValueEvent(valueEventListener);

    }

    public void checkOutPerson(String name, LocalDateTime lt) {
        DatabaseReference checkTimesRef = peopleRef.child(idAPP).child(name).child("checkTimes");
        ValueEventListener valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                int childrenCount = (int) dataSnapshot.getChildrenCount();
                peopleRef.child(idAPP).child(name).child("checkTimes").child(String.valueOf(childrenCount - 1)).child("checkOut").setValue(lt.toString());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.d(TAG, databaseError.getMessage()); //Don't ignore errors!
            }
        };
        checkTimesRef.addListenerForSingleValueEvent(valueEventListener);
    }

    public void writePerson(Person person) {
        final String uuid = UUID.randomUUID().toString().replace("-", "");
        peopleRef.child(idAPP).child(person.getName()).child("uuid").setValue(uuid);
        peopleRef.child(idAPP).child(person.getName()).child("pin").setValue(person.getPin());

    }
}
