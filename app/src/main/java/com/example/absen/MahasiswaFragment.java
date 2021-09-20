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

public class MahasiswaFragment extends Fragment {

    Button masuk_mahasiswa;
    TextView npm_mahasiswa;

    FirebaseFirestore firebaseFirestore;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_mahasiswa, container, false);

        firebaseFirestore = FirebaseFirestore.getInstance();

        masuk_mahasiswa = view.findViewById(R.id.masuk_mahasiswa);
        npm_mahasiswa = view.findViewById(R.id.npm_mahasiswa);

        masuk_mahasiswa.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (npm_mahasiswa.getText().toString().equals("")){
                    Toast.makeText(getActivity(), "Isikan NPM", Toast.LENGTH_SHORT).show();
                }else {
                    ProgressDialog pd = new ProgressDialog(getActivity());
                    pd.setMessage("sedang membuat...");
                    pd.show();
                    final DocumentReference documentReference = firebaseFirestore.collection("Daftar Mahasiswa")
                            .document(npm_mahasiswa.getText().toString());
                    documentReference.get().addOnCompleteListener(new OnCompleteListener< DocumentSnapshot >() {
                        @Override
                        public void onComplete(@NonNull Task< DocumentSnapshot > task) {
                            if (task.isSuccessful()){
                                DocumentSnapshot documentSnapshot = task.getResult();
                                if (documentSnapshot.exists()){
                                    Home_Mahasiswa.npm_login = npm_mahasiswa.getText().toString();
                                    Barcode_Mahasiswa.barcode_mahasiswa = npm_mahasiswa.getText().toString();
                                    Rekap_Absen_Mahasiswa.npm_mahasiswa = npm_mahasiswa.getText().toString();
                                    Intent intent = new Intent(getActivity(), Home_Mahasiswa.class);
                                    startActivity(intent);
                                    getActivity().finish();
                                }else {
                                    Toast.makeText(getContext(), "NPM tidak ditemukan", Toast.LENGTH_SHORT).show();
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
