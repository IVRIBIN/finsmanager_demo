package com.tutu.finsmanager.dao.repository;


import com.tutu.finsmanager.dao.abstraction.UserCacheEx;
import com.tutu.finsmanager.dao.entities.Business;
import com.tutu.finsmanager.dao.entities.UserCache;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface UserCacheRepository extends JpaRepository<UserCache, Integer> {
    //Выбрать все селектом по проекту
    @Query(value = "SELECT t1.login login,t2.id userid,CASE WHEN t2.app_user_role = 'USER' THEN t2.id ELSE t2.parent_id END userparentid,CASE WHEN t1.active_business is null THEN 0 ELSE t1.active_business END activebusinessid,t3.id activecompanyid,t2.app_user_role userrole, t2.control_resp ControlResp, t2.article_resp ArticleResp, t2.analytic_resp AnalyticResp, t2.c_agent_resp CAgentResp\n" +
            "FROM usercache t1 join app_user t2 on t1.login = t2.email left join company t3 on t1.active_business = t3.business_id\n" +
            "WHERE t1.login = :user_login", nativeQuery = true)
    UserCacheEx GetUserCache(@Param("user_login") String UserLogin);

}
