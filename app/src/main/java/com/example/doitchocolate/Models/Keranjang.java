package com.example.doitchocolate.Models;

public class Keranjang {
    private String id_barang;
    private String nama_barang;
    private int harga_satuan;
    private int jumlah_barang;
    private int stock_barang;

    public String getId_barang() {
        return id_barang;
    }

    public void setId_barang(String id_barang) {
        this.id_barang = id_barang;
    }

    public int getStock_barang() {
        return stock_barang;
    }

    public void setStock_barang(int stock_barang) {
        this.stock_barang = stock_barang;
    }

    public Keranjang() {
    }

    public String getNama_barang() {
        return nama_barang;
    }

    public void setNama_barang(String nama_barang) {
        this.nama_barang = nama_barang;
    }

    public int getHarga_satuan() {
        return harga_satuan;
    }

    public void setHarga_satuan(int harga_satuan) {
        this.harga_satuan = harga_satuan;
    }

    public int getJumlah_barang() {
        return jumlah_barang;
    }

    public void setJumlah_barang(int jumlah_barang) {
        this.jumlah_barang = jumlah_barang;
    }
}
