
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
}
