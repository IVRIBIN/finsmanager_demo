package com.tutu.finsmanager.dao.jdbc;

import com.tutu.finsmanager.dao.abstraction.UserCacheEx;
import com.tutu.finsmanager.dao.mapper.ArticleExMapper;
import com.tutu.finsmanager.dao.mapper.ProjectExMapper;
import com.tutu.finsmanager.model.Article.ArticleEx;
import com.tutu.finsmanager.model.Article.RequestArticle;
import com.tutu.finsmanager.model.Project.ProjectEx;
import com.tutu.finsmanager.model.Project.RequestProject;
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
import java.util.List;

@Component
public class ProjectJdbc {
    private Logger logger = LoggerFactory.getLogger(ProjectJdbc.class);
    @Autowired
    JdbcTemplate jdbcTemplate;
    @Autowired
    ProjectExMapper projectExMapper;

    private String GetMainQuery(String strFilter,String strOrder,String strLimit, Long projectId, Long mainUserId){
        String strMainQuery = "";
        strMainQuery = "SELECT row_number() OVER () AS rowNumber,t0.id, t0.created, t0.name, t0.description,t0.type, t0.checked, t0.selected,\n" +
                "t0.active, t0.plan_coming, t0.plan_expense, t0.plan_coming-t0.plan_expense plan_profit,\n" +
                "t0.actual_coming,t0.actual_expense,(t0.actual_coming-t0.actual_expense) actual_profit,\n" +
                "t0.plan_coming-t0.actual_coming delta_coming,t0.plan_expense-t0.actual_expense delta_expense,\n" +
                "t0.plan_profit-(t0.actual_coming-t0.actual_expense) delta_profit,\n" +
                "ROUND(((100*t0.actual_coming)/t0.plan_coming)) coming_percent,\n" +
                "ROUND((100*t0.actual_expense)/t0.plan_expense) expense_percent,\n" +
                "ROUND((100*(t0.actual_coming-t0.actual_expense))/(t0.plan_coming-t0.plan_expense)) profit_percent,\n" +
                "0.00 eff_plan_percent, 0.00 eff_actual_percent, 0.00 eff_delta_percent\n" +
                "FROM (SELECT t1.id, t1.created, t1.name, t1.description,t1.type, t1.checked, t1.selected,\n" +
                "t1.active, t1.plan_coming, t1.plan_expense, t1.plan_coming-t1.plan_expense plan_profit,\n" +
                "case when sum(fc_in.amount) is null then 0.00 else sum(fc_in.amount) end actual_coming,\n" +
                "case when sum(fc_out.amount) is null then 0.00 else sum(fc_out.amount) end actual_expense\n" +
                "FROM projectf t1\n" +
                "    left join (select project_id, sum(amount) amount from control where type='income' and project_id=" + projectId + " and main_user_id=" + mainUserId + " group by project_id) fc_in on t1.id = fc_in.project_id\n" +
                "    left join (select project_id, sum(amount) amount from control where type='expenses' and project_id=" + projectId + " and main_user_id=" + mainUserId + " group by project_id) fc_out on t1.id = fc_out.project_id\n" +
                strFilter + "\n" +
                "    group by t1.id\n" +
                strOrder + "\n" +
                ")t0\n" +
                strLimit;
        return strMainQuery;
    }

    private String GetMainQueryAll(String strFilter,String strOrder,String strLimit){
        String strMainQuery = "";
        strMainQuery = "SELECT row_number() OVER () AS rowNumber,t1.id, t1.created, t1.name, t1.description,t1.type, t1.checked, t1.active, t1.selected, t1.plan_coming, t1.plan_expense, \n" +
                "0.00 plan_profit, 0.00 actual_coming, 0.00 actual_expense, 0.00 actual_profit, 0.00 delta_coming, 0.00 delta_expense, 0.00 delta_profit, \n" +
                "0.00 coming_percent, 0.00 expense_percent, 0.00 profit_percent, 0.00 eff_plan_percent, 0.00 eff_actual_percent, 0.00 eff_delta_percent \n" +
                "FROM projectf t1\n" +
                strFilter + "\n" +
                strOrder + "\n" +
                strLimit;
        return strMainQuery;
    }
    
    public List<ProjectEx> GetListAll(Integer Limit, Integer Offset, Long mainUserId, Long businessId, String OrderBy){
        try{
            String strFilter = "WHERE t1.main_user_id = " + mainUserId + " AND t1.parent_id = " + businessId +" AND active = true";
            String strOrder = "ORDER BY " + OrderBy;
            String strLimit = "LIMIT " + Limit + " OFFSET " + Offset;
            if(Limit==0){strLimit="";}//если вызов для пиклиста
            String strQuery = GetMainQueryAll(strFilter,strOrder,strLimit);
            List<ProjectEx> projectExList = jdbcTemplate.query(strQuery , projectExMapper);
            return projectExList;
        }catch (Exception listAllEx){
            logger.info("ProjectJdbc.GetListAll -> ERROR: " + listAllEx);
            return null;
        }
    }

    public List<ProjectEx> GetListAllOff(Integer Limit, Integer Offset, Long mainUserId, Long businessId, String OrderBy){
        try{
            String strFilter = "WHERE t1.main_user_id = " + mainUserId + " AND t1.parent_id = " + businessId +" AND active = false";
            String strOrder = "ORDER BY t1." + OrderBy + "\n";
            String strLimit = "LIMIT " + Limit + " OFFSET " + Offset;
            String strQuery = GetMainQueryAll(strFilter,strOrder,strLimit);
            List<ProjectEx> projectExList = jdbcTemplate.query(strQuery , projectExMapper);
            return projectExList;
        }catch (Exception listAllEx){
            logger.info("ProjectJdbc.GetListAllOff -> ERROR: " + listAllEx);
            return null;
        }
    }

    public ProjectEx GetById(Long mainUserId,Long businessId, Long RowId){
        try{
            String strFilter = "WHERE t1.main_user_id = " + mainUserId + " AND t1.id = " + RowId + " AND t1.parent_id = " + businessId;
            String strQuery = GetMainQuery(strFilter,"","",RowId,mainUserId);

            logger.info(strQuery);
            ProjectEx projectEx = jdbcTemplate.queryForObject(strQuery, new ProjectExMapper());
            return projectEx;
        }catch (Exception byIdEx){
            logger.info("ProjectJdbc.GetById -> ERROR: " + byIdEx);
            return null;
        }
    }

    public void Update(RequestProject requestProject, UserCacheEx userCacheEx){
        try{
            jdbcTemplate.update("UPDATE projectf SET updated=CURRENT_TIMESTAMP, updated_by=?, name=?, description=?, checked=?, plan_coming=?, plan_expense=? WHERE main_user_id=? AND id=? AND parent_id=?",
                    userCacheEx.getUserId(),
                    requestProject.getName(),
                    requestProject.getDescription(),
                    requestProject.getChecked(),
                    requestProject.getPlanComing(),
                    requestProject.getPlanExpense(),
                    userCacheEx.getUserParentId(),
                    requestProject.getId(),
                    userCacheEx.getActiveBusinessId());
        }catch (Exception updateEx){
            logger.info("ProjectJdbc.Update -> ERROR: " + updateEx);
        }
    }
    public Integer Insert(RequestProject requestProject, UserCacheEx userCacheEx) {
        try{
            GeneratedKeyHolder holder = new GeneratedKeyHolder();
            jdbcTemplate.update(new PreparedStatementCreator() {
                @Override
                public PreparedStatement createPreparedStatement(Connection con) throws SQLException {
                    PreparedStatement statement = con.prepareStatement(
                            "INSERT INTO projectf (created,created_by,updated,updated_by,main_user_id,parent_id,name," +
                                    "description,type,active,checked,plan_coming,plan_expense,selected) \n" +
                            "VALUES (CURRENT_TIMESTAMP,?,CURRENT_TIMESTAMP,?,?,?,?,?,'',true,?,?,?,false)", Statement.RETURN_GENERATED_KEYS);
                    statement.setLong(1, userCacheEx.getUserId());
                    statement.setLong(2, userCacheEx.getUserId());
                    statement.setLong(3, userCacheEx.getUserParentId());
                    statement.setLong(4, userCacheEx.getActiveBusinessId());
                    statement.setString(5, requestProject.getName());
                    statement.setString(6, requestProject.getDescription());
                    //statement.setString(7, requestProject.getType());
                    statement.setBoolean(7, requestProject.getChecked());
                    statement.setFloat(8, requestProject.getPlanComing());
                    statement.setFloat(9, requestProject.getPlanExpense());
                    return statement;
                }
            }, holder);
            Integer intProjectId = Integer.parseInt(holder.getKeyList().get(0).get("id").toString());
            return intProjectId;
        }catch (Exception ex_insert){
            logger.info("ProjectJdbc.Insert -> ERROR: " + ex_insert);
            return null;
        }
    }

    public void Deactivate(RequestProject requestProject, UserCacheEx userCacheEx){
        try{
            jdbcTemplate.update("UPDATE projectf SET updated=CURRENT_TIMESTAMP,updated_by=?,active=false WHERE main_user_id=? AND id=? AND parent_id=?",
                    userCacheEx.getUserId(),
                    userCacheEx.getUserParentId(),
                    requestProject.getId(),
                    userCacheEx.getActiveBusinessId());
        }catch (Exception updateEx){
            logger.info("ProjectJdbc.Deactivate -> ERROR: " + updateEx);
        }
    }

    public void UpdateSelected(RequestProject requestProject, UserCacheEx userCacheEx){
        try{
            jdbcTemplate.update("UPDATE projectf SET updated=CURRENT_TIMESTAMP,updated_by=?,selected=(SELECT CASE WHEN selected THEN false ELSE true END FROM projectf WHERE main_user_id=? AND id=? AND parent_id=?) WHERE main_user_id=? AND id=? AND parent_id=?",
                    userCacheEx.getUserId(),
                    userCacheEx.getUserParentId(),
                    requestProject.getId(),
                    userCacheEx.getActiveBusinessId(),
                    userCacheEx.getUserParentId(),
                    requestProject.getId(),
                    userCacheEx.getActiveBusinessId()
            );
        }catch (Exception updateEx){
            logger.info("ProjectJdbc.UpdateSelected -> ERROR: " + updateEx);
        }
    }

    public void Delete(RequestProject requestProject, UserCacheEx userCacheEx){
        try{
            jdbcTemplate.update("delete from projectf WHERE main_user_id=? AND id=? AND parent_id=? AND active=false",
                    userCacheEx.getUserParentId(),
                    requestProject.getId(),
                    userCacheEx.getActiveBusinessId());
        }catch (Exception updateEx){
            logger.info("ProjectJdbc.Delete -> ERROR: " + updateEx);
        }
    }

    public Long GetAllCount(Long mainUserId, Long BusinessId){
        try {
            Long longResult = jdbcTemplate.queryForObject("SELECT COUNT(id) FROM projectf WHERE main_user_id = " + mainUserId + " AND parent_id = " + BusinessId + " AND active = true", new Object[]{}, Long.class);
            return longResult;
        }catch (Exception listCountEx){
            logger.info("ProjectJdbc.GetAllCount -> ERROR: " + listCountEx);
            return null;
        }
    }

    public Long GetAllOffCount(Long mainUserId, Long BusinessId){
        try {
            Long longResult = jdbcTemplate.queryForObject("SELECT COUNT(id) FROM projectf WHERE main_user_id = " + mainUserId + " AND parent_id = " + BusinessId + " AND active = false", new Object[]{}, Long.class);
            return longResult;
        }catch (Exception listCountEx){
            logger.info("ProjectJdbc.GetAllOffCount -> ERROR: " + listCountEx);
            return null;
        }
    }




}
