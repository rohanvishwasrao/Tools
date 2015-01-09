package LRMAutoAnalysisTool;

import java.util.ArrayList;
//Includes interface implementation

public interface ICommonOperations {
	public boolean startRun(LRMAutoAnalysisTool inst);
	public boolean stopRun();
	public void analyzeRun();
	public ArrayList<String> getRunStatusForAllTemplates();
	public boolean startMonitoringTheRun();
	public void rerunTestScenarios();
	public boolean rerunFailedScenarios();
	public boolean rerurnAllTheScenarios();
	public void createALabRun();
	public void analyzeLabRun();
	
}
