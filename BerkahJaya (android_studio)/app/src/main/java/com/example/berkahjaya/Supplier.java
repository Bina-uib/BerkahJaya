package com.example.berkahjaya;

import com.google.gson.annotations.SerializedName;

public class Supplier {
    private int id;
    @SerializedName("nama_supplier")
    private String namaSupplier;
    private String telepon;
    private String alamat;

    public int getId() { return id; }
    public String getNamaSupplier() { return namaSupplier; }
    public String getTelepon() { return telepon; }
    public String getAlamat() { return alamat; }
}
