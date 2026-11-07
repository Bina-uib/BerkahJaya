
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

public class CategoryActivity extends AppCompatActivity {

    private ApiService api;
    private LinearLayout listContainer;
    private final List<Category> items = new ArrayList<>();

    @Override
    protected void onCreate(Bundle state) {
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

        root.addView(scroll, new LinearLayout.LayoutParams(
                -1, 0, 1
        ));

        Button back = new Button(this);
        back.setText("Kembali");
        root.addView(back);
        back.setOnClickListener(v -> finish());

        setContentView(root);
        loadData();
    }

    private void loadData() {
        api.getCategories().enqueue(new Callback<ApiResponse<Category>>() {

            @Override
            public void onResponse(
                    Call<ApiResponse<Category>> call,
                    Response<ApiResponse<Category>> response
            ) {
                if (response.isSuccessful()
                        && response.body() != null
                        && response.body().isSuccess()) {

                    items.clear();

                    if (response.body().getData() != null) {
                        items.addAll(response.body().getData());
                    }

                    render();

                } else {
                    showResponseError("Gagal memuat kategori", response);
                }
            }

            @Override
            public void onFailure(
                    Call<ApiResponse<Category>> call,
                    Throwable t
            ) {
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

            LinearLayout.LayoutParams rp =
                    new LinearLayout.LayoutParams(-1, -2);

            rp.setMargins(0, 10, 0, 0);
            listContainer.addView(row, rp);

            TextView name = new TextView(this);
            name.setText(item.getNamaKategori());
            name.setTextSize(18);
            name.setTextColor(0xFF172554);
            row.addView(name);

            LinearLayout actions = new LinearLayout(this);
            row.addView(actions);

            Button edit = new Button(this);
            edit.setText("Edit");

            Button delete = new Button(this);
            delete.setText("Hapus");

            actions.addView(edit);
            actions.addView(delete);

            edit.setOnClickListener(v -> showForm(item));
            delete.setOnClickListener(v -> confirmDelete(item));
        }
    }

    private void showForm(Category item) {
        boolean editMode = item != null;

        EditText input = new EditText(this);
        input.setSingleLine(true);
        input.setHint("Nama kategori");

        if (editMode) {
            input.setText(item.getNamaKategori());
        }

        new AlertDialog.Builder(this)
                .setTitle(editMode ? "Edit Kategori" : "Tambah Kategori")
                .setView(input)
                .setNegativeButton("Batal", null)
                .setPositiveButton("Simpan", (dialog, which) -> {

                    String name = input.getText().toString().trim();

                    if (name.isEmpty()) {
                        toast("Nama kategori wajib diisi");
                        return;
                    }

                    Call<ApiResponse<Category>> call = editMode
                            ? api.updateCategory(item.getId(), name)
                            : api.addCategory(name);

                    call.enqueue(new Callback<ApiResponse<Category>>() {

                        @Override
                        public void onResponse(
                                Call<ApiResponse<Category>> call,
                                Response<ApiResponse<Category>> response
                        ) {
                            if (response.isSuccessful()
                                    && response.body() != null
                                    && response.body().isSuccess()) {

                                String message = response.body().getMessage();

                                toast(message == null
                                        ? "Kategori berhasil disimpan"
                                        : message);

                                loadData();

                            } else {
                                showResponseError(
                                        "Gagal menyimpan kategori",
                                        response
                                );
                            }
                        }

                        @Override
                        public void onFailure(
                                Call<ApiResponse<Category>> call,
                                Throwable t
                        ) {
                            toast("Koneksi gagal: " + t.getMessage());
                        }
                    });
                })
                .show();
    }

    private void confirmDelete(Category item) {
        new AlertDialog.Builder(this)
                .setTitle("Hapus kategori?")
                .setMessage("Hapus " + item.getNamaKategori() + "?")
                .setNegativeButton("Batal", null)
                .setPositiveButton("Hapus", (dialog, which) -> {

                    api.deleteCategory(item.getId())
                            .enqueue(new Callback<ApiResponse<Category>>() {

                                @Override
                                public void onResponse(
                                        Call<ApiResponse<Category>> call,
                                        Response<ApiResponse<Category>> response
                                ) {
                                    if (response.isSuccessful()
                                            && response.body() != null
                                            && response.body().isSuccess()) {

                                        toast("Kategori dihapus");
                                        loadData();

                                    } else {
                                        showResponseError(
                                                "Gagal menghapus kategori",
                                                response
                                        );
                                    }
                                }

                                @Override
                                public void onFailure(
                                        Call<ApiResponse<Category>> call,
                                        Throwable t
                                ) {
                                    toast("Koneksi gagal: " + t.getMessage());
                                }
                            });
                })
                .show();
    }

    private void showResponseError(
            String title,
            Response<?> response
    ) {
        String error = "";

        try {
            error = response.errorBody() != null
                    ? response.errorBody().string()
                    : "Response kosong atau success=false";

            if (error.length() > 250) {
                error = error.substring(0, 250);
            }

        } catch (Exception e) {
            error = e.getMessage();
        }

        toast(title + " | HTTP " + response.code() + ": " + error);
    }

    private void toast(String message) {
        Toast.makeText(
                this,
                message,
                Toast.LENGTH_LONG
        ).show();
    }
}
