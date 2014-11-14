package it.uniroma2.reasoner.ConfigurationHandler;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

import org.apache.commons.io.FileUtils;

/**
 * Management the application configuration parameters. Parameters are specified into a properties file. 
 * 
 * This class is implemented by singleton pattern,  to istanziate a new object of this
 * class you must call the method getIstance(). 
 * 
 * To access a parameter value, you must call getPropertyMethod and use the the constants specified into this class 
 * to get the names of the available parameters. Similarly to modify the parameters. 
 * 
 * @author Giovanni Lorenzo Napoleoni
 *
 */
public class ConfigurationParameter {

	private static ConfigurationParameter istance;
	
	private Properties prop;
	
	public static final String CONFIGURATION_FILE_NAME = "config.properties";
	
	public static final String FILE_PATH_RULES = "reasoner.inferencerules.file.path";

	public static final String PRODUCE_OUTPUT = "ouput.produce";

	public static final String NUMBER_OF_EXECUTION = "number.of.cycle.resoning";
	
	public static final String WHICHRULEEXECUTE = "which.rule.execute";
	
	private static final String DEFAULT_RULES_FILE ="./default_rule_file.txt";
	
	public static final String EDITABLE_RULES_FILE = "editable_rule_file.txt";
	
	public String path;
	
	
	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	private ConfigurationParameter() throws FileNotFoundException, IOException{
		//Create new Properties Object
		prop = new Properties();
		//Load properties
		prop.load(getClass().getClassLoader().getResourceAsStream(CONFIGURATION_FILE_NAME));
		path = getClass().getClassLoader().getResource(CONFIGURATION_FILE_NAME).getPath();
	}

	/**
	 * Get the only instance of configuration Parameter class if it exists, otherwise calls the private constructor class.
	 * 
	 * @return instance of Configuration Parameter
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	public static ConfigurationParameter getIstance() throws FileNotFoundException, IOException {
		if(istance == null){
			
			istance = new ConfigurationParameter();
		}
		
		return istance;
	}
	
	/**
	 * Get the value of parameter. 
	 * 
	 * @param propertyName the name of property
	 * @return the value of property
	 */
	public String getProperty(String propertyName){
		return prop.getProperty(propertyName);
	}
	
	
	/**
	 * Change the parameter identified by the key passed as a parameter.
	 * @param key name of parameter
	 * @param value new value of parameter
	 * @throws IOException
	 */
	public void modifyProperties(String key,String value) throws IOException{
		//Get properties file
		//File f = new File(getClass().getClassLoader().getResource(CONFIGURATION_FILE_NAME_OUTPUT).getFile());
		//Creat a file stream
		//FileOutputStream out = new FileOutputStream(f);
		//Update the property value
		prop.setProperty(key, value);
		//save update
		//prop.store(out, null);
		//close file stream
        //out.close();
	}
	
	public void restoreDefaultInferenceRuleFile() throws IOException{
		getClass().getClassLoader().getResource("editable_rule_file.txt").getFile();
		
		
		
		String path = getClass().getClassLoader().getResource(EDITABLE_RULES_FILE).getPath();
		FileUtils.forceDelete(new File(getClass().getClassLoader().getResource(EDITABLE_RULES_FILE).getPath()));
		FileUtils.copyFile(new File(getClass().getClassLoader().getResource(DEFAULT_RULES_FILE).getPath()), new File(path));
		
		
	}
	
}
