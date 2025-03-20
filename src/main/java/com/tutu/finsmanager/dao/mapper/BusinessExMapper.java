package com.tutu.finsmanager.dao.mapper;


import com.tutu.finsmanager.dao.abstraction.BusinessEx;
import com.tutu.finsmanager.model.Business.BusinessExImpl;
import com.tutu.finsmanager.model.Control.ControlEx;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import java.sql.ResultSet;
import java.sql.SQLException;

@Component
public class BusinessExMapper implements RowMapper<BusinessExImpl> {
    @Override
    public BusinessExImpl mapRow(ResultSet rs, int i) throws SQLException {
        BusinessExImpl businessExImpl = new BusinessExImpl();

        businessExImpl.setBalanceTotal(rs.getFloat("balance_total"));
        businessExImpl.setIncomeTotal(rs.getFloat("income_total"));
        businessExImpl.setExpanseTotal(rs.getFloat("expense_total"));
        businessExImpl.setTransferTotal(rs.getFloat("transfer_total"));

        return businessExImpl;
    }
}
