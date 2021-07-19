package com.pora.stempl;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

import com.pora.lib.PersonInfoEditModel;


public class PersonInfoAdapter extends RecyclerView.Adapter<PersonInfoAdapter.ViewHolder> {
    public static final String TAG = PersonInfoAdapter.class.getSimpleName();

    private ApplicationMy app;
    private OnItemClickListener listener;

    public static ArrayList<PersonInfoEditModel> personInfoEditArrayList;

    public interface OnItemClickListener {
        void onItemClick(View itemView, int position);

    }

    // Define the method that allows the parent activity or fragment to define the listener
    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    public PersonInfoAdapter(ApplicationMy app, String personName) {
        this.app = app;

        personInfoEditArrayList = new ArrayList<PersonInfoEditModel>();
        getFromFirebase(this, personName);
    }


    public void getFromFirebase(PersonInfoAdapter personInfoAdapter, String personName) {
        personInfoEditArrayList.clear();
        FirebaseDatabase.getInstance().getReference().child("people").child(app.idAPP).child(personName)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        for (DataSnapshot pairSnapshot : dataSnapshot.child("checkTimes").getChildren()) {
                            PersonInfoEditModel model = new PersonInfoEditModel();
                            model.setCheckedInTime(LocalDateTime.parse(pairSnapshot.child("checkIn").getValue().toString()));
                            if(pairSnapshot.child("checkOut").exists())
                                model.setCheckedOutTime(LocalDateTime.parse(pairSnapshot.child("checkOut").getValue().toString()));

                            personInfoEditArrayList.add(model);
                            personInfoAdapter.notifyDataSetChanged();
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
        View view = inflater.inflate(R.layout.rv_rowlayout_person_info, parent, false);
        // Return a new holder instance
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PersonInfoAdapter.ViewHolder holder, int position) {
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd. MM. yyyy");
        String formattedStartDate = personInfoEditArrayList.get(position).getCheckedTime().getCheckIn().format(dateFormatter);
        String formattedEndDate = personInfoEditArrayList.get(position).getCheckedTime().getCheckOut().format(dateFormatter);

        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH.mm");
        String formattedStartTime = personInfoEditArrayList.get(position).getCheckedTime().getCheckIn().format(timeFormatter);
        String formattedEndTime = personInfoEditArrayList.get(position).getCheckedTime().getCheckOut().format(timeFormatter);

        holder.tvStart.setText(formattedStartDate + ' ' + app.getString(R.string.at) + ' ' + formattedStartTime);
        holder.tvEnd.setText(formattedEndDate + ' ' + app.getString(R.string.at) + ' ' + formattedEndTime);

        if (position % 2 == 1) {
            holder.ozadje.setBackgroundColor(Color.LTGRAY);
        } else {
            holder.ozadje.setBackgroundColor(Color.WHITE);
        }
    }


    @Override
    public int getItemCount() {
        return personInfoEditArrayList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public TextView tvStart;
        public TextView tvEnd;
        public View ozadje;

        public ViewHolder(@NonNull View v) {
            super(v);
            tvStart = (TextView) v.findViewById(R.id.tvStart);
            tvEnd = (TextView) v.findViewById(R.id.tvEnd);

            ozadje = v.findViewById(R.id.layoutrow_info);
            v.setOnClickListener(view -> {
                if (listener != null) {
                    int position = this.getLayoutPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        listener.onItemClick(itemView, position);
                    }
                }
            });

        }

        @Override
        public void onClick(View v) {
        }
    }
}
