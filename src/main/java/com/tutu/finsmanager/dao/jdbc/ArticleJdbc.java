package com.tutu.finsmanager.dao.jdbc;

import com.tutu.finsmanager.dao.abstraction.UserCacheEx;
import com.tutu.finsmanager.dao.mapper.ArticleExMapper;
import com.tutu.finsmanager.model.Article.ArticleEx;
import com.tutu.finsmanager.model.Article.RequestArticle;
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
public class ArticleJdbc {
    private Logger logger = LoggerFactory.getLogger(ArticleJdbc.class);
    @Autowired
    JdbcTemplate jdbcTemplate;
    @Autowired
    ArticleExMapper articleExMapper;

    private String strMainQuery = "SELECT row_number() OVER () AS rowNumber,t0.* \n" +
            "FROM (SELECT t1.id, t1.name, t1.category, t1.display, t1.selected\n" +
            "FROM article t1\n";

    public Integer Insert(RequestArticle requestArticle, UserCacheEx userCacheEx) {
        try{
            GeneratedKeyHolder holder = new GeneratedKeyHolder();
            jdbcTemplate.update(new PreparedStatementCreator() {
                @Override
                public PreparedStatement createPreparedStatement(Connection con) throws SQLException {
                    PreparedStatement statement = con.prepareStatement("INSERT INTO article (created,created_by,name,category,display,selected,updated,updated_by,main_user_id,parent_id) \n" +
                            "VALUES (CURRENT_TIMESTAMP,?,?,?,?,false,CURRENT_TIMESTAMP,?,?,?)", Statement.RETURN_GENERATED_KEYS);
                    statement.setLong(1, userCacheEx.getUserId());
                    statement.setString(2, requestArticle.getName());
                    statement.setString(3, requestArticle.getCategory());
                    statement.setBoolean(4, requestArticle.getDisplay());
                    statement.setLong(5, userCacheEx.getUserId());
                    statement.setLong(6, userCacheEx.getUserParentId());
                    statement.setLong(7, userCacheEx.getActiveBusinessId());
                    return statement;
                }
            }, holder);
            Integer intArticleId = Integer.parseInt(holder.getKeyList().get(0).get("id").toString());
            return intArticleId;
        }catch (Exception ex_insert){
            logger.info("ArticleJdbc.Insert -> ERROR: " + ex_insert);
            return null;
        }
    }

    public List<ArticleEx> GetListAll(Integer Limit, Integer Offset, Long mainUserId, Long businessId, String Name, String OrderBy){
        try{
            if(Name == null){Name = "";}
            String strFilter = "WHERE t1.main_user_id = " + mainUserId + " AND t1.parent_id = " + businessId +" AND UPPER(name) like '%" + Name + "%'\n";
            String strOrder = "ORDER BY " + OrderBy + ") t0\n";
            String strLimit = "LIMIT " + Limit + " OFFSET " + Offset;
            if(Limit==0){strLimit="";}//если вызов для пиклиста
            List<ArticleEx> articleExList = jdbcTemplate.query(strMainQuery + strFilter + strOrder + strLimit , articleExMapper);
            return articleExList;
        }catch (Exception listAllEx){
            logger.info("ArticleJdbc.GetListAll -> ERROR: " + listAllEx);
            return null;
        }
    }

    public ArticleEx GetById(Long mainUserId,Long businessId, Long RowId){
        try{
            String strFilter = "WHERE t1.main_user_id = " + mainUserId + " AND t1.id = " + RowId + " AND t1.parent_id = " + businessId + "\n";
            String strOrder = ") t0\n";
            ArticleEx articleEx = jdbcTemplate.queryForObject(strMainQuery + strFilter + strOrder, new ArticleExMapper());
            return articleEx;
        }catch (Exception byIdEx){
            logger.info("ArticleJdbc.GetById -> ERROR: " + byIdEx);
            return null;
        }
    }

    public void Update(RequestArticle requestArticle, UserCacheEx userCacheEx){
        try{
            jdbcTemplate.update("UPDATE article SET name=?, category=?, display=? WHERE main_user_id=? AND id=? AND parent_id=?",
                    requestArticle.getName(),
                    requestArticle.getCategory(),
                    requestArticle.getDisplay(),
                    userCacheEx.getUserParentId(),
                    requestArticle.getId(),
                    userCacheEx.getActiveBusinessId());
        }catch (Exception updateEx){
            logger.info("ArticleJdbc.Update -> ERROR: " + updateEx);
        }
    }

    public void Delete(RequestArticle requestArticle, UserCacheEx userCacheEx){
        try{
            jdbcTemplate.update("DELETE FROM article WHERE main_user_id=? AND id=? AND parent_id=?",
                    userCacheEx.getUserParentId(),
                    requestArticle.getId(),
                    userCacheEx.getActiveBusinessId());
        }catch (Exception updateEx){
            logger.info("ArticleJdbc.Delete -> ERROR: " + updateEx);
        }
    }

    public Long GetAllCount(Long mainUserId, Long BusinessId){
        try {
            Long longResult = jdbcTemplate.queryForObject("SELECT COUNT(id) FROM article WHERE main_user_id = " + mainUserId + " AND parent_id = " + BusinessId, new Object[]{}, Long.class);
            return longResult;
        }catch (Exception listCountEx){
            logger.info("ArticleJdbc.GetAllCount -> ERROR: " + listCountEx);
            return null;
        }
    }

    public void UpdateSelected(Long mainUserId, Long BusinessId, Long RowId, Boolean Selected){
        jdbcTemplate.update("UPDATE article SET selected = ? WHERE main_user_id = ? AND parent_id = ? AND id = ?", Selected, mainUserId, BusinessId, RowId);
    }
}
