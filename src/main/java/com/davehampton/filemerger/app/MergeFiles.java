package com.davehampton.filemerger.app;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TimeZone;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.davehampton.filemerger.app.model.FileCollator;
import com.davehampton.filemerger.app.model.MyFile;
import com.davehampton.filemerger.app.util.CommonHelper;
import com.davehampton.filemerger.app.util.DuplicateFileCopier;
import com.davehampton.filemerger.app.util.GoodFileCopier;
import com.drew.imaging.ImageProcessingException;
import com.drew.metadata.MetadataException;

@Component
public class MergeFiles {

	@Value("#{props.source}")
	private String SOURCE;

	int prinitCnt = 0;

	@Autowired
	CommonHelper helper;
	
	@Autowired
	GoodFileCopier goodFileCopier;
	
	@Autowired
	DuplicateFileCopier duplicateFileCopier;

	public void run() throws ImageProcessingException, MetadataException, IOException {
		final long startMillis = System.currentTimeMillis();
		Map<String, List<MyFile>> sourceFiles = new HashMap<String, List<MyFile>>();

		System.out.println("Collating fileCount");

		String[] split = SOURCE.split(",");
		for (String source : split) {
			helper.processDir(source.trim(), sourceFiles);
		}
		List<FileCollator> fileCollators = getFileCollators(sourceFiles);
		moveFiles(fileCollators);
		System.out.println("Completed");

        final long duration = System.currentTimeMillis() - startMillis;
        DateFormat df = new SimpleDateFormat("HH 'hours', mm 'mins,' ss 'seconds'");
        df.setTimeZone(TimeZone.getTimeZone("GMT+0"));
        System.out.println(df.format(new Date(duration)));
	}

	private List<FileCollator> getFileCollators(Map<String, List<MyFile>> sourceFiles) throws ImageProcessingException,
			IOException {
		System.out.println("Collating .=normal ?=noDateTaken");
		List<FileCollator> fileCollators = new ArrayList<FileCollator>();
		Set<String> keySet = sourceFiles.keySet();
		for (String key : keySet) {
			helper.print('.');
			List<MyFile> list = sourceFiles.get(key);
			FileCollator sourceFileContainer = collateFiles(list);
			fileCollators.add(sourceFileContainer);

		}
		return fileCollators;
	}

	private List<FileCollator> moveFiles(List<FileCollator> fileCollators) throws ImageProcessingException,
			IOException {
		
		
		System.out.println("\n============\nMoving files\n============");
		helper.setPrinitCnt(0);
		for (FileCollator fileCollator : fileCollators) {
			helper.print('+');
			MyFile keep = fileCollator.getKeep();
			goodFileCopier.copyGood(keep);
			List<MyFile> older = fileCollator.getOlder();
			for (MyFile myFile : older) {
				helper.print('-');
				duplicateFileCopier.copyOlder(myFile, keep);
			}
		}
		return fileCollators;
	}

	
	
	private FileCollator collateFiles(List<MyFile> list) throws ImageProcessingException, IOException {
		FileCollator fileCollator = new FileCollator();
		if (list.size() == 1) {
			MyFile toKeep = list.get(0);
			// Only keep this file if it has a Date Taken. We'll deal with this later on in the method
			fileCollator.setKeep(toKeep);
		} else {
			for (MyFile file : list) {
				if (fileCollator.getKeep() == null) {
					fileCollator.setKeep(file);
				} else {
					MyFile toKeep = fileCollator.getKeep();
					if (!file.isNoDate()) {
						/*
						 *  To the microsecond. Almost impossible for 2 entirely different photos
						 *  to share the same millisecond value.
						 */
						if (file.getDateTaken().equals(toKeep.getDateTaken())) {
								// Larger file takes presidence.
	 							if (file.getSize() > toKeep.getSize()) {
									fileCollator.setKeep(file);
									break;
								}
	 							// Similar name, same size - do nothing
	 							else if(file.getSize()==toKeep.getSize()) {
									break;
								}
	 							else {
	 								// Fix for MIXes only
	 								if (file.getName().indexOf("MIX")!=-1) {
										fileCollator.addOlder(file);
									}
									else {
										// If we have a MIX as keeping and the new file is not a MIX then the new file takes presedence
										if (toKeep.getName().indexOf("MIX")!=-1 && file.getName().indexOf("MIX")==-1) {
											fileCollator.setKeep(file);
										}
									}
	 								// END Fix for MIXes only
	 							}
						} 
						else if (file.getDateTaken().isBefore(toKeep.getDateTaken())) {
							fileCollator.setKeep(file);
							
						} else {
							fileCollator.addOlder(file);
						}
					} else {
						if (!(file.getDateTaken().equals(toKeep.getDateTaken())&& file.getSize()==toKeep.getSize())) {
							if(file.getName().equals(toKeep.getName()+".jpg")) {
								break;
							}
							if(!toKeep.getName().contains("MIX") && toKeep.getSize() > file.getSize() && toKeep.getDirectory().equals(file.getDirectory())) {
								break;
							}
							fileCollator.addOlder(file);
						}
					}
				}
			}
		}
		return fileCollator;
	}
}
