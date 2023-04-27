package com.example.catfeederapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    FloatingActionButton fab;
    RecyclerView schedRecyclerView;

    List<Schedule> scheduleList;

    DatabaseReference databaseReference;

    ScheduleAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        fab = findViewById(R.id.fab);
        schedRecyclerView = findViewById(R.id.schedList);

        // fetch the schedules from the database
        databaseReference = FirebaseDatabase.getInstance().getReference("Schedules");
        scheduleList = new ArrayList<>();
        schedRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        adapter = new ScheduleAdapter(this, scheduleList);
        schedRecyclerView.setAdapter(adapter);

        // listen for changes in the database
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                scheduleList.clear();

                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    // print the data
                    Log.d("Data", dataSnapshot.getValue().toString());

                    String schedId = dataSnapshot.child("sched_token").getValue().toString();
                    String schedTime = dataSnapshot.child("sched_time").getValue().toString();
                    String schedRepeat = dataSnapshot.child("sched_repeat").getValue().toString();
                    String bodyWeight = dataSnapshot.child("body_weight").getValue().toString();
                    String totalGrams = dataSnapshot.child("total_grams").getValue().toString();
                    String dateCreated = dataSnapshot.child("date_created").getValue().toString();
                    boolean isEnabled = Boolean.parseBoolean(dataSnapshot.child("enabled").getValue().toString());

                    // create a new schedule object
                    Schedule schedule = new Schedule(schedId, schedTime, schedRepeat, bodyWeight, totalGrams, dateCreated, isEnabled);
                    scheduleList.add(schedule);

                    // update the adapter
                    adapter.notifyDataSetChanged();

                }

                // print the size of the list
                Log.d("Size", String.valueOf(scheduleList.size()));

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(MainActivity.this, "Error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });


        fab.setOnClickListener(v -> {
            // call the AddSchedule activity
            Intent intent = new Intent(this, AddSchedule.class);
            startActivity(intent);
        });
    }
}