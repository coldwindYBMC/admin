package cn.model;

import java.util.Properties;

/**
 * @author xuqin
 * 
 */
public class Config {
    
	public final String excelDirectoty;	// excel目录库 ->svn目录
	public final DBConf db;
	
	public Config(Properties properties,String resoure, String excel) {
		this.excelDirectoty = properties.getProperty("exceldirectory"+excel);
		this.db = parse(properties,resoure);
	}
	public Config() {
		this.excelDirectoty = "C:/Users/Administrator/Desktop/test";
		this.db = parseTest();
	}
	public Config(String dbname){
		this.excelDirectoty = "C:/Users/Administrator/Desktop/20";
		this.db = parseDBname(dbname);
	}
    private DBConf parseDBname(String dbname) {
    	return new DBConf(
                "192.168.1.121",
                3306,
                dbname,
                "root",
                "sangoroot!@#",
                1, 1);
	}
	private DBConf parse(Properties properties,String resoure) {
        return new DBConf(
                properties.getProperty("templatesdb.host"+resoure),			//远端主机
                Integer.parseInt(properties.getProperty("templatesdb.port")),//远端端口
                properties.getProperty("templatesdb.name"+resoure),		//远端模板数据库名
                properties.getProperty("templatesdb.user"),			//用户名
                properties.getProperty("templatesdb.password"),		//密码
                1, 1);
    }
    private DBConf parseTest() {
        return new DBConf(
                "192.168.150.146",
                3306,
                "aa",
                "root",
                "root",
                1, 1);
    }
}
