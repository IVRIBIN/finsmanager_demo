package com.tutu.finsmanager.dao.mapper.analytics;

import com.tutu.finsmanager.model.Analytics.Dataset;
import com.tutu.finsmanager.model.Article.ArticleEx;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import java.sql.ResultSet;
import java.sql.SQLException;

@Component
public class DatasetMapper implements RowMapper<Dataset> {
    @Override
    public Dataset mapRow(ResultSet rs, int i) throws SQLException {
        Dataset dataset = new Dataset();
        dataset.setId(rs.getLong("id"));
        dataset.setLabel(rs.getString("label"));
        return dataset;
    }
}
