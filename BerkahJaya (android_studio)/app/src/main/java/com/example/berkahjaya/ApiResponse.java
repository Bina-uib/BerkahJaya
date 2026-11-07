package com.example.berkahjaya;

import java.util.List;

public class ApiResponse<T> {
    private boolean success;
    private String message;
    private List<T> data;

    public boolean isSuccess() { return success; }
    public String getMessage() { return message; }
    public List<T> getData() { return data; }
}
