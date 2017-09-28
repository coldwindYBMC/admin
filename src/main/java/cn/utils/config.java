package cn.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.commons.configuration.CompositeConfiguration;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;

public class config { 
	
	 public static String datetostring(Date date){
         SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
         String s1 = sdf.format(date);
         return s1;
     }
     
     public static Date stringtodate(String s) throws ParseException{
         Date d=new SimpleDateFormat("yyyy-MM-dd").parse(s);
         return d;
     }
	
    //灞炴�鏂囦欢鐨勮矾寰�  
    
     public static String getKeyValue(String key) {  
      
        CompositeConfiguration config = new CompositeConfiguration();
        
        try {
            config.addConfiguration(new PropertiesConfiguration("dataSource.properties"));
        } catch (ConfigurationException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        
        return config.getString(key);   
    }   
  
  
      
      public static String writeProperties(String keyname,String keyvalue) {          
        
            
          PropertiesConfiguration config;
        try {
            config = new PropertiesConfiguration("dataSource.properties");
            config.setProperty(keyname, keyvalue);
            config.save();
            return config.getString(keyname);
        } catch (ConfigurationException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
          
         
            return null;
            
            
    }   
  
  
    //娴嬭瘯浠ｇ爜   
   public static void main(String[] args) throws ConfigurationException {
      
       PropertiesConfiguration config = new PropertiesConfiguration("dataSource.properties");
       config.setProperty("abc", "2221");
       config.save();
       System.out.println(config.getString("abc"));
       
       
      
}
} 
