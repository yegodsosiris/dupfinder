package com.davehampton.filemerger.app.util;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.davehampton.filemerger.app.model.MyFile;
import com.drew.imaging.ImageMetadataReader;
import com.drew.imaging.ImageProcessingException;
import com.drew.metadata.Directory;
import com.drew.metadata.Metadata;
import com.drew.metadata.MetadataException;
import com.drew.metadata.exif.ExifDirectory;

@Component
public class CommonHelper {
	
	private int prinitCnt = 0;

	@Value("#{props.validExtensions}")
	private String validExtensions;

	@Value("#{props.copyNameSuffices}")
	private String copyNameSuffices;

	int fileCount;
	
	List<String> allExtensions = null;
	
	public List<String> getAllExtensions() {
		return allExtensions;
	}
	
	public int getFileCount() {
		return fileCount;
	}
	
	public void init() {
		if(allExtensions==null) {
			allExtensions = new ArrayList<String>();
			String[] extensions = validExtensions.split(",");
			String[] suffices = copyNameSuffices.split(",");
			for (String extension : extensions) {
				for (String suf : suffices) {
					if(suf.contains("%s")) {
						for (int i=1;i<11;i++) {
							allExtensions.add(String.format("%s%s", String.format(suf, i).toLowerCase(), extension.toLowerCase().trim()));
						}
					}
					else {
						allExtensions.add(String.format("%s%s", suf.toLowerCase(), extension.toLowerCase().trim()));
					}
				}
			}
		}
	}

	public void processDir(String d, Map<String, List<MyFile>> allFiles) throws IOException, ImageProcessingException, MetadataException {
		init();
		System.out.println("Scanning " + d);
		File folder = new File(d);
		File[] listOfFiles = folder.listFiles();
		List<File> asList = null;
		try {
			asList = Arrays.asList(listOfFiles);
		} catch (NullPointerException e) {
			System.err.println("Directory does not exist - "+d);
		}
		for (File file : asList) {
			String name = file.getName();
			if (file.isFile()) {
				fileCount++;
				if (validExtension(name)) {
					addToCollection(allFiles, new MyFile(file));
				}
			} else if (file.isDirectory()) {
				processDir(d + "\\" + name, allFiles);
			}
		}
	}
	
	private boolean validExtension(String name) {
		name=name.toLowerCase();
		String[] split = validExtensions.split(",");
		for (String extension : split) {
			if (name.endsWith(extension.trim())){
				return true;
			}
		}
		return false;
	}
	
	public DateTime getDateTaken(MyFile myFile) throws ImageProcessingException {
		Directory directory = null;
		try {
			Metadata metadata = ImageMetadataReader.readMetadata( myFile.getFile() );
			directory = metadata.getDirectory( ExifDirectory.class );
		} catch (Exception e) {
			return new DateTime(myFile.getFile().lastModified());
		}
		
		Date date;
		try {
			date = directory.getDate( ExifDirectory.TAG_DATETIME_ORIGINAL );
			return new DateTime(date);
		} catch (MetadataException e) {
			myFile.setNoDate(true);
			return new DateTime(myFile.getFile().lastModified());
		}
	}


	public void addToCollection(Map<String, List<MyFile>> map, MyFile data) throws ImageProcessingException {
		if(data.getName().contains("img_20130203_150233 (2)")) {
			System.out.println();
		}
		data.setDateTaken(this);
		String key = getKey(data);
		List<MyFile> list = map.get(key);
		if (list!=null) {
			list.add(data);
		} else {
			list = new ArrayList<MyFile>();
			list.add(data);
			map.put(key, list);
		}
	}
	
	public void setPrinitCnt(int prinitCnt) {
		this.prinitCnt = prinitCnt;
	}

	public void print(char c) {
		int i = prinitCnt  % 100;
		if (i == 0) {
			System.out.println(" ("+prinitCnt+"/"+fileCount+")");
		} 
		System.out.print(c);
		prinitCnt++;
	}

	public String getKey(MyFile myFile) {
		String data = myFile.getName();
		String key = null;
		for(String matcherName: allExtensions) {
			boolean endsWith = data.toLowerCase().endsWith(matcherName);
			if (endsWith) {
				key = StringUtils.substringBefore(data, matcherName);
				break;
			}
		}
		if (key==null) {
			key = StringUtils.substringBefore(data, ".");
		}
		return StringUtils.replace(key+myFile.getDateTaken().getMillis(), " ", "");
	}


}
