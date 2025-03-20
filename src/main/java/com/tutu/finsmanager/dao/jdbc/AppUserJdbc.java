package com.tutu.finsmanager.dao.jdbc;

import com.tutu.finsmanager.dao.mapper.AppUserMapper;
import com.tutu.finsmanager.model.AppUser.AppUserEx;
import com.tutu.finsmanager.model.AppUser.RequestAppUser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class AppUserJdbc {
    private Logger logger = LoggerFactory.getLogger(AppUserJdbc.class);
    @Autowired
    JdbcTemplate jdbcTemplate;
    @Autowired
    AppUserMapper appUserMapper;

    private String strMainQuery1 = "SELECT row_number() OVER () AS rowNumber,t0.* FROM (SELECT id, last_name lastName, first_name firstName, middle_name middleName, phone, \n" +
            "access_status accessStatus, enabled, locked, email, password, control_resp controlResp, article_resp articleResp, analytic_resp analyticResp, c_agent_resp cAgentResp FROM app_user ";
    private String strMainQuery2 = "SELECT row_number() OVER () AS rowNumber,t0.* FROM (SELECT id, last_name lastName, first_name firstName, middle_name middleName, phone, \n" +
            "access_status accessStatus, enabled, locked, email, '' as password, control_resp controlResp, article_resp articleResp, analytic_resp analyticResp, c_agent_resp cAgentResp FROM app_user ";

    public List<AppUserEx> findAllEx(Integer Limit, Integer Offset, Long mainUserId, String OrderBy){
        String strFilter = "WHERE app_user_role='SUB_USER' AND parent_id = " + mainUserId  + "\n";
        String strOrder = "ORDER BY " + OrderBy + ") t0\n";
        String strLimit = "LIMIT " + Limit + " OFFSET " + Offset;
        List<AppUserEx> employeeExImplList = jdbcTemplate.query(strMainQuery1 + strFilter + strOrder + strLimit , appUserMapper);
        return employeeExImplList;
    }

    public List<AppUserEx> getById(Long mainUserId, Long UserId){
        String strFilter = "WHERE parent_id = " + mainUserId + " AND id = " + UserId + ") t0";
        List<AppUserEx> employeeExImplList = jdbcTemplate.query(strMainQuery2 + strFilter,appUserMapper);
        return employeeExImplList;
    }

    public Integer updateById(Long mainUserId, RequestAppUser requestAppUser){
        try{
            Integer intResult = 0;
            intResult = jdbcTemplate.update("UPDATE app_user SET last_name=upper(?), first_name=upper(?), middle_name=upper(?), phone=? WHERE parent_id=? AND id=?",
                    requestAppUser.getLastName(),requestAppUser.getFirstName(),requestAppUser.getMiddleName(),requestAppUser.getPhone(),mainUserId,requestAppUser.getId());
            return intResult;
        }catch (Exception exp_sql){
            logger.info("AppUserJdbc.updateById -> ERROR: " + exp_sql);
            return 0;
        }
    }

    public Integer updateMainShort(Long mainUserId, RequestAppUser requestAppUser){
        try{
            Integer intResult = 0;
            intResult = jdbcTemplate.update("UPDATE app_user SET last_name=upper(?), first_name=upper(?), middle_name=upper(?), phone=? WHERE id=?",
                    requestAppUser.getLastName(),requestAppUser.getFirstName(),requestAppUser.getMiddleName(),requestAppUser.getPhone(),mainUserId);
            return intResult;
        }catch (Exception exp_sql){
            logger.info("AppUserJdbc.updateById -> ERROR: " + exp_sql);
            return 0;
        }
    }

    public Integer deleteById(Long mainUserId, RequestAppUser requestAppUser){
        try{
            Integer intResult = 0;
            intResult = jdbcTemplate.update("DELETE FROM confirmation_token WHERE app_user_id = (SELECT id FROM app_user WHERE parent_id = ? AND app_user_role='SUB_USER' AND id = ?)",
                    mainUserId,requestAppUser.getId());
            intResult = jdbcTemplate.update("DELETE FROM app_user WHERE parent_id=? AND id=? AND app_user_role='SUB_USER'",
                    mainUserId,requestAppUser.getId());
            return intResult;
        }catch (Exception exp_sql){
            logger.info("AppUserJdbc.deleteById -> ERROR: " + exp_sql);
            return 0;
        }
    }

    public Integer lockById(Long mainUserId, RequestAppUser requestAppUser){
        try{
            Integer intResult = 0;
            intResult = jdbcTemplate.update("UPDATE app_user SET locked = (SELECT CASE WHEN locked THEN false ELSE true END AS val FROM app_user WHERE parent_id=? AND id=? AND app_user_role='SUB_USER') WHERE parent_id=? AND id=? AND app_user_role='SUB_USER'",
                    mainUserId,requestAppUser.getId(),mainUserId,requestAppUser.getId());
            return intResult;
        }catch (Exception exp_sql){
            logger.info("AppUserJdbc.lockById -> ERROR: " + exp_sql);
            return 0;
        }
    }

    public Integer setResp(Long mainUserId, RequestAppUser requestAppUser){
        try{
            Integer intResult = 0;
            intResult = jdbcTemplate.update("UPDATE app_user SET control_resp=?, c_agent_resp=?, article_resp=?, analytic_resp=?  WHERE parent_id=? AND id=? AND app_user_role='SUB_USER'",
                    requestAppUser.getControlResp(),requestAppUser.getcAgentResp(),requestAppUser.getArticleResp(),requestAppUser.getAnalyticResp(),mainUserId,requestAppUser.getId());
            return intResult;
        }catch (Exception exp_sql){
            logger.info("AppUserJdbc.setResp -> ERROR: " + exp_sql);
            return 0;
        }
    }

    public void addToBusiness(Long mainUserId, Long subUserId, Long businessId){
        try{
            jdbcTemplate.update("INSERT INTO user_business (business_id,main_user,user_id) values (?,?,(SELECT id FROM app_user WHERE parent_id = ? AND id = ?)) ON CONFLICT (business_id,user_id) DO NOTHING;",
                    businessId,mainUserId,mainUserId,subUserId);
        }catch (Exception exp_sql){
            logger.info("AppUserJdbc.addToBusiness -> ERROR: " + exp_sql);
        }
    }

    public void deleteFromBusiness(Long mainUserId, Long subUserId, Long businessId){
        try{
            jdbcTemplate.update("DELETE from user_business WHERE business_id = ? AND main_user = ? AND user_id = (SELECT id FROM app_user WHERE parent_id = ? AND id = ?);",
                    businessId,mainUserId,mainUserId,subUserId);
        }catch (Exception exp_sql){
            logger.info("AppUserJdbc.deleteFromBusiness -> ERROR: " + exp_sql);
        }
    }
}
