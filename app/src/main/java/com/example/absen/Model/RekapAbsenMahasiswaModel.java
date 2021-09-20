package com.example.absen.Model;

public class RekapAbsenMahasiswaModel {

    private String nama_dosen;
    private String nip_dosen;
    private String matkul;
    private String foto_dosen;

    public RekapAbsenMahasiswaModel() {
    }

    public RekapAbsenMahasiswaModel(String nama_dosen, String nip_dosen, String matkul, String foto_dosen) {
        this.nama_dosen = nama_dosen;
        this.nip_dosen = nip_dosen;
        this.matkul = matkul;
        this.foto_dosen = foto_dosen;
    }

    public String getNama_dosen() {
        return nama_dosen;
    }

    public void setNama_dosen(String nama_dosen) {
        this.nama_dosen = nama_dosen;
    }

    public String getNip_dosen() {
        return nip_dosen;
    }

    public void setNip_dosen(String nip_dosen) {
        this.nip_dosen = nip_dosen;
    }

    public String getMatkul() {
        return matkul;
    }

    public void setMatkul(String matkul) {
        this.matkul = matkul;
    }

    public String getFoto_dosen() {
        return foto_dosen;
    }

    public void setFoto_dosen(String foto_dosen) {
        this.foto_dosen = foto_dosen;
    }
}
