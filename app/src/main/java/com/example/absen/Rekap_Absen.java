package com.example.absen;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.Toast;

import com.example.absen.Adapter.HomeDosenAdapter;
import com.example.absen.Adapter.RekapAbsenAdapter;
import com.example.absen.Model.RekapAbsenModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class Rekap_Absen extends AppCompatActivity implements DatePickerDialog.OnDateSetListener {

    TextView get_date, tanggal, matkul, kelas;
    Button masuk_mahasiswa;
    RecyclerView list_rekap_absen;
    FirebaseFirestore firebaseFirestore;

    RekapAbsenAdapter mAdapter;

    public static String nip_dosen = "";
    public static String kelas_scanner = "";
    public static String matkul_scanner = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rekap__absen);

        firebaseFirestore = FirebaseFirestore.getInstance();

        get_date = findViewById(R.id.get_date);
        masuk_mahasiswa = findViewById(R.id.masuk_mahasiswa);
        list_rekap_absen = findViewById(R.id.list_rekap_absen);
        tanggal = findViewById(R.id.get_date);
        matkul = findViewById(R.id.matkul);
        kelas = findViewById(R.id.kelas);

        matkul.setText(matkul_scanner);
        kelas.setText(kelas_scanner);

        get_date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DialogFragment datePicker = new DatePickerFragment();
                datePicker.show(getSupportFragmentManager(), "date picker");
            }
        });

        masuk_mahasiswa.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (tanggal.getText().toString().equals("")){
                    Toast.makeText(Rekap_Absen.this, "Isi terlebih dahulu tanggal", Toast.LENGTH_SHORT).show();
                }else {
                    prosesMahasiswa();
                }

            }
        });
    }

    private void prosesMahasiswa() {

        firebaseFirestore.collection("Dosen").document(nip_dosen).collection("Kelas dan Matkul")
                .document(kelas_scanner+", "+matkul_scanner).collection(tanggal.getText().toString()).get()
                .addOnCompleteListener(new OnCompleteListener< QuerySnapshot >() {
                    @Override
                    public void onComplete(@NonNull Task< QuerySnapshot > task) {
                        if (task.isSuccessful()){
                            int rating;
                            rating = task.getResult().size();
                            if (rating == 0){
                                Toast.makeText(Rekap_Absen.this, "Tidak ada mahasiswa hadir pada tanggal tersebut.", Toast.LENGTH_SHORT).show();
                            }else {
                                getRekapAbsen();
                            }
                        }
                    }
                });
    }

    private void getRekapAbsen() {

        mAdapter = new RekapAbsenAdapter(nip_dosen);
        list_rekap_absen.setLayoutManager(new LinearLayoutManager(this));
        list_rekap_absen.setAdapter(mAdapter);

        firebaseFirestore.collection("Dosen").document(nip_dosen).collection("Kelas dan Matkul")
                .document(kelas_scanner+", "+matkul_scanner).collection(tanggal.getText().toString())
                .addSnapshotListener(this, new EventListener< QuerySnapshot >() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                        List< RekapAbsenModel > rekapAbsenModels = queryDocumentSnapshots.toObjects(RekapAbsenModel.class);

                        mAdapter.setData(rekapAbsenModels);
                        list_rekap_absen.smoothScrollToPosition(mAdapter.getItemCount());
                    }
                });
    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(getApplicationContext(), Home_Dosen.class));
        super.onBackPressed();
    }

    public void back_home(View view) {
        startActivity(new Intent(getApplicationContext(), Home_Dosen.class));
        this.finish();
    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {

        Calendar c = Calendar.getInstance();
        c.set(Calendar.YEAR, year);
        c.set(Calendar.MONTH, month);
        c.set(Calendar.DAY_OF_MONTH, dayOfMonth);
        year = c.get(Calendar.YEAR);
        month = c.get(Calendar.MONTH);
        dayOfMonth = c.get(Calendar.DAY_OF_MONTH);

        int bulan = month+1;

        if (bulan < 10){
            if (dayOfMonth < 10){
                tanggal.setText("0"+dayOfMonth + "-" +"0"+bulan + "-" + year);
            }else{
                tanggal.setText(dayOfMonth + "-" +"0"+bulan + "-" + year);
            }
        }else{
            if (dayOfMonth < 10){
                tanggal.setText("0"+dayOfMonth + "-" +bulan + "-" + year);
            }else{
                tanggal.setText(dayOfMonth + "-" + bulan + "-" + year);
            }
        }

    }

}
