package com.example.demo;

public class ApiModel {

    private Long id;

    private String apiName;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getApiName() {
        return apiName;
    }

    public void setApiName(String apiName) {
        this.apiName = apiName;
    }

    @Override
    public String toString() {
        return "ApiModel{" +
                "id=" + id +
                ", apiName='" + apiName + '\'' +
                '}';
    }
}
