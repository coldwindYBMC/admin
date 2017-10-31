package cn.dao;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
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
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

import cn.model.ChangeData;
import cn.model.Column;
import cn.model.Config;
import cn.model.Line;
import cn.model.Record;
import cn.utils.WorkbookUtil;

public class DataOperation {
	private Config config;
	private Map<Integer, String> fieldsMap = new HashMap<Integer, String>(); //字段
	public Map<String, Column> columnList = new HashMap<String, Column>();	//列信息map
	private List<String> pri = new ArrayList<String>();			//在数据库中读取主键列表

	public DataOperation(Config config) {
		this.config = config;
	}

	@SuppressWarnings({ "unchecked", "serial", "rawtypes" })
	public final static Map map = new HashMap() {
		{
			put("t_s_droptemplate", new ArrayList<String>() {
				{
					add("dropid");
				}
			});
			put("t_s_activitysevengoaltemplate", new ArrayList<String>() {
				{
					add("step");
					add("day");
				}
			});
			put("t_s_cwautomonstertemplate", new ArrayList<String>() {
				{
					add("level");
				}
			});
			put("t_s_cwcavemonstergrouptemplate", new ArrayList<String>() {
				{
					add("groupId");
					add("playerLevel");
				}
			});
			put("t_s_cwbossrankingawardtemplate", new ArrayList<String>() {
				{
					add("BossId");
					add("RankInverval");
				}
			});
			put("t_s_cwminimapallianceinfo", new ArrayList<String>() {
				{
					add("Zone");
					add("CityCoord");
				}
			});
			put("t_s_npcarmyextendtemplate", new ArrayList<String>() {
				{
					add("NpcArmyExtendId");
					add("WorldLevel");
				}
			});
			put("t_s_npcarmynumtemplate", new ArrayList<String>() {
				{
					add("NpcArmyNumId");
					add("WorldLevel");
				}
			});

		}
	};
	public Map<String, List<Line>> readDBData(String tableName) throws Exception {
		Connection conn = connectSQL();
		Map<String, List<Line>> lineMap = new HashMap<String, List<Line>>();
		try {
			Statement stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery("select * from " + tableName);
			while (rs.next()) {
				Map<String, Record> recordMap = new HashMap<String, Record>();
				StringBuffer pris = new StringBuffer();
				for (Entry<Integer, String> entry : fieldsMap.entrySet()) {
					Record record = new Record(entry.getValue(), rs.getString(entry.getValue()));
					Column column1 = columnList.get(entry.getValue());
					record.setDefaultValue(column1.getDefaultValue());
					recordMap.put(entry.getValue().toLowerCase(), record);
					System.out.println("pri"+pri.size());
					pri.forEach(action1->System.out.println(action1));
					
					if (pri.contains(record.getFieldName())) {
						pris.append(record.getValue());
					}
				}
				Line line = new Line();
				line.setRecordMap(recordMap);
				line.setPri(pri);
				line.setTableName(tableName);
				if (lineMap.get(pris.toString()) == null) {
					List<Line> lineList = new ArrayList<Line>();
					lineMap.put(pris.toString(), lineList);
				}
				lineMap.get(pris.toString()).add(line);
			}
		} catch (Exception e1) {
			e1.printStackTrace();
			StringWriter sw = new StringWriter();
			e1.printStackTrace(new PrintWriter(sw, true));
			throw new Exception(sw.toString());
		} finally {
			closeConnection(conn);
		}
		return lineMap;
	}
	/**
	 * 读取excel数据
	 * @param changeData TODO
	 * 
	 * */
	@SuppressWarnings("deprecation")
	public Map<String, List<Line>> readExcelData(String tableName, Workbook workbook, int startLine,
			Map<String, List<Line>> excelData, ChangeData changeData) {
		try {
			fieldsMap.clear();
			Sheet sheet = workbook.getSheetAt(0);
			// get rows
			int rows = sheet.getPhysicalNumberOfRows();
			Iterator<Cell> fieldCells = sheet.getRow(0).cellIterator(); // 第0行为列名，需要跟数据库表字段对应
			List<String> DBColumn = selectTableColumn(tableName);//数据库字段名
			while (fieldCells.hasNext()) {
				Cell cell = fieldCells.next();
				if (cell.getCellTypeEnum() == CellType.BLANK) {
					continue;
				}
				//数据库不包含excel表中的某一列
				if (!DBColumn.contains(WorkbookUtil.getCellString(cell).trim().toLowerCase())) {//trim去掉字符串
					changeData.getUselessField().add(WorkbookUtil.getCellString(cell).trim().toLowerCase());
					continue;
				}	//列坐标，cell值->字段
//				System.out.println(cell.getColumnIndex());
//				System.out.println(WorkbookUtil.getCellString(cell).trim().toLowerCase());
				fieldsMap.put(cell.getColumnIndex(), WorkbookUtil.getCellString(cell).trim().toLowerCase());
			}
			for (int i = startLine - 1; i < rows; i++) {
				Map<String, Record> recordMap = new HashMap<String, Record>();
				StringBuffer pris = new StringBuffer();
				Row row = sheet.getRow(i); // 第i行的数据
				for (Entry<Integer, String> entry : fieldsMap.entrySet()) {
					Cell cell = null;
					try {
						cell = row.getCell(entry.getKey());//根据字段列读取 cell(对象)内容
						if(cell != null){
						//	System.out.println(WorkbookUtil.getCellString(cell).trim());
						}
					} catch (Exception ex) {
						System.out.println("该单元格获取不到!");
					}
					
					Record record = new Record(entry.getValue(),	//字段
							cell == null ? "" : WorkbookUtil.getCellString(cell).trim());//值
					Column column1 = columnList.get(record.getFieldName());//根据字段得到Column，Column是从数据库得到的
					if (column1 == null) {
//						sb.append("数据库字段和Excel中的对不上，大概率是大小写对不上!");
						System.out.println("数据库字段和Excel中的对不上，大概率是大小写对不上!");
					}
					record.setDefaultValue(column1.getDefaultValue());
					recordMap.put(record.getFieldName().toLowerCase(), record);
					if (pri.contains(entry.getValue())) {	//是主键
						pris.append(record.getValue());
					}
				}
				
				Line line = new Line();
				line.setRecordMap(recordMap);
				line.setPri(pri);
				line.setTableName(tableName);
				//recordMap.keySet().forEach(action -> System.out.println(action.toString()));
				//存储到excelData
				if (excelData.get(pris.toString()) == null) {	
					List<Line> lineList = new ArrayList<Line>();
					if ("".equals(pris.toString())) {
						System.out.println(pris.toString());
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

	@SuppressWarnings("deprecation")
	public List<String> readExcelAndGenerateSQL(String tableName, Workbook rwb, int startLine, boolean isUpdate,
			StringBuffer sb) {
		List<String> insertSqlList = new LinkedList<String>();
		try {
			Sheet sheet = rwb.getSheetAt(0);

			// get rows
			int rows = sheet.getPhysicalNumberOfRows();
			Map<Integer, String> fieldsMap = new HashMap<Integer, String>();
			Iterator<Cell> fieldCells = sheet.getRow(0).cellIterator(); // 第0行为列名，需要跟数据库表字段对应
			List<String> DBColumn = selectTableColumn(tableName);
			while (fieldCells.hasNext()) {
				Cell cell = fieldCells.next();
				if (cell.getCellTypeEnum() == CellType.BLANK) {
					continue;
				}
				if (!DBColumn.contains(WorkbookUtil.getCellString(cell).trim().toLowerCase())) {
					sb.append("<font size=\"6\">" + WorkbookUtil.getCellString(cell).trim().toLowerCase() + "列没有导入数据库中!"
							+ "</font>");
					continue;
				}
				fieldsMap.put(cell.getColumnIndex(), WorkbookUtil.getCellString(cell).trim());
			}
			for (int i = startLine - 1; i < rows; i++) {
				StringBuffer sql = new StringBuffer();
				List<Integer> Indexlist = new LinkedList<Integer>();
				List<String> dataList = new LinkedList<String>();
				Iterator<Cell> row = sheet.getRow(i).cellIterator(); // 第i行的数据
				while (row.hasNext()) {
					Cell cell = row.next();
					if ("".equals(WorkbookUtil.getCellString(cell))) {
						continue;
					}
					Indexlist.add(cell.getColumnIndex());
					dataList.add(WorkbookUtil.getCellString(cell).trim());
				}
				if (Indexlist.size() != 0) {
					parseSQLStr(tableName, fieldsMap, dataList, sql, Indexlist);
				}
				insertSqlList.add(sql.toString());
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return insertSqlList;
	}

	private void parseSQLStr(String tableName, Map<Integer, String> fieldsMap, List<String> dataList, StringBuffer sql,
			List<Integer> indexlist) {
		sql.append("insert into " + tableName + " (");
		List<Integer> list = new ArrayList<Integer>();
		for (int i = 0; i < indexlist.size(); i++) {
			if (fieldsMap.get(indexlist.get(i)) == null) {
				list.add(i);
				continue;
			}
			sql.append("`" + fieldsMap.get(indexlist.get(i)) + "`" + ",");
		}
		sql.delete(sql.length() - 1, sql.length());
		sql.append(") values(");
		for (int i = 0; i < dataList.size(); i++) {
			if (list.contains(i)) {
				continue;
			}
			sql.append("'" + dataList.get(i) + "',");
		}
		sql.delete(sql.length() - 1, sql.length());
		sql.append(")");
	}

	/**
	 * 清空表中的数据
	 * 
	 * @param tableName
	 * @return
	 */
	public int truncateTable(String tableName) {
		int s = excute1("select count(*) from " + tableName);
		try {
			String sql = "delete from " + tableName;
			excute(sql);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return s;
	}

	public List<String> selectTableColumn(String tableName) {
		Connection conn = connectSQL();
		List<String> column = new ArrayList<String>();
		try {
			Statement stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery("select * from " + tableName);
			ResultSetMetaData data = rs.getMetaData();
			for (int j = 1; j <= data.getColumnCount(); j++) {
				// 获得指定列的列名
				column.add(data.getColumnName(j).toLowerCase());
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		} finally {
			closeConnection(conn);
		}
		return column;
	}

	@SuppressWarnings("unchecked")
	public void selectTablePri(String tableName) {

		Connection conn = connectSQL();
		try {
			Statement stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery("desc " + tableName);	//降序
			// pri.clear();
			while (rs.next()) {
				Column column = new Column();
				column.setType(rs.getString(2));
				column.setIsnull(rs.getString(3));
				column.setKey(rs.getString(4));
				
				column.setDefaultValue(rs.getString(5));
				column.setExtra(rs.getString(6));	
				if ("PRI".equals(rs.getString(4))) {	
					pri.add(rs.getString(1).toLowerCase());		//设置主键
				}
				columnList.put(rs.getString(1).toLowerCase(), column);// 字段名，列号
			}
			if (map.containsKey(tableName)) {
				pri = (List<String>) map.get(tableName);
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		} finally {

			closeConnection(conn);
		}

	}

	private int excute1(String sql) {
		Connection conn = connectSQL();
		String pass = null;
		try {
			Statement stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery(sql);
			while (rs.next()) {
				pass = rs.getString(1); // 此方法比较高�?
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		} finally {

			closeConnection(conn);
		}
		return Integer.valueOf(pass);
	}

	public Map<Integer, String> excuteReturn(String sql) {
		Connection conn = connectSQL();
		try {
			Map<Integer, String> map = new HashMap<Integer, String>();
			Statement stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery(sql);
			while (rs.next()) {
				map.put(Integer.valueOf(rs.getString(1)), rs.getString(2));
			}
			return map;
		} catch (Exception ex) {
			ex.printStackTrace();
		} finally {

			closeConnection(conn);
		}
		return null;
	}

	private void excute(String sql) {

		Connection conn = connectSQL();
		try {
			java.sql.PreparedStatement stat = conn.prepareStatement(sql);
			stat.execute();
		} catch (Exception ex) {
			ex.printStackTrace();
		} finally {

			closeConnection(conn);
		}
	}

	public int excute(List<String> sqls) throws Exception {
		Connection conn = connectSQL();

		try {
			conn.setAutoCommit(false);
			int i = 0;
			System.out.println("开始");
			long time = System.currentTimeMillis();
			long time1 = System.currentTimeMillis();
			Statement statement = conn.createStatement();
			for (String sql : sqls) {
				if (sql != null && !"".equals(sql)) {
					i++;
					statement.addBatch(sql);
					if ((i + 1) % 1000 == 0) {
						statement.executeBatch();
						statement.clearBatch();
						conn.commit();
						System.out.println("执行了" + i + "条,耗时" + (System.currentTimeMillis() - time1) + ",共耗时："
								+ (System.currentTimeMillis() - time));
						time1 = System.currentTimeMillis();
					}
				}
			}
			statement.executeBatch();
			System.out.println("执行结束 耗时" + (System.currentTimeMillis() - time1));
			conn.commit();
			return i;
		} catch (SQLException ee) {
			ee.printStackTrace();
			StringWriter sw = new StringWriter();
			ee.printStackTrace(new PrintWriter(sw, true));
			throw new Exception(sw.toString());
		} finally {
			closeConnection(conn);
		}
	}

	public int excuteOne(List<String> sqls) throws Exception {
		Connection conn = connectSQL();

		try {
			conn.setAutoCommit(false);
			int i = 0;
			for (String sql : sqls) {
				if (sql != null && !"".equals(sql)) {
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
		} finally {
			closeConnection(conn);
		}
	}

	/**
	 * 连接SQL
	 * 
	 * @param sb
	 * 
	 * @return
	 */
	public Connection connectSQL() {
		String dbName = "jdbc:mysql://" + config.db.host + ":3306" + "/" + config.db.name
				+ "?useUnicode=true&characterEncoding=utf-8";
		try {
			Class.forName("com.mysql.jdbc.Driver").newInstance();
		} catch (Exception ex) {
			ex.printStackTrace();
			System.out.println("载入MySQL数据库驱动时出错");
		}
		Connection conn = null;
		try {
			conn = java.sql.DriverManager.getConnection(dbName, config.db.user, config.db.pass);
		} catch (Exception ex) {
			ex.printStackTrace();
			System.out.println("连接到MySQL数据库时出错");
		}
		return conn;
	}

	/**
	 * 关闭连接
	 * 
	 * @param connection
	 */
	public void closeConnection(Connection connection) {
		// 关闭连接
		try {
			connection.close();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	@SuppressWarnings("resource")
	public int importcampaignbossnode() {
		InputStream is = null;
		try {
			is = new FileInputStream(new File(config.excelDirectoty, "t_s_campaignbossnode" + ".xls"));
			Workbook rwb = new HSSFWorkbook(is);
			Sheet sheet = rwb.getSheetAt(0);

			// get rows
			int rows = sheet.getPhysicalNumberOfRows();

			Row fieldCells = sheet.getRow(0); // �?0行为列名，需要跟数据库表字段�?�?

			List<String> insertSqlList = new LinkedList<String>();
			for (int i = 1; i < rows; i++) {
				String s;
				Row cellData = sheet.getRow(i); // 第i行的数据
				if (isData("t_s_campaignnode", WorkbookUtil.getCellString(fieldCells.getCell(0)),
						WorkbookUtil.getCellString(cellData.getCell(0)))) {
					s = "update t_s_campaignnode set ";
					for (int j = 1; j < 6; j++) {
						if (cellData.getCell(j) == null) {
							continue;
						}
						s = s + WorkbookUtil.getCellString(fieldCells.getCell(j)) + "='"
								+ WorkbookUtil.getCellString(cellData.getCell(j)) + "',";
					}
					s = s.substring(0, s.length() - 1);
					s = s + " where " + fieldCells.getCell(0) + "=" + WorkbookUtil.getCellString(cellData.getCell(0));
					insertSqlList.add(s);
				}
			}
			int i = excute(insertSqlList);
			return i;
		} catch (Exception ex) {
			ex.printStackTrace();
		} finally {
			if (is != null) {
				try {
					is.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return 0;
	}

	private boolean isData(String string, String contents, String contents2) {
		Connection conn = connectSQL();
		try {
			Statement stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery("select * from " + string + " where " + contents + "=" + contents2);
			;
			if (rs.next()) {
				return true; // 此方法比较高�?
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		} finally {

			closeConnection(conn);
		}
		return false;
	}

	@SuppressWarnings("resource")
	public Map<String, Row> getExcelDate(String string, int sheetPage) {
		InputStream is = null;
		Map<String, Row> list = new HashMap<String, Row>();
		try {
			is = new FileInputStream(new File(config.excelDirectoty, string + ".xls"));
			Workbook rwb = new HSSFWorkbook(is);
			Sheet sheet = rwb.getSheetAt(sheetPage);
			// get rows
			int rows = sheet.getPhysicalNumberOfRows();
			for (int i = 0; i < rows; i++) {
				Row row = sheet.getRow(i); // 第i行的数据
				if (row.getCell(0) == null) {
					continue;
				}
				list.put(WorkbookUtil.getCellString(row.getCell(0)).trim(), row);

			}

			return list;
		} catch (Exception ex) {
			ex.printStackTrace();
		} finally {
			if (is != null) {
				try {
					is.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return null;
	}

	@SuppressWarnings("resource")
	public Map<String, Row> getExcelDate(Map<String, Row> excel3, String string) {
		InputStream is = null;
		try {
			is = new FileInputStream(new File(config.excelDirectoty, string + ".xls"));
			Workbook rwb = new HSSFWorkbook(is);
			Sheet sheet = rwb.getSheetAt(0);
			// get rows
			int rows = sheet.getPhysicalNumberOfRows();
			for (int i = 0; i < rows; i++) {
				Row cellData = sheet.getRow(i); // 第i行的数据
				excel3.put(WorkbookUtil.getCellString(cellData.getCell(0)).trim(), cellData);
			}
			return excel3;
		} catch (Exception ex) {
			ex.printStackTrace();
		} finally {
			if (is != null) {
				try {
					is.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return null;
	}

	@SuppressWarnings({ "resource" })
	public Map<String, List<Row>> getExcelDateToQuest(String string, int sheetPage) {
		InputStream is = null;
		Map<String, List<Row>> list = new HashMap<String, List<Row>>();
		try {
			is = new FileInputStream(new File(config.excelDirectoty, string + ".xls"));
			Workbook rwb = new HSSFWorkbook(is);
			Sheet sheet = rwb.getSheetAt(0);
			// get rows
			int rows = sheet.getPhysicalNumberOfRows();
			int RequireLevel = 0;
			RequireLevel = getRequireLevel(sheet.getRow(0), "RequireLevel");
			for (int i = 0; i < rows; i++) {
				Row row = sheet.getRow(i); // 第i行的数据
				if (row.getCell(RequireLevel) == null) {
					continue;
				}
				if (list.get(WorkbookUtil.getCellString(row.getCell(RequireLevel)).trim()) == null) {
					List<Row> rowlist = new ArrayList<Row>();
					list.put(WorkbookUtil.getCellString(row.getCell(RequireLevel)).trim(), rowlist);
				}
				list.get(WorkbookUtil.getCellString(row.getCell(RequireLevel)).trim()).add(row);
			}

			return list;
		} catch (Exception ex) {
			ex.printStackTrace();
		} finally {
			if (is != null) {
				try {
					is.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return null;
	}

	public int getRequireLevel(Row row, String name) {
		int index = 0;
		Iterator<Cell> cells1 = row.cellIterator();
		while (cells1.hasNext()) {
			Cell cell = cells1.next();
			if (name.equals(WorkbookUtil.getCellString(cell))) {
				index = cell.getColumnIndex();
				break;
			}
		}
		return index;
	}

	public Map<String, List<Line>> readCsvData(String tableName, List<List<String>> list, int startLine,
			Map<String, List<Line>> excelData) {
		try {
			// get rows
			Map<Integer, String> fieldsMap = new HashMap<Integer, String>();
			List<String> DBColumn = selectTableColumn(tableName);	//数据库列名
			List<String> column = list.get(0);
			for (int i = 0; i < column.size(); i++) {
				if (DBColumn.contains(column.get(i).toLowerCase())) {
					fieldsMap.put(i, column.get(i));
				}
			}
			for (int i = startLine - 1; i < list.size(); i++) {
				Map<String, Record> recordMap = new HashMap<String, Record>();
				StringBuffer pris = new StringBuffer();
				List<String> row = list.get(i);
				for (Entry<Integer, String> entry : fieldsMap.entrySet()) {
					Record record = new Record(entry.getValue(), row.get(entry.getKey()));
					Column column1 = columnList.get(record.getFieldName());
					record.setDefaultValue(column1.getDefaultValue());
					recordMap.put(record.getFieldName(), record);
					if (pri.contains(entry.getValue())) {
						pris.append(record.getValue());
					}
				}
				Line line = new Line();
				line.setRecordMap(recordMap);
				line.setPri(pri);
				line.setTableName(tableName);
				if (excelData.get(pris.toString()) == null) {
					List<Line> lineList = new ArrayList<Line>();
					if ("".equals(pris.toString())) {
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
}