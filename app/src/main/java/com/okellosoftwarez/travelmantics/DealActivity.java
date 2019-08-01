package com.okellosoftwarez.travelmantics;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class DealActivity extends AppCompatActivity {
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;
    EditText etTitle;
    EditText etPrice;
    EditText etDescription;
    TravelDeal deal;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_insert);
        FirebaseUtil.openReference("TravelDeal",this);
        firebaseDatabase = FirebaseUtil.firebaseDatabase;
        databaseReference = FirebaseUtil.databaseReference;
        etTitle = findViewById(R.id.etTitle);
        etPrice = findViewById(R.id.etPrice);
        etDescription = findViewById(R.id.etDescription);
        Intent passedIntent = getIntent();
        TravelDeal deal = (TravelDeal) passedIntent.getSerializableExtra("Deal");
        if (deal == null){
            deal = new TravelDeal();
        }
        this.deal = deal;
        etTitle.setText(deal.getTitle());
        etDescription.setText(deal.getDescription());
        etPrice.setText(deal.getPrice());
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
                backList();
                return true;
            case R.id.delete_menu:
                Delete();
                Toast.makeText(this,"Deal Deleted", Toast.LENGTH_LONG);
                backList();
                return true;

                default:
                            return super.onOptionsItemSelected(item);
        }
    }

    private void clean() {
        etTitle.setText("");
        etPrice.setText("");
        etDescription.setText("");
        etTitle.requestFocus();
    }

    private void save() {
        deal.setTitle(etTitle.getText().toString());
        deal.setPrice(etPrice.getText().toString());
        deal.setDescription(etDescription.getText().toString());
        if (deal.getID() == null){
            databaseReference.push().setValue(deal);
        }
        else {
            databaseReference.child(deal.getID()).setValue(deal);
        }
    }
    private void Delete(){
        if (deal.getID() == null){
            Toast.makeText(this, "Please Save Before Deleting any Deal",Toast.LENGTH_LONG).show();
            return;
        }
        else {
            databaseReference.child(deal.getID()).removeValue();
        }
    }
    private void backList(){
        Intent backIntent = new Intent(this,ListActivity.class);
        startActivity(backIntent);
    }

}
