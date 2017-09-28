package cn.model;
/**
 * Record，记录了excel表格中，某一行某字段的单元格信息
 * 
 * 
 * */
public class Record {
	private String fieldName;//  字段
	private String type;
	private String isnull;
	private String key;
	private String defaultValue;
	private String extra;
	private String value;
	private boolean ident;
	private boolean isFormula;
	public Record(String name,String value,boolean bool){
		this.fieldName=name;
		this.value=value;
		this.ident=false;
		this.isFormula=bool;
	}
	/**
	 * @param name  字段
	 * @param value cell单元格数据
	 * 
	 * */
	public Record(String name,String value){
		this.fieldName=name;
		this.value=value;
		this.ident=false;
	}
	
	public boolean isFormula() {
		return isFormula;
	}

	public void setFormula(boolean isFormula) {
		this.isFormula = isFormula;
	}

	public boolean isIdent() {
		return ident;
	}

	public void setIdent(boolean ident) {
		this.ident = ident;
	}

	public String getFieldName() {
		return fieldName;
	}
	public void setFieldName(String fieldName) {
		this.fieldName = fieldName;
	}
	public String getValue() {
		return value;
	}
	public void setValue(String value) {
		this.value = value;
	}
	
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getIsnull() {
		return isnull;
	}
	public void setIsnull(String isnull) {
		this.isnull = isnull;
	}
	public String getKey() {
		return key;
	}
	public void setKey(String key) {
		this.key = key;
	}
	public String getDefaultValue() {
		return defaultValue;
	}
	public void setDefaultValue(String defaultValue) {
		this.defaultValue = defaultValue;
	}
	public String getExtra() {
		return extra;
	}
	public void setExtra(String extra) {
		this.extra = extra;
	}
	public boolean equal(Record record){
		
		
		if(this.fieldName.equals(record.fieldName)&&this.value.equals(record.value)){	
			return true;
		}
		if(this.fieldName.equals(record.fieldName)&&"".equals(this.value)&&(this.defaultValue==record.value||record.value.equals(this.defaultValue))){
			return true;
		}
		this.ident=true;
		return false;
	}
	
}
