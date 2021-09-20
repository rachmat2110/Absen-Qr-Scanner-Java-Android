package com.example.absen;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import me.dm7.barcodescanner.core.IViewFinder;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.squareup.picasso.Picasso;

public class Home_Mahasiswa extends AppCompatActivity {

    public static String npm_login = "";
    FirebaseFirestore firebaseFirestore;

    TextView nama_mahasiswa, npm_mahasiswa, kelas_mahasiswa;
    ImageView foto_mahasiswa;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home__mahasiswa);

        nama_mahasiswa = findViewById(R.id.nama_mahasiswa);
        npm_mahasiswa = findViewById(R.id.npm_mahasiswa);
        kelas_mahasiswa = findViewById(R.id.kelas_mahasiswa);
        foto_mahasiswa = findViewById(R.id.foto_mahasiswa);

        firebaseFirestore = FirebaseFirestore.getInstance();

        DocumentReference documentReference = firebaseFirestore.collection("Daftar Mahasiswa")
                .document(npm_login);
        documentReference.addSnapshotListener(this, new EventListener< DocumentSnapshot >() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                nama_mahasiswa.setText("Selamat Datang, \n"+documentSnapshot.getString("nama_mahasiswa"));
                Picasso.get().load(documentSnapshot.getString("foto_mahasiswa")).into(foto_mahasiswa);
                npm_mahasiswa.setText(documentSnapshot.getString("npm_mahasiswa"));
                kelas_mahasiswa.setText(documentSnapshot.getString("kelas_mahasiswa"));
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

    public void absen_mahasiswa(View view) {
        Rekap_Absen_Mahasiswa.kelas_mahasiswa = kelas_mahasiswa.getText().toString();
        startActivity(new Intent(getApplicationContext(), Rekap_Absen_Mahasiswa.class));
        this.finish();
    }

    public void to_barcode(View view) {
        startActivity(new Intent(getApplicationContext(), Barcode_Mahasiswa.class));
        this.finish();
    }
}
