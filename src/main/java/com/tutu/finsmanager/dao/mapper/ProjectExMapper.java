package com.tutu.finsmanager.dao.mapper;

import com.tutu.finsmanager.model.Article.ArticleEx;
import com.tutu.finsmanager.model.Project.ProjectEx;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import java.sql.ResultSet;
import java.sql.SQLException;


@Component
public class ProjectExMapper implements RowMapper<ProjectEx> {
    @Override
    public ProjectEx mapRow(ResultSet rs, int i) throws SQLException {
        ProjectEx projectEx = new ProjectEx();

        projectEx.setId(rs.getLong("id"));
        projectEx.setRownumber(rs.getLong("rownumber"));
        projectEx.setName(rs.getString("name"));
        projectEx.setDescription(rs.getString("description"));
        projectEx.setType(rs.getString("type"));
        projectEx.setActive(rs.getBoolean("active"));
        projectEx.setChecked(rs.getBoolean("checked"));
        projectEx.setCreated(rs.getDate("created"));
        projectEx.setSelected(rs.getBoolean("selected"));
        projectEx.setPlanComing(rs.getFloat("plan_coming"));
        projectEx.setPlanExpense(rs.getFloat("plan_expense"));
        projectEx.setPlanProfit(rs.getFloat("plan_profit"));
        projectEx.setActualComing(rs.getFloat("actual_coming"));
        projectEx.setActualExpense(rs.getFloat("actual_expense"));
        projectEx.setActualProfit(rs.getFloat("actual_profit"));
        projectEx.setDeltaComing(rs.getFloat("delta_coming"));
        projectEx.setDeltaExpense(rs.getFloat("delta_expense"));
        projectEx.setDeltaProfit(rs.getFloat("delta_profit"));
        projectEx.setComingPercent(rs.getFloat("coming_percent"));
        projectEx.setExpensePercent(rs.getFloat("expense_percent"));
        projectEx.setProfitPercent(rs.getFloat("profit_percent"));
        projectEx.setEffPlanPercent(rs.getFloat("eff_plan_percent"));
        projectEx.setEffActualPercent(rs.getFloat("eff_actual_percent"));
        projectEx.setEffDeltaPercent(rs.getFloat("eff_delta_percent"));

        return projectEx;
    }
}
