package com.example.Query_Service.dto;


public class GenericResponse <T> {
    private String message;
    private T data;
    private int statuscode;

    public GenericResponse(String message, T data,int statuscode) {
        this.message = message;
        this.data = data;
        this.statuscode = statuscode;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }
    public int getStatuscode() {
        return statuscode;
    }
    public void setStatuscode(int statuscode) {
        this.statuscode = statuscode;
    }
}
