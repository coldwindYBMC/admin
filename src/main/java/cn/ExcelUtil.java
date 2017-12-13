package cn;

import java.io.File;
import java.io.FileOutputStream;
import java.io.RandomAccessFile;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Sheet;

public class ExcelUtil {
	/**
	 * 判断一个字符是否是中文
	 */
	public static boolean isChinese(char c) {
		return c >= 0x4E00 && c <= 0x9FA5;// 根据字节码判断
	}

	/**
	 * 判断一个字符串是否含有中文
	 */
	public static boolean isChinese(String str) {
		if (str == null)
			return false;
		for (char c : str.toCharArray()) {
			if (isChinese(c))
				return true;// 有一个中文字符就返回
		}
		return false;
	}
	/**
	 * 判断excel第一行是否是注释
	 *  cell1存在汉字 && cell2 不存在汉字 跳过
	 *	强制  cell1 == ID，跳过。
	 *
	 */
	@SuppressWarnings("deprecation")
	public static boolean existsSecondline(Sheet sheet) {
		if (sheet.getRow(2) == null ||sheet.getRow(1) == null)  //没有前几行，不跳过
			return false;
		
		Cell cell1 = sheet.getRow(1).getCell(0);
		Cell cell2 = sheet.getRow(2).getCell(0);
		if(cell1 == null && cell2 != null){
			return true;
		}
		
		if (cell2 == null || cell1 == null)    //单元格为空
			return false;	
	
		cell1.setCellType(HSSFCell.CELL_TYPE_STRING);
		cell2.setCellType(HSSFCell.CELL_TYPE_STRING);
		String value = cell1.getStringCellValue(); 
		if(value.contains("Id") || value.contains("id")  || value.contains("ID") || value.contains("iD")){
			return true;
		}		
		if(cell1.getStringCellValue().equals(" ")){
			return true;
		}
		if (isChinese(cell1.getStringCellValue()) && !isChinese(cell2.getStringCellValue()))
			return true;
		
		return false;
	}
	
	
	/** 
	 * Java文件操作 获取不带扩展名的文件名 
	 */  
	    public static String getFileNameNoEx(String filename) {   
	        if ((filename != null) && (filename.length() > 0)) {   
	            int dot = filename.lastIndexOf('.');  		// . 所在的位置 
	            if ((dot >-1) && (dot < (filename.length()))) {   
	                return filename.substring(0, dot);   
	            }   
	        }   
	        return filename;   
	    }   
	    
	public static boolean isFirstLower(String value) {
		if (value.isEmpty()) {
			System.out.println("isFirstLower，字符串为空，无大小写，return true");
			return true;
		}
		char chr = value.charAt(0);
		if (Character.isLowerCase(chr)) {
			return true;
		} else {
			return false;
		}

	}
	/**
     * 递归删除目录下的所有文件及子目录下所有文件
     * @param dir 将要删除的文件目录
     * @return boolean Returns "true" if all deletions were successful.
     *                 If a deletion fails, the method stops attempting to
     *                 delete and returns "false".
     */
    public static boolean deleteDir(File dir) {
        if (dir.isDirectory()) {
            String[] children = dir.list();
            //递归删除目录中的子目录下
            for (int i=0; i<children.length; i++) {
                boolean success = deleteDir(new File(dir, children[i]));
                if (!success) {
                    return false;
                }
            }
        }
        // 目录此时为空，可以删除
        return dir.delete();
    }

    public static boolean writeTxtFile(String content,File  fileName)throws Exception{  
    	  RandomAccessFile mm=null;  
    	  boolean flag=false;  
    	  FileOutputStream o=null;  
    	  try {  
    		  System.out.println(fileName);
    		  System.out.println(content);
    	   o = new FileOutputStream(fileName);  
    	      o.write(content.getBytes("GBK"));  
    	      o.close();  
    	//   mm=new RandomAccessFile(fileName,"rw");  
    	//   mm.writeBytes(content);  
    	   flag=true;  
    	  } catch (Exception e) {  
    	   // TODO: handle exception  
    	   e.printStackTrace();  
    	  }finally{  
    	   if(mm!=null){  
    	    mm.close();  
    	   }  
    	  }  
    	  return flag;  
    	 }  
    	  

}
