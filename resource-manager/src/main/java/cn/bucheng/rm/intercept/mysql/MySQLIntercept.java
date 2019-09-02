package cn.bucheng.rm.intercept.mysql;


import cn.bucheng.rm.aware.SpringEnvironmentAware;
import cn.bucheng.rm.holder.XidContext;
import com.mysql.cj.MysqlConnection;
import com.mysql.cj.Query;
import com.mysql.cj.interceptors.QueryInterceptor;
import com.mysql.cj.jdbc.ClientPreparedStatement;
import com.mysql.cj.log.Log;
import com.mysql.cj.protocol.Resultset;
import com.mysql.cj.protocol.ServerSession;
import lombok.extern.slf4j.Slf4j;

import java.sql.SQLException;
import java.util.Properties;
import java.util.function.Supplier;


@Slf4j
public class MySQLIntercept implements QueryInterceptor {

    public static final String SPRING_DATASOURCE_URL = "spring.datasource.url";
    public static final String SPRING_DATASOURCE_USERNAME = "spring.datasource.username";
    public static final String SPRING_DATASOURCE_PASSWORD = "spring.datasource.password";


    @Override
    public QueryInterceptor init(MysqlConnection mysqlConnection, Properties properties, Log log) {
        return this;
    }

    @Override
    public <T extends Resultset> T preProcess(Supplier<String> supplier, Query query) {
        if (query != null && query instanceof ClientPreparedStatement) {
            ClientPreparedStatement statement = (ClientPreparedStatement) query;
            String sql = "";
            try {
                sql = statement.asSql();
                log.info("xid:{} ,url:{}, username:{}, password:{}, sql:{}", XidContext.getXid(), SpringEnvironmentAware.getValue(SPRING_DATASOURCE_URL), SpringEnvironmentAware.getValue(SPRING_DATASOURCE_USERNAME), SpringEnvironmentAware.getValue(SPRING_DATASOURCE_PASSWORD), statement.asSql());
            } catch (SQLException e) {
                log.error("xid:{}, execute sql fail,sql:{}", sql);
                throw new RuntimeException(e);
            }
        }
        return null;
    }

    @Override
    public boolean executeTopLevelOnly() {
        return false;
    }

    @Override
    public void destroy() {

    }

    @Override
    public <T extends Resultset> T postProcess(Supplier<String> supplier, Query query, T t, ServerSession serverSession) {
        return null;
    }
}