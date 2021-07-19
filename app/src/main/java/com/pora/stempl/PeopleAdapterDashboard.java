package com.pora.stempl;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.ToggleButton;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.time.LocalDateTime;
import java.util.ArrayList;

import com.pora.lib.PeopleEditModel;


public class PeopleAdapterDashboard extends RecyclerView.Adapter<PeopleAdapterDashboard.ViewHolder> {
    public static final String TAG = PeopleAdapterDashboard.class.getSimpleName();
    private ApplicationMy app;
    private OnItemClickListener listener;

    public static ArrayList<PeopleEditModel> peopleEditModelArrayList;

    public interface OnItemClickListener {
        void onItemClick(View itemView, int position);
        void onToggleButton(View itemView, int position);
        void onInfoButton(View itemView, int position);

    }

    // Define the method that allows the parent activity or fragment to define the listener
    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    public PeopleAdapterDashboard(ApplicationMy app) {
        peopleEditModelArrayList = new ArrayList<PeopleEditModel>();
        getFromFirebase(this);

        this.app = app;
    }


    public void getFromFirebase(PeopleAdapterDashboard peopleAdapter) {
        peopleEditModelArrayList.clear();
        FirebaseDatabase.getInstance().getReference().child("people")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        for (DataSnapshot snapshot : dataSnapshot.child(app.idAPP).getChildren()) {
                            PeopleEditModel model = new PeopleEditModel();
                            model.setName(snapshot.getKey());
                            if (snapshot.child("pin").exists()) {
                                model.setPin(snapshot.child("pin").getValue().toString());
                            }
                            if (snapshot.child("checkTimes").exists()){
                                for (DataSnapshot checkedSnapshot : dataSnapshot.child(app.idAPP).child(snapshot.getKey()).child("checkTimes").getChildren()){
                                    model.addCheckIn(LocalDateTime.parse(checkedSnapshot.child("checkIn").getValue().toString()));
                                    if(checkedSnapshot.child("checkOut").exists())
                                        model.addCheckOut(LocalDateTime.parse(checkedSnapshot.child("checkOut").getValue().toString()));
                                }
                            }

                            peopleEditModelArrayList.add(model);
                            peopleAdapter.notifyDataSetChanged();
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                    }
                });

    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        // Inflate the custom layout
        View view = inflater.inflate(R.layout.rv_rowlayout_person_dashboard, parent, false);
        // Return a new holder instance
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PeopleAdapterDashboard.ViewHolder holder, int position) {
        holder.tvName.setText(peopleEditModelArrayList.get(position).getName());
        holder.tbCheckInOut.setChecked(!peopleEditModelArrayList.get(position).isCheckedIn());

        if (position % 2 == 1) {
            holder.ozadje.setBackgroundColor(Color.LTGRAY);
        } else {
            holder.ozadje.setBackgroundColor(Color.WHITE);
        }
    }


    @Override
    public int getItemCount() {
        return peopleEditModelArrayList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public TextView tvName;
        public ToggleButton tbCheckInOut;
        public Button btInfo;
        public View ozadje;

        public ViewHolder(@NonNull View v) {
            super(v);
            tvName = (TextView) v.findViewById(R.id.tv_name);
            tbCheckInOut = (ToggleButton) v.findViewById(R.id.tbCheckInOut);
            btInfo = (Button) v.findViewById(R.id.btInfo);
            ozadje = v.findViewById(R.id.layoutrow_person);
            v.setOnClickListener(view -> {
                if (listener != null) {
                    int position = this.getLayoutPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        listener.onItemClick(itemView, position);
                    }
                }
            });

            tbCheckInOut.setOnClickListener(this);
            btInfo.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.tbCheckInOut:
                    listener.onToggleButton(v, this.getLayoutPosition());
                    break;
                case R.id.btInfo:
                    listener.onInfoButton(v, this.getLayoutPosition());
                    break;
                default:
                    break;
            }
        }
    }
}
