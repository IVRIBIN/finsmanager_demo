package com.tutu.finsmanager.dao.jdbc;

import com.tutu.finsmanager.dao.abstraction.UserCacheEx;
import com.tutu.finsmanager.dao.mapper.analytics.DatasetMapper;
import com.tutu.finsmanager.model.Analytics.Dataset;
import com.tutu.finsmanager.model.Analytics.RequestAnalytics;
import com.tutu.finsmanager.model.Analytics.ResponseAnalytics;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;


@Component
public class AnalyticsJdbc {
    private Logger logger = LoggerFactory.getLogger(AnalyticsJdbc.class);
    @Autowired
    JdbcTemplate jdbcTemplate;
    @Autowired
    DatasetMapper datasetMapper;

    //Получение данных по балансу контрагентов по месецам
    public ResponseAnalytics GetBalanceFinsInfo(RequestAnalytics requestAnalytics, UserCacheEx userCacheEx){
        try{
            String filterAttrib1 = requestAnalytics.getFilterAttrib1();
            if(filterAttrib1.compareTo("day")==0){filterAttrib1="'dd.mm.yyyy'";}
            if(filterAttrib1.compareTo("month")==0){filterAttrib1="'mm.yyyy'";}
            if(filterAttrib1.compareTo("year")==0){filterAttrib1="'yyyy'";}
            ResponseAnalytics responseAnalytics = new ResponseAnalytics();
            String strLableSql = GetDataLableBalanse(requestAnalytics.getReportName(),filterAttrib1,requestAnalytics.getFilterAttrib2(),userCacheEx);

            //Формирование списка заголовков горизонтальной оси
            List<String> counterAgentList = jdbcTemplate.query(strLableSql,new RowMapper<String>(){
                public String mapRow(ResultSet rs, int rowNum)
                        throws SQLException {
                    return rs.getString(1);
                }
            });
            responseAnalytics.setLabels(counterAgentList);

            //Формирование списка datasets
            String strDatasetListSql = "";
            if(requestAnalytics.getReportName().compareTo("CounteragentBalance")==0){
                strDatasetListSql = "select t1.id, t1.name as label from counteragent t1 where t1.main_user_id = " + userCacheEx.getUserParentId();
            }
            if(requestAnalytics.getReportName().compareTo("BusinessBalance")==0){
                strDatasetListSql = "select t1.id, t1.name as label from business t1 where t1.main_user = " + userCacheEx.getUserParentId();
            }
            if(requestAnalytics.getReportName().compareTo("ArticleBalance")==0){
                strDatasetListSql = "select t1.id, t1.name as label from article t1 where t1.main_user_id = " + userCacheEx.getUserParentId() + " and parent_id = " + userCacheEx.getActiveBusinessId();
            }

            List<Dataset> datasetList = jdbcTemplate.query(strDatasetListSql, datasetMapper);
            responseAnalytics.setDatasets(datasetList);


            for(int i = 0; i <= datasetList.size()-1; i++) {
                Long intDataSetId = datasetList.get(i).getId();
                String dataSql = "";

                if(requestAnalytics.getReportName().compareTo("CounteragentBalance")==0){
                    dataSql =  GetDataSqlCounteragentBalanse(strLableSql,intDataSetId,filterAttrib1);
                }
                if(requestAnalytics.getReportName().compareTo("BusinessBalance")==0){
                    dataSql = GetDataSqlBusinessBalanse(strLableSql,intDataSetId,filterAttrib1,userCacheEx.getUserParentId());
                }
                if(requestAnalytics.getReportName().compareTo("ArticleBalance")==0){
                    dataSql = GetDataSqlArticleBalanse(strLableSql,userCacheEx.getActiveBusinessId(),filterAttrib1,userCacheEx.getUserParentId(),intDataSetId);
                }

                List<String> dataList = jdbcTemplate.query(dataSql,new RowMapper<String>(){
                    public String mapRow(ResultSet rs, int rowNum)
                            throws SQLException {
                        return rs.getString(1);
                    }
                });
                datasetList.get(i).setData(dataList);
            }
            responseAnalytics.setDatasets(datasetList);

            return responseAnalytics;
        }catch (Exception queryEx){
            logger.info("AnalyticsJdbc.GetCounterAgentList -> ERROR: " + queryEx);
            return null;
        }
    }

    //Формирование подзапроса для баланса контрагента по месецам
    private String GetDataSqlCounteragentBalanse(String strLableSql, Long counteragentId,String filterAttrib1){
        return "select--реализации суммы с учетом предыдущих значние\n" +
                " sum(t_full.balance) OVER (PARTITION BY t_full.u_key ORDER BY lable) balance\n" +
                "from\n" +
                "("+
                "select\n" +
                " case when sum(agent_f.amount) is null then 0 else sum(agent_f.amount) end balance,\n" +
                " t_lable.lable,\n" +
                " 1 as u_key\n" +
                "from\n" +
                "(--lable data\n" +
                strLableSql + "\n" +
                ") t_lable\n" +
                "left join (--fins data\n" +
                "  select\n" +
                "    to_char(to_date(f.operation_date,'dd.mm.yyyy'),"+filterAttrib1+") f_lable,\n" +
                "\tcase when f.type='income' then f.amount else f.amount*-1 end amount\n" +
                "  from\n" +
                "    counteragent agent1\n" +
                "    left join control f on agent1.id = f.counteragent_id and (f.type='income' or f.type='expenses')\n" +
                "  where \n" +
                "    agent1.id = " + counteragentId + "\n" +
                ") agent_f on t_lable.lable = agent_f.f_lable\n" +
                "group by t_lable.lable\n" +
                ")t_full";
    }

    //Формирование подзапроса для баланса бизнеса по месецам
    private String GetDataSqlBusinessBalanse(String strLableSql, Long businessId,String filterAttrib1, Long naiUserId){
        return "select--реализации суммы с учетом предыдущих значние\n" +
                " sum(t_full.balance) OVER (PARTITION BY t_full.u_key ORDER BY lable) balance\n" +
                "from\n" +
                "(\n" +
                "select\n" +
                " case when sum(b_f.amount) is null then 0 else sum(b_f.amount) end balance,\n" +
                " t_lable.lable,\n" +
                " 1 as u_key\t\n" +
                "from\n" +
                "(--lable data\n" +
                strLableSql + "\n" +
                ") t_lable\n" +
                "left join (--fins data\n" +
                "  select\n" +
                "    to_char(to_date(f.operation_date,'dd.mm.yyyy')," + filterAttrib1 + ") f_lable,\n" +
                "\tcase when f.type='income' then f.amount else f.amount*-1 end amount,\n" +
                "\tb1.id business_id\n" +
                "  from\n" +
                "    business b1\n" +
                "    left join control f on b1.id = f.parent_id and (f.type='income' or f.type='expenses')\n" +
                "  where\n" +
                "\tb1.main_user = " + naiUserId + " and b1.id = " + businessId + "\n" +
                ") b_f on t_lable.lable = b_f.f_lable\n" +
                "group by t_lable.lable\n" +
                ")t_full\n";
    }

    //Формирование подзапроса для баланса статей по месецам
    private String GetDataSqlArticleBalanse(String strLableSql, Long businessId,String filterAttrib1, Long mainUserId, Long articleId){
        return "select\n" +
                " case when sum(b_f.amount) is null then 0 else sum(b_f.amount) end balance\t\n" +
                "from\n" +
                "(--lable data\n" +
                strLableSql +
                ") t_lable\n" +
                "left join (--fins data\n" +
                "  select\n" +
                "    to_char(to_date(f.operation_date,'dd.mm.yyyy')," + filterAttrib1 + ") f_lable,\n" +
                "\tcase when f.type='income' then f.amount else f.amount*-1 end amount\n" +
                "  from\n" +
                "    business b1\n" +
                "    left join control f on b1.id = f.parent_id and (f.type='income' or f.type='expenses') and to_char(to_date(f.operation_date,'dd.mm.yyyy'),'yyyy')='2023'\n" +
                "\tleft join article art on f.article_id = art.id\n" +
                "  where\n" +
                "\tb1.main_user = " + mainUserId + " and b1.id = " + businessId + " and art.id=" + articleId + "\n" +
                ") b_f on t_lable.lable = b_f.f_lable\n" +
                "group by t_lable.lable";
    }

    //Формирование подзапроса для получения списка Lable
    private String GetDataLableBalanse(String strReportName,String filterAttrib1,String filterAttrib2, UserCacheEx userCacheEx){
        String strLableSql = "";
        if(strReportName.compareTo("CounteragentBalance")==0){
            strLableSql = "  select t1.lable from\n" +
                    "  (\n" +
                    "    select\n" +
                    "      to_char(to_date(f1.operation_date,'dd.mm.yyyy'),"+filterAttrib1+") lable\n" +
                    "    from\n" +
                    "      counteragent agent1\n" +
                    "      left join control f1 on agent1.id = f1.counteragent_id\n" +
                    "    where agent1.main_user_id = " + userCacheEx.getUserParentId() + "\n" +
                    "  ) t1\n" +
                    "  group by t1.lable\n" +
                    "  order by t1.lable";
        }
        if(strReportName.compareTo("BusinessBalance")==0){
            strLableSql = "  select t1.lable from\n" +
                    "  (\n" +
                    "    select\n" +
                    "      to_char(to_date(f1.operation_date,'dd.mm.yyyy'),"+filterAttrib1+") lable\n" +
                    "    from\n" +
                    "      business b1\n" +
                    "      left join control f1 on b1.id = f1.parent_id\n" +
                    "    where b1.main_user = " + userCacheEx.getUserParentId() + "\n" +
                    "  ) t1\n" +
                    "  group by t1.lable\n" +
                    "  order by t1.lable";
        }
        if(strReportName.compareTo("ArticleBalance")==0){
            strLableSql = "  select t1.lable from\n" +
                    "  (\n" +
                    "    select\n" +
                    "      to_char(to_date(f1.operation_date,'dd.mm.yyyy'),"+filterAttrib1+") lable\n" +
                    "    from\n" +
                    "      business b1\n" +
                    "      left join control f1 on b1.id = f1.parent_id\n" +
                    "    where b1.main_user = " + userCacheEx.getUserParentId() + " and b1.id = " + userCacheEx.getActiveBusinessId() + " and to_char(to_date(f1.operation_date,'dd.mm.yyyy'),'yyyy')='" + filterAttrib2 + "'\n" +
                    "  ) t1\n" +
                    "  group by t1.lable\n" +
                    "  order by t1.lable";
        }
        return strLableSql;
    }


}
