package com.example.absen;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class KampusFragment extends Fragment {

    TextView username, password;
    Button masuk;

    FirebaseFirestore firebaseFirestore;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_kampus, container, false);

        username = view.findViewById(R.id.username_kampus);
        password = view.findViewById(R.id.password_kampus);
        masuk = view.findViewById(R.id.masuk_kampus);

        firebaseFirestore = FirebaseFirestore.getInstance();

        masuk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (username.getText().toString().equals("")){
                    Toast.makeText(getActivity(), "Isikan username", Toast.LENGTH_SHORT).show();
                }else if (password.getText().toString().equals("")){
                    Toast.makeText(getActivity(), "Isikan password", Toast.LENGTH_SHORT).show();
                }else {
                    ProgressDialog pd = new ProgressDialog(getActivity());
                    pd.setMessage("sedang membuat...");
                    pd.show();
                    DocumentReference documentReference = firebaseFirestore.collection("Admin Kampus")
                            .document(username.getText().toString()+", "+password.getText().toString());
                    documentReference.get().addOnCompleteListener(new OnCompleteListener< DocumentSnapshot >() {
                        @Override
                        public void onComplete(@NonNull Task< DocumentSnapshot > task) {
                            if (task.isSuccessful()){
                                DocumentSnapshot documentSnapshot = task.getResult();
                                if (documentSnapshot.exists()){
                                    Intent intent = new Intent(getActivity(), Home_Kampus.class);
                                    startActivity(intent);
                                    getActivity().finish();
                                }else {
                                    Toast.makeText(getActivity(), "Maaf username dan password tidak valid", Toast.LENGTH_SHORT).show();
                                }
                            }
                        }
                    });
                    pd.dismiss();
                }
            }
        });
        
        return view;
    }
}
