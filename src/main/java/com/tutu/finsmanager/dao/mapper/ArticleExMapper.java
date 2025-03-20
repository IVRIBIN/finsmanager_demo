package com.tutu.finsmanager.dao.mapper;

import com.tutu.finsmanager.model.Article.ArticleEx;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import java.sql.ResultSet;
import java.sql.SQLException;


@Component
public class ArticleExMapper implements RowMapper<ArticleEx> {
    @Override
    public ArticleEx mapRow(ResultSet rs, int i) throws SQLException {
        ArticleEx articleEx = new ArticleEx();

        articleEx.setId(rs.getLong("id"));
        articleEx.setRownumber(rs.getLong("rownumber"));
        articleEx.setName(rs.getString("name"));
        articleEx.setCategory(rs.getString("category"));
        articleEx.setDisplay(rs.getBoolean("display"));
        articleEx.setSelected(rs.getBoolean("selected"));

        return articleEx;
    }
}
