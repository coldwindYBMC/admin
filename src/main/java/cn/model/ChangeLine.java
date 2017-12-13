package cn.model;

import java.util.ArrayList;
import java.util.List;

public class ChangeLine implements Comparable<ChangeLine> {
	public List<ChangeRecord> changeRecords = new ArrayList<>();
	public int state;//0 删除 1 修改 2 插入
	
	@Override
	public int compareTo(ChangeLine cl) {
		if(isNumeric(this.getChangeRecords().get(0).getValue())){
			return Integer.valueOf(this.getChangeRecords().get(0).getValue()).compareTo(Integer.valueOf(cl.getChangeRecords().get(0).getValue()));
		} else{
			return this.getChangeRecords().get(0).getValue().compareTo(cl.getChangeRecords().get(0).getValue());
		}
	}
	

	public static boolean isNumeric(String str) {
		for (int i = 0; i < str.length(); i++) {
			if (!Character.isDigit(str.charAt(i))) {
				return false;
			}
		}
		return true;
	}
	
	public void addChangeRecord(Record DBRecord, Record excelRecord) {
		ChangeRecord changeRecord = new ChangeRecord();
		changeRecord.setIsChange(1);
		if(excelRecord == null && DBRecord == null) {
			changeRecord.setOldValue("");
			changeRecords.add(changeRecord);
			return;
		}
		if(excelRecord == null) {
			changeRecord.setValue(DBRecord.getValue());
			changeRecords.add(changeRecord);
			return;
		}
		if(DBRecord == null) {
			changeRecord.setValue(excelRecord.getValue());
			changeRecords.add(changeRecord);
			return;
		}
		changeRecord.setOldValue(DBRecord.getValue());
		changeRecord.setValue(excelRecord.getValue());
		changeRecord.setIsChange(excelRecord.isIdent()? 1:0);
		changeRecords.add(changeRecord);
	}
	public List<ChangeRecord> getChangeRecords() {
		return changeRecords;
	}
	public void setChangeRecords(List<ChangeRecord> changeRecords) {
		this.changeRecords = changeRecords;
	}
	public int getState() {
		return state;
	}
	public void setState(int state) {
		this.state = state;
	}
	public void addChangeRecordNull() {
		ChangeRecord changeRecord = new ChangeRecord();
		changeRecord.setIsChange(1);
		changeRecord.setValue("");
		changeRecord.setOldValue("");
		changeRecords.add(changeRecord);
	}

	public void addChangeRecordNull(Record DBrecord) {
		ChangeRecord changeRecord = new ChangeRecord();
		changeRecord.setIsChange(1);
		changeRecord.setValue("Change to Null value");
		changeRecord.setOldValue(DBrecord.getValue());
		changeRecords.add(changeRecord);
		
	}
	
}
