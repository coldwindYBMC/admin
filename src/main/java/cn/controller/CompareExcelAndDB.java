package cn.controller;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

import cn.ExcelUtil;
import cn.dao.DataOperation;
import cn.model.ChangeData;
import cn.model.Line;
import cn.service.ExcelService;
import cn.utils.WorkbookUtil;

public class CompareExcelAndDB {
	private DataOperation dataOperation;
	private ExcelService excelService;
	ChangeData changeData;
	Map<String, List<Line>> excelData = null;	//excel信息存储
	Map<String, List<Line>> DBData = null;//数据库表信息存储
	Map<String,List<String>> recordChangeFileMap;//记录有改变的表
	List<String> recordNullFileList;//记录不幸为null的表
	Map<String,List<String>> recordExcelFieldMap;//记录excel中有字段，而数据库中无
	Map<String,List<String>> recordDBFieldMap;//记录数据库中有字段，而excel中无
	public CompareExcelAndDB (){
		
		 recordChangeFileMap = new HashMap<>();
		 recordNullFileList = new ArrayList<>();
		 excelService = new ExcelService();
		 recordExcelFieldMap = new HashMap<>();
		 recordDBFieldMap = new HashMap<>();
	}
	
	private void init(){
		 dataOperation = new DataOperation(CleanDB.config);
		 changeData = new ChangeData();
	}
	
	public void readExcelAndDB(List<File> list){
		init();
		list.forEach(f->{
			System.out.println(f.getName());
			 if((excelData = readExcel(f)) == null){
				 recordNullFileList.add(getFileName(f));
				 return;
			 }
			 try {
				DBData = dataOperation.readDBData(getFileName(f));
			} catch (Exception e) {
				e.printStackTrace();
			}
			 List<String> diffLine =  excelService.dataCompare2(excelData, DBData); //对比数据库和excel信息
			 if(!diffLine.isEmpty()){
				 recordChangeFileMap.put(getFileName(f), diffLine);
			 }
		});
	
		getChangeFileRecord();
	}
	
	private void getChangeFileRecord(){
		recordChangeFileMap.forEach((ck,cv) -> {
			System.out.println("数据有出入的表有："+ ck);
//			cv.forEach(value->{
//				System.out.println("出入数据为"+value);
//			});
		});
		recordNullFileList.forEach(nf ->{
			System.out.println("为null的表有："+nf);
		});
	} 
	
	private Map<String, List<Line>> readExcel(File f) {
			//drop表不对比
			if (getFileName(f).indexOf("t_s_droptemplate") != -1 || getFileName(f).indexOf("t_s_droptemplate2") != -1||getFileName(f).indexOf("t_s_droptemplate3") != -1){
				return null;
			}
			//excel信息存储
			Map<String, List<Line>> excelData = new HashMap<String, List<Line>>();//主键，行信息
			//版本遗留，list
		    Workbook workbook = WorkbookUtil.creatWorkbook(CleanDB.config.excelDirectoty, getFileName(f));
			List<Object> workbookList = new ArrayList<Object>();
			workbookList.add(workbook);
			//计算excel开始读取位置
			int startLine = ExcelUtil.existsSecondline(workbook.getSheetAt(0)) == true ? 3:2;
			
			changeData.setTableName(f.getName());
			//在数据库中找主键
			dataOperation.selectTablePri(getFileName(f));
			if (DataOperation.map.containsKey(getFileName(f))) {
				dataOperation.readExcelData(getFileName(f), (Workbook) workbookList.get(0), startLine, excelData, changeData);
			} else {
				dataOperation.readExcelData(getFileName(f), (Workbook) workbookList.get(0), startLine, excelData, changeData);
			}
			return excelData;

	}
	//得到去掉后缀的表名
	private String getFileName(File f){
		return f.getName().substring(0,f.getName().lastIndexOf("."));
	
	}
	//对比数据库和excel缺少的字段
	@SuppressWarnings("deprecation")
	public void cpmareField(List<File> list) {
		init();
		list.forEach(f->{
			System.out.println(getFileName(f));
			//drop表不对比
			if (getFileName(f).indexOf("t_s_droptemplate") != -1 || getFileName(f).indexOf("t_s_droptemplate2") != -1||getFileName(f).indexOf("t_s_droptemplate3") != -1){
				return;
			}
			Workbook workbook = WorkbookUtil.creatWorkbook(CleanDB.config.excelDirectoty, getFileName(f));
			Sheet sheet = workbook.getSheetAt(0);
			Iterator<Cell> fieldCells = sheet.getRow(0).cellIterator(); // 第0行为列名，需要跟数据库表字段对应
			List<String> DBColumn =dataOperation.selectTableColumn(getFileName(f));// 数据库字段名
			List<String> excelFiled = new ArrayList<>();//更改用列表记录excel字段
			List<String> DBNotFiled = new ArrayList<>();//excel有，数据库无
			while (fieldCells.hasNext()) {
				Cell cell = fieldCells.next();
				if (cell.getCellTypeEnum() == CellType.BLANK) {
					continue;
				}
				excelFiled.add(WorkbookUtil.getCellString(cell).trim().toLowerCase());
				// 数据库不包含excel表中的某一列
				if (!DBColumn.contains(WorkbookUtil.getCellString(cell).trim().toLowerCase())) {// trim去掉字符串
					 DBNotFiled.add(WorkbookUtil.getCellString(cell).trim().toLowerCase());
					continue;
				} 
			}
			if(!DBNotFiled.isEmpty()){
				recordExcelFieldMap.put(getFileName(f), DBNotFiled);
			}
		
			//excel不包含数据库的字段
			List<String> excelNotFiled = new ArrayList<>();//excel无，数据库有
			DBColumn.forEach(db->{
				if(!excelFiled.contains(db)){
					excelNotFiled.add(db);
				}
			});
			if(!excelNotFiled.isEmpty()){
				recordDBFieldMap.put(getFileName(f), excelNotFiled);
			}
		
		});
		getfieldRecord();
	}

	private void getfieldRecord() {
		System.out.println("excel中有，数据库中没有的字段：");
		recordExcelFieldMap.forEach((k,v)->{
			System.out.println("数据库名:"+k);
			System.out.print("字段:");
			v.forEach(field->{
				System.out.print(field+" ");
			});
			System.out.println("");
		});
		System.out.println("数据库中有，excel中没有的字段：");
		recordDBFieldMap.forEach((k,v)->{
			System.out.println("数据库名:"+k);
			System.out.print("字段:");
			v.forEach(field->{
				System.out.print(field+" ");
			});
			System.out.println("");
		});
	}
}
