package com.tutu.finsmanager.dao.repository;

import com.tutu.finsmanager.dao.entities.Business;
import com.tutu.finsmanager.dao.abstraction.BusinessEx;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
@Repository
@Transactional
public interface BusinessRepository extends JpaRepository<Business, Integer>{

    //@Query(value = "select id,name,description,created,main_user,'' singleVal from business", nativeQuery = true)
    //List<Business> findAll();

    @Query(value = "select t1.id, t1.name, t1.description, case when t1.id = :active_business then 'Y' else 'N' end activeflg\n" +
            "from business t1 join user_business t2 on t1.id = t2.business_id\n" +
            "where t1.main_user = :main_user_id and t2.user_id = :user_id", nativeQuery = true)
    List<BusinessEx> findAllEx(@Param("main_user_id") Long mainUserId, @Param("user_id") Long UserId,@Param("active_business") Long ActiveBusinessId);

    @Query(value = "select id, name, description from business where id = :id and main_user=:main_user_id", nativeQuery = true)
    BusinessEx getBusiness(@Param("id") Long id,@Param("main_user_id") Long mainUserId);

}
