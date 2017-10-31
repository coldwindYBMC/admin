package transfer;

import java.util.HashMap;
import java.util.Map;

public class TypeTransfer {
    
    static final Map<String, String> typeTransferMap;
    
    static {
        typeTransferMap = new HashMap<String, String>();
        
        typeTransferMap.put("int", "INTEGER");
        typeTransferMap.put("tinyint", "INTEGER");
        typeTransferMap.put("float", "FLOAT");
        typeTransferMap.put("smallint", "INTEGER");
        typeTransferMap.put("bigint", "INTEGER");
        typeTransferMap.put("varchar", "VARCHAR");
        typeTransferMap.put("text", "TEXT");
        typeTransferMap.put("timestamp", "TEXT");
        typeTransferMap.put("time", "TEXT");
        typeTransferMap.put("datetime", "TEXT");
        typeTransferMap.put("char", "VARCHAR");
    }
    
    public static String mysql2sqlite(String type) {
        type = type.toLowerCase();
        if(type.contains("(")) type = type.substring(0, type.indexOf("("));
        String ret = typeTransferMap.get(type);
        if(ret == null) {
            System.err.println("sqlite type not found : " + type);
        }
        return ret;
    }

}
