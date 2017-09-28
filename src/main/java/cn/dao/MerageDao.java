package cn.dao;


import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

import cn.model.Config;
import cn.model.Line;
import cn.model.Record;
import cn.utils.WorkbookUtil;



public class MerageDao {
	private Config config;
	private List<String> pri=new ArrayList<String>();
	private List<String> mainField=new ArrayList<String>();
	public MerageDao(Config config) {
		this.config = config;
	}
	 @SuppressWarnings({ "unchecked", "serial", "rawtypes" })
	public final static Map map = new HashMap() {{    
		    put("t_s_droptemplate", new ArrayList<String>(){{add("DropId");}});    
		    put("t_s_activitysevengoaltemplate", new ArrayList<String>(){{add("Step");add("Day");}});    
		    put("t_s_cwautomonstertemplate", new ArrayList<String>(){{add("Level");}});    
		    put("t_s_cwcavemonstergrouptemplate", new ArrayList<String>(){{add("GroupId");add("PlayerLevel");}});    
		    put("t_s_cwbossrankingawardtemplate", new ArrayList<String>(){{add("BossId");add("RankInverval");}});    
		    put("t_s_cwminimapallianceinfo", new ArrayList<String>(){{add("Zone");add("CityCoord");}});    
		    put("t_s_npcarmyextendtemplate", new ArrayList<String>(){{add("NpcArmyExtendId");add("WorldLevel");}});    
		    put("t_s_npcarmynumtemplate", new ArrayList<String>(){{add("NpcArmyNumId");add("WorldLevel");}});    
		      
		}}; 
	
	@SuppressWarnings("deprecation")
	public Map<String,List<Line>> readExcelData(String tableName, Workbook workbook,Map<String, List<Line>> excelData) {
		try {
			Sheet sheet=workbook.getSheetAt(0);
			// get rows
			int rows = sheet.getPhysicalNumberOfRows();
			Map<Integer ,String> fieldsMap = new HashMap<Integer, String>();
			Iterator<Cell> fieldCells = sheet.getRow(0).cellIterator(); // 
			List<String> DBColumn=selectTableColumn(tableName);
			while(fieldCells.hasNext()){
				Cell cell=fieldCells.next();
				if(cell.getCellTypeEnum()==CellType.BLANK){
					continue;
				}
				if(!DBColumn.contains(WorkbookUtil.WorkString(cell).trim().toLowerCase())){
					continue;
				}
				fieldsMap.put(cell.getColumnIndex(), WorkbookUtil.WorkString(cell).trim());
			}
			for (int i = 1; i < rows; i++) {
				Map<String,Record> recordMap=new HashMap<String,Record>();
				StringBuffer pris=new StringBuffer();
				Row row = sheet.getRow(i);
				for(Entry<Integer,String> entry:fieldsMap.entrySet()){
					boolean bool=false;
					Cell cell=null;
					try{
						cell=row.getCell(entry.getKey());
						if(cell.getCellTypeEnum()==CellType.FORMULA){
							bool=true;
						}
					}catch(Exception ex){
						System.out.println("绌烘牸"+entry.getValue()+row.getRowNum());
					}
					Record record=new Record(entry.getValue(),cell==null?"":WorkbookUtil.WorkString(cell).trim(),bool);
					recordMap.put(record.getFieldName(),record);
					if(pri.contains(entry.getValue())){
						pris.append(record.getValue());
					}
				}
				Line line=new Line();
				line.setRecordMap(recordMap);
				line.setPri(pri);
				line.setTableName(tableName);
				if(excelData.get(pris.toString())==null){
					List<Line> lineList=new ArrayList<Line>();
					if("".equals(pris.toString())){
						continue;
					}
					excelData.put(pris.toString(), lineList);
				}
				excelData.get(pris.toString()).add(line);
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		} 
		return excelData;
	}
	
	
	public List<String> selectTableColumn(String tableName) {
		Connection conn = connectSQL();
		List<String> column=new ArrayList<String>();
		try {
			Statement stmt = conn.createStatement() ;
			ResultSet rs = stmt.executeQuery("select * from "+tableName) ;
			ResultSetMetaData data = rs.getMetaData(); 
			for (int j = 1; j <= data.getColumnCount(); j++) {
				column.add(data.getColumnName(j).toLowerCase());
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}finally {
			
			closeConnection(conn);
		}
		return column;
	}
	@SuppressWarnings("unchecked")
	public void selectTablePri(String tableName) {
		
		Connection conn = connectSQL();
		try {
			Statement stmt = conn.createStatement() ;
			ResultSet rs = stmt.executeQuery("desc "+tableName) ;
			
			while(rs.next()){
				if("PRI".equals(rs.getString(4))){
					pri.add(rs.getString(1));
				}
			}
			if(map.containsKey(tableName)){
				pri=(List<String>) map.get(tableName);
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}finally {
			
			closeConnection(conn);
		}
		
	}

	@SuppressWarnings("unused")
	private int excute1(String sql) {
		Connection conn = connectSQL();
		String pass=null;
		try {
			Statement stmt = conn.createStatement() ;
			ResultSet rs = stmt.executeQuery(sql) ;
			while(rs.next()){   
		     pass = rs.getString(1) ;
		     }    
		} catch (Exception ex) {
			ex.printStackTrace();
		}finally {
			
			closeConnection(conn);
		}
		return Integer.valueOf(pass);
	}

	public int excute(List<String> sqls)  throws Exception{
		Connection conn = connectSQL();
		
			try {
				conn.setAutoCommit(false);
				int i = 0;
				System.out.println("寮?濮?");
				long time=System.currentTimeMillis();
				long time1=System.currentTimeMillis();
				Statement statement = conn.createStatement();
				for (String sql : sqls) {
					if(sql!=null&&!"".equals(sql)){
						i++;
						statement.addBatch(sql);
						if((i+1)%1000==0){
							statement.executeBatch();
							statement.clearBatch();
							conn.commit();
							System.out.println("鎵ц浜?"+i+"鏉?,鑰楁椂"+(System.currentTimeMillis()-time1)+",鍏辫?楁椂锛?"+(System.currentTimeMillis()-time));
							time1=System.currentTimeMillis();
						}
					}
				}
			    statement.executeBatch();
				System.out.println("鎵ц缁撴潫 鑰楁椂"+(System.currentTimeMillis()-time1));
				conn.commit();
				return i;
			} catch (SQLException ee) {
				ee.printStackTrace();
				StringWriter sw = new StringWriter();
				ee.printStackTrace(new PrintWriter(sw, true));
				throw new Exception(sw.toString());
			}finally {
				closeConnection(conn);
			}
	}
	
	public int excuteOne(List<String> sqls)  throws Exception{
		Connection conn = connectSQL();
		
		try {
			conn.setAutoCommit(false);
			int i = 0;
			for (String sql : sqls) {
				if(sql!=null&&!"".equals(sql)){
					i++;
					System.out.println("----" + i + "-----------" + sql);
					java.sql.PreparedStatement stat = conn.prepareStatement(sql);
					stat.execute();
				}
			}
			conn.commit();
			return i;
		} catch (SQLException e) {
			e.printStackTrace();
			StringWriter sw = new StringWriter();
            e.printStackTrace(new PrintWriter(sw, true));
			throw new Exception(sw.toString());
		}finally {
			closeConnection(conn);
		}
	}

	
	/**
	 * 
	 * 
	 * @return
	 */
	public Connection connectSQL() {
		String dbName = "jdbc:mysql://" + config.db.host +":3306"+ "/" + config.db.name + "?useUnicode=true&characterEncoding=utf-8";
		try {
			Class.forName("com.mysql.jdbc.Driver").newInstance();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		Connection conn = null;
		try {
			conn = java.sql.DriverManager.getConnection(dbName, config.db.user, config.db.pass);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return conn;
	}

	/**
	 *
	 * 
	 * @param connection
	 */
	public void closeConnection(Connection connection) {
		// 
		try {
			connection.close();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}


	@SuppressWarnings("deprecation")
	public void writeExcel(Map<String, List<Line>> zhubiaoMap) throws Exception {
		creatExcel();
		OutputStream out = null; 
		File file=new File("D:/temp/merge.xls");
		Workbook workbook=(Workbook) WorkbookUtil.creatWorkbookFile(file);
		if(workbook != null){
			System.out.println("workbook");
		}
		Sheet sheet = workbook.createSheet();
//		Sheet sheet = workbook.getSheetAt(0);
		out =  new FileOutputStream("D:/temp/merge.xls");  
		workbook.write(out);
		CellStyle cellStyle=workbook.createCellStyle();
		cellStyle.setFillForegroundColor(IndexedColors.YELLOW.getIndex());
		cellStyle.setFillPattern(CellStyle.SOLID_FOREGROUND);
		Font font = workbook.createFont();
		font.setColor(HSSFColor.RED.index);
		cellStyle.setFont(font);
		CellStyle cellStyle1=workbook.createCellStyle();
		cellStyle1.setFillForegroundColor(IndexedColors.YELLOW.getIndex());
		cellStyle1.setFillPattern(CellStyle.SOLID_FOREGROUND);
		CellStyle cellStyle2=workbook.createCellStyle();
		cellStyle2.setFillForegroundColor(IndexedColors.ORANGE.getIndex());
		cellStyle2.setFillPattern(CellStyle.SOLID_FOREGROUND);
		
		int i=1;
		List<String> formula=new ArrayList<>();
		for(Entry<String,List<Line>> entry:zhubiaoMap.entrySet()){
        	for(int j=0;j<entry.getValue().size();j++){
        		Row row=sheet.createRow(i);
        		boolean bool=false;
        		for(int k=0;k<mainField.size();k++){
        			if(entry.getValue().get(j).isChange()){
        				bool=true;
        				break;
        			}
        			if(entry.getValue().get(j).getRecordMap().get(mainField.get(k)).isIdent()){
        				bool=true;
        				break;
        			}
        		}
        		for(int k=0;k<mainField.size();k++){
        			Cell cell=row.createCell(k);
        			if(entry.getValue().get(j).getRecordMap().get(mainField.get(k)).isFormula()){
        				System.out.println(entry.getValue().get(j).getRecordMap().get(mainField.get(k)).getValue());
        				try{
        					cell.setCellFormula(entry.getValue().get(j).getRecordMap().get(mainField.get(k)).getValue());
        				}catch(Exception e){
        					cell.setCellValue("="+entry.getValue().get(j).getRecordMap().get(mainField.get(k)).getValue());
        					cell.setCellStyle(cellStyle2);
        					if(!formula.contains(mainField.get(k))) {
        						formula.add(mainField.get(k));
        					}
        				}
        				
        			}else{
        				cell.setCellValue(entry.getValue().get(j).getRecordMap().get(mainField.get(k)).getValue());
        			}
        			if(bool){
        				if(entry.getValue().get(j).isChange()){
        					cell.setCellStyle(cellStyle);
        					continue;
        				}
        				if(entry.getValue().get(j).getRecordMap().get(mainField.get(k)).isIdent()){
        					cell.setCellStyle(cellStyle);
        				}else{
        					cell.setCellStyle(cellStyle1);
        				}
        			}
        		}
        		i++;
        	}
        }
		Row row0=sheet.createRow(0);
		for(int m=0;m<mainField.size();m++){
			Cell cell = row0.createCell(m);
			cell.setCellValue(mainField.get(m));
			if(formula.contains(mainField.get(m))){
				cell.setCellStyle(cellStyle2);
			}
		}
		// 创建文件输出流，准备输出电子表格：这个必须有，否则你在sheet上做的任何操作都不会有效  
        out =  new FileOutputStream("D:/temp/merge.xls");  
        workbook.write(out);
	}
	@SuppressWarnings("resource")
	private void creatExcel(){
		File file=new File("D:/temp", "merge" + ".xls");
	    FileOutputStream fOut = null;
	    HSSFWorkbook workbook = new HSSFWorkbook(); 
		// 工作薄建立完成，下面将工作薄存入文件  
        // 新建一输出文件流  
        try {
			fOut = new FileOutputStream(file);
			// 把相应的Excel 工作簿存盘  
			workbook.write(fOut);  
			fOut.flush();  
			// 操作结束，关闭文件  
			fOut.close();  
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}  finally {  
	        if (fOut != null) {  
	            try {  
	                fOut.close();  
	            } catch (IOException e1) {  
	            }  
	        }  
	    }  
	}


	@SuppressWarnings("deprecation")
	public void selectMainField(String tableName, Workbook workbook, Map<String, List<Line>> zhubiaoMap) {
		mainField.clear();
		Sheet sheet=workbook.getSheetAt(0);
		// get rows
		Iterator<Cell> fieldCells = sheet.getRow(0).cellIterator(); // 
		List<String> DBColumn=selectTableColumn(tableName);
		while(fieldCells.hasNext()){
			Cell cell=fieldCells.next();
			if(cell.getCellTypeEnum()==CellType.BLANK){
				continue;
			}
			if(!DBColumn.contains(WorkbookUtil.WorkString(cell).trim().toLowerCase())){
				continue;
			}
			mainField.add(WorkbookUtil.WorkString(cell).trim());
		}
	}
	
	@SuppressWarnings("deprecation")
	public static void main(String[] args) {
		File file=new File("D:/temp", "merge" + ".xls");
		Workbook workbook=(Workbook) WorkbookUtil.creatWorkbookFile(file);
		Sheet sheet=workbook.getSheetAt(0);
		Row row = sheet.getRow(0);
		for(int i=0;i<3;i++){
			Cell cell=row.getCell(i);
			if(cell.getCellTypeEnum()==CellType.FORMULA){
				System.out.println("formula------"+cell.getCellFormula());
			}else{
				cell.setCellType(CellType.STRING);
				System.out.println(cell.getStringCellValue());
			}
		}
		WorkbookUtil.close();
	}
}