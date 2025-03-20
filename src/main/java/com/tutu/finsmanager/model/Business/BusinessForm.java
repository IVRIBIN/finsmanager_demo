package com.tutu.finsmanager.model.Business;

public class BusinessForm {

    private String method;
    private Long id;
    private String name;
    private String description;
    private Integer main_user;


    public String getName() {
        return name;
    }
    public String getDescription() { return description; }
    public String getMethod() { return method; }
    public Long getId() { return id; }
    public Integer getMain_user() {return main_user; }

    public void setMethod(String method) { this.method = method; }
    public void setId(Long id) { this.id = id; }
    public void setMain_user(Integer main_user) { this.main_user = main_user; }
    public void setName(String name) {
        this.name = name;
    }
    public void setDescription(String description) {
        this.description = description;
    }


}
