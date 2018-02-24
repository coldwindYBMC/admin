package cn.controller;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import cn.dao.DataOperation;
import cn.model.Config;
import cn.utils.utils;

/**
 * 1.根据excel表，清空数据库
 * exceldirectory6 = C:/Users/Administrator/Desktop/test
 * templatesdb.name10=db_templates_test
 * 繁体
 * exceldirectory11 = C:/Users/Administrator/Desktop/foreignT
 * templatesdb.name14=db_templates_os_t_1.5.4
 * 简体
 * exceldirectory9 = C:/Users/Administrator/Desktop/foreign
 * templatesdb.name13=db_templates_os_1.5.4
 * 2.對比excel和数据库是否一致
 * 3.对比，数据库和表的字段缺少
 **/
public class CleanDB {
	private static String excelDir = "11";
	private static String DBlDir = "14";
	static Config config = null;
	static Properties properties;
	static List<File> fileList;
	static CompareExcelAndDB compareExcelAndDB;
	static Statement stmt;
	static Connection conn;
	public static void main(String args[]){
		init();
		fileList = getExcelList();
		
		//对比字段的缺少
		//compareExcelAndDB.cpmareField(fileList);
		//对比数据库
		compareExcelAndDB.readExcelAndDB(fileList);
		
		 //TODO  清除數據庫，沒事不要開。
		//claen(fileList);
		//		
	}
	
	
	
	private static void claen(List<File> list) {
		list.forEach(f->{
			StringBuffer sql = new StringBuffer().append("delete from ").append(f.getName().substring(0,f.getName().lastIndexOf(".")));
			 try {
				stmt.executeUpdate(sql.toString());
				System.out.println(sql.toString());
			} catch (Exception e) {
				e.printStackTrace();
			}//执行sql语句
		});
	}

	private static void init(){
		try {
			properties = utils.loadProperties("dbconf.properties");
			compareExcelAndDB = new CompareExcelAndDB();
		} catch (IOException e) {
			e.printStackTrace();
		}
		config = new Config(properties,DBlDir,  excelDir);
		 conn = DataOperation.connectSQL(config);//根据配置链接数据库
		try {
			stmt = conn.createStatement();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	//得到excel列表
	private static List<File> getExcelList(){
		String s = properties.getProperty("exceldirectory"+excelDir);
		System.out.println(s);
		return  getDirectory(new File(s));
	}
	

	
	//得到excel表全部.xls .xlsx文件
	public static List<File> getDirectory(File file) {
		List<File> list = new ArrayList<>();
		File flist[] = file.listFiles();
		if (flist == null || flist.length == 0) {
			return null;
		}
		for (File f : flist) {
			if (f.isDirectory()) {
				getDirectory(f);
			} else if (f.isFile() && f.getName().matches("^.+\\.(?i)((xls)|(xlsx))$")) {
//				if(f.getName().indexOf("t_s_livingtemplate") != -1)
					list.add(f);				
			}
		}
		return list;
	}
		
}
