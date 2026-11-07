package com.example.berkahjaya;

import android.os.Bundle;
import android.widget.*;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import java.util.ArrayList;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SupplierActivity extends AppCompatActivity {
    private ApiService api;
    private LinearLayout listContainer;
    private final List<Supplier> items = new ArrayList<>();

    @Override protected void onCreate(Bundle state) {
        super.onCreate(state);
        api = RetrofitClient.getApiService();

        LinearLayout root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setPadding(24, 24, 24, 24);
        root.setBackgroundColor(0xFFF5F7FA);

        TextView title = new TextView(this);
        title.setText("Manajemen Supplier");
        title.setTextSize(24);
        title.setTextColor(0xFF172554);
        title.setPadding(0, 0, 0, 20);
        root.addView(title);

        Button add = new Button(this);
        add.setText("+ Tambah Supplier");
        root.addView(add);
        add.setOnClickListener(v -> showForm(null));

        ScrollView scroll = new ScrollView(this);
        listContainer = new LinearLayout(this);
        listContainer.setOrientation(LinearLayout.VERTICAL);
        scroll.addView(listContainer);
        root.addView(scroll, new LinearLayout.LayoutParams(-1, 0, 1));

        Button back = new Button(this);
        back.setText("Kembali");
        root.addView(back);
        back.setOnClickListener(v -> finish());

        setContentView(root);
        loadData();
    }

    private void loadData() {
        api.getSuppliers().enqueue(new Callback<ApiResponse<Supplier>>() {
            @Override public void onResponse(Call<ApiResponse<Supplier>> c, Response<ApiResponse<Supplier>> r) {
                if (r.isSuccessful() && r.body() != null && r.body().isSuccess()) {
                    items.clear();
                    if (r.body().getData() != null) items.addAll(r.body().getData());
                    render();
                } else toast("Gagal memuat supplier. Periksa API PHP.");
            }
            @Override public void onFailure(Call<ApiResponse<Supplier>> c, Throwable t) { toast("Koneksi gagal: " + t.getMessage()); }
        });
    }

    private void render() {
        listContainer.removeAllViews();
        if (items.isEmpty()) {
            TextView empty = new TextView(this);
            empty.setText("Belum ada supplier.");
            empty.setPadding(8, 24, 8, 24);
            listContainer.addView(empty);
        }
        for (Supplier item : items) {
            LinearLayout row = new LinearLayout(this);
            row.setOrientation(LinearLayout.VERTICAL);
            row.setPadding(16, 12, 16, 12);
            row.setBackgroundColor(0xFFFFFFFF);
            LinearLayout.LayoutParams rp = new LinearLayout.LayoutParams(-1, -2);
            rp.setMargins(0, 10, 0, 0);
            listContainer.addView(row, rp);

            TextView name = new TextView(this);
            name.setText(item.getNamaSupplier());
            name.setTextSize(18);
            name.setTextColor(0xFF172554);
            row.addView(name);
            TextView detail = new TextView(this);
            detail.setText("Telepon: " + safe(item.getTelepon()) + "\nAlamat: " + safe(item.getAlamat()));
            row.addView(detail);

            LinearLayout actions = new LinearLayout(this);
            row.addView(actions);
            Button edit = new Button(this); edit.setText("Edit");
            Button delete = new Button(this); delete.setText("Hapus");
            actions.addView(edit); actions.addView(delete);
            edit.setOnClickListener(v -> showForm(item));
            delete.setOnClickListener(v -> confirmDelete(item));
        }
    }

    private String safe(String s) { return s == null || s.isEmpty() ? "-" : s; }

    private void showForm(Supplier item) {
        boolean editMode = item != null;
        LinearLayout form = new LinearLayout(this);
        form.setOrientation(LinearLayout.VERTICAL);
        form.setPadding(30, 8, 30, 0);

        EditText name = new EditText(this); name.setHint("Nama supplier");
        EditText phone = new EditText(this); phone.setHint("Nomor telepon");
        phone.setInputType(3);
        EditText address = new EditText(this); address.setHint("Alamat"); address.setMinLines(2);
        form.addView(name); form.addView(phone); form.addView(address);

        if (editMode) {
            name.setText(item.getNamaSupplier());
            phone.setText(item.getTelepon());
            address.setText(item.getAlamat());
        }

        new AlertDialog.Builder(this)
            .setTitle(editMode ? "Edit Supplier" : "Tambah Supplier")
            .setView(form)
            .setNegativeButton("Batal", null)
            .setPositiveButton("Simpan", (d, w) -> {
                String n = name.getText().toString().trim();
                String p = phone.getText().toString().trim();
                String a = address.getText().toString().trim();
                if (n.isEmpty()) { toast("Nama supplier wajib diisi"); return; }

                Call<ApiResponse<Supplier>> call = editMode
                    ? api.updateSupplier(item.getId(), n, p, a)
                    : api.addSupplier(n, p, a);
                call.enqueue(new Callback<ApiResponse<Supplier>>() {
                    @Override public void onResponse(Call<ApiResponse<Supplier>> c, Response<ApiResponse<Supplier>> r) {
                        if (r.isSuccessful() && r.body() != null && r.body().isSuccess()) {
                            toast(r.body().getMessage() == null ? "Berhasil disimpan" : r.body().getMessage());
                            loadData();
                        } else toast("Gagal menyimpan supplier. Periksa data/API.");
                    }
                    @Override public void onFailure(Call<ApiResponse<Supplier>> c, Throwable t) { toast("Koneksi gagal: " + t.getMessage()); }
                });
            }).show();
    }

    private void confirmDelete(Supplier item) {
        new AlertDialog.Builder(this).setTitle("Hapus supplier?")
            .setMessage("Hapus " + item.getNamaSupplier() + "?")
            .setNegativeButton("Batal", null)
            .setPositiveButton("Hapus", (d, w) ->
                api.deleteSupplier(item.getId()).enqueue(new Callback<ApiResponse<Supplier>>() {
                    @Override public void onResponse(Call<ApiResponse<Supplier>> c, Response<ApiResponse<Supplier>> r) {
                        if (r.isSuccessful() && r.body() != null && r.body().isSuccess()) { toast("Supplier dihapus"); loadData(); }
                        else toast("Gagal menghapus supplier. Pastikan supplier tidak sedang dipakai produk.");
                    }
                    @Override public void onFailure(Call<ApiResponse<Supplier>> c, Throwable t) { toast("Koneksi gagal: " + t.getMessage()); }
                })).show();
    }
    private void toast(String s) { Toast.makeText(this, s, Toast.LENGTH_LONG).show(); }
}
