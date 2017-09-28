package cn.model;

import java.util.ArrayList;
import java.util.List;

public class ChangeLine {
	public List<ChangeRecord> changeRecords = new ArrayList<>();
	public int state;//0 删除 1 修改 2 插入
	public void addChangeRecord(Record DBRecord, Record excelRecord) {
		ChangeRecord changeRecord = new ChangeRecord();
		changeRecord.setIsChange(1);
		if(excelRecord == null && DBRecord == null) {
			changeRecord.setOldValue("");
			changeRecords.add(changeRecord);
			return;
		}
		if(excelRecord == null) {
			changeRecord.setOldValue(DBRecord.getValue());
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
	
}
