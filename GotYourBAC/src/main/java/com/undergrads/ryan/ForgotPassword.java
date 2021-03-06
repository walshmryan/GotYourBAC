package com.undergrads.ryan;

import android.support.annotation.NonNull;
import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;

/*

Fragment class to help people who forget passwords

*/
public class ForgotPassword extends Fragment {

    private Button btnSend;
    private Button btnCancel;
    private EditText editEmail;
    final String firebaseTag = "firebase";
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private DatabaseReference mDatabase;
    FirebaseAuth auth;

    public ForgotPassword() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_forgottenpassword, container, false);

        //initialize values
        btnCancel = (Button) v.findViewById(R.id.btnCancel);
        btnSend = (Button) v.findViewById(R.id.btnSend);
        editEmail = (EditText) v.findViewById(R.id.edtEmail);
        auth = FirebaseAuth.getInstance();

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //go back to other fragment
                getFragmentManager().popBackStack();
            }
        });

        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // firebase code to send a password reset email
                try{
                    auth.sendPasswordResetEmail(editEmail.getText().toString())
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    CharSequence text = editEmail.getText().toString();
                                    if (task.isSuccessful()) {
                                        Toast toast = Toast.makeText(getActivity(),text,Toast.LENGTH_SHORT);
                                        toast.show();
                                    } else {
                                        text = "No account found for this email address.";
                                        Toast toast = Toast.makeText(getActivity(), text, Toast.LENGTH_SHORT);
                                        toast.show();
                                    }
                                }
                            });
                }catch (Exception e){
                    String text = "No account found for this email address.";
                    Toast toast = Toast.makeText(getActivity(), text, Toast.LENGTH_SHORT);
                    toast.show();
                }
            }
        });
        return v;
    }
}