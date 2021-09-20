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

public class DosenFragment extends Fragment {

    Button masuk_mahasiswa;
    TextView nip_dosen;

    FirebaseFirestore firebaseFirestore;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_dosen, container, false);

        masuk_mahasiswa = view.findViewById(R.id.masuk_mahasiswa);
        nip_dosen = view.findViewById(R.id.nip_dosen);

        firebaseFirestore = FirebaseFirestore.getInstance();

        masuk_mahasiswa.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (nip_dosen.getText().toString().equals("")){
                    Toast.makeText(getActivity(), "Isikan NIP", Toast.LENGTH_SHORT).show();
                }else {
                    ProgressDialog pd = new ProgressDialog(getActivity());
                    pd.setMessage("sedang membuat...");
                    pd.show();
                    DocumentReference documentReference = firebaseFirestore.collection("Dosen").document(nip_dosen.getText().toString());
                    documentReference.get().addOnCompleteListener(new OnCompleteListener< DocumentSnapshot >() {
                        @Override
                        public void onComplete(@NonNull Task< DocumentSnapshot > task) {
                            if (task.isSuccessful()){
                                DocumentSnapshot documentSnapshot = task.getResult();
                                if (documentSnapshot.exists()){
                                    Rekap_Absen.nip_dosen = nip_dosen.getText().toString();
                                    Home_Dosen.nip_dosen = nip_dosen.getText().toString();
                                    Grafik_Mahasiswa_Dosen.nip_dosen = nip_dosen.getText().toString();
                                    ScannerBarcode.nip_dosen = nip_dosen.getText().toString();
                                    Intent intent = new Intent(getActivity(), Home_Dosen.class);
                                    startActivity(intent);
                                    getActivity().finish();
                                }else {
                                    Toast.makeText(getActivity(), "NIP tidak ditemukan", Toast.LENGTH_SHORT).show();
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
