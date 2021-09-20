package com.example.absen;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import cn.pedant.SweetAlert.SweetAlertDialog;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.media.Image;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class Masukkan_Mahasiswa extends AppCompatActivity {

    ImageView foto_upload;
    TextView insert_mahasiswa, nama_mahasiswa, npm_mahasiswa;
    LinearLayout input_mahasiswa;
    Spinner spinner_kelas;
    String foto_mahasiswa;

    private ProgressDialog progressDialog;
    FirebaseFirestore firebaseFirestore;
    StorageReference Folder;

    private static final int IMAGE_PICK_CODE = 1;
    private static final int PERMISSION_CODE = 1001;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_masukkan__mahasiswa);

        foto_upload = findViewById(R.id.foto_upload);
        insert_mahasiswa = findViewById(R.id.insert_mahasiswa);
        input_mahasiswa = findViewById(R.id.input_mahasiswa);
        nama_mahasiswa = findViewById(R.id.nama_mahasiswa);
        npm_mahasiswa = findViewById(R.id.npm_mahasiswa);
        spinner_kelas = findViewById(R.id.spinner_kelas);

        firebaseFirestore = FirebaseFirestore.getInstance();
        Folder = FirebaseStorage.getInstance().getReference().child("ImageFolder");

        input_mahasiswa.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                InputMahasiswa();
            }
        });

        insert_mahasiswa.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
                    if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                    == PackageManager.PERMISSION_DENIED){
                        String [] permissions = {Manifest.permission.READ_EXTERNAL_STORAGE};

                        requestPermissions(permissions, PERMISSION_CODE);
                    }
                    else
                    {
                        pickImage();
                    }
                }
                else{
                    pickImage();
                }
            }
        });
    }

    private void InputMahasiswa() {

        if (nama_mahasiswa.getText().toString().equals("")){
            Toast.makeText(this, "Harap isi nama mahasiswa", Toast.LENGTH_SHORT).show();
        }else if (npm_mahasiswa.getText().toString().equals("")){
            Toast.makeText(this, "Harap isi npm mahasiswa", Toast.LENGTH_SHORT).show();
        }else if (spinner_kelas.getSelectedItem().toString().equals("")){
            Toast.makeText(this, "Harap isi kelas mahasiswa", Toast.LENGTH_SHORT).show();
        }else {
            DocumentReference documentReference = firebaseFirestore.collection("Daftar Mahasiswa")
                    .document(npm_mahasiswa.getText().toString());
            documentReference.get().addOnCompleteListener(new OnCompleteListener< DocumentSnapshot >() {
                @Override
                public void onComplete(@NonNull Task< DocumentSnapshot > task) {
                    if (task.isSuccessful()){
                        DocumentSnapshot documentSnapshot = task.getResult();
                        if (documentSnapshot.exists()){
                            new SweetAlertDialog(Masukkan_Mahasiswa.this, SweetAlertDialog.WARNING_TYPE)
                                    .setTitleText("OOPS!")
                                    .setContentText("Data sudah dimasukkan sebelumnya")
                                    .setConfirmText("OKE")
                                    .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                        @Override
                                        public void onClick(SweetAlertDialog sweetAlertDialog) {
                                            sweetAlertDialog.dismissWithAnimation();
                                        }
                                    }).show();
                        }else {
                            Map<String, Object> mahasiswa = new HashMap<>();
                            mahasiswa.put("nama_mahasiswa", nama_mahasiswa.getText().toString());
                            mahasiswa.put("kelas_mahasiswa", spinner_kelas.getSelectedItem().toString());
                            mahasiswa.put("npm_mahasiswa", npm_mahasiswa.getText().toString());
                            mahasiswa.put("foto_mahasiswa", foto_mahasiswa);

                            firebaseFirestore.collection("Mahasiswa").document(spinner_kelas.getSelectedItem().toString())
                                    .collection("Mahasiswa "+spinner_kelas.getSelectedItem().toString())
                                    .document(npm_mahasiswa.getText().toString())
                                    .set(mahasiswa);

                            Map<String, Object> kelas = new HashMap<>();
                            kelas.put("jumlah_mahasiswa", "");
                            firebaseFirestore.collection("Mahasiswa").document(spinner_kelas.getSelectedItem().toString())
                                    .set(kelas);

                            Map<String, Object> daftar_mahasiswa = new HashMap<>();
                            daftar_mahasiswa.put("nama_mahasiswa", nama_mahasiswa.getText().toString());
                            daftar_mahasiswa.put("kelas_mahasiswa", spinner_kelas.getSelectedItem().toString());
                            daftar_mahasiswa.put("npm_mahasiswa", npm_mahasiswa.getText().toString());
                            daftar_mahasiswa.put("foto_mahasiswa", foto_mahasiswa);
                            firebaseFirestore.collection("Daftar Mahasiswa").document(npm_mahasiswa.getText().toString())
                                    .set(daftar_mahasiswa);

                            Toast.makeText(getApplicationContext(), "Data berhasil ditambahkan", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            });
        }
    }

    private void pickImage() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        startActivityForResult(intent, IMAGE_PICK_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == IMAGE_PICK_CODE){
            if (resultCode == RESULT_OK){

                foto_upload.setImageURI(data.getData());
                Uri ImageData = data.getData();
                final StorageReference Imagename = Folder.child("image"+ImageData.getLastPathSegment());
                Imagename.putFile(ImageData).addOnSuccessListener(new OnSuccessListener< UploadTask.TaskSnapshot >() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                        progressDialog = new ProgressDialog(Masukkan_Mahasiswa.this);
                        progressDialog.setMessage("Please Wait...");
                        progressDialog.show();
                        Imagename.getDownloadUrl().addOnSuccessListener(new OnSuccessListener< Uri >() {
                            @Override
                            public void onSuccess(Uri uri) {
                                progressDialog.dismiss();
                                foto_mahasiswa = String.valueOf(uri);
                            }
                        });
                    }
                });

            }
        }
    }


    @Override
    public void onBackPressed() {
        startActivity(new Intent(getApplicationContext(), Home_Kampus.class));
        super.onBackPressed();
    }

    public void back_home(View view) {
        startActivity(new Intent(getApplicationContext(), Home_Kampus.class));
    }
}
