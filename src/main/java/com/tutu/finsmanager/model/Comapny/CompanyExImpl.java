package com.tutu.finsmanager.model.Comapny;

import com.tutu.finsmanager.dao.abstraction.CompanyEx;

public class CompanyExImpl implements CompanyEx {
    private Long id;
    private String name;
    private String description;
    private String created;
    private Long main_user;
    private String inn;
    private String kpp;
    private String account;
    private Boolean ValidFlg;
    private String nameDvm;
    private String descriptionDvm;
    private String innDvm;
    private String kppDvm;
    private String accountDvm;

    public void setId(Long id) { this.id = id; }
    public void setName(String name) { this.name = name; }
    public void setDescription(String description) { this.description = description; }
    public void setCreated(String created) { this.created = created; }
    public void setMain_user(Long main_user) { this.main_user = main_user; }
    public void setInn(String inn) { this.inn = inn; }
    public void setKpp(String kpp) { this.kpp = kpp; }
    public void setAccount(String account) { this.account = account; }
    public void setValidFlg(Boolean validFlg) { ValidFlg = validFlg; }
    public void setNameDvm(String nameDvm) { this.nameDvm = nameDvm; }
    public void setDescriptionDvm(String descriptionDvm) { this.descriptionDvm = descriptionDvm; }
    public void setInnDvm(String innDvm) { this.innDvm = innDvm; }
    public void setKppDvm(String kppDvm) { this.kppDvm = kppDvm; }
    public void setAccountDvm(String accountDvm) { this.accountDvm = accountDvm; }

    public Long getId() { return id; }
    public String getName() { return name; }
    public String getDescription() { return description; }
    public String getCreated() { return created; }
    public Long getMain_user() { return main_user; }
    public String getInn() { return inn; }
    public String getKpp() { return kpp; }
    public String getAccount() { return account; }
    public Boolean getValidFlg() { return ValidFlg; }
    public String getNameDvm() { return nameDvm; }
    public String getDescriptionDvm() { return descriptionDvm; }
    public String getInnDvm() { return innDvm; }
    public String getKppDvm() { return kppDvm; }
    public String getAccountDvm() { return accountDvm; }
}
