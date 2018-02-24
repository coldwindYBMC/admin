package cn.model;

import java.util.ArrayList;
import java.util.List;

import cn.service.ExcelService;

public class ChangeData {
	public String tableName;
	public List<String> title = new ArrayList<>();
	public List<ChangeLine> changeLines = new ArrayList<>();
	private List<String> uselessField = new ArrayList<>();
	private int delectNum = 0;
	private int insertNum = 0;
	private int updateNum = 0;
	public String exception = "";
	public int isException = 0;
	public int isExistsSecondLine;//第二行是否是注释
	private  int importRowLine;//开始导入的行数
	private  int existLine;//有几行未导入

	public int getIsExistsSecondLine() {
		return isExistsSecondLine;
	}
	public void setIsExistsSecondLine(int isExistsSecondLine) {
		this.isExistsSecondLine = isExistsSecondLine;
	}
	public int getImportRowLine() {
		return importRowLine;
	}
	public void setImportRowLine(int importRowLine) {
		this.importRowLine = importRowLine;
	}
	public int getExistLine() {
		return existLine;
	}
	public void setExistLine(int existLine) {
		this.existLine = existLine;
	}
	public ChangeData() {}
	public ChangeData(String tableName, String exception) {
		this.tableName = tableName;
		this.exception = exception;
		this.isException = 1;
	}
	public void addTitle(List<String> list) {
		title.addAll(list);
	}
	public void addChangeLine(Line DBLine, Line excelLine, Ident ident) {
		ChangeLine changeLine = new ChangeLine();
		switch (ident) {
		case DELECT:
			changeLine.state = 0;
			title.stream().forEach(s->{
				changeLine.addChangeRecord(DBLine.getRecordMap().get(s), null);
			});
			changeLines.add(changeLine);
			delectNum++;
			break;
		case UPDATE:
			changeLine.state = 1;
			for(int i = 0; i < title.size();i++){
				String s = title.get(i);
				if(excelLine.getRecordMap().get(s) == null){	
					changeLine.addChangeRecord("", "");
					System.out.println("该字段为空："+s);
				//	ExcelService.isExceptionString.append("该字段为空：").append(s).append("*****");
					continue;
				} 
				if("".equals(excelLine.getRecordMap().get(s).getValue()) && DBLine.getRecordMap().get(s).getValue() == null ) {
					changeLine.addChangeRecordNull();
				} else if("".equals(excelLine.getRecordMap().get(s).getValue()) && (DBLine.getRecordMap().get(s).getValue() != null
									&& !"".equals(DBLine.getRecordMap().get(s).getValue()))){
					changeLine.addChangeRecordNull(DBLine.getRecordMap().get(s));//如果数据库为不为NULL或者不会为""且excel为""
				}
				else {
					changeLine.addChangeRecord(DBLine.getRecordMap().get(s), excelLine.getRecordMap().get(s));
				}
				
			}
//			title.stream().forEach(s->{
//				if(excelLine.getRecordMap().get(s) == null){	
//					System.out.println("该字段为空："+s);
//					ExcelService.isExceptionString.append("该字段为空：").append(s).append("*****");
//					return;
//				} 
//				if("".equals(excelLine.getRecordMap().get(s).getValue()) && DBLine.getRecordMap().get(s).getValue() == null ) {
//					changeLine.addChangeRecordNull();
//				} else if("".equals(excelLine.getRecordMap().get(s).getValue()) && (DBLine.getRecordMap().get(s).getValue() != null
//									&& "".equals(DBLine.getRecordMap().get(s).getValue()))){
//					changeLine.addChangeRecordNull(DBLine.getRecordMap().get(s));//如果数据库为不为NULL或者不会为""且excel为""
//				}
//				else {
//					changeLine.addChangeRecord(DBLine.getRecordMap().get(s), excelLine.getRecordMap().get(s));
//				}
//			});
			changeLines.add(changeLine);
			updateNum++;
			break;
		case INSERT:
			changeLine.state = 2;
			for(int i = 0; i < title.size();i++){
				String s = title.get(i);
				if(excelLine.getRecordMap().get(s) == null){
					System.out.println("该字段为空："+s);
				//	ExcelService.isExceptionString.append("该字段为空：").append(s).append("*****");
					changeLine.addChangeRecord("", "");
//					System.out.println(ExcelService.isExceptionString);
					continue;
				}
				if("".equals(excelLine.getRecordMap().get(s).getValue())) {
					changeLine.addChangeRecordNull();
				}else {
					changeLine.addChangeRecord(null, excelLine.getRecordMap().get(s));
				}
			}
//			title.stream().forEach(s->{
//				if(excelLine.getRecordMap().get(s) == null){
//					System.out.println("该字段为空："+s);
//					ExcelService.isExceptionString.append("该字段为空：").append(s).append("*****");
//					System.out.println(ExcelService.isExceptionString);
//					return;
//				}
//				if("".equals(excelLine.getRecordMap().get(s).getValue())) {
//					changeLine.addChangeRecordNull();
//				}else {
//					changeLine.addChangeRecord(null, excelLine.getRecordMap().get(s));
//				}
//			});
			changeLines.add(changeLine);
			insertNum++;
			break;
		default:
			break;
		}
	}
	public List<String> getTitle() {
		return title;
	}
	public List<ChangeLine> getChangeLines() {
		return changeLines;
	}
	public int getDelectNum() {
		return delectNum;
	}
	public int getInsertNum() {
		return insertNum;
	}
	public int getUpdateNum() {
		return updateNum;
	}
	public String getTableName() {
		return tableName;
	}
	public void setTableName(String tableName) {
		this.tableName = tableName;
	}
	public String getException() {
		return exception;
	}
	public void setException(String exception) {
		this.exception = exception;
	}
	public void setTitle(List<String> title) {
		this.title = title;
	}
	public void setChangeLines(List<ChangeLine> changeLines) {
		this.changeLines = changeLines;
	}
	public void setDelectNum(int delectNum) {
		this.delectNum = delectNum;
	}
	public void setInsertNum(int insertNum) {
		this.insertNum = insertNum;
	}
	public void setUpdateNum(int updateNum) {
		this.updateNum = updateNum;
	}
	public int getIsException() {
		return isException;
	}
	public void setIsException(int isException) {
		this.isException = isException;
	}
	public List<String> getUselessField() {
		return uselessField;
	}
	public void setUselessField(List<String> uselessField) {
		this.uselessField = uselessField;
	}
	
}
