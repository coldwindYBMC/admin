package cn.utils;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import cn.model.Suffix;


public class WorkbookUtil {

	private static FileInputStream file;
	public static Workbook creatWorkbook(String excelDirectoty, String excelName) {
		if(new File(excelDirectoty, excelName + ".xls").exists()){
			try {
				File f = new File(excelDirectoty, excelName + ".xls");
				file = new FileInputStream(f);
				HSSFWorkbook wb=new HSSFWorkbook(file);
				return wb;
			} catch (Exception e) {
				e.printStackTrace();
			}
		}else if(new File(excelDirectoty, excelName + ".xlsx").exists()){
			try {
				file = new FileInputStream(new File(excelDirectoty, excelName + ".xlsx"));
				XSSFWorkbook wb=new XSSFWorkbook(file);
				return wb;
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return null;
	}
	
	
	
	public static void close(){
		try {
			file.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	@SuppressWarnings("deprecation")
	public static String WorkString(Cell cell){
		if (cell.getCellTypeEnum() == CellType.FORMULA) {
			return cell.getCellFormula();
		}
		cell.setCellType(CellType.STRING);
		return cell.getStringCellValue();
	}
	/**
	 *得到cell内容
	 *
	 **/
	public static String getCellString(Cell cell){
		cell.setCellType(CellType.STRING);
		return cell.getStringCellValue().trim();
	}


	public static Workbook creatWorkbookFile(File file2) {
		if(file2.exists()){
			try {
				file = new FileInputStream(file2);
				HSSFWorkbook wb=new HSSFWorkbook(file);
				return wb;
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return null;
	}
	public static Suffix findSuffix(String excelDirectoty, String excelName) {
		if(new File(excelDirectoty, excelName + ".xls").exists()){
			return Suffix.xls;
		}else if(new File(excelDirectoty, excelName + ".xlsx").exists()){
			return Suffix.xlsx;
		}else if(new File(excelDirectoty, excelName + ".csv").exists()){
			return Suffix.csv;
		}
		return null;
	}
	
	public static List<List<String>> importCsv(String file) throws FileNotFoundException{
		FileInputStream fileInput=new FileInputStream(new File(file));
        List<String> dataList=new ArrayList<String>();
        DataInputStream in = new DataInputStream(fileInput);
        BufferedReader br=null;
        try { 
        	System.out.println(FileCharsetDetector.guessFileEncoding(file));
            br = new BufferedReader(new InputStreamReader(in,FileCharsetDetector.guessFileEncoding(file)));//utf-8 无BOM
            String line = ""; 
            while ((line = br.readLine()) != null) { 
                dataList.add(line);
            }
        }catch (Exception e) {
        }finally{
            if(br!=null){
                try {
                    br.close();
                    br=null;
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        List<List<String>> data=new ArrayList<>();
        for(int i=0;i<dataList.size();i++){
        	data.add(CSVtoString(dataList.get(i)));
        }
        return data;
    }
	public static List<String> CSVtoString(String str){
   	 List<String> list=new ArrayList<>();
   	 int i=0;
   	 int sum=str.split(",").length;
   	 
   	 while(sum>i){
//   		 try{
   			 String s=str.split(",")[i];
   			 if(s.contains("\"")){
   				 while((consum(s,"\"")+1)%2==0){
//   				 System.out.println(s.split("\"").length);
//   				 String[] m=s.split("\"");
   					 i++;
   					 s=s+","+str.split(",")[i];
   				 }
   				 String ss="";
   				 if("\"".equals(s.substring(0,1))){
   					 ss=s.substring(1,s.length()-1);
   				 }else {
   					 ss=s;
   				 }
   				 String s1 = ss.replace("\"\"","\"");
   				 list.add(s1);
   			 }else{
   				 list.add(s);
   			 }
   			 i++;
//   		 }catch(Exception  e){
//   			 System.out.println("--111---"+str+"------2222-------------");
//   		 }
   	 }
   	 return list;
     }
	public static int consum(String s,String ss){
   	 int n = s.length()-s.replaceAll(ss, "").length();
   	 return n;
    }
	public static void main(String[] args) {
		try {
			//importCsv1(new FileInputStream(new File("D:/temp/fubiao01" + ".csv")));
			 InputStream inputStream=new FileInputStream(new File("D:/temp/fubiao0" + ".csv"));

			 byte[] head = new byte[3];  
		        inputStream.read(head);    
		        String code = "";  
		   
		            code = "gb2312";  
		        if (head[0] == -1 && head[1] == -2 )  
		            code = "UTF-16";  
		        if (head[0] == -2 && head[1] == -1 )  
		            code = "Unicode";  
		        if(head[0]==-17 && head[1]==-69 && head[2] ==-65)  
		            code = "UTF-8";  
		          
		        System.out.println(code); 
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
	}
}
