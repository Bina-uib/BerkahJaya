package com.example.berkahjaya;

import com.google.gson.annotations.SerializedName;

public class Category {
    private int id;
    @SerializedName("nama_kategori")
    private String namaKategori;

    public int getId() { return id; }
    public String getNamaKategori() { return namaKategori; }
}
