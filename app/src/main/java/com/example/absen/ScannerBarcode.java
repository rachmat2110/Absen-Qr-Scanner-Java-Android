package com.example.absen;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import cn.pedant.SweetAlert.SweetAlertDialog;
import me.dm7.barcodescanner.zxing.ZXingScannerView;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.budiyev.android.codescanner.CodeScanner;
import com.budiyev.android.codescanner.CodeScannerView;
import com.budiyev.android.codescanner.DecodeCallback;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.zxing.Result;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.PermissionRequestErrorListener;
import com.karumi.dexter.listener.single.PermissionListener;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class ScannerBarcode extends AppCompatActivity {

    RelativeLayout relative;
    private ImageView ivBgContent;
    private CodeScanner mCodeScanner;
    private CodeScannerView scannerView;

    private PermissionListener cameraPermissionListener;
    private PermissionRequestErrorListener errorListener;

    public static String kelas_scan = "";
    public static String matkul_scan = "";
    public static String minggu_ke = "";
    public static String nip_dosen = "";
    public static String nama_dosen = "";
    public static String foto_dosen = "";

    FirebaseFirestore firebaseFirestore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scanner_barcode);

        scannerView = findViewById(R.id.scanner_view);
        relative = findViewById(R.id.relative);

        firebaseFirestore = FirebaseFirestore.getInstance();

        mCodeScanner = new CodeScanner(this, scannerView);
        mCodeScanner.setDecodeCallback(new DecodeCallback() {
            @Override
            public void onDecoded(@NonNull final Result result) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        DocumentReference documentReference = firebaseFirestore.collection("Mahasiswa").document(kelas_scan)
                                .collection("Mahasiswa "+kelas_scan).document(result.getText());
                        documentReference.get().addOnCompleteListener(new OnCompleteListener< DocumentSnapshot >() {
                            @Override
                            public void onComplete(@NonNull Task< DocumentSnapshot > task) {
                                if (task.isSuccessful()){
                                    DocumentSnapshot documentSnapshot = task.getResult();
                                    if (documentSnapshot.exists()){
                                        Snackbar snackbar = Snackbar
                                                .make(relative, "Terimakasih sudah absen pada minggu ke- "+minggu_ke, Snackbar.LENGTH_LONG);
                                        snackbar.show();

                                        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
                                        final String date = dateFormat.format(new Date());

                                        Map<String, Object> input_tanggal_masuk = new HashMap<>();
                                        input_tanggal_masuk.put("npm_mahasiswa", result.getText());
                                        input_tanggal_masuk.put("tanggal", date);
                                        input_tanggal_masuk.put("nama_mahasiswa", documentSnapshot.getString("nama_mahasiswa"));
                                        firebaseFirestore.collection("Dosen").document(nip_dosen)
                                                .collection("Kelas dan Matkul").document(kelas_scan+", "+matkul_scan)
                                                .collection(date).document(result.getText()).set(input_tanggal_masuk);

                                        Map<String, Object> histori_mahasiswa = new HashMap<>();
                                        histori_mahasiswa.put("tanggal", date);
                                        histori_mahasiswa.put("dosen", nip_dosen);
                                        histori_mahasiswa.put("matkul", matkul_scan);
                                        firebaseFirestore.collection("Mahasiswa").document(kelas_scan).collection("Mahasiswa "+kelas_scan)
                                                .document(result.getText()).collection(nip_dosen).document(date)
                                                .set(histori_mahasiswa);

                                        Map<String, Object> list_dosen = new HashMap<>();
                                        list_dosen.put("nama_dosen", nama_dosen);
                                        list_dosen.put("nip_dosen", nip_dosen);
                                        list_dosen.put("matkul", matkul_scan);
                                        list_dosen.put("foto_dosen", foto_dosen);
                                        firebaseFirestore.collection("Mahasiswa").document(kelas_scan).collection("Mahasiswa "+kelas_scan)
                                                .document(result.getText()).collection("list_dosen").document(nip_dosen)
                                                .set(list_dosen);

                                        firebaseFirestore.collection("Dosen").document(nip_dosen)
                                                .collection("Kelas dan Matkul").document(kelas_scan+", "+matkul_scan)
                                                .collection(date).get().addOnCompleteListener(new OnCompleteListener< QuerySnapshot >() {
                                            @Override
                                            public void onComplete(@NonNull Task< QuerySnapshot > task) {
                                                if (task.isSuccessful()){
                                                    Integer jumlah;
                                                    jumlah = task.getResult().size();

                                                    Map<String, Object> line_chart = new HashMap<>();
                                                    line_chart.put("jumlah", jumlah);
                                                    line_chart.put("Minggu ke "+minggu_ke, "Minggu ke "+minggu_ke);
                                                    line_chart.put("tanggal", date);
                                                    firebaseFirestore.collection("Dosen").document(nip_dosen)
                                                            .collection("Kelas dan Matkul").document(kelas_scan+", "+matkul_scan)
                                                            .collection("line_chart").document("Minggu ke "+minggu_ke)
                                                            .set(line_chart);
                                                }
                                            }
                                        });

                                        onResume();
//                                        startActivity(new Intent(getApplicationContext(), Home_Dosen.class));
//                                        finish();

                                    }else {
                                        new SweetAlertDialog(ScannerBarcode.this, SweetAlertDialog.WARNING_TYPE)
                                                .setTitleText("OOPS!")
                                                .setContentText("Maaf data tidak ditemukan")
                                                .setConfirmText("OKE")
                                                .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                                    @Override
                                                    public void onClick(SweetAlertDialog sweetAlertDialog) {
                                                        sweetAlertDialog.dismissWithAnimation();
                                                    }
                                                }).show();
                                        onResume();
                                    }
                                }
                            }
                        });
                    }
                });
            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
        checkCameraPermission();
        mCodeScanner.startPreview();
    }

    @Override
    protected void onPause() {
        mCodeScanner.releaseResources();
        super.onPause();
    }

    private void checkCameraPermission(){
        Dexter.withContext(this)
                .withPermission(Manifest.permission.CAMERA)
                .withListener(new PermissionListener() {
                    @Override
                    public void onPermissionGranted(PermissionGrantedResponse response) {
                        mCodeScanner.startPreview();
                    }

                    @Override
                    public void onPermissionDenied(PermissionDeniedResponse response) {
                        Toast.makeText(ScannerBarcode.this, "request permisson is required", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(PermissionRequest permission, PermissionToken token) {
                        token.continuePermissionRequest();
                    }
                })
                .check();
    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(getApplicationContext(), Home_Dosen.class));
        super.onBackPressed();
    }

}
