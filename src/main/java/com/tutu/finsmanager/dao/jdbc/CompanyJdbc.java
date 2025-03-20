package com.tutu.finsmanager.dao.jdbc;

import com.tutu.finsmanager.dao.abstraction.UserCacheEx;
import com.tutu.finsmanager.model.Comapny.CompanyForm;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;


@Component
public class CompanyJdbc {
    private Logger logger = LoggerFactory.getLogger(BusinessJdbc.class);
    @Autowired
    JdbcTemplate jdbcTemplate;

    //Создание записи
    public Integer CompanyFormAction(CompanyForm companyForm, UserCacheEx userCacheEx) {
        try{
            Integer intResult = 0;
            String strMethod = companyForm.getMethod();

            switch(strMethod){
                case "update" : {
                    intResult = jdbcTemplate.update("UPDATE company SET name=?, description=?, inn=?, kpp=?, account=? WHERE business_id = ? and main_user = ?", companyForm.getName(), companyForm.getDescription(), companyForm.getInn(), companyForm.getKpp(), companyForm.getAccount(),userCacheEx.getActiveBusinessId(),userCacheEx.getUserParentId());
                }break;
                default:{
                    logger.info("CompanyJdbc.CompanyFormAction: Неизвестная операция" + strMethod);
                }
            }
            return intResult;

        }catch (Exception exp_sql){
            logger.info("CompanyJdbc.CompanyFormAction -> ERROR: " + exp_sql);
            return null;
        }
    }
}
