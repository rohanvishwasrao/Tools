package LRMAutoAnalysisTool;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;

import org.openqa.selenium.*;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;

public  class LRMAutoAnalysisTool {
	
	private WebDriver driver;
	private URL browserURL;
	private String lrmConnectionURL;
	private String userName;
	private String password;
	
	public LRMAutoAnalysisTool(String browserURL, String lrmConnectionURL, String userName, String pwd) throws MalformedURLException
	{
		this.setLrmConnectionURL(lrmConnectionURL); 
		this.setUserName(userName); 
		this.setPassword(pwd); 
	}
	public String getLrmConnectionURL() {
		return lrmConnectionURL;
	}

	public void setLrmConnectionURL(String lrmConnectionURL) {
		this.lrmConnectionURL = lrmConnectionURL;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}
	
	public static void main(String [] args)
	{
		try 
 	 	{
			ConfigReader config = new ConfigReader();
			config.getProperties();
			LRMAutoAnalysisTool inst = new LRMAutoAnalysisTool( config.browserURL,config.lrmConnectionURL,config.userName,config.password);
			String completeURL = formatURL(config.lrmConnectionURL,config.userName,config.password,config.templateId);
			inst.driver = setUpConnection(inst.browserURL,config.browserType);
			inst.driver.get(completeURL);
			ICommonOperations commonOps = new LabRunPageOperations(inst.driver);
			//commonOps.rerunTestScenarios();
			commonOps.analyzeLabRun();
			FileOperations ops = new FileOperations();
			ops.writeResultsToAFile(config.outputFileName);
			System.out.println(inst.driver.getTitle());
		
		}
        catch (MalformedURLException e) 
		{
			e.printStackTrace();
		}
		catch(IOException e)
		{
			e.printStackTrace();
		}
	}

	private static WebDriver setUpConnection(URL browserURL, String browserType) {
		if(browserType.equals("chrome"))
		{
			DesiredCapabilities capabilities = DesiredCapabilities.chrome();
			capabilities.setCapability("chrome.switches", Arrays.asList("--disable-logging","--start-maximized",
				    "--disable-popup-blocking"));
			return new RemoteWebDriver(browserURL,capabilities); 
		}
		return null;
	}

	private static String formatURL(String lrmConnectionURL, String userName,
			String password, String templateId) {
		String firstHalf = "http://"+userName+":"+password+"@";
		return firstHalf+lrmConnectionURL+templateId;
	}

		
}

