package cn.service;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.stereotype.Service;

import cn.dao.MerageDao;
import cn.model.Config;
import cn.model.Line;
import cn.utils.WorkbookUtil;
import cn.utils.utils;

@Service
public class MergeService {

	private MerageDao dataOperation;
	MergeService(){
		Config config=new Config("db_templates_luzj_001");
		this.dataOperation=new MerageDao(config);
	}
	public boolean merge(String tableName, int viceSize,StringBuffer sb) {
		Map<String,List<Line>> zhubiaoMap=new LinkedHashMap<>();
		Workbook workbook=(Workbook) WorkbookUtil.creatWorkbook("D:/temp", "zhubiao");
		dataOperation.selectTablePri(tableName);
		dataOperation.selectMainField(tableName,workbook,zhubiaoMap);
		dataOperation.readExcelData(tableName, workbook, zhubiaoMap);
		List<Map<String,List<Line>>> list=new ArrayList<>();	
		for(int i=0;i<viceSize;i++){
			Map<String,List<Line>> viceMap=new HashMap<>();
			Workbook workbook1=(Workbook) WorkbookUtil.creatWorkbook("D:/temp", "fubiao"+i);
			dataOperation.readExcelData(tableName, workbook1, viceMap);
			list.add(viceMap);
		}
		//先和副表
		for(int i=1;i<list.size();i++){
			if(!mergeExcel(list.get(0),list.get(i),sb)){
				utils.writererror(sb.toString());
				return false;
			}
		}
		mergeExcel1(zhubiaoMap,list.get(0));
		try {
			dataOperation.writeExcel(zhubiaoMap);
		} catch (Exception e) {
			StringWriter sw = new StringWriter(); 
			e.printStackTrace(new PrintWriter(sw, true));
			utils.writererror(sw.toString());
			e.printStackTrace();
			System.out.println("写数据出错!");
			return false;
		}
		return true;
	}
	//主键可以有相同，有相同，更新
	private void mergeExcel1(Map<String, List<Line>> zhubiaoMap, Map<String, List<Line>> map) {
		for(Entry<String,List<Line>> entry:map.entrySet()){
        	if(!zhubiaoMap.containsKey(entry.getKey())){//不包含
        		if("".equals(entry.getKey())){
        			continue;
        		}
        		entry.getValue().stream().forEach(line->{
        			line.setChange(true);
        		});
        		zhubiaoMap.put(entry.getKey(), entry.getValue());
        	}else{//包含
        		if("".equals(entry.getKey())){
        			continue;
        		}
        		entry.getValue().stream().forEach(line->{
        			line.setChange(false);
        		});
        		if(!entry.getValue().get(0).equalList(zhubiaoMap.get(entry.getKey()))){//有不同
        			
        		}
        		zhubiaoMap.put(entry.getKey(), entry.getValue());
        	}
        }
	}
	//主键不能有相同，有相同，就是冲突
	private boolean mergeExcel(Map<String, List<Line>> main, Map<String, List<Line>> vice, StringBuffer sb) {
		boolean bool=true;
		for(Entry<String,List<Line>> entry:vice.entrySet()){
        	if(!main.containsKey(entry.getKey())){
        		if("".equals(entry.getKey())){
        			continue;
        		}
        		main.put(entry.getKey(), entry.getValue());
        	}else{
        		bool=false;
        		sb.append("副表中主键为:"+entry.getKey()+"的列有冲突!\n");
        	}
        }
		return bool;
	}

}
