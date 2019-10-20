package com.davehampton.filemerger.app.util;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import com.davehampton.filemerger.app.model.MyFile;

@Component
public class DuplicateFileCopier extends FileCopyHelper{

	public void copyOlder(MyFile myFile, MyFile keep) {
		copyFile(myFile.isNoDate()?duplicateDir+"\\"+noDateDir:duplicateDir, dateDirectoryFormat(myFile), myFile);
		copyFile(myFile.isNoDate()?duplicateDir+"\\"+noDateDir:duplicateDir, dateDirectoryFormat(keep), keep, keep.getName()+"_"+keptFileName+"."+StringUtils.substringAfterLast(keep.getName(), "."));
	}
		
}
