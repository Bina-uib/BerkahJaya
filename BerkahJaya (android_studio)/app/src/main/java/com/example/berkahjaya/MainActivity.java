package com.example.berkahjaya;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

    private RecyclerView rvProducts;
    private TextView tvTotalProduk;
    private EditText etSearch;
    private EditText etMinStok, etMaxStok;
    private Button btnAddProduct;
    private Button btnResetFilter;
    private Button btnCategories, btnSuppliers;

    private ProductAdapter adapter;
    private ApiService apiService;

    private List<Product> productList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        rvProducts = findViewById(R.id.rvProducts);
        tvTotalProduk = findViewById(R.id.tvTotalProduk);
        etSearch = findViewById(R.id.etSearch);
        etMinStok = findViewById(R.id.etMinStok);
        etMaxStok = findViewById(R.id.etMaxStok);
        btnResetFilter = findViewById(R.id.btnResetFilter);
        btnAddProduct = findViewById(R.id.btnAddProduct);
        btnCategories = findViewById(R.id.btnCategories);
        btnSuppliers = findViewById(R.id.btnSuppliers);

        apiService = RetrofitClient.getApiService();

        adapter = new ProductAdapter(
                productList,
                new ProductAdapter.ProductListener() {
                    @Override
                    public void onEdit(Product product) {
                        showProductDialog(product);
                    }

                    @Override
                    public void onDelete(Product product) {
                        deleteProduct(product.getId());
                    }
                }
        );

        rvProducts.setLayoutManager(new LinearLayoutManager(this));
        rvProducts.setAdapter(adapter);

        btnAddProduct.setOnClickListener(v -> showProductDialog(null));
        btnCategories.setOnClickListener(v ->
                startActivity(new Intent(MainActivity.this, CategoryActivity.class)));

        btnSuppliers.setOnClickListener(v ->
                startActivity(new Intent(MainActivity.this, SupplierActivity.class)));

        // Pencarian + filter stok: setiap ketikan langsung menyaring daftar
        TextWatcher filterWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                applyFilters();
            }

            @Override
            public void afterTextChanged(Editable s) {}
        };

        etSearch.addTextChangedListener(filterWatcher);
        etMinStok.addTextChangedListener(filterWatcher);
        etMaxStok.addTextChangedListener(filterWatcher);

        btnResetFilter.setOnClickListener(v -> {
            etSearch.setText("");
            etMinStok.setText("");
            etMaxStok.setText("");
        });

        loadProducts();
    }

    // READ: Mengambil produk dari API
    private void loadProducts() {

        apiService.getProducts().enqueue(new Callback<ProductResponse>() {

            @Override
            public void onResponse(
                    Call<ProductResponse> call,
                    Response<ProductResponse> response) {

                if (response.isSuccessful() && response.body() != null) {

                    ProductResponse result = response.body();
                    Log.d("CEK_DATA", new com.google.gson.Gson().toJson(result));

                    if (result.isSuccess() && result.getData() != null) {

                        productList.clear();
                        productList.addAll(result.getData());

                        applyFilters();

                        tvTotalProduk.setText(productList.size() + " Produk");

                        Toast.makeText(
                                MainActivity.this,
                                "Berhasil memuat " + productList.size() + " produk",
                                Toast.LENGTH_SHORT
                        ).show();

                    } else {
                        Toast.makeText(
                                MainActivity.this,
                                "Data produk kosong atau respons tidak valid",
                                Toast.LENGTH_LONG
                        ).show();
                    }

                } else {
                    Toast.makeText(
                            MainActivity.this,
                            "HTTP Error: " + response.code(),
                            Toast.LENGTH_LONG
                    ).show();
                }
            }

            @Override
            public void onFailure(Call<ProductResponse> call, Throwable t) {
                Toast.makeText(
                        MainActivity.this,
                        "Koneksi gagal: " + t.getMessage(),
                        Toast.LENGTH_LONG
                ).show();
            }
        });
    }

    // SEARCH + FILTER: nama produk dan rentang stok
    private void applyFilters() {

        String keyword = etSearch.getText().toString().trim().toLowerCase();
        Integer minStok = parseIntOrNull(etMinStok);
        Integer maxStok = parseIntOrNull(etMaxStok);

        List<Product> filtered = new ArrayList<>();

        for (Product product : productList) {

            boolean cocokNama = keyword.isEmpty()
                    || (product.getNama_produk() != null
                    && product.getNama_produk().toLowerCase().contains(keyword));

            boolean cocokMin = minStok == null || product.getStok() >= minStok;
            boolean cocokMax = maxStok == null || product.getStok() <= maxStok;

            if (cocokNama && cocokMin && cocokMax) {
                filtered.add(product);
            }
        }

        adapter.updateData(filtered);
    }

    private Integer parseIntOrNull(EditText et) {
        String t = et.getText().toString().trim();
        if (t.isEmpty()) return null;
        try {
            return Integer.parseInt(t);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    // CREATE dan UPDATE: Form tambah/edit produk
    private void showProductDialog(Product product) {

        boolean isEdit = product != null;

        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(50, 20, 50, 10);

        EditText etNama = new EditText(this);
        etNama.setHint("Nama produk");

        EditText etHarga = new EditText(this);
        etHarga.setHint("Harga (contoh: 18000)");
        etHarga.setInputType(
                android.text.InputType.TYPE_CLASS_NUMBER
                        | android.text.InputType.TYPE_NUMBER_FLAG_DECIMAL
        );

        EditText etStok = new EditText(this);
        etStok.setHint("Jumlah stok");
        etStok.setInputType(android.text.InputType.TYPE_CLASS_NUMBER);

        layout.addView(etNama);
        layout.addView(etHarga);
        layout.addView(etStok);

        if (isEdit) {
            etNama.setText(product.getNama_produk());
            etHarga.setText(String.valueOf(product.getHarga()));
            etStok.setText(String.valueOf(product.getStok()));
        }

        AlertDialog dialog = new AlertDialog.Builder(this)
                .setTitle(isEdit ? "Edit Produk" : "Tambah Produk")
                .setView(layout)
                .setNegativeButton("Batal", null)
                .setPositiveButton(isEdit ? "Simpan" : "Tambah", null)
                .create();

        dialog.setOnShowListener(d -> {
            dialog.getButton(AlertDialog.BUTTON_POSITIVE)
                    .setOnClickListener(v -> {

                        String nama = etNama.getText().toString().trim();
                        String hargaText = etHarga.getText().toString().trim();
                        String stokText = etStok.getText().toString().trim();

                        if (nama.isEmpty() || hargaText.isEmpty() || stokText.isEmpty()) {
                            Toast.makeText(this, "Semua kolom wajib diisi",
                                    Toast.LENGTH_SHORT).show();
                            return;
                        }

                        try {
                            double harga = Double.parseDouble(hargaText);
                            int stok = Integer.parseInt(stokText);

                            if (harga < 0 || stok < 0) {
                                Toast.makeText(this, "Harga dan stok tidak boleh negatif",
                                        Toast.LENGTH_SHORT).show();
                                return;
                            }

                            if (isEdit) {
                                updateProduct(product.getId(), nama, harga, stok);
                            } else {
                                addProduct(nama, harga, stok);
                            }

                            dialog.dismiss();

                        } catch (NumberFormatException e) {
                            Toast.makeText(this, "Harga atau stok tidak valid",
                                    Toast.LENGTH_SHORT).show();
                        }
                    });
        });

        dialog.show();
    }

    // CREATE
    private void addProduct(String nama, double harga, int stok) {

        apiService.addProduct(nama, harga, stok)
                .enqueue(new Callback<ProductResponse>() {

                    @Override
                    public void onResponse(
                            Call<ProductResponse> call,
                            Response<ProductResponse> response) {

                        if (response.isSuccessful()
                                && response.body() != null
                                && response.body().isSuccess()) {

                            Toast.makeText(MainActivity.this,
                                    "Produk berhasil ditambahkan",
                                    Toast.LENGTH_SHORT).show();
                            loadProducts();

                        } else {
                            Toast.makeText(MainActivity.this,
                                    "Gagal menambahkan produk",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<ProductResponse> call, Throwable t) {
                        showConnectionError(t);
                    }
                });
    }

    // UPDATE
    private void updateProduct(int id, String nama, double harga, int stok) {

        apiService.updateProduct(id, nama, harga, stok)
                .enqueue(new Callback<ProductResponse>() {

                    @Override
                    public void onResponse(
                            Call<ProductResponse> call,
                            Response<ProductResponse> response) {

                        if (response.isSuccessful()
                                && response.body() != null
                                && response.body().isSuccess()) {

                            Toast.makeText(MainActivity.this,
                                    "Produk berhasil diperbarui",
                                    Toast.LENGTH_SHORT).show();
                            loadProducts();

                        } else {
                            Toast.makeText(MainActivity.this,
                                    "Gagal memperbarui produk",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<ProductResponse> call, Throwable t) {
                        showConnectionError(t);
                    }
                });
    }

    // DELETE
    private void deleteProduct(int id) {

        apiService.deleteProduct(id)
                .enqueue(new Callback<ProductResponse>() {

                    @Override
                    public void onResponse(
                            Call<ProductResponse> call,
                            Response<ProductResponse> response) {

                        if (response.isSuccessful()
                                && response.body() != null
                                && response.body().isSuccess()) {

                            Toast.makeText(MainActivity.this,
                                    "Produk berhasil dihapus",
                                    Toast.LENGTH_SHORT).show();
                            loadProducts();

                        } else {
                            Toast.makeText(MainActivity.this,
                                    "Gagal menghapus produk",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<ProductResponse> call, Throwable t) {
                        showConnectionError(t);
                    }
                });
    }

    private void showConnectionError(Throwable t) {
        Toast.makeText(this, "Koneksi gagal: " + t.getMessage(),
                Toast.LENGTH_LONG).show();
    }
}