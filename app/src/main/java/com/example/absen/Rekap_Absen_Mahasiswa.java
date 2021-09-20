package com.example.absen;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import com.example.absen.Adapter.HomeDosenAdapter;
import com.example.absen.Adapter.RekapAbsenMahasiswaAdapter;
import com.example.absen.Model.DaftarMahasiswaModel;
import com.example.absen.Model.RekapAbsenMahasiswaModel;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.List;

public class Rekap_Absen_Mahasiswa extends AppCompatActivity {

    FirebaseFirestore firebaseFirestore;

    RecyclerView list_rekap_mahasiswa;
    RekapAbsenMahasiswaAdapter mAdapter;
    ImageView btn_search;
    EditText txt_search;

    public static String npm_mahasiswa = "";
    public static String kelas_mahasiswa = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rekap__absen__mahasiswa);

        firebaseFirestore = FirebaseFirestore.getInstance();
        list_rekap_mahasiswa = findViewById(R.id.list_rekap_mahasiswa);
        btn_search = findViewById(R.id.btn_search);
        txt_search = findViewById(R.id.txt_search);

        btn_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (txt_search.getText().toString().equals("")){
                    getRekap();
                }else {
                    getRekapSearch();
                }
            }
        });

        RekapAbsenMahasiswaAdapter.kelas_mahasiswa = kelas_mahasiswa;
        RekapAbsenMahasiswaAdapter.npm_mahasiswa = npm_mahasiswa;
        getRekap();
    }

    private void getRekapSearch() {
        mAdapter = new RekapAbsenMahasiswaAdapter(npm_mahasiswa);
        list_rekap_mahasiswa.setLayoutManager(new LinearLayoutManager(this));
        list_rekap_mahasiswa.setAdapter(mAdapter);

        firebaseFirestore.collection("Mahasiswa").document(kelas_mahasiswa).collection("Mahasiswa "+kelas_mahasiswa)
                .document(npm_mahasiswa).collection("list_dosen").whereEqualTo("nama_dosen", txt_search.getText().toString())
                .addSnapshotListener(this, new EventListener< QuerySnapshot >() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                        List< RekapAbsenMahasiswaModel > rekapAbsenMahasiswaModels = queryDocumentSnapshots.toObjects(RekapAbsenMahasiswaModel.class);

                        mAdapter.setData(rekapAbsenMahasiswaModels);
                        list_rekap_mahasiswa.smoothScrollToPosition(mAdapter.getItemCount());
                    }
                });
    }

    private void getRekap() {
        mAdapter = new RekapAbsenMahasiswaAdapter(npm_mahasiswa);
        list_rekap_mahasiswa.setLayoutManager(new LinearLayoutManager(this));
        list_rekap_mahasiswa.setAdapter(mAdapter);

        firebaseFirestore.collection("Mahasiswa").document(kelas_mahasiswa).collection("Mahasiswa "+kelas_mahasiswa)
                .document(npm_mahasiswa).collection("list_dosen")
                .addSnapshotListener(this, new EventListener< QuerySnapshot >() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                        List< RekapAbsenMahasiswaModel > rekapAbsenMahasiswaModels = queryDocumentSnapshots.toObjects(RekapAbsenMahasiswaModel.class);

                        mAdapter.setData(rekapAbsenMahasiswaModels);
                        list_rekap_mahasiswa.smoothScrollToPosition(mAdapter.getItemCount());
                    }
                });
    }

    public void back_home(View view) {
        startActivity(new Intent(getApplicationContext(), Home_Mahasiswa.class));
        this.finish();
    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(getApplicationContext(), Home_Mahasiswa.class));
        super.onBackPressed();
    }

    public void to_tanggal(View view) {
        startActivity(new Intent(getApplicationContext(), Tanggal_Rekap_Mahasiswa.class));
        this.finish();
    }
}
