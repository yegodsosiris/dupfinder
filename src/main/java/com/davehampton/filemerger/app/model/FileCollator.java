package com.davehampton.filemerger.app.model;

import java.util.ArrayList;
import java.util.List;

public class FileCollator {
	
	String key;
	MyFile keep;
	List<MyFile> older = new ArrayList<MyFile>();
	
	public MyFile getKeep() {
		return keep;
	}
	public void setKeep(MyFile keep) {
		if (this.keep!=null) {
			add();
		}
		this.keep = keep;
	}
	public List<MyFile> getOlder() {
		return older;
	}
	public void addOlder(MyFile myFile) {
		older.add(myFile);
	}
	public void setOlder(List<MyFile> older) {
		this.older = older;
	}
	
	public void add() {
		older.add(keep);
	}
	
}
