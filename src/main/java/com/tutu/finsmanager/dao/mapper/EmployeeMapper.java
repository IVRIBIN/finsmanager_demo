package com.tutu.finsmanager.dao.mapper;


import com.tutu.finsmanager.model.Employee.EmployeeExImpl;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import java.sql.ResultSet;
import java.sql.SQLException;

@Component
public class EmployeeMapper implements RowMapper<EmployeeExImpl> {
    @Override
    public EmployeeExImpl mapRow(ResultSet rs, int i) throws SQLException {
        EmployeeExImpl employeeExImpl = new EmployeeExImpl();


        employeeExImpl.setId(rs.getLong("id"));
        employeeExImpl.setRowNumber(rs.getLong("rownumber"));
        employeeExImpl.setFirstName(rs.getString("firstName"));
        employeeExImpl.setLastName(rs.getString("lastName"));
        employeeExImpl.setMiddleName(rs.getString("middleName"));
        employeeExImpl.setPosition(rs.getString("position"));
        employeeExImpl.setPhone(rs.getString("phone"));
        employeeExImpl.setBalance(rs.getFloat("balance"));
        employeeExImpl.setSelected(rs.getBoolean("selected"));
        employeeExImpl.setAccount(rs.getString("account"));
        employeeExImpl.setAccName(rs.getString("accName"));
        employeeExImpl.setDescription(rs.getString("description"));
        employeeExImpl.setCardNum(rs.getString("cardNum"));
        employeeExImpl.setInn(rs.getString("inn"));
        employeeExImpl.setKpp(rs.getString("kpp"));
        employeeExImpl.setBik(rs.getString("bik"));
        employeeExImpl.setBankName(rs.getString("bankName"));
        employeeExImpl.setBalanceTotal(rs.getFloat("balanceTotal"));
        return employeeExImpl;
    }
}
