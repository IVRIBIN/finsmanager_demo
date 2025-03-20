package com.tutu.finsmanager.model.Comapny;

public class CompanyForm {

    private String method;
    private Long id;
    private String name;
    private String description;
    private Integer main_user;
    private String inn;
    private String kpp;
    private String account;


    public String getName() {
        return name;
    }
    public String getDescription() { return description; }
    public String getMethod() { return method; }
    public Long getId() { return id; }
    public Integer getMain_user() {return main_user; }
    public String getInn() { return inn; }
    public String getKpp() { return kpp; }
    public String getAccount() { return account; }

    public void setMethod(String method) { this.method = method; }
    public void setId(Long id) { this.id = id; }
    public void setMain_user(Integer main_user) { this.main_user = main_user; }
    public void setName(String name) {
        this.name = name;
    }
    public void setDescription(String description) {
        this.description = description;
    }
    public void setInn(String inn) { this.inn = inn; }
    public void setKpp(String kpp) { this.kpp = kpp; }
    public void setAccount(String account) { this.account = account; }
}
