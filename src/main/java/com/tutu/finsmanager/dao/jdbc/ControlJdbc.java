package com.tutu.finsmanager.dao.jdbc;

import com.tutu.finsmanager.dao.abstraction.UserCacheEx;
import com.tutu.finsmanager.dao.mapper.ControlExMapper;
import com.tutu.finsmanager.dao.mapper.ProjectExMapper;
import com.tutu.finsmanager.model.Article.RequestArticle;
import com.tutu.finsmanager.model.Control.ControlEx;
import com.tutu.finsmanager.model.Control.RequestControl;
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
public class ControlJdbc {
    private Logger logger = LoggerFactory.getLogger(ControlJdbc.class);
    @Autowired
    JdbcTemplate jdbcTemplate;
    @Autowired
    ControlExMapper controlExMapper;

    private String strMainQuery = "SELECT row_number() OVER () AS rowNumber,t0.*\n" +
            "FROM (SELECT t1.id,t1.counteragent_id,t1.acc_in,t1.acc_out,operation_date,\n" +
            "case when t1.type='expenses' then 'Расход' else case when t1.type='income' then 'Приход' else 'Перевод'end end as type,\n" +
            "t1.amount, t1.description, t1.selected, t1.article_id, art.name article_name, t1.project_id, prj.name project_name, t1.created,\n" +
            "usr.email created_by_name, cnt_agnt.name counteragent_name,\n" +
            "case when t1.type='expenses' then cnt_agnt_req_in.name else empl_in.acc_name end acc_in_name,\n" +
            "case when t1.type='expenses' then case when cnt_agnt_req_in.cash_type='Y' then 'наличные' else cnt_agnt_req_in.card_num end else empl_in.account end acc_in_req,\n" +
            "case when t1.type='expenses' then cnt_agnt.type else empl_in.description end acc_in_req_dsc,\n" +
            "case when t1.type='income' then cnt_agnt_req_out.name else empl_out.acc_name end acc_out_name,\n" +
            "case when t1.type='income' then case when cnt_agnt_req_out.cash_type='Y' then 'наличные' else cnt_agnt_req_out.card_num end else empl_out.account end acc_out_req,\n" +
            "case when t1.type='income' then cnt_agnt.type else empl_out.description end acc_out_dsc,\n" +
            "case when t1.type='income' then empl_in.id else case when t1.type='expenses' then empl_out.id else '0' end end empl_in_out_id,\n" +
            "case when t1.type='transfer' then empl_in.id else 0 end empl_transfer_in_id,\n" +
            "case when t1.type='transfer' then empl_out.id else 0 end empl_transfer_out_id\n" +
            "FROM control t1\n" +
            "left join counteragent cnt_agnt on t1.counteragent_id = cnt_agnt.id\n" +
            "left join requisite cnt_agnt_req_in on cnt_agnt.id = cnt_agnt_req_in.parent_id and cnt_agnt_req_in.id = t1.acc_in\n" +
            "left join requisite cnt_agnt_req_out on cnt_agnt.id = cnt_agnt_req_out.parent_id and cnt_agnt_req_out.id = t1.acc_out\n" +
            "left join employee empl_in on t1.acc_in = empl_in.id\n" +
            "left join employee empl_out on t1.acc_out = empl_out.id\n" +
            "left join article art on t1.article_id = art.id\n" +
            "left join projectf prj on t1.project_id = prj.id\n" +
            "left join app_user usr on t1.created_by = usr.id\n";

    public List<ControlEx> GetListAll(RequestControl requestControl, Long mainUserId, Long businessId){
        try{
            String OrderBy = "created";
            Integer Limit = requestControl.getLimit();
            Integer Offset = requestControl.getOffset();

            String strUserFilter = "";
            if(requestControl.getFilterInitFlg() && requestControl.getFilterType().compareTo("Default")!=0){
                strUserFilter = "AND t1.type='" + requestControl.getFilterType() +"'\n";
            }

            String strUserFilterEmployee = "";
            if(requestControl.getFilterInitFlg() && requestControl.getFilterEmployee() != 0){
                strUserFilterEmployee = "WHERE (t0.empl_in_out_id = " + requestControl.getFilterEmployee() + " OR t0.empl_transfer_in_id = " + requestControl.getFilterEmployee() + " OR t0.empl_transfer_out_id = " + requestControl.getFilterEmployee() + ")\n";
            }

            String strUserFilterProject = "";
            if(requestControl.getFilterInitFlg() && requestControl.getFilterProject() != 0){
                strUserFilterProject = "AND t1.project_id='" + requestControl.getFilterProject() +"'\n";
            }

            String strFilter = "WHERE t1.main_user_id = " + mainUserId + " AND t1.parent_id = " + businessId +"\n";
            String strOrder = "ORDER BY " + OrderBy + ") t0\n";
            String strLimit = "LIMIT " + Limit + " OFFSET " + Offset;
            logger.info(strMainQuery + strFilter + strUserFilter + strUserFilterProject + strOrder + strUserFilterEmployee + strLimit);
            List<ControlEx> controlExList = jdbcTemplate.query(strMainQuery + strFilter + strUserFilter + strUserFilterProject + strOrder + strUserFilterEmployee + strLimit , controlExMapper);
            return controlExList;
        }catch (Exception listAllEx){
            logger.info("ControlJdbc.GetListAll -> ERROR: " + listAllEx);
            return null;
        }
    }

    public ControlEx GetById(Long mainUserId, Long businessId, Long RowId){
        try{
            String strFilter = "WHERE t1.main_user_id = " + mainUserId + " AND t1.id = " + RowId + " AND t1.parent_id = " + businessId + "\n";
            String strOrder = ") t0\n";
            ControlEx controlEx = jdbcTemplate.queryForObject(strMainQuery + strFilter + strOrder, new ControlExMapper());
            return controlEx;
        }catch (Exception byIdEx){
            logger.info("ControlJdbc.GetById -> ERROR: " + byIdEx);
            return null;
        }
    }

    public Integer Insert(RequestControl requestControl, UserCacheEx userCacheEx) {
        try{
            GeneratedKeyHolder holder = new GeneratedKeyHolder();
            jdbcTemplate.update(new PreparedStatementCreator() {
                @Override
                public PreparedStatement createPreparedStatement(Connection con) throws SQLException {
                    PreparedStatement statement = con.prepareStatement(
                            "INSERT INTO control (created,created_by,updated,updated_by,main_user_id,parent_id," +
                                    "counteragent_id,acc_in,acc_out,operation_date,type,amount,description,article_id,project_id,selected) \n" +
                                    "VALUES (CURRENT_TIMESTAMP,?,CURRENT_TIMESTAMP,?,?,?," +
                                    "?,?,?,?,?,?,?,?,?,false)", Statement.RETURN_GENERATED_KEYS);
                    statement.setLong(1, userCacheEx.getUserId());
                    statement.setLong(2, userCacheEx.getUserId());
                    statement.setLong(3, userCacheEx.getUserParentId());
                    statement.setLong(4, userCacheEx.getActiveBusinessId());
                    statement.setLong(5, requestControl.getCounteragentId());
                    statement.setLong(6, requestControl.getAccountIn());
                    statement.setLong(7, requestControl.getAccountOut());
                    statement.setString(8, requestControl.getOperationDate());
                    statement.setString(9, requestControl.getType());
                    statement.setFloat(10, requestControl.getAmount());
                    statement.setString(11, requestControl.getDescription());
                    statement.setLong(12, requestControl.getArticleId());
                    statement.setLong(13, requestControl.getProjectId());
                    return statement;
                }
            }, holder);
            Integer intProjectId = Integer.parseInt(holder.getKeyList().get(0).get("id").toString());
            return intProjectId;
        }catch (Exception ex_insert){
            logger.info("ControlJdbc.Insert -> ERROR: " + ex_insert);
            return null;
        }
    }

    public void Update(RequestControl requestControl, UserCacheEx userCacheEx){
        try{
            jdbcTemplate.update("UPDATE control SET updated=CURRENT_TIMESTAMP, updated_by=?, counteragent_id=?, acc_in=?, acc_out=?,operation_date=?, amount=?, description=? WHERE main_user_id=? AND id=? AND parent_id=?",
                    userCacheEx.getUserId(),
                    requestControl.getCounteragentId(),
                    requestControl.getAccountIn(),
                    requestControl.getAccountOut(),
                    requestControl.getOperationDate(),
                    requestControl.getAmount(),
                    requestControl.getDescription(),
                    userCacheEx.getUserParentId(),
                    requestControl.getId(),
                    userCacheEx.getActiveBusinessId());
        }catch (Exception updateEx){
            logger.info("ControlJdbc.Update -> ERROR: " + updateEx);
        }
    }

    public void Delete(RequestControl requestControl, UserCacheEx userCacheEx){
        try{
            jdbcTemplate.update("DELETE FROM control WHERE main_user_id=? AND id=? AND parent_id=?",
                    userCacheEx.getUserParentId(),
                    requestControl.getId(),
                    userCacheEx.getActiveBusinessId());
        }catch (Exception updateEx){
            logger.info("ArticleJdbc.Delete -> ERROR: " + updateEx);
        }
    }

    public Long GetAllCount(Long mainUserId, Long BusinessId){
        try {
            Long longResult = jdbcTemplate.queryForObject("SELECT COUNT(id) FROM control WHERE main_user_id = " + mainUserId + " AND parent_id = " + BusinessId, new Object[]{}, Long.class);
            return longResult;
        }catch (Exception listCountEx){
            logger.info("ControlJdbc.GetAllCount -> ERROR: " + listCountEx);
            return null;
        }
    }
}
