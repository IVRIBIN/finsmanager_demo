package com.tutu.finsmanager.model.Comapny;

import com.tutu.finsmanager.dao.abstraction.BusinessEx;
import com.tutu.finsmanager.dao.abstraction.CompanyEx;

import java.util.List;

/**
 Класс ответа AJAX
 */
public class ResponseCompany {
    private List<CompanyEx> companyExList;
    private CompanyEx companyEx;
    private CompanyExImpl companyExImpl;


    public List<CompanyEx> getCompanyExList() { return companyExList; }
    public void setCompanyExList(List<CompanyEx> companyExList) { this.companyExList = companyExList; }

    public CompanyEx getCompanyEx() {return companyEx; }
    public void setCompanyEx(CompanyEx companyEx) { this.companyEx = companyEx; }

    public CompanyExImpl getCompanyExImpl() { return companyExImpl; }
    public void setCompanyExImpl(CompanyExImpl companyExImpl) { this.companyExImpl = companyExImpl; }
}
