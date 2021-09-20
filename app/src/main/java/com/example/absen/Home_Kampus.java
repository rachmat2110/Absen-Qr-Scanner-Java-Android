package com.example.absen;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Spinner;
import java.util.ArrayList;
import java.util.List;
import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

public class Home_Kampus extends AppCompatActivity {

    Spinner pilih_kelas;
    Button masukkan_mahasiswa;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home__kampus);

        pilih_kelas = findViewById(R.id.spinner_kelas);
//        masukkan_mahasiswa = findViewById(R.id.masukkan_mahasiswa);

//        masukkan_mahasiswa.setOnClickListener(new OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Toast.makeText(Home_Kampus.this, pilih_kelas.getSelectedItem().toString(), Toast.LENGTH_SHORT).show();
//            }
//        });
    }

    public void masukkan_mahasiswa(View view) {
        startActivity(new Intent(getApplicationContext(), Masukkan_Mahasiswa.class));
        this.finish();
    }

    public void masukkan_dosen(View view) {
        startActivity(new Intent(getApplicationContext(), Masukkan_Dosen.class));
        this.finish();
    }

    public void grafik_mahasiswa_kampus(View view) {
        startActivity(new Intent(getApplicationContext(), Grafik_Mahasiswa_Kampus.class));
        this.finish();
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
                    }
                }).create().show();
    }

    public void back_fragment(View view) {
        new AlertDialog.Builder(this)
                .setTitle("Ingin keluar ?")
                .setMessage("Apakah benar anda ingin kembali ke halaman utama ?")
                .setNegativeButton("tidak", null)
                .setPositiveButton("ya", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        startActivity(new Intent(getApplicationContext(), MainActivity.class));
                    }
                }).create().show();
    }
}
