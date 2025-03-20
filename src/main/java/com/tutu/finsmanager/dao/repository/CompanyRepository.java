package com.tutu.finsmanager.dao.repository;

import com.tutu.finsmanager.dao.abstraction.BusinessEx;
import com.tutu.finsmanager.dao.abstraction.CompanyEx;
import com.tutu.finsmanager.dao.entities.Company;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
@Transactional
public interface CompanyRepository extends JpaRepository<Company, Integer> {
    @Query(value = "select t1.id, t1.name, t1.description, t1.inn, t1.kpp, t1.account\n" +
            "from company t1\n" +
            "where t1.main_user = :main_user_id and t1.business_id = :business_id", nativeQuery = true)
    List<CompanyEx> findAllEx(@Param("main_user_id") Long mainUserId, @Param("business_id") Long BusinessId);

    @Query(value = "select id, name, description, inn, kpp, account from company where business_id = :business_id and main_user=:main_user_id", nativeQuery = true)
    CompanyEx getCompany(@Param("business_id") Long businessId, @Param("main_user_id") Long mainUserId);
}
