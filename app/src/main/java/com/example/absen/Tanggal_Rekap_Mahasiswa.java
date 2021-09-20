package com.example.absen;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class Tanggal_Rekap_Mahasiswa extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tanggal__rekap__mahasiswa);
    }

    public void back_rekap(View view) {
        startActivity(new Intent(getApplicationContext(), Rekap_Absen_Mahasiswa.class));
        this.finish();
    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(getApplicationContext(), Rekap_Absen_Mahasiswa.class));
        super.onBackPressed();
    }
}
