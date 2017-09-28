package cn.model;

public class Column {
	private String type;
	private String isnull;
	private String key;
	private String defaultValue;
	private String extra;
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
	
}
