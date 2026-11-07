package com.example.berkahjaya;

import android.os.Bundle;
import android.view.View;
import android.widget.*;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import java.util.ArrayList;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CategoryActivity extends AppCompatActivity {
    private ApiService api;
    private LinearLayout listContainer;
    private final List<Category> items = new ArrayList<>();

    @Override protected void onCreate(Bundle state) {
        super.onCreate(state);
        api = RetrofitClient.getApiService();

        LinearLayout root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setPadding(24, 24, 24, 24);
        root.setBackgroundColor(0xFFF5F7FA);

        TextView title = new TextView(this);
        title.setText("Manajemen Kategori");
        title.setTextSize(24);
        title.setTextColor(0xFF172554);
        title.setPadding(0, 0, 0, 20);
        root.addView(title);

        Button add = new Button(this);
        add.setText("+ Tambah Kategori");
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
        api.getCategories().enqueue(new Callback<ApiResponse<Category>>() {
            @Override public void onResponse(Call<ApiResponse<Category>> c, Response<ApiResponse<Category>> r) {
                if (r.isSuccessful() && r.body() != null && r.body().isSuccess()) {
                    items.clear();
                    if (r.body().getData() != null) items.addAll(r.body().getData());
                    render();
                } else toast("Gagal memuat kategori. Periksa API PHP.");
            }
            @Override public void onFailure(Call<ApiResponse<Category>> c, Throwable t) {
                toast("Koneksi gagal: " + t.getMessage());
            }
        });
    }

    private void render() {
        listContainer.removeAllViews();
        if (items.isEmpty()) {
            TextView empty = new TextView(this);
            empty.setText("Belum ada kategori.");
            empty.setPadding(8, 24, 8, 24);
            listContainer.addView(empty);
        }
        for (Category item : items) {
            LinearLayout row = new LinearLayout(this);
            row.setOrientation(LinearLayout.VERTICAL);
            row.setPadding(16, 12, 16, 12);
            row.setBackgroundColor(0xFFFFFFFF);
            LinearLayout.LayoutParams rp = new LinearLayout.LayoutParams(-1, -2);
            rp.setMargins(0, 10, 0, 0);
            listContainer.addView(row, rp);

            TextView name = new TextView(this);
            name.setText(item.getNamaKategori());
            name.setTextSize(18);
            name.setTextColor(0xFF172554);
            row.addView(name);

            LinearLayout actions = new LinearLayout(this);
            row.addView(actions);
            Button edit = new Button(this); edit.setText("Edit");
            Button delete = new Button(this); delete.setText("Hapus");
            actions.addView(edit); actions.addView(delete);
            edit.setOnClickListener(v -> showForm(item));
            delete.setOnClickListener(v -> confirmDelete(item));
        }
    }

    private void showForm(Category item) {
        boolean editMode = item != null;
        EditText input = new EditText(this);
        input.setSingleLine(true);
        input.setHint("Nama kategori");
        if (editMode) input.setText(item.getNamaKategori());

        new AlertDialog.Builder(this)
            .setTitle(editMode ? "Edit Kategori" : "Tambah Kategori")
            .setView(input)
            .setNegativeButton("Batal", null)
            .setPositiveButton("Simpan", (d, w) -> {
                String name = input.getText().toString().trim();
                if (name.isEmpty()) { toast("Nama kategori wajib diisi"); return; }
                Call<ApiResponse<Category>> call = editMode
                    ? api.updateCategory(item.getId(), name)
                    : api.addCategory(name);
                call.enqueue(new Callback<ApiResponse<Category>>() {
                    @Override public void onResponse(Call<ApiResponse<Category>> c, Response<ApiResponse<Category>> r) {
                        if (r.isSuccessful() && r.body() != null && r.body().isSuccess()) {
                            toast(r.body().getMessage() == null ? "Berhasil disimpan" : r.body().getMessage());
                            loadData();
                        } else toast("Gagal menyimpan kategori. Periksa data/API.");
                    }
                    @Override public void onFailure(Call<ApiResponse<Category>> c, Throwable t) { toast("Koneksi gagal: " + t.getMessage()); }
                });
            }).show();
    }

    private void confirmDelete(Category item) {
        new AlertDialog.Builder(this).setTitle("Hapus kategori?")
            .setMessage("Hapus " + item.getNamaKategori() + "?")
            .setNegativeButton("Batal", null)
            .setPositiveButton("Hapus", (d, w) ->
                api.deleteCategory(item.getId()).enqueue(new Callback<ApiResponse<Category>>() {
                    @Override public void onResponse(Call<ApiResponse<Category>> c, Response<ApiResponse<Category>> r) {
                        if (r.isSuccessful() && r.body() != null && r.body().isSuccess()) { toast("Kategori dihapus"); loadData(); }
                        else toast("Gagal menghapus kategori. Pastikan kategori tidak sedang dipakai produk.");
                    }
                    @Override public void onFailure(Call<ApiResponse<Category>> c, Throwable t) { toast("Koneksi gagal: " + t.getMessage()); }
                })).show();
    }
    private void toast(String s) { Toast.makeText(this, s, Toast.LENGTH_LONG).show(); }
}
