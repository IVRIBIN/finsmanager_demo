package com.tutu.finsmanager.model.Business;

import com.tutu.finsmanager.dao.abstraction.BusinessEx;

public class BusinessExImpl implements BusinessEx {
    private Long Id;
    private String Name;
    private String Description;
    private String ActiveFlg;

    private Boolean ValidFlg;
    private String NameDvm;
    private String DescriptionDvm;

    //Финансовая информация по бизнесу
    private Float ExpanseTotal;
    private Float IncomeTotal;
    private Float TransferTotal;
    private Float BalanceTotal;

    @Override
    public Long getId() {return Id;}
    @Override
    public String getName() {return Name;}
    @Override
    public String getDescription() {return Description;}
    @Override
    public String getActiveFlg() {return ActiveFlg;}
    public Boolean getValidFlg() { return ValidFlg; }
    public String getNameDvm() { return NameDvm; }
    public String getDescriptionDvm() { return DescriptionDvm; }

    public void setId(Long id) {Id = id;}
    public void setName(String name) {Name = name;}
    public void setDescription(String description) {Description = description;}
    public void setActiveFlg(String activeFlg) {ActiveFlg = activeFlg;}
    public void setValidFlg(Boolean validFlg) {ValidFlg = validFlg; }
    public void setNameDvm(String nameDvm) { NameDvm = nameDvm; }
    public void setDescriptionDvm(String descriptionDvm) { DescriptionDvm = descriptionDvm; }

    public Float getExpanseTotal() {
        return ExpanseTotal;
    }

    public void setExpanseTotal(Float expanseTotal) {
        ExpanseTotal = expanseTotal;
    }

    public Float getIncomeTotal() {
        return IncomeTotal;
    }

    public void setIncomeTotal(Float incomeTotal) {
        IncomeTotal = incomeTotal;
    }

    public Float getTransferTotal() {
        return TransferTotal;
    }

    public void setTransferTotal(Float transferTotal) {
        TransferTotal = transferTotal;
    }

    public Float getBalanceTotal() {
        return BalanceTotal;
    }

    public void setBalanceTotal(Float balanceTotal) {
        BalanceTotal = balanceTotal;
    }
}
