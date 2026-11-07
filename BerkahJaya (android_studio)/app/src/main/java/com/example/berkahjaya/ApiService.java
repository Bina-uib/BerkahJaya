
package com.example.berkahjaya;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.Field;

public interface ApiService {

    @GET("get_products.php")
    Call<ProductResponse> getProducts();

    @FormUrlEncoded
    @POST("add_product.php")
    Call<ProductResponse> addProduct(
            @Field("nama_produk") String nama,
            @Field("harga") double harga,
            @Field("stok") int stok
    );

    @FormUrlEncoded
    @POST("update_product.php")
    Call<ProductResponse> updateProduct(
            @Field("id") int id,
            @Field("nama_produk") String nama,
            @Field("harga") double harga,
            @Field("stok") int stok
    );

    @FormUrlEncoded
    @POST("delete_product.php")
    Call<ProductResponse> deleteProduct(
            @Field("id") int id
    );

    @GET("get_suppliers.php")
    Call<ApiResponse<Supplier>> getSuppliers();

    @FormUrlEncoded
    @POST("add_supplier.php")
    Call<ApiResponse<Supplier>> addSupplier(
            @Field("nama_supplier") String nama,
            @Field("telepon") String telepon,
            @Field("alamat") String alamat
    );

    @FormUrlEncoded
    @POST("update_supplier.php")
    Call<ApiResponse<Supplier>> updateSupplier(
            @Field("id") int id,
            @Field("nama_supplier") String nama,
            @Field("telepon") String telepon,
            @Field("alamat") String alamat
    );

    @FormUrlEncoded
    @POST("delete_supplier.php")
    Call<ApiResponse<Supplier>> deleteSupplier(
            @Field("id") int id
    );

    @GET("get_categories.php")
    Call<ApiResponse<Category>> getCategories();

    @FormUrlEncoded
    @POST("add_category.php")
    Call<ApiResponse<Category>> addCategory(
            @Field("nama_kategori") String nama
    );

    @FormUrlEncoded
    @POST("update_category.php")
    Call<ApiResponse<Category>> updateCategory(
            @Field("id") int id,
            @Field("nama_kategori") String nama
    );

    @FormUrlEncoded
    @POST("delete_category.php")
    Call<ApiResponse<Category>> deleteCategory(
            @Field("id") int id
    );
}
