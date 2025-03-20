package com.tutu.finsmanager.model.Business;

import com.tutu.finsmanager.dao.abstraction.BusinessEx;

import java.util.List;

/**
 Класс ответа AJAX
 */
public class ResponseBusiness {
    private List<BusinessEx> businessList;
    private BusinessEx businessEx;
    private BusinessExImpl businessExImp;

    public void setBusinessList(List<BusinessEx> businessList) {
        this.businessList = businessList;
    }
    public List<BusinessEx> getBusinessList() {
        return businessList;
    }

    public void setBusinessEx(BusinessEx businessEx) {this.businessEx = businessEx;}
    public BusinessEx getBusinessEx() {return businessEx;}

    public void setBusinessExImp(BusinessExImpl businessExImp) { this.businessExImp = businessExImp; }
    public BusinessExImpl getBusinessExImp() { return businessExImp; }
}
