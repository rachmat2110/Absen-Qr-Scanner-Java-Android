package com.example.absen;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import cn.pedant.SweetAlert.SweetAlertDialog;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.HashMap;
import java.util.Map;

public class Masukkan_Dosen extends AppCompatActivity {

    ImageView foto_upload;
    TextView nama_dosen, nip_dosen;
    LinearLayout input_dosen, insert_foto_dosen;
    Spinner spinner_kelas, spinner_matkul;
    String foto_mahasiswa;

    private ProgressDialog progressDialog;
    FirebaseFirestore firebaseFirestore;
    StorageReference Folder;

    private static final int IMAGE_PICK_CODE = 1;
    private static final int PERMISSION_CODE = 1001;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_masukkan__dosen);

        foto_upload = findViewById(R.id.foto_upload);
        nama_dosen = findViewById(R.id.nama_dosen);
        nip_dosen = findViewById(R.id.nip_dosen);
        input_dosen = findViewById(R.id.input_dosen);
        insert_foto_dosen = findViewById(R.id.insert_foto_dosen);
        spinner_kelas = findViewById(R.id.spinner_kelas);
        spinner_matkul = findViewById(R.id.spinner_matkul);

        insert_foto_dosen.setOnClickListener(new View.OnClickListener() {
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

        input_dosen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                inputDosen();
            }
        });

        firebaseFirestore = FirebaseFirestore.getInstance();
        Folder = FirebaseStorage.getInstance().getReference().child("ImageFolderDosen");
    }

    private void inputDosen() {
        if (nama_dosen.getText().toString().equals("")){
            Toast.makeText(this, "Harap isi nama mahasiswa", Toast.LENGTH_SHORT).show();
        }else if (nip_dosen.getText().toString().equals("")){
            Toast.makeText(this, "Harap isi npm mahasiswa", Toast.LENGTH_SHORT).show();
        }else if (spinner_matkul.getSelectedItem().toString().equals("")){
            Toast.makeText(this, "Harap isi mata kuliah mahasiswa", Toast.LENGTH_SHORT).show();
        }else if (spinner_kelas.getSelectedItem().toString().equals("")){
            Toast.makeText(this, "Harap isi kelas mahasiswa", Toast.LENGTH_SHORT).show();
        }else {
            Map<String, Object> dosen = new HashMap<>();
            dosen.put("nama_dosen", nama_dosen.getText().toString());
            dosen.put("nip_dosen", nip_dosen.getText().toString());
            dosen.put("foto_dosen", foto_mahasiswa);
            firebaseFirestore.collection("Dosen").document(nip_dosen.getText().toString())
                    .set(dosen);

            Map<String, Object> matkul = new HashMap<>();
            matkul.put("mata_kuliah", spinner_matkul.getSelectedItem().toString());
            firebaseFirestore.collection("Dosen").document(nip_dosen.getText().toString())
                    .collection("Mata Kuliah").document(spinner_matkul.getSelectedItem().toString())
                    .set(matkul);

            Map<String, Object> kelas = new HashMap<>();
            kelas.put("kelas", spinner_kelas.getSelectedItem().toString());
            firebaseFirestore.collection("Dosen").document(nip_dosen.getText().toString())
                    .collection("Kelas").document(spinner_kelas.getSelectedItem().toString())
                    .set(kelas);

            Map<String, Object> kelas_matkul = new HashMap<>();
            kelas_matkul.put("kelas", spinner_kelas.getSelectedItem().toString());
            kelas_matkul.put("mata_kuliah", spinner_matkul.getSelectedItem().toString());
            firebaseFirestore.collection("Dosen").document(nip_dosen.getText().toString())
                    .collection("Kelas dan Matkul").document(spinner_kelas.getSelectedItem().toString()+", "+spinner_matkul.getSelectedItem().toString())
                    .set(kelas_matkul);

            Toast.makeText(getApplicationContext(), "Data berhasil ditambahkan", Toast.LENGTH_SHORT).show();
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
                        progressDialog = new ProgressDialog(Masukkan_Dosen.this);
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
