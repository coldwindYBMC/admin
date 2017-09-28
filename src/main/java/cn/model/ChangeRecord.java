package cn.model;

public class ChangeRecord {
	private String value;
	private String oldValue;
	private int isChange;//true 是改变了
	public String getValue() {
		return value;
	}
	public void setValue(String value) {
		this.value = value;
	}
	public String getOldValue() {
		return oldValue;
	}
	public void setOldValue(String oldValue) {
		this.oldValue = oldValue;
	}
	public int getIsChange() {
		return isChange;
	}
	public void setIsChange(int isChange) {
		this.isChange = isChange;
	}
	
}
