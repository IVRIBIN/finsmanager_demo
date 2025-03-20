package com.tutu.finsmanager.dao.jdbc;


import com.tutu.finsmanager.dao.abstraction.EmployeeEx;
import com.tutu.finsmanager.dao.abstraction.UserCacheEx;
import com.tutu.finsmanager.dao.mapper.EmployeeMapper;
import com.tutu.finsmanager.model.Employee.EmployeeExImpl;
import com.tutu.finsmanager.model.Employee.EmployeeForm;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Component;

import javax.swing.tree.RowMapper;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

@Component
public class EmployeeJdbc {
    private Logger logger = LoggerFactory.getLogger(EmployeeJdbc.class);
    @Autowired
    JdbcTemplate jdbcTemplate;
    @Autowired
    private EmployeeMapper employeeMapper;

    /*
    private String strMainQuery = "SELECT row_number() OVER () AS rowNumber,t0.* FROM (SELECT t1.id, t1.selected, \n" +
            "t1.last_name lastName, t1.first_name firstName, t1.middle_name middleName, t1.position, t1.phone, \n" +
            "t1.account, t1.balance, t1.acc_name accName, t1.description description, t1.card_num cardNum, t1.inn,\n" +
            "t1.kpp, t1.bik, t1.bank_name bankName\n" +
            "FROM employee t1\n";
    */

    private String strMainQuery = "SELECT row_number() OVER () AS rowNumber, t1.id, t1.selected, t1.last_name lastName, t1.first_name firstName,\n" +
            "t1.middle_name middleName, t1.position, t1.phone, t1.account, t1.balance,\n" +
            "t1.acc_name AccName, t1.description description, t1.card_num cardNum, t1.inn,\n" +
            "t1.kpp, t1.bik, t1.bank_name bankName,\n" +
            "(acc_in.income + c_t_in.income_t)-(acc_exp.expenses + c_t_out.expenses_t) balanceTotal\n" +
            "FROM \n" +
            "employee t1\n" +
            "left join\n" +
            "\t(select t1.id acc_id,\n" +
            "\tcase when sum(c_out.amount) is null then 0 else sum(c_out.amount) end expenses\n" +
            "\tFROM employee t1\n" +
            "\tleft join control c_out on t1.id = c_out.acc_out and c_out.type = 'expenses'\n" +
            "\t--WHERE t1.id = 1 AND t1.main_user=1\n" +
            "\tgroup by t1.id) acc_exp on t1.id = acc_exp.acc_id\n" +
            "left join\t\t\n" +
            "\t(select t1.id acc_id,\n" +
            "\tcase when sum(c_in.amount) is null then 0 else sum(c_in.amount) end income\n" +
            "\tFROM employee t1\n" +
            "\tleft join control c_in on t1.id = c_in.acc_in and c_in.type = 'income'\n" +
            "\t--WHERE t1.id = 1 AND t1.main_user=1\n" +
            "\tgroup by t1.id) acc_in on t1.id = acc_in.acc_id\n" +
            "left join\n" +
            "\t(select t1.id acc_id,\n" +
            "\tcase when sum(c_t_in.amount) is null then 0 else sum(c_t_in.amount) end income_t\n" +
            "\tFROM employee t1\n" +
            "\tleft join control c_t_in on t1.id = c_t_in.acc_in and c_t_in.type = 'transfer'\n" +
            "\t--WHERE t1.id = 1 AND t1.main_user=1\n" +
            "\tgroup by t1.id) c_t_in on t1.id = c_t_in.acc_id\n" +
            "left join\n" +
            "\t(select t1.id acc_id,\n" +
            "\tcase when sum(c_t_out.amount) is null then 0 else sum(c_t_out.amount) end expenses_t\n" +
            "\tFROM employee t1\n" +
            "\tleft join control c_t_out on t1.id = c_t_out.acc_out and c_t_out.type = 'transfer'\n" +
            "\t--WHERE t1.id = 1 AND t1.main_user=1\n" +
            "\tgroup by t1.id) c_t_out on t1.id = c_t_out.acc_id\n";

    public List<EmployeeExImpl> findAllEx(Integer Limit,Integer Offset, Long mainUserId, Long CompanyId, String OrderBy){
        String strFilter = "WHERE t1.main_user = " + mainUserId + " AND t1.company_id = " + CompanyId + "\n";
        String strOrder = "ORDER BY " + OrderBy + "\n";
        String strLimit = "LIMIT " + Limit + " OFFSET " + Offset;
        if(Limit==0){strLimit="";}//если вызов для пиклиста
        //logger.info("EmployeeJdbc.findAllEx: " + strMainQuery + strFilter + strOrder + strLimit);
        List<EmployeeExImpl> employeeExImplList = jdbcTemplate.query(strMainQuery + strFilter + strOrder + strLimit , employeeMapper);
        return employeeExImplList;
    }

    public List<EmployeeExImpl> findAllExAndFullNameAndPosition(Integer Limit,Integer Offset, Long mainUserId, Long CompanyId, String OrderBy, String fullName, String position){
        String strFilter = "WHERE t1.main_user = " + mainUserId + " AND t1.company_id = " + CompanyId + " AND ((t1.last_name||' '||t1.first_name||' '||t1.middle_name) like(UPPER('%'||'" + fullName + "'||'%')) AND position like ('" + position + "'))\n";
        String strOrder = "ORDER BY " + OrderBy + ") t0\n";
        String strLimit = "LIMIT " + Limit + " OFFSET " + Offset;
        List<EmployeeExImpl> employeeExImplList = jdbcTemplate.query(strMainQuery + strFilter + strOrder + strLimit , employeeMapper);
        return employeeExImplList;
    }


    public Integer EmployeeFormAction(EmployeeForm employeeForm, UserCacheEx userCacheEx) {
        try{
            Integer intResult = 0;
            String strMethod = employeeForm.getMethod();

            switch(strMethod){
                //t1.acc_name, t1.description, t1.card_num, t1.inn, t1.kpp, t1.bik, t1.bank_name bankName
                case "update" : {
                    intResult = jdbcTemplate.update("UPDATE employee SET updated_by=?, updated=now()::timestamp, last_name=UPPER(?), first_name=UPPER(?), middle_name=UPPER(?), position=?, phone=?, \n" +
                                    "account=?, balance=0, acc_name=?, description=?, card_num=?, inn=?, kpp=?, bik=?, bank_name=?  \n" +
                                    "WHERE id = ? AND company_id = ? AND main_user = ?",
                            userCacheEx.getUserId(),employeeForm.getLastName(),employeeForm.getFirstName(),employeeForm.getMiddleName(),employeeForm.position,employeeForm.getPhone(),
                            employeeForm.getAccount()/*,Float.parseFloat(employeeForm.getBalance())*/,employeeForm.getAccName(),employeeForm.getDescription(),employeeForm.getCardNum(),
                            employeeForm.getInn(),employeeForm.getKpp(),employeeForm.getBik(),employeeForm.getBank_name(),
                            employeeForm.getId(),userCacheEx.getActiveCompanyId(),userCacheEx.getUserParentId());
                }break;
                case "insert" : {
                    //Создание записи
                    GeneratedKeyHolder holder = new GeneratedKeyHolder();
                    jdbcTemplate.update(new PreparedStatementCreator() {
                        @Override
                        public PreparedStatement createPreparedStatement(Connection con) throws SQLException {
                            PreparedStatement statement = con.prepareStatement("INSERT INTO employee (main_user,company_id,created_by,updated_by,last_name,first_name,middle_name,position,phone," +
                                    "account,balance,acc_name,description,card_num,inn,kpp,bik,bank_name) VALUES (?,?,?,?,UPPER(?),UPPER(?),UPPER(?),?,?,?,?,?,?,?,?,?,?,?) ", Statement.RETURN_GENERATED_KEYS);
                            statement.setLong(1, userCacheEx.getUserParentId());
                            statement.setLong(2, userCacheEx.getActiveCompanyId());
                            statement.setLong(3, userCacheEx.getUserId());
                            statement.setLong(4, userCacheEx.getUserId());
                            statement.setString(5, employeeForm.lastName);
                            statement.setString(6, employeeForm.firstName);
                            statement.setString(7, employeeForm.middleName);
                            statement.setString(8, employeeForm.position);
                            statement.setString(9, employeeForm.phone);
                            statement.setString(10, employeeForm.account);
                            statement.setFloat(11, 0/*Float.parseFloat(employeeForm.balance)*/);
                            statement.setString(12, employeeForm.getAccName());
                            statement.setString(13, employeeForm.getDescription());
                            statement.setString(14, employeeForm.getCardNum());
                            statement.setString(15, employeeForm.getInn());
                            statement.setString(16, employeeForm.getKpp());
                            statement.setString(17, employeeForm.getBik());
                            statement.setString(18, employeeForm.getBank_name());
                            return statement;
                        }
                    }, holder);
                    Integer intEmployeeId = Integer.parseInt(holder.getKeyList().get(0).get("id").toString());
                }break;
                case "delete" : {
                    intResult = jdbcTemplate.update("DELETE FROM employee WHERE main_user = ? AND company_id = ? AND id = ?", userCacheEx.getUserParentId(),userCacheEx.getActiveCompanyId(),employeeForm.getId());
                }break;
                case "update_selected" : {
                    intResult = jdbcTemplate.update("UPDATE employee SET selected = ?  WHERE id = ? AND company_id = ? AND main_user = ?",
                            employeeForm.getSelected(),employeeForm.getId(),userCacheEx.getActiveCompanyId(),userCacheEx.getUserParentId());
                }break;
                case "delete_selected" : {
                    intResult = jdbcTemplate.update("DELETE FROM employee WHERE main_user = ? AND company_id = ? AND selected=true", userCacheEx.getUserParentId(),userCacheEx.getActiveCompanyId());
                }break;
                default:{
                    logger.info("EmployeeJdbc.EmployeeFormAction: Неизвестная операция" + strMethod);
                }

            }
            return intResult;

        }catch (Exception exp_sql){
            logger.info("EmployeeJdbc.EmployeeFormAction -> ERROR: " + exp_sql);
            return null;
        }
    }

}
