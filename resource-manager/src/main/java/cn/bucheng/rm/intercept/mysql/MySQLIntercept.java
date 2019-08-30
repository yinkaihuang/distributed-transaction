package cn.bucheng.rm.intercept.mysql;


import cn.bucheng.rm.holder.XidContext;
import com.mysql.jdbc.*;
import lombok.extern.slf4j.Slf4j;


import java.sql.SQLException;
import java.util.Properties;

@Slf4j
public class MySQLIntercept implements StatementInterceptorV2 {
    @Override
    public void init(Connection connection, Properties properties) throws SQLException {
           log.info("init mysql intercept");
    }

    @Override
    public ResultSetInternalMethods preProcess(String sql, Statement statement, Connection connection) throws SQLException {
        if (statement instanceof PreparedStatement) {
            PreparedStatement preparedStatement = (PreparedStatement) statement;
            sql = preparedStatement.asSql();
            if (XidContext.existXid()) {
                log.info("xid:{} execute sql:{}", XidContext.getXid(), sql);
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
    public ResultSetInternalMethods postProcess(String sql, Statement interceptedStatement, ResultSetInternalMethods originalResultSet, Connection connection, int warningCount, boolean noIndexUsed, boolean noGoodIndexUsed, SQLException statementException) throws SQLException {
        return null;
    }
}