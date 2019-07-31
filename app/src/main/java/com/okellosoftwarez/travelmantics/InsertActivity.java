package com.okellosoftwarez.travelmantics;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class InsertActivity extends AppCompatActivity {
    private FirebaseDatabase mfirebaseDatabase;
    private DatabaseReference mdatabaseReference;
    private EditText etTitle,etPrice,etDescription;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_insert);
        mfirebaseDatabase = FirebaseDatabase.getInstance();
        mdatabaseReference = mfirebaseDatabase.getReference().child("TravelDeal");
        etTitle = findViewById(R.id.etTitle);
        etPrice = findViewById(R.id.etPrice);
        etDescription = findViewById(R.id.etDescription);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.save_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.save_menu:
                    save();
                    Toast.makeText(this, "Deal Saved", Toast.LENGTH_LONG).show();
                    clean();
                    return true;

                default:
                    return super.onOptionsItemSelected(item);
        }
    }

    private void clean() {
        etTitle.setText("");
        etPrice.setText("");
        etDescription.setText("");
    }

    private void save() {
        String title = etTitle.getText().toString();
        String price = etPrice.getText().toString();
        String description = etDescription.getText().toString();
        TravelDeal deal = new TravelDeal(title, price, description, "");
        mdatabaseReference.push().setValue(deal);
    }

}