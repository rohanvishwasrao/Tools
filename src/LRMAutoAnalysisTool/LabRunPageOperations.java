package LRMAutoAnalysisTool;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.WebDriverWait;

import com.thoughtworks.selenium.Wait;

public class LabRunPageOperations implements ICommonOperations{
	 private WebDriver webDriver;
	 private ConfigReader config;
	 WebDriverWait wait ;
	 
	public LabRunPageOperations(WebDriver webDriver) {
		this.webDriver = webDriver;
		this.config = new ConfigReader();
		wait = new WebDriverWait(webDriver, 60);
	}
	 
	static class RunStats{
		int passPercent;
		int failPercent;
		int totalTestCases;
	}
	 class FailureDetails{
		String error;
		String site;
		String langId;
		String testSuite;
	}

	@Override
	public boolean startRun(LRMAutoAnalysisTool inst) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean stopRun() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void analyzeRun() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public ArrayList<String> getRunStatusForAllTemplates() {
		// TODO Auto-generated method stub
		return null;
	}

	public void rerunTestScenarios() {
		try
		{
			config.getProperties();
			int currRunIteration = 0;
			while(currRunIteration != Integer.parseInt(config.runCount))
			{
				if(startMonitoringTheRun())
				{
					//Kick off another iteration of failed test cases
					System.out.println("Finished the "+ currRunIteration +" iterations of the run");
					System.out.println("Kick off another iteration of failed test cases");
					rerunFailedScenarios();
					//Commenting the option to execute all the scenarios
					//rerurnAllTheScenarios();
					currRunIteration++;
					
				}
				else throw new Exception("LRM run is not responding!");
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();;
		}
		
	}
	
	public boolean startMonitoringTheRun() {
	try
	{
		webDriver.navigate().refresh();
		int completedRuns = getCompleteRunCount();
		int totalRuns = getTotalRunCount("LabRunsGridView_BoundField_DisplayStatus");
		while(completedRuns!=totalRuns)
		{
			System.out.println("Sleeping for 2 mins...");
			Thread.sleep(1000*60*2);
			System.out.println("Refresing page...");
			webDriver.navigate().refresh();
			completedRuns = getCompleteRunCount();
			totalRuns = getTotalRunCount("LabRunsGridView_BoundField_DisplayStatus");
		}
		if(completedRuns == totalRuns) return true;
		
	} 
	catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	return false;
	}
	private int getCompleteRunCount()
	{
		int completedCount = 0;
        List<WebElement> elements =  webDriver.findElements(By.id("LabRunsGridView_BoundField_DisplayStatus"));
        int iteration = 3;
        for(WebElement e : elements)
        {
        	if(e.getText().compareTo("Complete")==0 || hasCompletedAllTestCases(iteration)) 
        	{
        		completedCount++;
        	}
        	int totalRuns = getTotalRunCount("LabRunsGridView_BoundField_DisplayStatus");
        	if(iteration-2 < totalRuns)iteration++;
        }
        System.out.println("Completed Runs "+completedCount);
        return completedCount;
	}
	private int getTotalRunCount(String id)
	{
		List<WebElement> elements =  webDriver.findElements(By.id(id));
      //  System.out.println("Total Runs "+elements.size());
        return elements.size();
	}

	public boolean rerunFailedScenarios() {
		
		findByIdAndClick("chkAllItems");
		findByIdAndClick("tfx_TfxContent_RerunFailedAssignmentsLink");
		return true;
	}
	
	private void findByIdAndClick(String id)
	{
		WebElement element = webDriver.findElement(By.id(id));
		element.click();
	}

	public boolean rerurnAllTheScenarios() {
		
		findByIdAndClick("chkAllItems");
		findByIdAndClick("tfx_TfxContent_StartLabRunsLink");
		try {
			Thread.sleep(1000*15);
			webDriver.navigate().refresh();
		} catch (InterruptedException e) {
		
			e.printStackTrace();
		}
		return true;
	}

	@Override
	public void createALabRun() {
		
		
	}

	@Override
	public void analyzeLabRun() {
		try {
			int totalTemplateRunCount = getTotalRunCount("LabRunsGridView_BoundField_DisplayStatus");
			Map<String, List<FailureDetails>> failureTypeMap = new HashMap<String,List<FailureDetails>>();
			int iteration = 1;
			ExpectedCondition < Boolean > pageLoad = new
				    ExpectedCondition < Boolean > () {
				        public Boolean apply(WebDriver driver) {
				            return ((JavascriptExecutor) driver).executeScript("return document.readyState").equals("complete");
				        }
				    };
			while(iteration <= totalTemplateRunCount)
			{
				int runNo = iteration +2;
				String runNum="";
				if(runNo <= 9) 
				{
					StringBuffer appendZero = new StringBuffer();
					appendZero.append("0");
					appendZero.append(runNo);
					runNum = appendZero.toString();
				}
				else
				{
					runNum = String.valueOf(runNo);
				}
				String templRunXPath = "//*[@id='tfx_TfxContent_LabRunsGridView_ctl"+runNum+"_LabRunNameLink']";
				WebElement templateRunLink = webDriver.findElement(By.xpath(templRunXPath));
				System.out.println();
				System.out.println("TEMPLATE DETAILS: "+templateRunLink.getText());
				if(hasFailedTestCases(iteration+2))
				{
					System.out.println(" > There were more than 5% failures - Analyzing the results");
					try {
						failureTypeMap = extractFailures(templateRunLink);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					wait.until(pageLoad);
					webDriver.navigate().back();
					Thread.sleep(5000);
				}
				else System.out.println(" >> No failures for the run");
				iteration++;
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private boolean hasFailedTestCases(int row)
	{
		boolean hasFailed = false;
		WebElement passFailElement = webDriver.findElement(By.xpath("//*[@id='tfx_TfxContent_LabRunsGridView']/tbody/tr["+row+"]/td[5]/span/img"));
		String runStats = passFailElement.getAttribute("title");
		if(getFailedPercentage(runStats) > 5)
		{
			hasFailed = true;
		}
		return hasFailed;
	}
	private int getFailedPercentage(String runStatus)
	{
		FileOperations fileOps = new FileOperations();
		String percent = fileOps.readFromAnotherString(runStatus, "Fail:");
		int pc = Integer.parseInt(percent.replaceAll("%", ""));
		return pc;
	}
	
	private boolean hasCompletedAllTestCases(int row)
	{
		boolean hasFailed = false;
		WebElement passFailElement = webDriver.findElement(By.xpath("//*[@id='tfx_TfxContent_LabRunsGridView']/tbody/tr["+row+"]/td[5]/span/img"));
		String runStats = passFailElement.getAttribute("title");
		if(getCompletedPercentage(runStats) == 100 )
		{
			hasFailed = true;
		}
		return hasFailed;
	}
	private int getCompletedPercentage(String runStatus)
	{
		FileOperations fileOps = new FileOperations();
		String percent = fileOps.readFromAnotherString(runStatus, "Complete:");
		int pc = Integer.parseInt(percent.replaceAll("%", ""));
		return pc;
	}
	
	private Map<String,List<FailureDetails>> extractFailures(WebElement templateRunLink) throws InterruptedException
	{
		Map<String, List<FailureDetails>> failedTestCases = null; 
		try {
				failedTestCases = new HashMap<String, List<FailureDetails>>();
				templateRunLink.click();
				Thread.sleep(5000);
				int totalTestCount = getTotalRunCount("AssignmentDetailsGridView_BoundField_ClientName");
				System.out.println(" > Total Test Cases in the template: " +totalTestCount);
				System.out.println("----------------------------------------------");
				webDriver.findElement(By.id("Header_ConfigDiv")).click();
				Thread.sleep(2000);
				int metaDataCount =0;
				for(int i = 3; i <=totalTestCount+2 ;i++)
				{
					String error = getTestCaseDetails("//*[@id='tfx_TfxContent_AssignmentDetailsGridView']/tbody/tr["+i+"]/td[3]/div[3]/span[1]").getText();
					if(!error.isEmpty())
					{
						FailureDetails fdetails = new FailureDetails();
						fdetails.error = error;
						String tag = "//*[@id='AssignmentRelatedConfigsDiv" + metaDataCount + "']";
						System.out.println(tag);
						String testCaseName = getTestCaseDetails("//*[@id='tfx_TfxContent_AssignmentDetailsGridView']/tbody/tr["+i+"]/td[3]/a/span[2]").getText();
						String site = getTestCaseDetails(tag+"/div[2]/div[1]/table/tbody/tr[7]/td[2]").getText();
						fdetails.site = site;
						String testSuite = getTestCaseDetails("//*[@id='tfx_TfxContent_AssignmentDetailsGridView']/tbody/tr["+i+"]/td[3]/a/span[1]").getText();
						fdetails.testSuite = testSuite;
						String langId = getTestCaseDetails(tag+"/div[2]/div[1]/table/tbody/tr[5]/td[2]").getText(); 
						fdetails.langId = langId;
						if(failedTestCases.get(testCaseName) == null)
						{
							List<FailureDetails> multipleFailures = new ArrayList<FailureDetails>();
							multipleFailures.add(fdetails);
							failedTestCases.put(testCaseName, multipleFailures);
						}
						else
						{
							List<FailureDetails> placeHolder = failedTestCases.get(testCaseName);
							placeHolder.add(fdetails);
							failedTestCases.put(testCaseName, placeHolder);
						}
					}
					if(metaDataCount<totalTestCount)metaDataCount++;
				}
		
				printErrorDetailsMap(failedTestCases);
				System.out.println("----------------------------------------------");
				System.out.println();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return failedTestCases;
	}

	private void printErrorDetailsMap(
			Map<String, List<FailureDetails>> failedTestCases) {
		for (Entry<String, List<FailureDetails>> entry : failedTestCases.entrySet()) {
				System.out.println();
			 	System.out.println(entry.getKey().toUpperCase());
			 	List<FailureDetails> values = entry.getValue();
			 	for(FailureDetails failure: values)
			 	{
			 		System.out.println("   TC Failure: " + failure.error);
			 		System.out.println("   TC Site: " + failure.site);
			 		System.out.println("   TC Language ID: " + failure.langId);
			 		System.out.println("   TC Suite: " + failure.testSuite);
			 		System.out.println();
			 	}
			}
	}
	
	private WebElement getTestCaseDetails(String xpath)
	{
		return webDriver.findElement(By.xpath(xpath));
	}

}
