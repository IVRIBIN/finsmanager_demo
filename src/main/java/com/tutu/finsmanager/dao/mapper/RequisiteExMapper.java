package com.tutu.finsmanager.dao.mapper;


import com.tutu.finsmanager.model.Requisite.RequisiteEx;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import java.sql.ResultSet;
import java.sql.SQLException;

@Component
public class RequisiteExMapper implements RowMapper<RequisiteEx> {
    @Override
    public RequisiteEx mapRow(ResultSet rs, int i) throws SQLException {
        RequisiteEx requisiteEx = new RequisiteEx();
        requisiteEx.setId(rs.getLong("id"));
        requisiteEx.setParentId(rs.getLong("parent_id"));
        requisiteEx.setRownumber(rs.getLong("rownumber"));
        requisiteEx.setName(rs.getString("name"));
        requisiteEx.setDescription(rs.getString("description"));
        requisiteEx.setCardNum(rs.getString("card_num"));
        requisiteEx.setInn(rs.getString("inn"));
        requisiteEx.setKpp(rs.getString("kpp"));
        requisiteEx.setBankAcc(rs.getString("bank_acc"));
        requisiteEx.setBik(rs.getString("bik"));
        requisiteEx.setBankName(rs.getString("bank_name"));
        requisiteEx.setCrspAcc(rs.getString("crsp_acc"));
        requisiteEx.setAddrIndex(rs.getString("addr_index"));
        requisiteEx.setAddrCity(rs.getString("addr_city"));
        requisiteEx.setAddrFull(rs.getString("addr_full"));
        requisiteEx.setPhone(rs.getString("phone"));
        requisiteEx.setEmail(rs.getString("email"));
        requisiteEx.setWebsite(rs.getString("website"));
        requisiteEx.setCashType(rs.getString("cash_type"));
        requisiteEx.setMainFlg(rs.getBoolean("main_flg"));
        requisiteEx.setSelected(rs.getBoolean("selected"));
        requisiteEx.setParentName(rs.getString("parent_name"));

        return requisiteEx;
    }
}
