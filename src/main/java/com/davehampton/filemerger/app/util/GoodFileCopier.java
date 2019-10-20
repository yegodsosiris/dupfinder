package com.davehampton.filemerger.app.util;

import org.springframework.stereotype.Component;

import com.davehampton.filemerger.app.model.MyFile;

@Component
public class GoodFileCopier extends FileCopyHelper{

	public void copyGood(MyFile myFile) {
		String targetDir = null;
		if(myFile.isNoDate()) {
			helper.print('?');
			targetDir = noDateDir+dateDirectoryFormat(myFile);
			copyFile(goodDir, targetDir, myFile);
		}
		else {
			targetDir = dateDirectoryFormat(myFile);
			copyFile(goodDir, targetDir, myFile);
		}
	}	
}
