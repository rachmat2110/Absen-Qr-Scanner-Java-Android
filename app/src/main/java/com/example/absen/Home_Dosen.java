package com.example.absen;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import cn.pedant.SweetAlert.SweetAlertDialog;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.absen.Adapter.HomeDosenAdapter;
import com.example.absen.Model.DaftarMahasiswaModel;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Home_Dosen extends AppCompatActivity {

    Spinner spinner_matkul, spinner_kelas, spinner_minggu;
    LinearLayout linearlayout;
    FirebaseFirestore firebaseFirestore;
    TextView tanggal, nama_dosen;
    Button proses_mahasiswa;

    HomeDosenAdapter mAdapter;
    private RecyclerView list_mahasiswa;

    ArrayAdapter matkul;
    ArrayList<String> matkulArray;

    ArrayAdapter kelasAdapter;
    ArrayList<String> kelasArray;

    String foto_dosen;
    public static String nip_dosen = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home__dosen);

        spinner_matkul = findViewById(R.id.spinner_matkul);
        spinner_kelas = findViewById(R.id.spinner_kelas);
        spinner_minggu = findViewById(R.id.spinner_minggu);
        linearlayout = findViewById(R.id.linearlayout);
        tanggal = findViewById(R.id.tanggal);
        nama_dosen = findViewById(R.id.nama_dosen);
        proses_mahasiswa = findViewById(R.id.proses_mahasiswa);
        list_mahasiswa = findViewById(R.id.list_mahasiswa);

        firebaseFirestore = FirebaseFirestore.getInstance();
        matkulArray = new ArrayList<>();
        kelasArray = new ArrayList<>();

        DocumentReference documentReference = firebaseFirestore.collection("Dosen").document(nip_dosen);
        documentReference.addSnapshotListener(this, new EventListener< DocumentSnapshot >() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                nama_dosen.setText(documentSnapshot.getString("nama_dosen"));
                foto_dosen = documentSnapshot.getString("foto_dosen");
            }
        });

        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
        String date = dateFormat.format(new Date());
        tanggal.setText(date);

        SpinnerMatkul();
        SpinnerKelas();

        Snackbar snackbar = Snackbar
                .make(linearlayout, "Harap masukkan mata kuliah dan kelas", Snackbar.LENGTH_LONG);
        snackbar.show();

        HomeDosenAdapter.keterangan = "load";
        load_mahasiswa();
        proses_mahasiswa.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                prosesMahasiswa();
            }
        });
    }

    private void prosesMahasiswa() {
        DocumentReference documentReference = firebaseFirestore.collection("Dosen")
                .document(nip_dosen).collection("Kelas dan Matkul")
                .document(spinner_kelas.getSelectedItem().toString()+", "+spinner_matkul.getSelectedItem().toString());
        documentReference.get().addOnCompleteListener(new OnCompleteListener< DocumentSnapshot >() {
            @Override
            public void onComplete(@NonNull Task< DocumentSnapshot > task) {
                if (task.isSuccessful()){
                    DocumentSnapshot documentSnapshot = task.getResult();
                    if (documentSnapshot.exists()){
                        list_mahasiswa.setVisibility(View.VISIBLE);
                        HomeDosenAdapter.keterangan = "proses";
                        HomeDosenAdapter.nip_dosen = nip_dosen;
                        HomeDosenAdapter.kelas_spinner = spinner_kelas.getSelectedItem().toString();
                        HomeDosenAdapter.matkul_spinner = spinner_matkul.getSelectedItem().toString();
                        load_mahasiswa_proses();
                        Toast.makeText(Home_Dosen.this, "Data ditemukan", Toast.LENGTH_SHORT).show();
                    }else {
                        list_mahasiswa.setVisibility(View.GONE);
                        new SweetAlertDialog(Home_Dosen.this, SweetAlertDialog.WARNING_TYPE)
                                .setTitleText("OOPS!")
                                .setContentText("Data tidak ditemukan")
                                .setConfirmText("OKE")
                                .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                    @Override
                                    public void onClick(SweetAlertDialog sweetAlertDialog) {
                                        sweetAlertDialog.dismissWithAnimation();
                                    }
                                }).show();
                    }
                }
            }
        });
    }

    private void SpinnerMatkul() {
        firebaseFirestore.collection("Dosen").document(nip_dosen)
                .collection("Mata Kuliah").addSnapshotListener(new EventListener< QuerySnapshot >() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                if (e != null){
                    Log.d("TAG", "Gagal meload", e);
                    return;
                }
                for (DocumentSnapshot documentSnapshot : queryDocumentSnapshots){
                    matkulArray.add(documentSnapshot.get("mata_kuliah").toString());
                }
                matkul = new ArrayAdapter(Home_Dosen.this, android.R.layout.simple_spinner_dropdown_item, matkulArray);
                spinner_matkul.setAdapter(matkul);
            }
        });
    }

    private void SpinnerKelas() {
        firebaseFirestore.collection("Dosen").document(nip_dosen)
                .collection("Kelas").addSnapshotListener(new EventListener< QuerySnapshot >() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                if (e != null){
                    Log.d("TAG", "Gagal meload", e);
                    return;
                }
                for (DocumentSnapshot documentSnapshot : queryDocumentSnapshots){
                    kelasArray.add(documentSnapshot.get("kelas").toString());
                }
                kelasAdapter = new ArrayAdapter(Home_Dosen.this, android.R.layout.simple_spinner_dropdown_item, kelasArray);
                spinner_kelas.setAdapter(kelasAdapter);
            }
        });
    }

    private void load_mahasiswa() {

        mAdapter = new HomeDosenAdapter(nip_dosen);
        list_mahasiswa.setLayoutManager(new LinearLayoutManager(this));
        list_mahasiswa.setAdapter(mAdapter);

        firebaseFirestore.collection("Mahasiswa").document("3IA18").collection("Mahasiswa 3IA18").orderBy("nama_mahasiswa", Query.Direction.ASCENDING)
                .addSnapshotListener(this, new EventListener< QuerySnapshot >() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                        List<DaftarMahasiswaModel> daftarMahasiswaModelList = queryDocumentSnapshots.toObjects(DaftarMahasiswaModel.class);

                        mAdapter.setData(daftarMahasiswaModelList);
                        list_mahasiswa.smoothScrollToPosition(mAdapter.getItemCount());
                    }
                });
    }

    private void load_mahasiswa_proses() {
        mAdapter = new HomeDosenAdapter(nip_dosen);
        list_mahasiswa.setLayoutManager(new LinearLayoutManager(this));
        list_mahasiswa.setAdapter(mAdapter);

        firebaseFirestore.collection("Mahasiswa").document(spinner_kelas.getSelectedItem().toString()).collection("Mahasiswa "+spinner_kelas.getSelectedItem().toString()).orderBy("nama_mahasiswa", Query.Direction.ASCENDING)
                .addSnapshotListener(this, new EventListener< QuerySnapshot >() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                        List<DaftarMahasiswaModel> daftarMahasiswaModelList = queryDocumentSnapshots.toObjects(DaftarMahasiswaModel.class);

                        mAdapter.setData(daftarMahasiswaModelList);
                        list_mahasiswa.smoothScrollToPosition(mAdapter.getItemCount());
                    }
                });
    }

    @Override
    public void onBackPressed() {
        new AlertDialog.Builder(this)
                .setTitle("Ingin keluar ?")
                .setMessage("Apakah benar anda ingin kembali ke halaman utama ?")
                .setNegativeButton("tidak", null)
                .setPositiveButton("ya", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        startActivity(new Intent(getApplicationContext(), MainActivity.class));
                        finish();
                    }
                }).create().show();
    }

    public void back_home(View view) {
        new AlertDialog.Builder(this)
                .setTitle("Ingin keluar ?")
                .setMessage("Apakah benar anda ingin kembali ke halaman utama ?")
                .setNegativeButton("tidak", null)
                .setPositiveButton("ya", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        startActivity(new Intent(getApplicationContext(), MainActivity.class));
                        finish();
                    }
                }).create().show();
    }

    public void to_date(View view) {
        Rekap_Absen.kelas_scanner = spinner_kelas.getSelectedItem().toString();
        Rekap_Absen.matkul_scanner = spinner_matkul.getSelectedItem().toString();
        startActivity(new Intent(getApplicationContext(), Rekap_Absen.class));
    }

    public void to_grafik(View view) {
        Grafik_Mahasiswa_Dosen.kelas_scan = spinner_kelas.getSelectedItem().toString();
        Grafik_Mahasiswa_Dosen.matkul_scan = spinner_matkul.getSelectedItem().toString();
        Grafik_Mahasiswa_Dosen.minggu_ke = spinner_minggu.getSelectedItem().toString();
        startActivity(new Intent(getApplicationContext(), Grafik_Mahasiswa_Dosen.class));
    }

    public void to_scan(View view) {
        DocumentReference documentReference = firebaseFirestore.collection("Dosen").document(nip_dosen).collection("Kelas dan Matkul")
                .document(spinner_kelas.getSelectedItem().toString()+", "+spinner_matkul.getSelectedItem().toString())
                .collection("line_chart").document("Minggu ke "+spinner_minggu.getSelectedItem().toString());
        documentReference.addSnapshotListener(this, new EventListener< DocumentSnapshot >() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                String sukses;
                SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
                final String date = dateFormat.format(new Date());

                sukses = documentSnapshot.getString("tanggal");
                if (date.equals(sukses)){
                    ScannerBarcode.kelas_scan = spinner_kelas.getSelectedItem().toString();
                    ScannerBarcode.matkul_scan = spinner_matkul.getSelectedItem().toString();
                    ScannerBarcode.minggu_ke = spinner_minggu.getSelectedItem().toString();
                    ScannerBarcode.nama_dosen = nama_dosen.getText().toString();
                    ScannerBarcode.foto_dosen = foto_dosen;
                    startActivity(new Intent(getApplicationContext(), ScannerBarcode.class));
                }else {
                    firebaseFirestore.collection("Dosen").document(nip_dosen).collection("Kelas dan Matkul")
                            .document(spinner_kelas.getSelectedItem().toString()+", "+spinner_matkul.getSelectedItem().toString())
                            .collection("line_chart").get().addOnCompleteListener(new OnCompleteListener< QuerySnapshot >() {
                        @Override
                        public void onComplete(@NonNull Task< QuerySnapshot > task) {
                            Integer jumlah = task.getResult().size();
                            Integer hasil = jumlah + 1;

                            if (spinner_minggu.getSelectedItem().toString().equals(Integer.toString(hasil))){
                                ScannerBarcode.kelas_scan = spinner_kelas.getSelectedItem().toString();
                                ScannerBarcode.matkul_scan = spinner_matkul.getSelectedItem().toString();
                                ScannerBarcode.minggu_ke = spinner_minggu.getSelectedItem().toString();
                                ScannerBarcode.nama_dosen = nama_dosen.getText().toString();
                                ScannerBarcode.foto_dosen = foto_dosen;
                                startActivity(new Intent(getApplicationContext(), ScannerBarcode.class));
//                                Toast.makeText(Home_Dosen.this, "Silahkan lanjut", Toast.LENGTH_SHORT).show();
                            }else {
                                Toast.makeText(Home_Dosen.this, "Sekarang merupakan minggu ke- "+hasil, Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            }
        });
    }
}
