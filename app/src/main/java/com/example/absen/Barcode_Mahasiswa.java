package com.example.absen;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.journeyapps.barcodescanner.BarcodeEncoder;

public class Barcode_Mahasiswa extends AppCompatActivity {

    ImageView barcode;
    public static String barcode_mahasiswa = "";

    FirebaseFirestore firebaseFirestore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_barcode__mahasiswa);

        barcode = findViewById(R.id.barcode);

        firebaseFirestore = FirebaseFirestore.getInstance();

        DocumentReference documentReference = firebaseFirestore.collection("Daftar Mahasiswa").document(barcode_mahasiswa);
        documentReference.addSnapshotListener(this, new EventListener< DocumentSnapshot >() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {

                MultiFormatWriter multiFormatWriter = new MultiFormatWriter();
                try {
                    BitMatrix bitMatrix = multiFormatWriter.encode(documentSnapshot.getString("npm_mahasiswa"), BarcodeFormat.QR_CODE, 500,500);
                    BarcodeEncoder barcodeEncoder = new BarcodeEncoder();
                    Bitmap bitmap = barcodeEncoder.createBitmap(bitMatrix);
                    barcode.setImageBitmap(bitmap);
                } catch (WriterException ex) {
                    ex.printStackTrace();
                }

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
        this.finish();
        super.onBackPressed();
    }
}
