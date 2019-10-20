package com.davehampton.filemerger;

import java.io.IOException;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.davehampton.filemerger.app.MergeFiles;
import com.drew.imaging.ImageProcessingException;
import com.drew.metadata.MetadataException;

public class App {
	public static void main(String[] args) throws ImageProcessingException, MetadataException, IOException {
		
		ApplicationContext context = 
		    	  new ClassPathXmlApplicationContext("application-config.xml");
		 
		    	context.getBean(MergeFiles.class).run();
		
	}
}
