package com.tutu.finsmanager.core;

import com.tutu.finsmanager.dao.abstraction.UserCacheEx;
import com.tutu.finsmanager.dao.entities.UserCache;
import com.tutu.finsmanager.dao.jdbc.UsercacheJdbc;
import com.tutu.finsmanager.dao.repository.UserCacheRepository;
import com.tutu.finsmanager.model.UserCacheForm;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

@Service
public class UserCacheService {
    @Autowired
    UserCacheRepository userCacheRepository;
    @Autowired
    UsercacheJdbc usercacheJdbc;
    private Logger logger = LoggerFactory.getLogger(UserCacheService.class);

    public UserCacheService(){
        logger.info("UserCacheService create");
    }

    public void SetUserCache(String UserLogin){
        UserCacheForm userCacheForm = new UserCacheForm();
        userCacheForm.setLogin(UserLogin);
        userCacheForm.setMethod("insert");
        usercacheJdbc.UsercacheAction(userCacheForm);

    }

    public UserCacheEx GetUserCache(){
        UserCacheEx userCacheEx = userCacheRepository.GetUserCache(GetUserLogin());
        return userCacheEx;
    }

    public void SetActiveBusiness(Long BusinessId){
        UserCacheForm userCacheForm = new UserCacheForm();
        userCacheForm.setLogin(GetUserLogin());
        userCacheForm.setActiveBusiness(BusinessId);
        userCacheForm.setMethod("set_active");
        usercacheJdbc.UsercacheAction(userCacheForm);
    }

    public void ResetActiveBusiness(){
        UserCacheForm userCacheForm = new UserCacheForm();
        userCacheForm.setLogin(GetUserLogin());
        userCacheForm.setMethod("reset_active");
        usercacheJdbc.UsercacheAction(userCacheForm);
    }

    //------------------------------------------------------------------------------------------------------------------
    //Получить логин пользователя
    public String GetUserLogin(){
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
