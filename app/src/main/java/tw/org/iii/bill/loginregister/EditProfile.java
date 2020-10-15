package tw.org.iii.bill.loginregister;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.util.HashMap;

public class EditProfile extends AppCompatActivity {

    public static final String TAG ="TAG";
    EditText profileFullName,profileEmail,proFilePhone;
    ImageView profileImageView;
    Button editPageSave;
    FirebaseAuth fAuth;
    FirebaseFirestore fStore;
    FirebaseUser user;
    StorageReference storageReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        profileFullName=findViewById(R.id.editTextTextPersonName);
        profileEmail=findViewById(R.id.editTextTextEmailAddress);
        proFilePhone=findViewById(R.id.editTextTextPhone);
        profileImageView=findViewById(R.id.edit_page_image);
        editPageSave=findViewById(R.id.edit_page_save);

        storageReference= FirebaseStorage.getInstance().getReference();



        fAuth=FirebaseAuth.getInstance();
        fStore=FirebaseFirestore.getInstance();
        user=fAuth.getCurrentUser();

        StorageReference profileRef=storageReference.child("users/"+fAuth.getCurrentUser().getUid()+"/profile.jpg");
        profileRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Picasso.get().load(uri).into(profileImageView);
            }
        });


        Intent data=getIntent();
        final String fullName=data.getStringExtra("fullName");
        String email=data.getStringExtra("email");
        final String phone=data.getStringExtra("phone");

        profileFullName.setText(fullName);
        profileEmail.setText(email);
        proFilePhone.setText(phone);
        profileImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent openGalleryIntent=new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(openGalleryIntent,1000);

            }
        });

        editPageSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(profileFullName.getText().toString().isEmpty()||profileEmail.getText().toString().isEmpty()||proFilePhone.getText().toString().isEmpty()){
                    Toast.makeText(EditProfile.this,"One or Many fields are empty.",Toast.LENGTH_SHORT).show();
                    return;
                }

                final String email=profileEmail.getText().toString();
                user.updateEmail(email).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        DocumentReference documentReference=fStore.collection("users").document(user.getUid());
                        HashMap<String,Object> edited=new HashMap<>();
                        edited.put("email",email);
                        edited.put("fName",profileFullName.getText().toString());
                        edited.put("phone",proFilePhone.getText().toString());
                        documentReference.update(edited);

                        Toast.makeText(EditProfile.this,"Edit Profile Successfully.",Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(getApplicationContext(),MainActivity.class));
                        finish();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(EditProfile.this,e.getMessage(),Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

        //ctrl+alt+select string will do line 11
        Log.d(TAG,"onCreate:"+fullName+" "+email+" "+phone);

    }

    protected void onActivityResult(int requestCode, int resultCode, @androidx.annotation.Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==1000){
            if(resultCode == Activity.RESULT_OK){
                Uri imageUri=data.getData();
                uploadImageToFirebase(imageUri);

            }
        }
    }
    private void uploadImageToFirebase(Uri imageUri){
        //upload image tp firebase storage

        final StorageReference fileRef=storageReference.child("users/"+fAuth.getCurrentUser().getUid()+"/profile.jpg");
        fileRef.putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                fileRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        //third api
                        Picasso.get().load(uri).into(profileImageView);
                    }
                });

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(EditProfile.this,"Uploaded Failed.",Toast.LENGTH_SHORT).show();
            }
        });
    }

}