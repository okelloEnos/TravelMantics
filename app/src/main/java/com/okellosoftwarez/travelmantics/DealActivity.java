package com.okellosoftwarez.travelmantics;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

public class DealActivity extends AppCompatActivity {
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;
    private static final int PICTURE = 42;
    EditText etTitle;
    EditText etPrice;
    EditText etDescription;
    TravelDeal deal;
    ImageView detailImage;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_deal);
        firebaseDatabase = FirebaseUtil.firebaseDatabase;
        databaseReference = FirebaseUtil.databaseReference;
        etTitle = findViewById(R.id.etTitle);
        etPrice = findViewById(R.id.etPrice);
        etDescription = findViewById(R.id.etDescription);
        detailImage = findViewById(R.id.image);
        Intent passedIntent = getIntent();
        TravelDeal deal = (TravelDeal) passedIntent.getSerializableExtra("Deal");
        if (deal == null){
            deal = new TravelDeal();
        }
        this.deal = deal;
        etTitle.setText(deal.getTitle());
        etDescription.setText(deal.getDescription());
        etPrice.setText(deal.getPrice());
        showImage(deal.getImageUrl());
        Button btnImage = findViewById(R.id.btnSearch);
        btnImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent imageIntent = new Intent();
                imageIntent.setType("image/*");
                imageIntent.setAction(Intent.ACTION_GET_CONTENT);
                imageIntent.putExtra(Intent.EXTRA_LOCAL_ONLY, true);
                startActivityForResult(imageIntent.createChooser(imageIntent, "Insert Image"),PICTURE);

            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.save_menu, menu);
        if (FirebaseUtil.isAdmin){
                menu.findItem(R.id.save_menu).setVisible(true);
                menu.findItem(R.id.delete_menu).setVisible(true);
                editTextEnabled(true);
                findViewById(R.id.btnSearch).setEnabled(true);
        }
        else {
            menu.findItem(R.id.save_menu).setVisible(false);
            menu.findItem(R.id.delete_menu).setVisible(false);
            editTextEnabled(false);
            findViewById(R.id.btnSearch).setEnabled(false);
        }
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICTURE && resultCode == RESULT_OK && data != null && data.getData() != null){
            Uri imageUri = data.getData();
//            int width = Resources.getSystem().getDisplayMetrics().widthPixels;
//            Picasso.with(this)
//                    .load(imageUri)
//                    .resize(width, width*2/3)
//                    .centerCrop()
//                    .into(detailImage);
            StorageReference reference = FirebaseUtil.storageReference.child(imageUri.getLastPathSegment());
            reference.putFile(imageUri).addOnSuccessListener(this, new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
//                    String url = taskSnapshot.getMetadata().getReference().getDownloadUrl().toString();
//                    deal.setImageUrl(url);
//                    showImage(url);
//                    if (taskSnapshot.getMetadata() != null) {
//                        if (taskSnapshot.getMetadata().getReference() != null) {
//                            Task<Uri> result = taskSnapshot.getStorage().getDownloadUrl();
//                            result.addOnSuccessListener(new OnSuccessListener<Uri>() {
//                                @Override
//                                public void onSuccess(Uri uri) {
//                                    String imageUrl = uri.toString();
                                    //createNewPost(imageUrl);
                    if (taskSnapshot.getMetadata() != null){
                        if (taskSnapshot.getMetadata().getReference() != null){
                            Task<Uri> result = taskSnapshot.getStorage().getDownloadUrl();
                            result.addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    String imageUrl = uri.toString();
                                    deal.setImageUrl(imageUrl);
                                    Log.d("Url: ", imageUrl);
                                    showImage(imageUrl);
                                }
                            });
                        }
                    }
                    String pictureName = taskSnapshot.getStorage().getPath();
                    deal.setImageName(pictureName);
                    Log.d("Name", pictureName);
                }
            });
        }
    }
//                }});
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
    private void Delete() {
        if (deal.getID() == null) {
            Toast.makeText(this, "Please Save Before Deleting any Deal", Toast.LENGTH_LONG).show();
            return;
        } else {
            databaseReference.child(deal.getID()).removeValue();
            Log.d("image name", deal.getImageName());
            if (deal.getImageName() != null && deal.getImageName().isEmpty() == false) {
                StorageReference picRef = FirebaseUtil.firebaseStorage.getReference().child(deal.getImageName());
                picRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d("Delete Image", "Image Successfully Deleted");
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d("Delete Image", e.getMessage());
                    }
                });
            }
        }

    }
    private void backList(){
        Intent backIntent = new Intent(this,ListActivity.class);
        startActivity(backIntent);
    }
    private void editTextEnabled(boolean isEnabled){
        etTitle.setEnabled(isEnabled);
        etPrice.setEnabled(isEnabled);
        etDescription.setEnabled(isEnabled);
    }
    private void showImage(String url){
        if (url != null && url.isEmpty() == false){
            int width = Resources.getSystem().getDisplayMetrics().widthPixels;
            Picasso.with(this)
                    .load(url)
                    .resize(width, width*2/3)
                    .centerCrop()
                    .into(detailImage);
        }

    }

}
