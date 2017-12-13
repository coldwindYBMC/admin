package transfer;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;

public class Transfer {
    
    private static List<Table> tables = new ArrayList<>();
     
    public static void transfer(Conf conf, String targetDir) throws SQLException, FileNotFoundException {
        transfer0(conf);
        revisenpcavatar();
        check();//检查campaignnode中的数据
        savefile(targetDir);
    }

    private static void check() {
		Table cnode = findTable("t_s_campaignnode");
		int nodeIDIndex = cnode.findColumnIndex("NodeId");
		int bossIndex = cnode.findColumnIndex("Bosses");
		int formationIndex = cnode.findColumnIndex("Formation");
		
		Table lnode = findTable("t_s_livingtemplate");
		int livingIdIndex = lnode.findColumnIndex("TemplateId");
		List<Record> livingList = lnode.records;
		List<String> livingIdList = new ArrayList<>();
		for(Record living : livingList){
			livingIdList.add(living.values.get(livingIdIndex));
		}
		
		Iterator<Record> it = cnode.records.iterator();
		//检查bosses和formation的长度是否一致
		StringBuffer msg1 = new StringBuffer();
		StringBuffer msg2 = new StringBuffer();
        while(it.hasNext()) {
        	Record r = it.next();
        	String bosses = r.values.get(bossIndex);
        	String formation = r.values.get(formationIndex);
        	if(bosses!=null && !"".equals(bosses) &&formation!=null&& !"".equals(formation)){
        		if(bosses.split(",").length != formation.split(",").length){
        			msg1.append("NodeId = "+r.values.get(nodeIDIndex)+"的怪和站位不匹配，请检查。");
        			msg1.append("\r\n");
        		};
        		String[] bossesArr = bosses.split(",");
        		//检查怪是否存在
        		for(String bossId : bossesArr){
        			if(!livingIdList.contains(bossId)){
//        				msg2.append("NodeId = "+r.values.get(nodeIDIndex)+"的怪BossId="+bossId+"不存在。");
//        				msg2.append("\r\n");
        			}
        		}
        	}
        }
        System.out.println(msg1.toString());
        System.out.println(msg2.toString());
	}

	private static void savefile(String targetDir) throws FileNotFoundException {
        File file = new File(targetDir, "hlsango.sql");
        PrintStream ps = new PrintStream(new FileOutputStream(file));
        ps.println("PRAGMA locking_mode = EXCLUSIVE;");
        ps.println("BEGIN EXCLUSIVE;");
        ps.println();

        for (Table table : tables) {
            ps.println(table);
        }

        ps.println();
        ps.print("COMMIT;");
        ps.flush();
        ps.close();
    }

    private static void transfer0(Conf conf) throws SQLException {
        tables.clear();
        Connection connect = DBConnection.connect(conf);
        for (String table : Environment.transferTables) {
            transferTable(connect, table);
        }
        connect.close();
    }

    private static void revisenpcavatar() throws SQLException {
        Table npcavatar = findTable("t_s_npcavatar");
        
        //clear generate records
        int idx = npcavatar.findColumnIndex("TemplateId");
        Iterator<Record> it = npcavatar.records.iterator();
        while(it.hasNext()) {
            if(it.next().values.get(idx).length() != 4) {
                it.remove();
            }
        }
        
        revisenpcavatarfromanothertable(npcavatar, findTable("t_s_livingtemplate"));
        revisenpcavatarfromanothertable(npcavatar, findTable("t_s_soldiertemplate"));
    }

    private static void revisenpcavatarfromanothertable(Table npcavatar, Table another) {
        int idx1 = npcavatar.findColumnIndex("TemplateId");
        int idx2 = npcavatar.findColumnIndex("TemplateName");
        int idx3 = npcavatar.findColumnIndex("Quality");
        
        int idx00 = another.findColumnIndex("NpcTemplateId");
        int idx01 = another.findColumnIndex("TemplateId");
        int idx02 = another.findColumnIndex("TemplateName");
        int idx03 = another.findColumnIndex("Quality");
        
        Iterator<Record> it = another.records.iterator();
        while(it.hasNext()) {
            Record next = it.next();
            npcavatar.records.add(npcavatar.findRecordByValIdx(idx1, next.values.get(idx00))
                                           .copy()
                                           .replaceValByIdx(idx1, next.values.get(idx01))
                                           .replaceValByIdx(idx2, next.values.get(idx02))
                                           .replaceValByIdx(idx3, next.values.get(idx03)));
        }
    }

    private static Table findTable(String name) {
        for (Table table : tables) {
            if(table.name.equals(name)) {
                return table;
            }
        }
        return null;
    }
    
    private static void transferTable(Connection conn, String tableName) throws SQLException {
        ResultSet t = conn.getMetaData().getTables(null, null, tableName, new String[] {"TABLE"});
        if(t.next()) {
            PreparedStatement pstmt = conn.prepareStatement("DESC " + tableName);
            ResultSet rs = pstmt.executeQuery();
            Table table = new Table(tableName);
            for(;rs.next();) {
                table.columns.add(new Column().parseFromRS(tableName, rs));
            }
            rs.close();
            pstmt.close();
            
            pstmt = conn.prepareStatement("SELECT * FROM " + tableName);
            rs = pstmt.executeQuery();
            for(;rs.next();) {
                table.parseRecord(rs);
            }
            tables.add(table);
        } else {
            System.out.println(tableName + " not exists");
        }
    }

    public static void main(String[] args) throws SQLException, IOException {
       System.out.println(System.getProperty("user.home"));
    }
    public static void exe(String name, Properties properties) throws SQLException, IOException {
	    	
	    	String gitAddress = properties.getProperty("gitAddress");
		Properties properties1 = new Properties();
		properties1.load(new FileInputStream(new File(properties.getProperty("tuibiao_"+name)+"transfer.properties")));
	    	transfer(Conf.parseFrom(properties1,properties.getProperty("tuibiao_"+name)), gitAddress);
    }

    public static String getResource(String file) {
        if(new File(file).exists()) return file;
        URL resource = Transfer.class.getClassLoader().getResource(file);
        if(resource == null) {
            resource = Transfer.class.getResource(file);
        }
        return resource.getFile();
    }
    
}
