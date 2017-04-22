package com.google.example.tbmpskeleton;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.undergrads.ryan.R;


public class CreateICEFragment extends Fragment{

    private EditText nameTxt;
    private EditText numberTxt;
    private Button save;
    private Button skip;

    public interface iceCreateListener{
        public void goToStroopBaseline();

    }

    iceCreateListener listener;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_ice_create, container, false);

        nameTxt = (EditText) v.findViewById(R.id.name);
        numberTxt = (EditText) v.findViewById(R.id.number);
        save = (Button) v.findViewById(R.id.save);
        skip = (Button) v.findViewById(R.id.skip);

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = nameTxt.getText().toString();
                String number = numberTxt.getText().toString();

                FirebaseCall fRef = new FirebaseCall();
                fRef.updateContactInfo(name, number);

                listener.goToStroopBaseline();

            }
        });

        skip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.goToStroopBaseline();

            }
        });


        return v;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        // This makes sure that the container activity has implemented
        // the callback interface. If not, it throws an exception
        try {
            listener = (CreateICEFragment.iceCreateListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnHeadlineSelectedListener");
        }
    }

}

