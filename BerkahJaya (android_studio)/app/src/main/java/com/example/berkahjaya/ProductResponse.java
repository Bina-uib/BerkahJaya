package com.example.berkahjaya;

import java.util.List;

public class ProductResponse {
    private boolean success;
    private String message;
    private List<Product> data;

    public boolean isSuccess() {
        return success;
    }

    public String getMessage() {
        return message;
    }

    public List<Product> getData() {
        return data;
    }
}