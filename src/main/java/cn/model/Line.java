package cn.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;



public class Line {
	private String tableName;
	private Map<String,Record> recordMap=new HashMap<String,Record>();
	private Ident ident;
	private boolean isChange;//比较中判断此行是否有变化 没变化是false
	public boolean isChange() {
		return isChange;
	}
	public void setChange(boolean isChange) {
		this.isChange = isChange;
	}

	private List<String> pri=new ArrayList<String>();
	public Map<String, Record> getRecordMap() {
		return recordMap;
	}
    public Line clon(){
    	Line line=new Line();
    	line.setTableName(this.tableName);
    	line.setRecordMap(this.recordMap);
    	line.setPri(this.pri);
    	return line;
    }

	public String getTableName() {
		return tableName;
	}

	public void setTableName(String tableName) {
		this.tableName = tableName;
	}


	public List<String> getPri() {
		return pri;
	}


	public void setPri(List<String> pri) {
		this.pri = pri;
	}


	public void setRecordMap(Map<String, Record> recordMap) {
		this.recordMap = recordMap;
	}


	public Ident getIdent() {
		return ident;
	}


	public void setIdent(Ident ident) {
		this.ident = ident;
	}


	public boolean equal(Line line){
		boolean bool=true;
		for(Entry<String,Record> entry:recordMap.entrySet()){
			if(!entry.getValue().equal(line.getRecordMap().get(entry.getKey()))){
//				System.out.println(this.getRecordMap().get(entry.getKey()).getValue());
//				System.out.println(line.getRecordMap().get(entry.getKey()).getName()+"---"+line.getRecordMap().get(entry.getKey()).getValue());
				bool = false;
			}
		}
		return bool;
	}
	//执行sql
	public String generateSql() {
		StringBuffer sb=new StringBuffer();
		switch (this.ident) {
		case INSERT:
			sb.append("insert into "+tableName+"(`");
			for(Entry<String ,Record> entry:this.recordMap.entrySet()){
				sb.append(""+entry.getKey()+"`,`");
			}
			sb.delete(sb.length() - 2, sb.length());
			sb.append(") values(");
			for(Entry<String ,Record> entry:this.recordMap.entrySet()){
				
				if("".equals(entry.getValue().getValue())){
					sb.append("default,");
				}else{
					String respaceSql= entry.getValue().getValue().replaceAll("\\\\n", "\\\\\\\\n");
					sb.append("'"+respaceSql+"',");
				}
			}
			sb.delete(sb.length() - 1, sb.length());
			sb.append(")");
			break;
		case UPDATE:
			sb.append("update "+tableName+" set `");
			for(Entry<String ,Record> entry:this.recordMap.entrySet()){
				if("".equals(entry.getValue().getValue())){
					sb.append(entry.getKey()+"`=default,`");
				}else{
					String respaceSql = entry.getValue().getValue().replaceAll("\\\\n", "\\\\\\\\n");
					sb.append(entry.getKey()+"`='"+respaceSql+"',`");
				}
			}
			sb.delete(sb.length() - 2, sb.length());
			sb.append(" where `");
			for(int i=0;i<pri.size();i++){
				if(!recordMap.containsKey(pri.get(i))){
					System.out.println(pri.get(i));
					continue;
				}
				sb.append(pri.get(i)+"`='"+recordMap.get(pri.get(i)).getValue()+"' and `");
			}
			sb.delete(sb.length() - 5, sb.length());
			break;
		case DELECT:
			sb.append("delete from "+tableName+" where `");
			for(int i=0;i<pri.size();i++){
				if(!recordMap.containsKey(pri.get(i))){
					System.out.println(pri.get(i));
					continue;
				}
				sb.append(pri.get(i)+"`='"+recordMap.get(pri.get(i)).getValue()+"' and `");
			}
			sb.delete(sb.length() - 5, sb.length());
			break;
		default:
			break;
		}
		return sb.toString();
	}

	public String generateSql1() {
		StringBuffer sb=new StringBuffer();
		switch (this.ident) {
		case INSERT:
			sb.append("<font color=\"red\">");
			sb.append("insert into "+tableName+"(`");
			for(Entry<String ,Record> entry:this.recordMap.entrySet()){
				sb.append(""+entry.getKey()+"`,`");
			}
			sb.delete(sb.length() - 2, sb.length());
			sb.append(") values(");
			for(Entry<String ,Record> entry:this.recordMap.entrySet()){
				if("".equals(entry.getValue().getValue())){
					sb.append("default,");
				}else{
					sb.append("'"+entry.getValue().getValue()+"',");
				}
			}
			sb.delete(sb.length() - 1, sb.length());
			sb.append(")");
			sb.append("</font>");
			break;
		case UPDATE:
			sb.append("update "+tableName+" set `");
			for(Entry<String ,Record> entry:this.recordMap.entrySet()){
				if(entry.getValue().isIdent()){
					sb.append("<font color=\"red\">");
				}
				try{
					if("".equals(entry.getValue().getValue())){
						sb.append(entry.getKey()+"`=default,`");
					}else{
						sb.append(entry.getKey()+"`='"+entry.getValue().getValue()+"',`");
					}
				}catch(Exception e){
					System.out.println("字体加红报错!");
				}finally {
					if(entry.getValue().isIdent()){
						sb.append("</font>  ");
					}
				}
			}
			sb.delete(sb.length() - 2, sb.length());
			sb.append(" where `");
			for(int i=0;i<pri.size();i++){
				if(!recordMap.containsKey(pri.get(i))){
					System.out.println(pri.get(i));
					continue;
				}
				sb.append(pri.get(i)+"`='"+recordMap.get(pri.get(i)).getValue()+"' and `");
			}
			sb.delete(sb.length() - 5, sb.length());
			break;
		case DELECT:
			sb.append("<font color=\"red\">");
			sb.append("delete from "+tableName+" where `");
			for(int i=0;i<pri.size();i++){
				if(!recordMap.containsKey(pri.get(i))){
					System.out.println(pri.get(i));
					continue;
				}
				sb.append(pri.get(i)+"`='"+recordMap.get(pri.get(i)).getValue()+"' and `");
			}
			sb.delete(sb.length() - 5, sb.length());
			sb.append("</font>");
			break;
		default:
			break;
		}
		return sb.toString();
	}

	public boolean equalList(List<Line> list) {
		for(int i=0;i<list.size();i++){
			if(equal(list.get(i))){
				return true;
			}
		}
		return false;
	}
}
