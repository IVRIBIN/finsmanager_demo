package com.tutu.finsmanager.dao.jdbc;

import com.tutu.finsmanager.dao.abstraction.UserCacheEx;
import com.tutu.finsmanager.dao.mapper.CounteragentExMapper;
import com.tutu.finsmanager.model.Counteragent.CounteragentEx;
import com.tutu.finsmanager.model.Counteragent.RequestCounteragent;
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
public class CounteragentJdbc {
    private Logger logger = LoggerFactory.getLogger(CounteragentJdbc.class);
    @Autowired
    JdbcTemplate jdbcTemplate;
    @Autowired
    private CounteragentExMapper counteragentExMapper;

    private String strMainQuery = "SELECT row_number() OVER () AS rowNumber,t0.*,0 as bincome, 0 as bexpenses, 0 as bbalance \n" +
            "FROM (SELECT t1.id, t1.name, t1.description, t1.phone, t1.type\n" +
            "FROM counteragent t1\n";

    private String GetQueryFins(Long mainUserId, Long intBusinessId, Long intAgentId){
        return "select \n" +
                "0 as rowNumber, 0 id, '' as name, '' as description, '' as phone, '' as type,\n" +
                "t0.bincome, t0.bexpenses, 0 as bbalance\n" +
                "from\n" +
                "(\n" +
                "select\n" +
                "(\n" +
                "select\n" +
                " CASE WHEN SUM(c_out.amount) IS NULL THEN 0 ELSE SUM(c_out.amount) END bincome\n" +
                "from \n" +
                " counteragent agnt\n" +
                " left join control c_out on agnt.id = c_out.counteragent_id and c_out.type = 'expenses' and c_out.parent_id = " + intBusinessId + "\n" +
                "where\n" +
                " agnt.id = " + intAgentId + " and agnt.main_user_id = " + mainUserId + "\n" +
                ") bincome,\n" +
                "(\n" +
                "select\n" +
                " CASE WHEN SUM(c_in.amount) IS NULL THEN 0 ELSE SUM(c_in.amount) END bexpenses\n" +
                "from \n" +
                " counteragent agnt\n" +
                " left join control c_in on agnt.id = c_in.counteragent_id and c_in.type = 'income' and c_in.parent_id = " + intBusinessId + "\n" +
                "where\n" +
                " agnt.id = " + intAgentId + " and agnt.main_user_id = " + mainUserId + "\n" +
                ") bexpenses\n" +
                ")t0\n";
    }

    public Integer Insert(RequestCounteragent requestCounteragent, UserCacheEx userCacheEx) {
        try{
            GeneratedKeyHolder holder = new GeneratedKeyHolder();
            jdbcTemplate.update(new PreparedStatementCreator() {
                @Override
                public PreparedStatement createPreparedStatement(Connection con) throws SQLException {
                    PreparedStatement statement = con.prepareStatement("INSERT INTO counteragent (created,created_by,name,description,phone,type,updated,updated_by,main_user_id) \n" +
                            "VALUES (CURRENT_TIMESTAMP,?,?,?,?,?,CURRENT_TIMESTAMP,?,?)", Statement.RETURN_GENERATED_KEYS);
                    statement.setLong(1, userCacheEx.getUserId());
                    statement.setString(2, requestCounteragent.getName());
                    statement.setString(3, requestCounteragent.getDescription());
                    statement.setString(4, requestCounteragent.getPhone());
                    statement.setString(5, requestCounteragent.getType());
                    statement.setLong(6, userCacheEx.getUserId());
                    statement.setLong(7, userCacheEx.getUserParentId());
                    return statement;
                }
            }, holder);
            Integer intCounteragentId = Integer.parseInt(holder.getKeyList().get(0).get("id").toString());
            return intCounteragentId;
        }catch (Exception ex_insert){
            logger.info("CounteragentJdbc.Insert -> ERROR: " + ex_insert);
            return null;
        }
    }

    public List<CounteragentEx> GetListAll(Integer Limit,Integer Offset, Long mainUserId, String Name,String OrderBy){
        try{
            if(Name == null){Name = "";}
            if(Limit == null){Limit = 0;};//если вызов для пиклиста
            String strFilter = "WHERE t1.main_user_id = " + mainUserId + " AND name like '%"+ Name +"%'\n";
            String strOrder = "ORDER BY " + OrderBy + ") t0\n";
            String strLimit = "LIMIT " + Limit + " OFFSET " + Offset;
            if(Limit==0){strLimit="";}//если вызов для пиклиста
            List<CounteragentEx> counteragentExList = jdbcTemplate.query(strMainQuery + strFilter + strOrder + strLimit , counteragentExMapper);
            return counteragentExList;
        }catch (Exception listAllEx){
            logger.info("CounteragentJdbc.GetListAll -> ERROR: " + listAllEx);
            return null;
        }
    }

    public CounteragentEx GetById(Long mainUserId,Long RowId){
        try{
            String strFilter = "WHERE t1.main_user_id = " + mainUserId + " AND t1.id = " + RowId + "\n";
            String strOrder = ") t0\n";
            CounteragentEx counteragentEx = jdbcTemplate.queryForObject(strMainQuery + strFilter + strOrder, new CounteragentExMapper());
            return counteragentEx;
        }catch (Exception listAllEx){
            logger.info("CounteragentJdbc.GetById -> ERROR: " + listAllEx);
            return null;
        }
    }

    public void Update(RequestCounteragent requestCounteragent, UserCacheEx userCacheEx){
        try{
            jdbcTemplate.update("UPDATE counteragent SET name=?, description=?, phone=?, type=? WHERE main_user_id=? AND id=?",
                    requestCounteragent.getName(),
                    requestCounteragent.getDescription(),
                    requestCounteragent.getPhone(),
                    requestCounteragent.getType(),
                    userCacheEx.getUserParentId(),
                    requestCounteragent.getId());
        }catch (Exception updateEx){
            logger.info("CounteragentJdbc.Update -> ERROR: " + updateEx);
        }
    }

    public void Delete(RequestCounteragent requestCounteragent, UserCacheEx userCacheEx){
        try{
            jdbcTemplate.update("DELETE FROM counteragent WHERE main_user_id=? AND id=?",
                    userCacheEx.getUserParentId(),
                    requestCounteragent.getId());
            jdbcTemplate.update("DELETE FROM agent_business WHERE main_user=? AND agent_id=?",
                    userCacheEx.getUserParentId(),
                    requestCounteragent.getId());
        }catch (Exception updateEx){
            logger.info("CounteragentJdbc.Delete -> ERROR: " + updateEx);
        }
    }

    public Long GetAllCount(Long mainUserId){
        try {
            Long longResult = jdbcTemplate.queryForObject("SELECT COUNT(id) FROM counteragent WHERE main_user_id = " + mainUserId, new Object[]{}, Long.class);
            return longResult;
        }catch (Exception listCountEx){
            logger.info("CounteragentJdbc.GetAllCount -> ERROR: " + listCountEx);
            return null;
        }
    }

    public List<CounteragentEx> GetListByBusiness(Long mainUserId, Long businessId, String OrderBy){
        try{
            //String strFilter = "WHERE t1.main_user_id = " + mainUserId + "\n";
            String strFilter = "WHERE t1.main_user_id = " + mainUserId + " AND t1.id in (SELECT agent_id FROM agent_business WHERE business_id = " + businessId + " AND main_user = " + mainUserId + ")\n";
            String strOrder = "ORDER BY " + OrderBy + ") t0\n";
            List<CounteragentEx> counteragentExList = jdbcTemplate.query(strMainQuery + strFilter + strOrder , counteragentExMapper);
            return counteragentExList;
        }catch (Exception listAllEx){
            logger.info("CounteragentJdbc.GetListAll -> ERROR: " + listAllEx);
            return null;
        }
    }

    public void AddToActiveBusiness(Long mainUserId, Long businessId, Long agentId){
        try{
            jdbcTemplate.update("INSERT INTO agent_business (business_id,main_user,agent_id) values (?,?,(SELECT id FROM counteragent WHERE main_user_id = ? AND id = ?)) ON CONFLICT (business_id,agent_id) DO NOTHING;",
                    businessId,mainUserId,mainUserId,agentId);
        }catch (Exception addAgentToBusEx){
            logger.info("CounteragentJdbc.AddToActiveBusiness -> ERROR: " + addAgentToBusEx);
        }
    }

    public void DeleteFromBusiness(Long mainUserId, Long businessId, Long agentId){
        try{
            jdbcTemplate.update("DELETE from agent_business WHERE business_id = ? AND main_user = ? AND agent_id = (SELECT id FROM counteragent WHERE main_user_id = ? AND id = ?);",
                    businessId,mainUserId,mainUserId,agentId);
        }catch (Exception deleteAgentFromBusEx){
            logger.info("CounteragentJdbc.DeleteFromBusiness -> ERROR: " + deleteAgentFromBusEx);
        }
    }

    public CounteragentEx GetFinsInfoBusiness(Long mainUserId, Long businessId, Long agentId){
        try{
            String strQuery = GetQueryFins(mainUserId, businessId, agentId);
            CounteragentEx counteragentEx = jdbcTemplate.queryForObject(strQuery, new CounteragentExMapper());
            logger.info(strQuery);
            return counteragentEx;
        }catch (Exception finsInfoEx){
            logger.info("CounteragentJdbc.GetFinsInfoById -> ERROR: " + finsInfoEx);
            return null;
        }
    }
}
