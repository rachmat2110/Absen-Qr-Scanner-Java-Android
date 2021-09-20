package com.example.absen.Model;

public class DaftarMahasiswaModel {

    private String nama_mahasiswa;
    private String npm_mahasiswa;

    public DaftarMahasiswaModel() {
    }

    public DaftarMahasiswaModel(String nama_mahasiswa, String npm_mahasiswa) {
        this.nama_mahasiswa = nama_mahasiswa;
        this.npm_mahasiswa = npm_mahasiswa;
    }

    public String getNama_mahasiswa() {
        return nama_mahasiswa;
    }

    public void setNama_mahasiswa(String nama_mahasiswa) {
        this.nama_mahasiswa = nama_mahasiswa;
    }

    public String getNpm_mahasiswa() {
        return npm_mahasiswa;
    }

    public void setNpm_mahasiswa(String npm_mahasiswa) {
        this.npm_mahasiswa = npm_mahasiswa;
    }
}
