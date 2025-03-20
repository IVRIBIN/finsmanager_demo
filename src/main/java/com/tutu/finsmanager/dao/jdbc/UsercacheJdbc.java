package com.tutu.finsmanager.dao.jdbc;


import com.tutu.finsmanager.model.UserCacheForm;
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
public class UsercacheJdbc {
    private Logger logger = LoggerFactory.getLogger(UsercacheJdbc.class);
    @Autowired
    JdbcTemplate jdbcTemplate;

    public Integer UsercacheAction(UserCacheForm usercacheform) {
        try{
            Integer intResult = 0;
            String strOperationType = usercacheform.getMethod();
            //logger.info("UsercacheJdbc.UsercacheAction: " + strOperationType);

            switch(strOperationType){
                case "update" : {
                    //logger.info("UsercacheJdbc.UsercacheAction.update -> " +  usercacheform.getActiveBusiness() + " " + usercacheform.getLogin());
                    jdbcTemplate.update("update usercache set active_business = ? where login = ?",
                            usercacheform.getActiveBusiness());
                }break;
                case "insert" : {
                    //Создание записи
                    //logger.info("UsercacheJdbc.UsercacheAction.insert -> " + usercacheform.getLogin());
                    GeneratedKeyHolder holder = new GeneratedKeyHolder();
                    jdbcTemplate.update(new PreparedStatementCreator() {
                        @Override
                        public PreparedStatement createPreparedStatement(Connection con) throws SQLException {
                            PreparedStatement statement = con.prepareStatement("INSERT INTO usercache (login) VALUES (?) ", Statement.RETURN_GENERATED_KEYS);
                            statement.setString(1, usercacheform.getLogin());
                            return statement;
                        }
                    }, holder);

                    String strUserCacheId = holder.getKeyList().get(0).get("id").toString();
                    intResult = Integer.parseInt(strUserCacheId);
                }break;
                case "delete" : {
                    //
                }break;
                case "set_active" : {
                    //logger.info("UsercacheJdbc.UsercacheAction.set_active -> " +  usercacheform.getActiveBusiness() + " " + usercacheform.getLogin());
                    jdbcTemplate.update("update usercache set active_business = ? where login = ?",usercacheform.getActiveBusiness(),usercacheform.getLogin());
                }break;
                case "reset_active" : {
                    jdbcTemplate.update("update usercache set active_business = null where login = ?",usercacheform.getLogin());
                }break;

                default:{
                    logger.info("UsercacheJdbc.UsercacheAction: Неизвестная операция" + strOperationType);
                }
            }
            return intResult;

        }catch (Exception exp_sql){
            logger.info("UsercacheJdbc.UsercacheAction -> ERROR: " + exp_sql);
            return null;
        }
    }
}
