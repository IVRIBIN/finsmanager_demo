package com.tutu.finsmanager.dao.mapper;

import com.tutu.finsmanager.model.Counteragent.CounteragentEx;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import java.sql.ResultSet;
import java.sql.SQLException;

@Component
public class CounteragentExMapper implements RowMapper<CounteragentEx> {
    @Override
    public CounteragentEx mapRow(ResultSet rs, int i) throws SQLException {
        CounteragentEx counteragentEx = new CounteragentEx();

        counteragentEx.setId(rs.getLong("id"));
        counteragentEx.setRownumber(rs.getLong("rownumber"));
        counteragentEx.setName(rs.getString("name"));
        counteragentEx.setDescription(rs.getString("description"));
        counteragentEx.setPhone(rs.getString("phone"));
        counteragentEx.setType(rs.getString("type"));
        counteragentEx.setBusinessIncome(rs.getFloat("bincome"));
        counteragentEx.setBusinessExpenses(rs.getFloat("bexpenses"));
        counteragentEx.setBusinessBalance(rs.getFloat("bbalance"));

        return counteragentEx;
    }

}
