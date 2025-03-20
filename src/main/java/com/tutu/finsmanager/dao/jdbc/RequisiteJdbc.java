package com.tutu.finsmanager.dao.jdbc;

import com.tutu.finsmanager.dao.abstraction.UserCacheEx;
import com.tutu.finsmanager.dao.mapper.RequisiteExMapper;
import com.tutu.finsmanager.model.Requisite.RequestRequisite;
import com.tutu.finsmanager.model.Requisite.RequisiteEx;
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
public class RequisiteJdbc {
    private Logger logger = LoggerFactory.getLogger(RequisiteJdbc.class);
    @Autowired
    JdbcTemplate jdbcTemplate;
    @Autowired
    RequisiteExMapper requisiteExMapper;

    private String strMainQuery = "SELECT row_number() OVER () AS rowNumber,t0.* \n" +
            "FROM (SELECT t1.id, t1.parent_id, t1.name, t1.description, t1.card_num, t1.inn,t1.kpp,t1.bank_acc,t1.bik,t1.bank_name,t1.crsp_acc,t1.addr_index,\n" +
            "t1.addr_city,t1.addr_full,t1.phone,t1.email,t1.website,t1.main_flg,t1.cash_type,t1.selected,t2.name parent_name\n" +
            "FROM requisite t1\n" +
            "left join counteragent t2 on t1.parent_id = t2.id\n";

    public Integer Insert(RequestRequisite requestRequisite, UserCacheEx userCacheEx) {
        try{
            GeneratedKeyHolder holder = new GeneratedKeyHolder();
            jdbcTemplate.update(new PreparedStatementCreator() {
                @Override

                public PreparedStatement createPreparedStatement(Connection con) throws SQLException {
                    String strCity = requestRequisite.getAddrCity();
                    if(strCity== null){strCity = "";}

                    PreparedStatement statement = con.prepareStatement(
                            "INSERT INTO requisite (created,created_by,updated,updated_by,main_user_id,parent_id,name,description,card_num,inn,kpp,bank_acc,\n" +
                            "bik,bank_name,crsp_acc,addr_index,addr_city,addr_full,phone,email,website,main_flg,cash_type)\n" +
                            "VALUES(CURRENT_TIMESTAMP,?,CURRENT_TIMESTAMP,?,?,?,?,?,?,?,?,?,\n" +
                                    "?,?,?,?,?,?,?,?,?,?,?)",
                            Statement.RETURN_GENERATED_KEYS);
                    statement.setLong(1, userCacheEx.getUserId());
                    statement.setLong(2, userCacheEx.getUserId());
                    statement.setLong(3, userCacheEx.getUserParentId());
                    statement.setLong(4, requestRequisite.getParentId());
                    statement.setString(5, requestRequisite.getName());
                    statement.setString(6, requestRequisite.getDescription());
                    statement.setString(7, requestRequisite.getCardNum());
                    statement.setString(8, requestRequisite.getInn());
                    statement.setString(9, requestRequisite.getKpp());
                    statement.setString(10, requestRequisite.getBankAcc());
                    statement.setString(11, requestRequisite.getBik());
                    statement.setString(12, requestRequisite.getBankName());
                    statement.setString(13, requestRequisite.getCrspAcc());
                    statement.setString(14, requestRequisite.getAddrIndex());
                    statement.setString(15, requestRequisite.getAddrCity());
                    statement.setString(16, requestRequisite.getAddrFull());
                    statement.setString(17, requestRequisite.getPhone());
                    statement.setString(18, requestRequisite.getEmail());
                    statement.setString(19, requestRequisite.getWebsite());
                    statement.setBoolean(20, requestRequisite.isMainFlg());
                    statement.setString(21, requestRequisite.getCashType());
                    return statement;
                }
            }, holder);
            Integer intRequisiteId = Integer.parseInt(holder.getKeyList().get(0).get("id").toString());

            if(requestRequisite.isMainFlg()){
                ResetMain(requestRequisite.getParentId(),Long.valueOf(intRequisiteId),userCacheEx);
            }

            return intRequisiteId;
        }catch (Exception ex_insert){
            logger.info("RequisiteJdbc.Insert -> ERROR: " + ex_insert);
            return null;
        }
    }


    public List<RequisiteEx> GetListAll(Integer Limit, Integer Offset, Long mainUserId, Long parentId , String OrderBy){
        try{
            String strFilter = "WHERE t1.main_user_id = " + mainUserId + " AND parent_id = " + parentId + "\n";
            String strOrder = "ORDER BY t1." + OrderBy + ") t0\n";
            String strLimit = "LIMIT " + Limit + " OFFSET " + Offset;
            if(Limit==0){strLimit="";}//если вызов для пиклиста
            List<RequisiteEx> requisiteExList = jdbcTemplate.query(strMainQuery + strFilter + strOrder + strLimit , requisiteExMapper);
            return requisiteExList;
        }catch (Exception listAllEx){
            logger.info("RequisiteJdbc.GetListAll -> ERROR: " + listAllEx);
            return null;
        }
    }

    public Long GetAllCount(Long mainUserId, Long parentId){
        try {
            Long longResult = jdbcTemplate.queryForObject("SELECT COUNT(id) FROM requisite WHERE main_user_id = " + mainUserId + " AND parent_id = " + parentId, new Object[]{}, Long.class);
            return longResult;
        }catch (Exception listCountEx){
            logger.info("RequisiteJdbc.GetAllCount -> ERROR: " + listCountEx);
            return null;
        }
    }

    public void Update(RequestRequisite requestRequisite, UserCacheEx userCacheEx){
        try{
            logger.info("RequisiteJdbc.GetListAll -> UPDATE: " + userCacheEx.getUserParentId() + " " + requestRequisite.getParentId() + " " + requestRequisite.getId());

            jdbcTemplate.update("UPDATE requisite SET updated=CURRENT_TIMESTAMP,updated_by=?,name=?,description=?,card_num=?,inn=?,kpp=?,bank_acc=?,\n" +
                            "bik=?,bank_name=?,crsp_acc=?,addr_index=?,addr_city=?,addr_full=?,phone=?,email=?,website=?,main_flg=?,cash_type=?\n" +
                            "WHERE main_user_id=? AND parent_id=? AND id=?",
                    userCacheEx.getUserId(),
                    requestRequisite.getName(),
                    requestRequisite.getDescription(),
                    requestRequisite.getCardNum(),
                    requestRequisite.getInn(),
                    requestRequisite.getKpp(),
                    requestRequisite.getBankAcc(),
                    requestRequisite.getBik(),
                    requestRequisite.getBankName(),
                    requestRequisite.getCrspAcc(),
                    requestRequisite.getAddrIndex(),
                    requestRequisite.getAddrCity(),
                    requestRequisite.getAddrFull(),
                    requestRequisite.getPhone(),
                    requestRequisite.getEmail(),
                    requestRequisite.getWebsite(),
                    requestRequisite.isMainFlg(),
                    requestRequisite.getCashType(),
                    userCacheEx.getUserParentId(),
                    requestRequisite.getParentId(),
                    requestRequisite.getId()
            );

            if(requestRequisite.isMainFlg()){
                ResetMain(requestRequisite.getParentId(),requestRequisite.getId(),userCacheEx);
            }
        }catch (Exception updateEx){
            logger.info("RequisiteJdbc.Update -> ERROR: " + updateEx);
        }
    }

    public void Delete(RequestRequisite requestRequisite, UserCacheEx userCacheEx){
        try{
            jdbcTemplate.update("DELETE FROM requisite WHERE main_user_id=? AND id=?",
                    userCacheEx.getUserParentId(),
                    requestRequisite.getId());
        }catch (Exception deleteEx){
            logger.info("RequisiteJdbc.Delete -> ERROR: " + deleteEx);
        }
    }

    public void ResetMain(Long ParentId, Long Id, UserCacheEx userCacheEx){
        try{
            jdbcTemplate.update("UPDATE requisite SET main_flg=false\n" +
                            "WHERE main_user_id=? AND parent_id=? AND id<>?",
                    userCacheEx.getUserParentId(),
                    ParentId,
                    Id
            );
        }catch (Exception resetMainEx){
            logger.info("RequisiteJdbc.ResetMain -> ERROR: " + resetMainEx);
        }
    }

    public RequisiteEx GetById(Long mainUserId, Long parentId, Long RowId){
        try{
            String strFilter = "WHERE t1.main_user_id = " + mainUserId + " AND t1.parent_id = " + parentId + " AND t1.id = " + RowId + "\n";
            String strOrder = ") t0\n";
            RequisiteEx requisiteEx = jdbcTemplate.queryForObject(strMainQuery + strFilter + strOrder, new RequisiteExMapper());
            return requisiteEx;
        }catch (Exception listAllEx){
            logger.info("RequisiteJdbc.GetById -> ERROR: " + listAllEx);
            return null;
        }
    }
}
