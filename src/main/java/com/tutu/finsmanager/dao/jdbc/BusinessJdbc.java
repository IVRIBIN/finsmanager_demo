package com.tutu.finsmanager.dao.jdbc;

import com.tutu.finsmanager.dao.abstraction.UserCacheEx;
import com.tutu.finsmanager.dao.mapper.ArticleExMapper;
import com.tutu.finsmanager.dao.mapper.BusinessExMapper;
import com.tutu.finsmanager.model.Article.ArticleEx;
import com.tutu.finsmanager.model.Business.BusinessExImpl;
import com.tutu.finsmanager.model.Business.BusinessForm;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Component;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;

@Component
public class BusinessJdbc {
    private Logger logger = LoggerFactory.getLogger(BusinessJdbc.class);
    @Autowired
    JdbcTemplate jdbcTemplate;

    //Создание записи
    public Integer BusinessFormAction(BusinessForm businessForm, UserCacheEx userCacheEx) {
        try{
            Integer intResult = 0;
            String strMethod = businessForm.getMethod();

            switch(strMethod){
                case "update" : {
                    intResult = jdbcTemplate.update("UPDATE business SET name = ?, description = ? WHERE id = ? and main_user = ?", businessForm.getName(), businessForm.getDescription(), businessForm.getId(),userCacheEx.getUserParentId());
                }break;
                case "delete" : {
                    intResult = jdbcTemplate.update("DELETE from business WHERE id = ? and main_user = ?", businessForm.getId(), userCacheEx.getUserParentId());
                    if(intResult == 1){
                        jdbcTemplate.update("DELETE FROM user_business where business_id = ?", businessForm.getId());
                    }

                }break;
                case "insert" : {
                    //Создание записи
                    GeneratedKeyHolder holder = new GeneratedKeyHolder();
                    jdbcTemplate.update(new PreparedStatementCreator() {
                        @Override
                        public PreparedStatement createPreparedStatement(Connection con) throws SQLException {
                            PreparedStatement statement = con.prepareStatement("INSERT INTO business (main_user,name,description) VALUES (?,?,?) ", Statement.RETURN_GENERATED_KEYS);
                            statement.setLong(1, userCacheEx.getUserParentId());
                            statement.setString(2, businessForm.getName());
                            statement.setString(3, businessForm.getDescription());
                            return statement;
                        }
                    }, holder);
                    //Long intBusinessId = Integer.parseInt(holder.getKeyList().get(0).get("id").toString());
                    Integer intBusinessId = Integer.parseInt(holder.getKeyList().get(0).get("id").toString());
                    jdbcTemplate.update("insert into user_business (user_id, business_id, main_user) values (?,?,?)", userCacheEx.getUserId() ,intBusinessId, userCacheEx.getUserId());


                    //Создание дефолтной компании
                    GeneratedKeyHolder holderCompany = new GeneratedKeyHolder();
                    jdbcTemplate.update(new PreparedStatementCreator() {
                        @Override
                        public PreparedStatement createPreparedStatement(Connection con) throws SQLException {
                            PreparedStatement statement = con.prepareStatement("INSERT INTO company (main_user,business_id,name,description,inn,kpp,account) VALUES (?,?,?,?,'000000000000','000000000','00000000000000000000') ", Statement.RETURN_GENERATED_KEYS);
                            statement.setLong(1, userCacheEx.getUserParentId());
                            statement.setLong(2, intBusinessId);
                            statement.setString(3, businessForm.getName());
                            statement.setString(4, businessForm.getDescription());
                            return statement;
                        }
                    }, holderCompany);

                    intResult = intBusinessId;
                }break;
                default:{
                    logger.info("BusinessJdbc.BusinessFormAction: Неизвестная операция" + strMethod);
                }
            }
            return intResult;

        }catch (Exception exp_sql){
            logger.info("BusinessJdbc.BusinessFormAction -> ERROR: " + exp_sql);
            return null;
        }
    }

    public BusinessExImpl GetFinsInfo(Long mainUserId, Long businessId){
        try{
            String strFilter = "WHERE b.id=" + businessId+" and b.main_user=" + mainUserId + "\n";
            String strMainQuery = "SELECT \n" +
                    "t0.expense_total,\n" +
                    "t0.income_total,\n" +
                    "t0.transfer_total,\n" +
                    "t0.income_total - t0.expense_total balance_total\n" +
                    "FROM\n" +
                    "(\n" +
                    "SELECT\n" +
                    "(SELECT \n" +
                    " CASE WHEN SUM(c_out.amount) IS NULL THEN 0 ELSE SUM(c_out.amount) END expense_total\n" +
                    "FROM\n" +
                    " business b\n" +
                    " LEFT JOIN control c_out ON b.id = c_out.parent_id AND c_out.type='expenses'\n" +
                    strFilter +
                    ") expense_total,\n" +
                    "(SELECT \n" +
                    " CASE WHEN SUM(c_in.amount) IS NULL THEN 0 ELSE SUM(c_in.amount) END income_total\n" +
                    "FROM\n" +
                    " business b\n" +
                    " LEFT JOIN control c_in ON b.id = c_in.parent_id AND c_in.type='income'\n" +
                    strFilter +
                    ") income_total,\n" +
                    "(SELECT \n" +
                    " CASE WHEN SUM(c_tr.amount) IS NULL THEN 0 ELSE SUM(c_tr.amount) END transfer_total\n" +
                    "FROM\n" +
                    " business b\n" +
                    " LEFT JOIN control c_tr ON b.id = c_tr.parent_id AND c_tr.type='transfer'\n" +
                    strFilter +
                    ") transfer_total\n" +
                    ")t0\n";
            BusinessExImpl businessExImpl = jdbcTemplate.queryForObject(strMainQuery, new BusinessExMapper());
            return businessExImpl;
        }catch (Exception jdbcEx){
            logger.info("BusinessJdbc.GetFinsInfo -> ERROR: " + jdbcEx);
            return null;
        }
    }
}
