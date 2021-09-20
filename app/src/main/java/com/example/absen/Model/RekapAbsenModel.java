package com.example.absen.Model;

public class RekapAbsenModel {
    private String npm_mahasiswa;
    private String tanggal;
    private String nama_mahasiswa;

    public RekapAbsenModel() {
    }

    public RekapAbsenModel(String npm_mahasiswa, String tanggal, String nama_mahasiswa) {
        this.npm_mahasiswa = npm_mahasiswa;
        this.tanggal = tanggal;
        this.nama_mahasiswa = nama_mahasiswa;
    }

    public String getNpm_mahasiswa() {
        return npm_mahasiswa;
    }

    public void setNpm_mahasiswa(String npm_mahasiswa) {
        this.npm_mahasiswa = npm_mahasiswa;
    }

    public String getTanggal() {
        return tanggal;
    }

    public void setTanggal(String tanggal) {
        this.tanggal = tanggal;
    }

    public String getNama_mahasiswa() {
        return nama_mahasiswa;
    }

    public void setNama_mahasiswa(String nama_mahasiswa) {
        this.nama_mahasiswa = nama_mahasiswa;
    }
}
