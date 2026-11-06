
package com.example.berkahjaya;

import android.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;
import java.util.ArrayList;

public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ViewHolder> {

    public interface ProductListener {
        void onEdit(Product product);
        void onDelete(Product product);
    }

    private List<Product> products;
    private ProductListener listener;

    public ProductAdapter(List<Product> products, ProductListener listener) {
        this.products = new ArrayList<>(products);
        this.listener = listener;
    }

    public void updateData(List<Product> newProducts) {
        products.clear();
        products.addAll(newProducts);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_product, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Product product = products.get(position);

        holder.tvNama.setText(product.getNama_produk());

        NumberFormat formatter =
                NumberFormat.getCurrencyInstance(new Locale("id", "ID"));
        formatter.setMaximumFractionDigits(0);
        formatter.setMinimumFractionDigits(0);

        holder.tvHarga.setText(formatter.format(product.getHarga()));
        holder.tvStok.setText("Stok: " + product.getStok());

        holder.btnEdit.setOnClickListener(v ->
                listener.onEdit(product));

        holder.btnDelete.setOnClickListener(v ->
                new AlertDialog.Builder(v.getContext())
                        .setTitle("Hapus Produk")
                        .setMessage("Yakin ingin menghapus " +
                                product.getNama_produk() + "?")
                        .setNegativeButton("Batal", null)
                        .setPositiveButton("Hapus", (dialog, which) ->
                                listener.onDelete(product))
                        .show());
    }

    @Override
    public int getItemCount() {
        return products.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        TextView tvNama, tvHarga, tvStok;
        Button btnEdit, btnDelete;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            tvNama = itemView.findViewById(R.id.tvNamaProduk);
            tvHarga = itemView.findViewById(R.id.tvHarga);
            tvStok = itemView.findViewById(R.id.tvStok);
            btnEdit = itemView.findViewById(R.id.btnEdit);
            btnDelete = itemView.findViewById(R.id.btnDelete);
        }
    }
}
