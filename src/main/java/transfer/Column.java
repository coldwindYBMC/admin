package transfer;

import java.sql.ResultSet;
import java.sql.SQLException;

public class Column {
    
    public String name;
    public String type;
    public boolean iskey;
    public boolean isIgnored;
    
    public Column parseFromRS(String tableName, ResultSet rs) throws SQLException {
        this.name = rs.getString(1);
        this.type = TypeTransfer.mysql2sqlite(rs.getString(2));
        this.iskey = "PRI".equals(rs.getString(4));
        this.isIgnored = Environment.isIgnoreColumn(tableName, this.name);
        return this;
    }

    public String toString(boolean ignoreKey) {
        StringBuilder sb = new StringBuilder();
        sb.append("\"" + this.name + "\"")
            .append(" ").append(this.type);
        return (iskey && !ignoreKey) ? sb.append(" PRIMARY KEY").toString() : sb.toString();
    }
    
}
