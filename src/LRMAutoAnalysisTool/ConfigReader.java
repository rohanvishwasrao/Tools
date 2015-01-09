package LRMAutoAnalysisTool;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;
import java.io.InputStream;

public class ConfigReader {
	public String browserURL ;
	public String lrmConnectionURL; 
	String userName; 
	String password; 
	String browserType;
	String templateId;
	String runCount;
	String outputFileName;
	public void getProperties() throws IOException{
		Properties prop = new Properties();
		String propFileName = "config.properties";
 
		InputStream inputStream = ConfigReader.class.getClassLoader().getResourceAsStream(propFileName);
		prop.load(inputStream);
		if (inputStream == null) {
			throw new FileNotFoundException("property file '" + propFileName + "' not found in the classpath");
		}
		templateId = prop.getProperty("templateId");
		browserURL = prop.getProperty("browserURL");
		lrmConnectionURL = prop.getProperty("lrmConnectionURL");
		userName = prop.getProperty("userName");
		password = prop.getProperty("password");
		browserType = prop.getProperty("browserType");
		runCount = prop.getProperty("runCount");
		outputFileName = prop.getProperty("outputFileName");
		
 	}
}
