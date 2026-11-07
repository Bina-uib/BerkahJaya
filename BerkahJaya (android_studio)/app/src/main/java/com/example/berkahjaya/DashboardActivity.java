package com.example.berkahjaya;

import android.Manifest;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DashboardActivity extends AppCompatActivity {

    // Stok <= angka ini dianggap "menipis"
    private static final int LOW_STOCK_THRESHOLD = 10;

    private TextView tvTotalProducts, tvTotalCategories, tvLowStockCount, tvEmpty;
    private ProgressBar progressBar;
    private RecyclerView rvLowStock;
    private LowStockAdapter adapter;
    private ApiService api;

    private final ActivityResultLauncher<String> notifPermissionLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestPermission(), granted -> { });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        tvTotalProducts = findViewById(R.id.tvTotalProducts);
        tvTotalCategories = findViewById(R.id.tvTotalCategories);
        tvLowStockCount = findViewById(R.id.tvLowStockCount);
        tvEmpty = findViewById(R.id.tvEmpty);
        progressBar = findViewById(R.id.progressBar);
        rvLowStock = findViewById(R.id.rvLowStock);

        adapter = new LowStockAdapter();
        rvLowStock.setLayoutManager(new LinearLayoutManager(this));
        rvLowStock.setAdapter(adapter);

        api = RetrofitClient.getApiService();

        if (Build.VERSION.SDK_INT >= 33) {
            notifPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadDashboard(); // refresh setiap kembali ke layar ini
    }

    private void loadDashboard() {
        progressBar.setVisibility(View.VISIBLE);
        tvEmpty.setVisibility(View.GONE);

        api.getProducts().enqueue(new Callback<ProductResponse>() {
            @Override
            public void onResponse(Call<ProductResponse> call, Response<ProductResponse> response) {
                progressBar.setVisibility(View.GONE);
                if (!response.isSuccessful() || response.body() == null) {
                    showError("Gagal memuat produk (" + response.code() + ")");
                    return;
                }
                List<Product> products = response.body().getData();
                if (products == null) products = new ArrayList<>();
                showProducts(products);
            }

            @Override
            public void onFailure(Call<ProductResponse> call, Throwable t) {
                progressBar.setVisibility(View.GONE);
                showError("Tidak bisa terhubung: " + t.getMessage());
            }
        });

        loadCategoryCount();
    }

    private void showProducts(List<Product> products) {
        List<Product> lowStock = new ArrayList<>();
        for (Product p : products) {
            if (p.getStok() <= LOW_STOCK_THRESHOLD) {
                lowStock.add(p);
            }
        }

        tvTotalProducts.setText(String.valueOf(products.size()));
        tvLowStockCount.setText(String.valueOf(lowStock.size()));
        adapter.setItems(lowStock);

        tvEmpty.setText("Semua stok aman 👍");
        tvEmpty.setVisibility(lowStock.isEmpty() ? View.VISIBLE : View.GONE);

        // Picu notifikasi kalau ada stok menipis
        StockNotificationHelper.showLowStockNotification(this, lowStock.size());
    }

    private void loadCategoryCount() {
        api.getCategories().enqueue(new Callback<ApiResponse<Category>>() {
            @Override
            public void onResponse(Call<ApiResponse<Category>> call,
                                   Response<ApiResponse<Category>> response) {
                if (response.isSuccessful() && response.body() != null
                        && response.body().getData() != null) {
                    tvTotalCategories.setText(
                            String.valueOf(response.body().getData().size()));
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<Category>> call, Throwable t) {
                // biarkan tampil "-", error utama sudah ditampilkan dari produk
            }
        });
    }

    private void showError(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
        tvEmpty.setText("Gagal memuat data");
        tvEmpty.setVisibility(View.VISIBLE);
    }
}