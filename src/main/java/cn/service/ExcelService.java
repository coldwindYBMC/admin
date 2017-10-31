package cn.service;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.stereotype.Service;

import cn.dao.DataOperation;
import cn.model.ChangeData;
import cn.model.ChangeResource;
import cn.model.Config;
import cn.model.Ident;
import cn.model.Line;
import cn.model.Suffix;
import cn.utils.WorkbookUtil;
import cn.utils.utils;

@Service
public class ExcelService {

	private boolean isDelete;

	public boolean isDelete() {
		return isDelete;
	}

	public void setDelete(boolean isDelete) {
		this.isDelete = isDelete;
	}

	/**
	 * @param excelNames 表名 内容
	 **/

	public List<ChangeData> converter(Config config, List<String> excelNames, boolean bool,ChangeResource changeResource ) {
		try {
			if (!utils.svnup(config)) {
				List<ChangeData> listData = new ArrayList<>();
				listData.add(new ChangeData("all table", "SVN更新失败!"));
				return listData;
			}
		} catch (IOException e) {
			System.out.println("svn更新报错！");
		}
		return start(config, excelNames, bool, changeResource );
	}

	@SuppressWarnings("unchecked")
	public List<ChangeData> start(Config config, Object excels, boolean bool,ChangeResource changeResource ) {
		this.isDelete = bool;
		List<String> excel = (List<String>) excels;
		return importExcelToSQL(config, excel, changeResource );
	}

	/**
	 * @param excels 表名 内容
	 **/
	private List<ChangeData> importExcelToSQL(Config config, List<String> excels,ChangeResource changeResource ) {
		DataOperation dataOperation = new DataOperation(config);
		List<ChangeData> listData = new ArrayList<>();
		for (String lineStr : excels) {
			int row = 2;
			if (lineStr.split(":").length > 1) {
				row = Integer.valueOf(lineStr.split(":")[1]);
			}
			String excelName = lineStr.split(":")[0];

			String tableName = "";
			if (excelName.indexOf("t_s_droptemplate") != -1 || excelName.indexOf("t_s_droptemplate2") != -1|| excelName.indexOf("t_s_droptemplate3") != -1){
				tableName = "t_s_droptemplate";
			} else {
				tableName = excelName;
			}
			try {
				Object workbook = WorkbookUtil.creatWorkbook(config.excelDirectoty, excelName);
				List<Object> workbookList = new ArrayList<Object>();
				workbookList.add(workbook);
				if (DataOperation.map.containsKey(tableName)) {
					listData.add(excelUpdateDBNoKey(dataOperation, row, tableName, workbookList, 
							WorkbookUtil.findSuffix(config.excelDirectoty, excelName)));//后缀
				} else {
					listData.add(excelUpdateDB(dataOperation, row, tableName, workbookList,
							WorkbookUtil.findSuffix(config.excelDirectoty, excelName)));
				}
			} catch (Exception e) {
				e.printStackTrace();
				StringWriter sw = new StringWriter();
				e.printStackTrace(new PrintWriter(sw, true));
				String s = sw.toString().substring(0, 2000);
				listData.add(new ChangeData(tableName, s));
				changeResource.setList(listData);
			} finally {
				WorkbookUtil.close();
			}
		}
		return listData;
	}
	
	/**
	 * @param DataOperation 数据操作
	 * @param startLine row开始行数
	 * @param workbookList excel工作对象列表
	 * @param sb	记录信息
	 * @param suffix 后缀名字
	 * */
	@SuppressWarnings("unchecked")
	private ChangeData excelUpdateDBNoKey(DataOperation dataOperation, int startLine, String Name, List<Object> workbookList,
			 Suffix suffix) throws Exception {
		ChangeData changeData = new ChangeData();
		changeData.setTableName(Name);
		
		String tableName = Name.split("@")[0];		//
		changeData.addTitle(dataOperation.selectTableColumn(tableName));
		List<Line> diffLine = new ArrayList<Line>();
		Map<String, List<Line>> excelData = new HashMap<String, List<Line>>();
		dataOperation.selectTablePri(tableName);
		for (int i = 0; i < workbookList.size(); i++) {
			if (suffix == Suffix.csv) {
				dataOperation.readCsvData(tableName, (List<List<String>>) workbookList.get(i), startLine, excelData);
				System.out.println("获取到Csv数据");
			} else {
				dataOperation.readExcelData(tableName, (Workbook) workbookList.get(i), startLine, excelData,changeData);
			}  
		}
		Map<String, List<Line>> DBData = dataOperation.readDBData(tableName);
		if (this.isDelete) {
			for (Entry<String, List<Line>> entry : DBData.entrySet()) {
				if (!excelData.containsKey(entry.getKey())) {
					entry.getValue().get(0).setIdent(Ident.DELECT);
					diffLine.add(entry.getValue().get(0));
					entry.getValue().stream().forEach(line->changeData.addChangeLine(line, null, Ident.DELECT));
				}
			} 
		}
		for (Entry<String, List<Line>> entry : excelData.entrySet()) {
			if (!DBData.containsKey(entry.getKey())) {
				if ("".equals(entry.getKey())) {
					continue;
				}
				for (int i = 0; i < entry.getValue().size(); i++) {
					entry.getValue().get(i).setIdent(Ident.INSERT);
					diffLine.add(entry.getValue().get(i));
					changeData.addChangeLine(null, entry.getValue().get(i), Ident.INSERT);
				}
			} else {
				for (int i = 0; i < entry.getValue().size(); i++) {
					if (!entry.getValue().get(i).equalList(DBData.get(entry.getKey()))) {
						Line line = entry.getValue().get(i).clon();
						line.setIdent(Ident.DELECT);
						diffLine.add(line);
						entry.getValue().stream().forEach(line1->changeData.addChangeLine(line1, null, Ident.DELECT));
						for (int j = 0; j < entry.getValue().size(); j++) {
							entry.getValue().get(j).setIdent(Ident.INSERT);
							diffLine.add(entry.getValue().get(j));
							changeData.addChangeLine(null, entry.getValue().get(j), Ident.INSERT);
						}
						break;
					}
				}
			}
		}
		List<String> sqlList = generateSql(diffLine);
		// 执行插入语句
		int m = dataOperation.excute(sqlList);
		System.out.println("import " + tableName + " success");
		return changeData;
	}

	@SuppressWarnings("unchecked")
	private ChangeData excelUpdateDB(DataOperation dataOperation, int startLine, String Name, List<Object> workbookList,
			 Suffix suffix) throws Exception {
		ChangeData changeData = new ChangeData();
		changeData.setTableName(Name);
		// 生成sql语句
		String tableName = Name.split("@")[0];
		Map<String, List<Line>> excelData = new HashMap<String, List<Line>>();//主键，行信息
		dataOperation.selectTablePri(tableName);//在数据库中找主键
		changeData.addTitle(dataOperation.selectTableColumn(tableName));
		for (int i = 0; i < workbookList.size(); i++) {//循环excel表
			if (suffix == Suffix.csv) {
				dataOperation.readCsvData(tableName, (List<List<String>>) workbookList.get(i), startLine, excelData);
				System.out.println("获取到Csv数据");
			} else {
				dataOperation.readExcelData(tableName, (Workbook) workbookList.get(i), startLine, excelData, changeData);
			}
		}
		// 字段，record信息
		Map<String, List<Line>> DBData = dataOperation.readDBData(tableName);
		List<Line> diffLine = dataCompare(excelData, DBData, changeData); //对比数据库和excel信息，设置操作等
		List<String> sqlList = generateSql(diffLine);
		// 执行插入语句
		try {
			dataOperation.excute(sqlList);
		} catch (Exception e) {
			Map<String, List<Line>> DBData1 = dataOperation.readDBData(tableName);
			List<Line> diffLine1 = dataCompare(excelData, DBData1, changeData);
			List<String> sqlList1 = generateSql(diffLine1);
			try {
				dataOperation.excuteOne(sqlList1);
			} catch (Exception e1) {
				e1.printStackTrace();
				StringWriter sw = new StringWriter();
				e1.printStackTrace(new PrintWriter(sw, true));
				throw new Exception(sw.toString());
			}
			e.printStackTrace();
		}
		System.out.println("import " + tableName + " success");
		return changeData;
	}

	private List<Line> dataCompare(Map<String, List<Line>> excelData, Map<String, List<Line>> DBData, ChangeData changeData) {
		List<Line> diffLine = new ArrayList<Line>();
		if (this.isDelete) {
			for (Entry<String, List<Line>> entry : DBData.entrySet()) {
				if (!excelData.containsKey(entry.getKey())) {	//excel表数据字段不包含 数据库字段
					entry.getValue().get(0).setIdent(Ident.DELECT);//设置 删除标记
					diffLine.add(entry.getValue().get(0));	//记录数据库信息的值
					changeData.addChangeLine(entry.getValue().get(0), null, Ident.DELECT);
				}
			}
		}
		for (Entry<String, List<Line>> entry : excelData.entrySet()) {
			if (!DBData.containsKey(entry.getKey())) {	//数据库表数据字段不包含 excel字段
				if ("".equals(entry.getKey())) {
					continue;
				}
				entry.getValue().get(0).setIdent(Ident.INSERT); //设置插入标记
				diffLine.add(entry.getValue().get(0));
				changeData.addChangeLine(null, entry.getValue().get(0), Ident.INSERT);
			} else {
				if (!entry.getValue().get(0).equalList(DBData.get(entry.getKey()))) { //值不相等，更新标识
					entry.getValue().get(0).setIdent(Ident.UPDATE);
					diffLine.add(entry.getValue().get(0));
					changeData.addChangeLine(DBData.get(entry.getKey()).get(0), entry.getValue().get(0), Ident.UPDATE);
				}
			}
		}
		System.out.println("插入:" + changeData.getInsertNum() + "条!更新:" + changeData.getUpdateNum() + "条!");
		return diffLine;
	}

	private static List<String> generateSql1(List<Line> diffLine) {
		List<String> sqlList = new LinkedList<String>();
		for (int i = 0; i < diffLine.size(); i++) {
			sqlList.add(diffLine.get(i).generateSql1());
		}
		return sqlList;
	}

	/**
	 * 生成sql语句
	 * */
	private static List<String> generateSql(List<Line> diffLine) {
		List<String> sqlList = new LinkedList<String>();
		for (int i = 0; i < diffLine.size(); i++) {
			sqlList.add(diffLine.get(i).generateSql());
		}
		return sqlList;
	}

	public String Living(Config config) throws Exception {
		StringBuffer sb = new StringBuffer();
		DataOperation dataOperation = new DataOperation(config);
		Map<Integer, String> excel1 = dataOperation
				.excuteReturn("select MonsterId,NpcArmyId from t_s_cwmonstertemplate");
		Map<Integer, String> excel2 = dataOperation
				.excuteReturn("select TemplateId,LivingIds from t_s_npcarmytemplate");
		Map<Integer, String> excel3 = dataOperation.excuteReturn(
				"select TemplateId,(Hp/6+(case when PhyAttack > MagAttack then PhyAttack else MagAttack end)+MagDefence+PhyDefence+Crit+Parry+Toughness+Mortar) from t_s_livingtemplate");
		List<String> sqllist = new LinkedList<String>();
		for (Entry<Integer, String> table : excel1.entrySet()) {
			int sum = 0;
			try {
				String livingIds = excel2.get(Integer.valueOf(table.getValue()));
				String[] livingid = livingIds.split(",");
				StringBuffer ss = new StringBuffer();
				ss.append("t_s_npcarmytemplate表id为:" + table.getValue() + "-livingId(");
				boolean bool = true;
				for (int i = 0; i < livingid.length; i++) {
					try {
						sum = sum + (int) (double) Double.valueOf(excel3.get(Integer.valueOf(livingid[i])));
					} catch (Exception e) {
						ss.append(livingid[i] + ",");
						bool = false;
					}
				}
				if (bool) {
					String s = "update t_s_cwmonstertemplate set AdviseFightCapacity=" + sum + " where MonsterId="
							+ table.getKey();
					sqllist.add(s);
				} else {
					ss.delete(ss.length() - 1, ss.length());
					ss.append(")找不到!<br>");
					System.out.println(ss.toString());
					sb.append(ss.toString());
				}
			} catch (Exception e) {
				System.out.println("t_s_npcarmytemplate表没有id：" + table.getValue() + "<br>");
				sb.append("t_s_npcarmytemplate表没有id：" + table.getValue() + "<br>");
			}
		}
		int m = dataOperation.excute(sqllist);
		return sb.toString() + "操作成功，共改变了" + m + "条数据！";
	}
}
