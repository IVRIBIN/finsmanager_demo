package com.tutu.finsmanager.dao.mapper;

import com.tutu.finsmanager.model.AppUser.AppUserEx;
import com.tutu.finsmanager.model.Employee.EmployeeExImpl;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import java.sql.ResultSet;
import java.sql.SQLException;

//https://mkyong.com/spring/spring-jdbctemplate-querying-examples/
@Component
public class AppUserMapper implements RowMapper<AppUserEx> {
    @Override
    public AppUserEx mapRow(ResultSet rs, int i) throws SQLException {
        AppUserEx appUserEx = new AppUserEx();

        appUserEx.setId(rs.getLong("id"));
        appUserEx.setRowNumber(rs.getLong("rowNumber"));
        appUserEx.setFirstName(rs.getString("firstName"));
        appUserEx.setLastName(rs.getString("lastName"));
        appUserEx.setMiddleName(rs.getString("middleName"));
        appUserEx.setPhone(rs.getString("phone"));
        appUserEx.setAccessStatus(rs.getString("accessStatus"));
        appUserEx.setEnabled(rs.getBoolean("enabled"));
        appUserEx.setLocked(rs.getBoolean("locked"));
        appUserEx.setEmail(rs.getString("email"));
        appUserEx.setPassword(rs.getString("password"));
        appUserEx.setControlResp(rs.getString("controlResp"));
        appUserEx.setArticleResp(rs.getString("articleResp"));
        appUserEx.setAnalyticResp(rs.getString("analyticResp"));
        appUserEx.setcAgentResp(rs.getString("cAgentResp"));
        return appUserEx;
    }
}
