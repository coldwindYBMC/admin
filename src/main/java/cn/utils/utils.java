package cn.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import cn.model.Config;

public class utils {
	public static void main(String[] args) {
		File file = new File("/Users/xuquanhan/dbconf.properties");
		Properties properties = new Properties();
		try {
			properties.load(new FileInputStream(file));
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println();
	}
	public static boolean svnup(Config config) throws IOException {
        String cmd = String.format("svn up %s", config.excelDirectoty);//
        System.out.println(cmd);
       
        Process process = Runtime.getRuntime().exec(cmd);//生成一个新的进程去运行调用的程序
        //getInputStream(),得到进程的标准输出信息流
        BufferedReader stdInput = new BufferedReader(new InputStreamReader(process.getInputStream(), StandardCharsets.UTF_8));
        BufferedReader stdError = new BufferedReader(new InputStreamReader(process.getErrorStream(), StandardCharsets.UTF_8));
        // read the output from the command
        System.out.println("Here is the standard output of the command:\n");
        String stream = null;
        while ((stream = stdInput.readLine()) != null) {
            System.out.println(new String(stream.getBytes(), "UTF-8"));
        }
        
        // read any errors from the attempted command
        boolean isSuccess = true;
        System.out.println("Here is the standard error of the command (if any):\n");
        while ((stream = stdError.readLine()) != null) {
            System.out.println(stream);
            isSuccess = false;
        }
      
      
        System.out.println("svn update success=" + isSuccess);
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return isSuccess;
    }
	public static boolean svnup(String config) throws IOException {
        String cmd = String.format("svn up %s", config);//
        System.out.println(cmd);
       
        Process process = Runtime.getRuntime().exec(cmd);//生成一个新的进程去运行调用的程序
        //getInputStream(),得到进程的标准输出信息流
        BufferedReader stdInput = new BufferedReader(new InputStreamReader(process.getInputStream(), StandardCharsets.UTF_8));
        BufferedReader stdError = new BufferedReader(new InputStreamReader(process.getErrorStream(), StandardCharsets.UTF_8));
        // read the output from the command
        System.out.println("Here is the standard output of the command:\n");
        String stream = null;
        while ((stream = stdInput.readLine()) != null) {
            System.out.println(new String(stream.getBytes(), "UTF-8"));
        }
        
        // read any errors from the attempted command
        boolean isSuccess = true;
        System.out.println("Here is the standard error of the command (if any):\n");
        while ((stream = stdError.readLine()) != null) {
            System.out.println(stream);
            isSuccess = false;
        }
      
      
        System.out.println("svn update success=" + isSuccess);
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return isSuccess;
    }
	
	  public static void writererror(Config config,String username){
	    	FileWriter fw;
			try {
				fw = new FileWriter(config.excelDirectoty+"\\excellog\\"+utils.getTime1()+"excellog.txt",true);
				fw.write(username+"插入表时失败：");
				fw.write("\n");
				
				//刷新缓冲区
				fw.flush();
				//关闭文件流对象
				fw.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
	        //遍历clist集合写入到fileName中
	        
	    }
	
	public static Properties loadProperties(String fileName) throws IOException {
		Properties properties = new Properties();
		properties.load(utils.class.getClassLoader().getResourceAsStream(fileName));
		return properties;
	}
	
	public static String Stringzip(String s){
		for(int i=1;i<=s.length()/2;i++){
			StringBuffer sb=new StringBuffer();
			s=Stringzip1(sb,s,i).toString();
			System.out.println("��"+i+"��ѹ����"+s);
		}
		return s;
		
	}
	
	public static StringBuffer Stringzip1(StringBuffer sb,String s,int n){
		String s1;
		String s2;
		if(s.length()<2*n){
			sb.append(s);
			return sb;
		}
		s1=s.substring(0, n);
		s2=s.substring(n, n+n);
		if(s1.equals(s2)){
			s=s.substring(n,s.length());
			sb=Stringzip1(sb,s,n);
		}else{
			sb.append(s.substring(0,1));
			s=s.substring(1,s.length());
			sb=Stringzip1(sb,s,n);
		}
		
		
		return sb;
	}
	
	     public static boolean delAllFile(String path) {
	         boolean flag = false;
	         File file = new File(path);
	         if (!file.exists()) {
	           return flag;
	         }
	         if (!file.isDirectory()) {
	           return flag;
	         }
	         String[] tempList = file.list();
	         File temp = null;
	         for (int i = 0; i < tempList.length; i++) {
	            if (path.endsWith(File.separator)) {
	               temp = new File(path + tempList[i]);
	            } else {
	                temp = new File(path + File.separator + tempList[i]);
	            }
	            if (temp.isFile()) {
	               temp.delete();
	            }
	            if (temp.isDirectory()) {
	               delAllFile(path + "/" + tempList[i]);//
	               flag = true;
	            }
	         }
	         return flag;
	       }
	     public static void writererror(String username){
	     	FileWriter fw;
	 		try {
	 			fw = new FileWriter("D:/temp"+"/"+"errorlog.txt",true);
	 			fw.write(username);
	 			fw.write("\n");
	 			
	 			//刷新缓冲区
	 			fw.flush();
	 			//关闭文件流对象
	 			fw.close();
	 		} catch (IOException e) {
	 			e.printStackTrace();
	 		}
	         //遍历clist集合写入到fileName中
	         
	     }
	     
	     public static String getTime(){
	 		Calendar ca = Calendar.getInstance();
	         int year = ca.get(Calendar.YEAR);//获取年份
	        int month=ca.get(Calendar.MONTH);//获取月份 
	        int day=ca.get(Calendar.DATE);//获取日
	        int minute=ca.get(Calendar.MINUTE);//分 
	        int hour=ca.get(Calendar.HOUR_OF_DAY); 
	        int second=ca.get(Calendar.SECOND);//秒
	        String s=year +"年"+ (month+1) +"月"+ day + "日  "+hour +"点"+ minute +"分"+ second+"秒";
	        return s;
	 	}
	     public static String getTime1(){
	    	 Calendar ca = Calendar.getInstance();
	    	 int year = ca.get(Calendar.YEAR);//获取年份
	    	 int month=ca.get(Calendar.MONTH);//获取月份 
	    	 int day=ca.get(Calendar.DATE);//获取日
	    	 String s=year + "" + (month+1) + day+"";
	    	 return s;
	     }
	     
	     
	     public static List<String> CSVtoString(String str){
	    	 List<String> list=new ArrayList<>();
	    	 int i=0;
	    	 int sum=str.split(",").length;
	    	 while(sum>i){
	    		 String s=str.split(",")[i];
	    		 if(s.contains("\"")){
	    			 while((consum(s,"\"")+1)%2==0){
//	    				 System.out.println(s.split("\"").length);
//	    				 String[] m=s.split("\"");
	    				 i++;
	    				 s=s+","+str.split(",")[i];
	    			 }
	    			 list.add(s);
	    		 }else{
	    			 list.add(s);
	    		 }
	    		 i++;
	    	 }
	    	 return list;
	      }
	     public static int consum(String s,String ss){
	    	 int n = s.length()-s.replaceAll(ss, "").length();
	    	 return n;
	     }
	     public static boolean isEmpty(String x) {
	 		int len;
	 		if (x != null && (len = x.length()) != 0) {
	 			do {
	 				if (len-- <= 0) {
	 					return true;
	 				}
	 			} while (Character.isWhitespace(x.charAt(len)));

	 			return false;
	 		} else {
	 			return true;
	 		}
	 	}
}
