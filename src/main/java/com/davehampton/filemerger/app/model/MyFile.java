package com.davehampton.filemerger.app.model;

import java.io.File;
import java.io.IOException;
import java.text.NumberFormat;

import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;

import com.davehampton.filemerger.app.util.CommonHelper;
import com.drew.imaging.ImageProcessingException;

public class MyFile {
	double size;
	String name, directory;
	DateTime dateTaken;
	File file;
	boolean noDate;
	
	public MyFile(File f) throws IOException, ImageProcessingException {
		super();
		file=f;
		directory  = StringUtils.substringAfter(f.getParent(), ":\\");
		size = f.length();
		name = f.getName().toLowerCase();
	}
	
	
	
	public double getSize() {
		return size;
	}



	public void setSize(double size) {
		this.size = size;
	}



	public String getName() {
		return name;
	}



	public void setName(String name) {
		this.name = name;
	}



	public String getDirectory() {
		return directory;
	}



	public void setDirectory(String directory) {
		this.directory = directory;
	}



	public DateTime getDateTaken() {
		return dateTaken;
	}



	public void setDateTaken(DateTime dateTaken) {
		this.dateTaken = dateTaken;
	}



	public File getFile() {
		return file;
	}



	public void setFile(File file) {
		this.file = file;
	}



	public boolean isNoDate() {
		return noDate;
	}



	public void setNoDate(boolean noDate) {
		this.noDate = noDate;
	}



	public void setDateTaken(CommonHelper helper) throws ImageProcessingException {
		dateTaken = helper.getDateTaken(this);
	}
	
	@Override
	public String toString() {
		return String.format("%s/%s %s ", name, dateTaken.toString("dd-MM-yy"), NumberFormat.getInstance().format(size));
	}

	
}