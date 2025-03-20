package com.tutu.finsmanager.core;

import com.tutu.finsmanager.dao.abstraction.BusinessEx;
import com.tutu.finsmanager.dao.abstraction.UserCacheEx;
import com.tutu.finsmanager.model.Business.BusinessExImpl;
import com.tutu.finsmanager.model.Business.ResponseBusiness;
import com.tutu.finsmanager.dao.jdbc.BusinessJdbc;
import com.tutu.finsmanager.dao.repository.BusinessRepository;
import com.tutu.finsmanager.model.Business.BusinessForm;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BusinessService {
    @Autowired
    BusinessRepository businessRepository ;

    @Autowired
    BusinessJdbc businessJdbc;
    private Logger logger = LoggerFactory.getLogger(BusinessService.class);
    private UserCacheService userCacheService;

    public BusinessService(UserCacheService userCacheService){
        this.userCacheService = userCacheService;
    }

    public List<BusinessEx> GetBusinessList()
    {
        try {
            //logger.info("BusinessService.GetBusinessList -> ");
            UserCacheEx userCacheEx = userCacheService.GetUserCache();
            List<BusinessEx> businessList = businessRepository.findAllEx(userCacheEx.getUserParentId(),userCacheEx.getUserId(),userCacheEx.getActiveBusinessId());
            return businessList;
        } catch (Exception bus_svc_ex) {
            logger.info("BusinessService.GetBusinessList -> Error: " + bus_svc_ex);
            return null;
        }
    }

    public ResponseBusiness BusinessFormAction(BusinessForm businessForm)
    {
        ResponseBusiness responseBusiness = new ResponseBusiness();
        try {
            UserCacheEx userCacheEx = userCacheService.GetUserCache();
            Integer intCount = 0;
            switch(businessForm.getMethod()){
                case "select" : {
                    //logger.info("BusinessService.BusinessFormAction: select " + businessForm.getId());
                    responseBusiness.setBusinessEx(businessRepository.getBusiness(businessForm.getId(),userCacheEx.getUserParentId()));
                }break;
                case "set_active" : {
                    //logger.info("BusinessService.BusinessFormAction: set_active " + businessForm.getId());
                    if(businessRepository.getBusiness(businessForm.getId(),userCacheEx.getUserParentId()) != null){
                        userCacheService.SetActiveBusiness(businessForm.getId());
                    }
                }break;
                case "delete" : {
                    intCount = businessJdbc.BusinessFormAction(businessForm,userCacheService.GetUserCache());
                    if(intCount > 0){
                        userCacheService.ResetActiveBusiness();
                    }
                }break;
                case "getFinsInfo" : {
                    responseBusiness.setBusinessExImp(businessJdbc.GetFinsInfo(userCacheService.GetUserCache().getUserParentId(),userCacheService.GetUserCache().getActiveBusinessId()));
                }break;
                default:{//insert,update
                    //logger.info("BusinessService.BusinessFormAction: default ");
                    BusinessFormValidation(businessForm,responseBusiness);
                    if(responseBusiness.getBusinessExImp().getValidFlg() == true) {
                        intCount = businessJdbc.BusinessFormAction(businessForm, userCacheService.GetUserCache());
                        logger.info("BusinessService.BusinessFormAction: default -> " + businessForm.getMethod() + " " + intCount);
                        if (intCount > 0) {
                            if(businessForm.getMethod().compareTo("insert")==0){
                                userCacheService.SetActiveBusiness(Long.valueOf(intCount));
                            }else{
                                userCacheService.SetActiveBusiness(businessForm.getId());
                            }
                        }
                    }
                }
            }
            return responseBusiness;
        } catch (Exception bus_svc_ex) {
            logger.info("BusinessService.BusinessFormAction -> Error: " + bus_svc_ex);
            return null;
        }
    }


    private void BusinessFormValidation(BusinessForm businessForm,ResponseBusiness responseBusiness){
        BusinessExImpl businessExImp = new BusinessExImpl();
        businessExImp.setValidFlg(true);
        if(businessForm.getName().length() > 255){
            businessExImp.setValidFlg(false);
            businessExImp.setNameDvm("Не должно превышать 255 символов");
        }
        if(businessForm.getDescription().length() > 255){
            businessExImp.setValidFlg(false);
            businessExImp.setDescriptionDvm("Не должно превышать 255 символов");
        }

        responseBusiness.setBusinessExImp(businessExImp);
    }

    //------------------------------------------------------------------------------------------------------------------
    //Получить логин пользователя
    private String GetUserLogin(){
        String strUserName = "";
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (principal instanceof UserDetails) {
            strUserName = ((UserDetails)principal).getUsername();
        } else {
            strUserName = principal.toString();
        }
        return strUserName;
    }
}
