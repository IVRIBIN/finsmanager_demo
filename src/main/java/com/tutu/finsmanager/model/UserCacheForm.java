package com.tutu.finsmanager.model;

public class UserCacheForm {
    private Integer Id;
    private String Login = "";
    private Long ActiveBusiness;
    private Long ActiveCompany;
    private String Method= ""; //insert/update/delete

    public void setId(Integer id) {
        Id = id;
    }

    public void setLogin(String login) {
        Login = login;
    }

    public void setActiveBusiness(Long activeBusiness) {
        ActiveBusiness = activeBusiness;
    }

    public void setMethod(String method) {
        Method = method;
    }

    public void setActiveCompany(Long activeCompany) { ActiveCompany = activeCompany; }

    public Integer getId() {
        return Id;
    }

    public String getLogin() {
        return Login;
    }

    public Long getActiveBusiness() {
        return ActiveBusiness;
    }

    public String getMethod() {
        return Method;
    }

    public Long getActiveCompany() { return ActiveCompany; }
}
