package com.example.mini_project_2_aous_alnima;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ListView;
import android.widget.Toast;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

public class Main2Activity extends AppCompatActivity {

    ListView visitlist;
    List<gym> gymList;
    DatabaseReference databaseGPS;
    List<gym> GymVisits;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        databaseGPS = FirebaseDatabase.getInstance().getReference("gymVisits/");
        databaseGPS.addChildEventListener(new Main2Activity.locationChildEventListener());

        visitlist = (ListView) findViewById(R.id.list);
        gymList = new ArrayList<>();
        gymlist adapter = new gymlist(this, gymList);
        visitlist.setAdapter(adapter);
    }

    private class locationChildEventListener implements ChildEventListener {
        @Override
        public void onChildAdded(@NonNull DataSnapshot ds, @Nullable String s) {
            gym NewVisit = ds.getValue(gym.class);
            MakeToast(NewVisit.getTimeIn());
            GymVisits.add(NewVisit);
        }

        @Override
        public void onChildRemoved (@NonNull DataSnapshot ds){
            if(ds.getChildrenCount()==4) {
                // do sth

            }
        }
        @Override
        public void onChildChanged (@NonNull DataSnapshot dataSnapshot, @Nullable String s){
        }
        @Override
        public void onChildMoved (@NonNull DataSnapshot dataSnapshot, @Nullable String s){
        }
        @Override
        public void onCancelled (@NonNull DatabaseError databaseError){
        }
    }
    public void MakeToast(String s){
        Toast.makeText(this, s, Toast.LENGTH_LONG).show();

    }
}
