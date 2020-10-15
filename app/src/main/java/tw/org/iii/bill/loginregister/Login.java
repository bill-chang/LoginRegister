package tw.org.iii.bill.loginregister;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

public class Login extends AppCompatActivity {
    EditText rEmail,rPassword;
    Button rLoginBtn;
    TextView rCreateBtn,forgetBtn;
    ProgressBar progressBar;
    FirebaseAuth fAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        rEmail=findViewById(R.id.Login_Email_et);
        rPassword=findViewById(R.id.Login_Pw_et);
        progressBar=findViewById(R.id.login_progress);
        rLoginBtn=findViewById(R.id.Login_bt);
        rCreateBtn=findViewById(R.id.Login_click_tv);
        forgetBtn=findViewById(R.id.forgetPw);
        fAuth=FirebaseAuth.getInstance();



        rLoginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String email=rEmail.getText().toString().trim();
                String password=rPassword.getText().toString().trim();

                if(TextUtils.isEmpty(email)){
                    rEmail.setError("Email is Required");
                    return;
                }

                if (TextUtils.isEmpty(password)){
                    rPassword.setError("Password is Required");
                    return;
                }

                if(password.length()<6){
                    rPassword.setError("Password Must be >=6 Characters");
                    return;
                }

                progressBar.setVisibility(View.VISIBLE);
                //
                fAuth.signInWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            Toast.makeText(Login.this,"Logged in Successfully",Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(getApplicationContext(),MainActivity.class));
                        }else{
                            Toast.makeText(Login.this,"Error!"+task.getException().getMessage(),Toast.LENGTH_SHORT).show();
                            progressBar.setVisibility(View.GONE);
                        }
                    }
                });
            }
        });

     rCreateBtn.setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View v) {
             startActivity(new Intent(getApplicationContext(),Register.class));

         }
     });

     forgetBtn.setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View v) {
             final EditText resetMail=new EditText(v.getContext());
             final AlertDialog.Builder passwordResetDialog=new AlertDialog.Builder(v.getContext());
             passwordResetDialog.setTitle("Reset Password?");
             passwordResetDialog.setMessage("Enter Your Email To Received Reset Link.");
             passwordResetDialog.setView(resetMail);

             passwordResetDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                 @Override
                 public void onClick(DialogInterface dialog, int which) {
                     //extract thi email and send reset link
                    String mail=resetMail.getText().toString();
                    fAuth.sendPasswordResetEmail(mail).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                          Toast.makeText(Login.this,"Reset Link Sent To Your Email.",Toast.LENGTH_SHORT).show();
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(Login.this,"Error!Reset Link is Not Sent"+e.getMessage(),Toast.LENGTH_SHORT).show();
                        }
                    });
                 }
             });

             passwordResetDialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
                 @Override
                 public void onClick(DialogInterface dialog, int which) {
                     //close the dialog

                 }
             });
             passwordResetDialog.create().show();
         }
     });
    }
}