package com.tutu.finsmanager.dao.repository;

import com.tutu.finsmanager.dao.abstraction.EmployeeEx;
import com.tutu.finsmanager.dao.entities.Employee;
import org.hibernate.annotations.Sort;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
@Transactional
public interface EmployeeRepository extends JpaRepository<Employee, Integer> {

    String strMainQuery = "SELECT row_number() OVER () AS rowNumber,t0.* FROM (SELECT t1.id, t1.selected, t1.last_name lastName,\n" +
            "t1.first_name firstName, t1.middle_name middleName, t1.position, t1.phone, t1.account, t1.balance,\n" +
            "t1.acc_name accName, t1.description description, t1.card_num cardNum, t1.inn,\n" +
            "t1.kpp, t1.bik, t1.bank_name bankName\n" +
            "FROM employee t1\n";
    String strMainOrder = "ORDER BY t1.created DESC) t0\n";

    /*
    @Query(value = strMainQuery +
            "WHERE t1.main_user = :main_user_id AND t1.company_id = :company_id  \n" +
            //strMainOrder +
            "ORDER BY :order_by) t0\n" +
            "LIMIT :limit_val OFFSET :offset_val", nativeQuery = true)
    List<EmployeeEx> findAllEx(@Param("limit_val") Integer Limit, @Param("offset_val") Integer Offset, @Param("main_user_id") Long mainUserId, @Param("company_id") Long BusinessId, @Param("order_by") String OrderBy);
    */

    /*
    @Query(value = strMainQuery +
            "WHERE t1.main_user = :main_user_id AND t1.company_id = :company_id AND ((t1.last_name||' '||t1.first_name||' '||t1.middle_name) like(UPPER('%'||:full_name||'%')) AND position like (:position))  \n"+
            strMainOrder +
            "LIMIT :limit_val OFFSET :offset_val", nativeQuery = true)
    List<EmployeeEx> findAllExAndFullNameAndPosition(@Param("limit_val") Integer Limit, @Param("offset_val") Integer Offset, @Param("main_user_id") Long mainUserId, @Param("company_id") Long BusinessId, @Param("full_name") String fullName, @Param("position") String position);
    */


    @Query(value = "SELECT count(t1.id) rowCount\n" +
            "FROM employee t1\n" +
            "WHERE t1.main_user = :main_user_id AND t1.company_id = :company_id", nativeQuery = true)
    Long GetRowCount(@Param("main_user_id") Long mainUserId, @Param("company_id") Long BusinessId);

    @Query(value = "SELECT count(t1.id) rowCount\n" +
            "FROM employee t1\n" +
            "WHERE t1.main_user = :main_user_id AND t1.company_id = :company_id AND t1.selected = true", nativeQuery = true)
    Long GetSelectedCount(@Param("main_user_id") Long mainUserId, @Param("company_id") Long BusinessId);

    @Query(value = "SELECT t1.id, t1.selected, t1.last_name lastName, t1.first_name firstName, \n" +
            "t1.middle_name middleName, t1.position, t1.phone, t1.account, t1.balance, \n" +
            "t1.acc_name AccName, t1.description description, t1.card_num cardNum, t1.inn,\n" +
            "t1.kpp, t1.bik, t1.bank_name bankName\n" +
            "FROM employee t1\n" +
            "WHERE t1.id = :employee_id AND t1.main_user=:main_user_id", nativeQuery = true)
    EmployeeEx getEmployee(@Param("employee_id") Long businessId, @Param("main_user_id") Long mainUserId);
}
