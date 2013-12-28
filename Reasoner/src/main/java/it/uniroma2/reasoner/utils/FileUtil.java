package it.uniroma2.reasoner.utils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;

/**
 * Utily class to manage file operation
 * 
 * @author Giovanni Lorenzo Napoleoni
 *
 */
public class FileUtil {
	
	public File getFileFromPath(String pathToFile){
		
		File file = new File(getClass().getClassLoader().getResource(pathToFile).getPath()) ;
		return file;
	}
	
	public  InputStream getFileInputStreamFromFile(String nameOfFile) throws FileNotFoundException{
		
		InputStream stream = getClass().getClassLoader().getResourceAsStream(nameOfFile);
		
		
		//FileInputStream fileInputStream = new FileInputStream(file);
		return stream;
	}
}
