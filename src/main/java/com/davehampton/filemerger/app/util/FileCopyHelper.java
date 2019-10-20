package com.davehampton.filemerger.app.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import com.davehampton.filemerger.app.model.MyFile;


public class FileCopyHelper {
	
	@Autowired
	CommonHelper helper;
	
	@Value("#{props.goodDir}")
	String goodDir;
	
	@Value("#{props.duplicateDir}")
	String duplicateDir;
	
	@Value("#{props.noDateDir}")
	String noDateDir;
	
	@Value("#{props.keptFileName}")
	String keptFileName;

	protected String dateDirectoryFormat(MyFile sourceFile) {
		return sourceFile.getDateTaken().toString("YYYY\\MM");
	}
	
	protected void copyFile(String rootDir, String subDir, MyFile sourceFile) {
		copyFile(rootDir, subDir, sourceFile, sourceFile.getName());
	}
	
	protected void copyFile(String rootDir, String subDir, MyFile sourceFile, String newName) {
		InputStream inStream = null;
		OutputStream outStream = null;
		List<String> allExtensions = helper.getAllExtensions();
		for (String matcherName : allExtensions) {
			if(newName.endsWith(matcherName)) {
				String substringAfterLast = StringUtils.substringAfterLast(newName, ".");
				newName = StringUtils.replace(newName, matcherName, "."+substringAfterLast);
				break;
			}
		}
		if(sourceFile.isNoDate()) {
			helper.print('x');
		}
		try {

			File afile = sourceFile.getFile();
			String newDir = String.format("%s\\%s", rootDir, subDir);
			new File(newDir).mkdirs();
			File bfile = new File(String.format("%s\\%s\\%s", rootDir, subDir, newName));
			if (bfile.exists()) {
				newName="DUP_"+newName;
			}
			inStream = new FileInputStream(afile);
			outStream = new FileOutputStream(bfile);
			byte[] buffer = new byte[1024];
			int length;
			// copy the file content in bytes
			while ((length = inStream.read(buffer)) > 0) {
				outStream.write(buffer, 0, length);
			}
			inStream.close();
			outStream.close();
			afile.delete();

		} catch (IOException e) {
			e.printStackTrace();
		}
	}	
}
