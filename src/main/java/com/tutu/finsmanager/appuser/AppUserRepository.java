package com.tutu.finsmanager.appuser;


import com.tutu.finsmanager.dao.abstraction.AppUserEx;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
@Transactional(readOnly = true)
public interface AppUserRepository
        extends JpaRepository<AppUser, Long> {

    @Query(value = "select * from app_user where email = :email and locked=false", nativeQuery = true)
    Optional<AppUser> findByEmail(@Param("email") String email);

    @Transactional
    @Modifying
    @Query("UPDATE AppUser a " +
            "SET a.enabled = TRUE WHERE a.email = ?1")
    int enableAppUser(String email);

    //Получить Id пользователя по логину
    //@Query(value = "select id from app_user where email = :user_login", nativeQuery = true)
    //Integer GetUserIdbyEmail(@Param("user_login") String user_login_in);

    @Query(value = "SELECT * FROM app_user WHERE email = :user_login", nativeQuery = true)
    AppUser GetUserByEmail(@Param("user_login") String user_login_in);

    @Query(value = "SELECT first_name FirstName, last_name LastName, middle_name MiddleName, email, phone FROM app_user WHERE email = :user_login", nativeQuery = true)
    AppUserEx GetShortUserByEmail(@Param("user_login") String user_login_in);

    //@Query(value = "SELECT * FROM app_user where parent_id = :parent_id and app_user_role = 'SUB_USER'", nativeQuery = true)
    //List<AppUser> GetSubUserList (@Param("parent_id") Integer parent_id);

    //@Query(value = "SELECT * FROM app_user where id = :id", nativeQuery = true)
    //AppUser GetMainUser (@Param("id") Integer id);

    //@Query(value = "SELECT control_resp controlResp, article_resp articleResp, analytic_resp analyticResp, c_agent_resp cAgentResp FROM app_user where id = :id and parent_id = :parent_id", nativeQuery = true)
    //AppUserEx GetSubUserResp (@Param("id") Long id, @Param("parent_id") Long parentId);

    @Query(value = "SELECT id, last_name||' '||first_name||' '||middle_name as Fio FROM app_user WHERE parent_id = :parent_id AND app_user_role = 'SUB_USER'", nativeQuery = true)
    List<AppUserEx> GetSubUserShortList (@Param("parent_id") Long parent_id);

    @Query(value = "SELECT id,last_name||' '||first_name||' '||middle_name as fio FROM app_user WHERE parent_id = :parent_id AND app_user_role = 'SUB_USER' AND id in (select user_id from user_business where business_id = :business_id and main_user = :parent_id)", nativeQuery = true)
    List<AppUserEx> GetSubUserShortBList (@Param("parent_id") Long parentId,@Param("business_id") Long businessId);

    @Transactional
    @Modifying
    @Query("UPDATE AppUser a " +
            "SET a.newpassword = ?2 WHERE a.id = ?1")
    int setNewPasswordAppUser(Integer AppUserId, String NewPassword);

    @Transactional
    @Modifying
    @Query("UPDATE AppUser a " +
            "SET a.password = a.newpassword WHERE a.email = ?1")
    int recoveryAppUser(String email);
}
