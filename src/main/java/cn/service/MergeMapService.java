package cn.service;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.stereotype.Service;

import cn.utils.WorkbookUtil;

@Service
public class MergeMapService {
	/**
	 * @param viceSize   file1.length
	 * 
	 * */
	public boolean merge(int viceSize,StringBuffer sb) {
		List<List<String>> data=new ArrayList<>();
		int index=0;
		try {
			List<List<String>> list=WorkbookUtil.importCsv("D:/temp/fubiao0" + ".csv");
			data.addAll(list);
			if(viceSize>1){
				for(int i=1;i<viceSize;i++){
					List<List<String>> list1=WorkbookUtil.importCsv("D:/temp/fubiao"+i + ".csv");
					list1.remove(0);
					data.addAll(list1);
				}
			}
			List<String> ss=list.get(0);
			for(int i=0;i<ss.size();i++){
				if("MapId".equals(ss.get(i))){
					index=i;
					break;
				}
			}
			writeExcel(data,index);
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		return true;
	}
	private void writeExcel(List<List<String>> data, int index) throws IOException {
		creatExcel();
		OutputStream out = null; 
		File file=new File("D:/temp/merge.xls");
		Workbook workbook=(Workbook) WorkbookUtil.creatWorkbookFile(file);
		Sheet sheet = workbook.getSheetAt(0);
		out =  new FileOutputStream("D:/temp/merge.xls");  
		workbook.write(out);
		CellStyle cellStyle=workbook.createCellStyle();
		Font font = workbook.createFont();
		font.setColor(HSSFColor.RED.index);
		cellStyle.setFont(font);
		Row row0=sheet.createRow(0);
		List<String> ss=data.get(0);
		int lineSize=data.get(0).size();
		for(int m=0;m<ss.size();m++){
			Cell cell = row0.createCell(m);
			cell.setCellValue(ss.get(m));
		}
		int size=data.size()-1;
		for(int i=0;i<4;i++){
        	for(int j=1;j<data.size();j++){
        		Row row=sheet.createRow(j+i*size);
        		List<String> s=data.get(j);
//        		System.out.println(s.toString());
        		if(s.size()!=lineSize){
        			System.out.println("csv解析出错!"+data.get(j)+"---size为"+lineSize);
        			try {
						throw new Exception("csv解析出错!");
					} catch (Exception e) {
						e.printStackTrace();
					}
        		}
        		for(int k=0;k<s.size();k++){
        			Cell cell=row.createCell(k);
        			if(k==index){
        				cell.setCellValue(Integer.valueOf(s.get(k))+i*10);
        			}else{
        				cell.setCellValue(s.get(k));
        			}
        		}
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
}
