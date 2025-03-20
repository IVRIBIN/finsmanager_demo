package com.tutu.finsmanager.dao.mapper;

import com.tutu.finsmanager.model.Control.ControlEx;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import java.sql.ResultSet;
import java.sql.SQLException;

@Component
public class ControlExMapper implements RowMapper<ControlEx> {
    @Override
    public ControlEx mapRow(ResultSet rs, int i) throws SQLException {
        ControlEx controlEx = new ControlEx();

        controlEx.setId(rs.getLong("id"));
        controlEx.setRownumber(rs.getLong("rownumber"));
        controlEx.setCounteragentId(rs.getLong("counteragent_id"));
        controlEx.setCounteragenName(rs.getString("counteragent_name"));
        controlEx.setAccountIn(rs.getLong("acc_in"));
        controlEx.setAccountInName(rs.getString("acc_in_name"));
        controlEx.setAccountInDsc(rs.getString("acc_in_req_dsc"));
        controlEx.setAccountInReq(rs.getString("acc_in_req"));
        controlEx.setAccountOut(rs.getLong("acc_out"));
        controlEx.setAccountOutDsc(rs.getString("acc_out_dsc"));
        controlEx.setAccountOutReq(rs.getString("acc_out_req"));
        controlEx.setAccountOutName(rs.getString("acc_out_name"));
        controlEx.setOperationDate(rs.getString("operation_date"));
        controlEx.setType(rs.getString("type"));
        controlEx.setAmount(rs.getFloat("amount"));
        controlEx.setDescription(rs.getString("description"));
        controlEx.setSelected(rs.getBoolean("selected"));
        controlEx.setArticleId(rs.getLong("article_id"));
        controlEx.setArticleName(rs.getString("article_name"));
        controlEx.setProjectId(rs.getLong("project_id"));
        controlEx.setProjectName(rs.getString("project_name"));
        controlEx.setCreated(rs.getDate("created"));
        controlEx.setCreatedByName(rs.getString("created_by_name"));

        return controlEx;
    }
}
