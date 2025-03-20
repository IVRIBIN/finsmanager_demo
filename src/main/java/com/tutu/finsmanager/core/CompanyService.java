package com.tutu.finsmanager.core;

import com.tutu.finsmanager.dao.abstraction.CompanyEx;
import com.tutu.finsmanager.dao.abstraction.UserCacheEx;
import com.tutu.finsmanager.dao.jdbc.CompanyJdbc;
import com.tutu.finsmanager.dao.repository.CompanyRepository;
import com.tutu.finsmanager.model.Business.BusinessExImpl;
import com.tutu.finsmanager.model.Business.BusinessForm;
import com.tutu.finsmanager.model.Business.ResponseBusiness;
import com.tutu.finsmanager.model.Comapny.CompanyExImpl;
import com.tutu.finsmanager.model.Comapny.CompanyForm;
import com.tutu.finsmanager.model.Comapny.ResponseCompany;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.regex.Pattern;

@Service
public class CompanyService {
    @Autowired
    CompanyRepository companyRepository ;
    @Autowired
    CompanyJdbc companyJdbc;
    private Logger logger = LoggerFactory.getLogger(CompanyService.class);
    private UserCacheService userCacheService;

    public CompanyService(UserCacheService userCacheService){
        this.userCacheService = userCacheService;
    }

    public List<CompanyEx> GetCompanyList()
    {
        try {
            UserCacheEx userCacheEx = userCacheService.GetUserCache();
            List<CompanyEx> companyExList = companyRepository.findAllEx(userCacheEx.getUserParentId(),userCacheEx.getActiveBusinessId());
            return companyExList;
        } catch (Exception comp_svc_ex) {
            logger.info("CompanyService.GetCompanyList -> Error: " + comp_svc_ex);
            return null;
        }
    }

    public ResponseCompany CompanyFormAction(CompanyForm companyForm)
    {
        ResponseCompany responseCompany = new ResponseCompany();
        try {
            UserCacheEx userCacheEx = userCacheService.GetUserCache();
            Integer intCount = 0;
            switch(companyForm.getMethod()){
                case "select" : {
                    logger.info("BusinessService.BusinessFormAction: select ");
                    responseCompany.setCompanyEx(companyRepository.getCompany(userCacheEx.getActiveBusinessId(),userCacheEx.getUserParentId()));
                }break;
                case "update" : {
                    logger.info("BusinessService.CompanyFormAction: update ");
                    CompanyFormValidation(companyForm,responseCompany);
                    if(responseCompany.getCompanyExImpl().getValidFlg()==true){
                        companyJdbc.CompanyFormAction(companyForm,userCacheEx);
                    }
                }break;
                default:{
                    //logger.info("BusinessService.BusinessFormAction: default ");
                }
            }
            return responseCompany;
        } catch (Exception bus_svc_ex) {
            logger.info("BusinessService.CompanyFormAction -> Error: " + bus_svc_ex);
            return null;
        }
    }

    private void CompanyFormValidation(CompanyForm companyForm,ResponseCompany responseCompany){
        CompanyExImpl companyExImpl = new CompanyExImpl();
        companyExImpl.setValidFlg(true);
        if(companyForm.getName().length() > 50){
            companyExImpl.setValidFlg(false);
            companyExImpl.setNameDvm("Не должно превышать 50 символов");
        }
        if(companyForm.getDescription().length() > 250){
            companyExImpl.setValidFlg(false);
            companyExImpl.setNameDvm("Не должно превышать 250 символов");
        }

        if((companyForm.getInn().length() != 12 && companyForm.getInn().length() != 10) || Pattern.matches("[0-9]{10}|[0-9]{12}",companyForm.getInn())==false){
            companyExImpl.setValidFlg(false);
            companyExImpl.setInnDvm("Должно содержать 10 или 12 цифр.");
        }

        if((companyForm.getKpp().length() != 9) || Pattern.matches("[0-9]{9}",companyForm.getKpp())==false){
            companyExImpl.setValidFlg(false);
            companyExImpl.setKppDvm("Должно содержать 9 цифр.");
        }

        if((companyForm.getAccount().length() != 20) || Pattern.matches("[0-9]{20}",companyForm.getAccount())==false){
            companyExImpl.setValidFlg(false);
            companyExImpl.setAccountDvm("Должно содержать 20 цифр.");
        }

        responseCompany.setCompanyExImpl(companyExImpl);
    }
}
