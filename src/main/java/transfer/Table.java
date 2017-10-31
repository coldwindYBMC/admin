package transfer;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class Table {
    
    String name;
    List<Column> columns = new ArrayList<>();
    List<Record> records = new ArrayList<>();
    
    public Table(String name) {
        this.name = name;
    }
    
    public int findColumnIndex(String columnName) {
        for (int i = 0; i < this.columns.size(); i++) {
            if(this.columns.get(i).name.equals(columnName)) {
                return i;
            }
        }
        return 0;
    }
    
    public Record findRecordByValIdx(int valIdx, String val) {
        for (Record record : records) {
            if(record.values.get(valIdx).equals(val)) {
                return record;
            }
        }
        return null;
    }
    
    public void parseRecord(ResultSet rs) throws SQLException {
        Record record = new Record(this);
        for(int index = 1; index <= this.columns.size(); index++){
            String val = rs.getString(index);
            record.values.add(val);
        }
        this.records.add(record);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("DROP TABLE IF EXISTS ").append(this.name).append(";").append("\n");
        sb.append("CREATE TABLE \"").append(this.name).append("\" (");
        boolean ignoreKey = this.columns.stream().filter(c->c.iskey).count() > 1;
        Iterator<Column> it = this.columns.iterator();
        boolean comma = false;
        for(;it.hasNext();){
            Column column = it.next();
            if(column.isIgnored) continue;
            if(comma) sb.append(",");
            sb.append(column.toString(ignoreKey));
            comma = true;
        }
        sb.append(");");
        
        sb.append("\n");
        for (Record insert : this.records) {
            sb.append(insert).append("\n");
        }
        return sb.toString();
    }

}
