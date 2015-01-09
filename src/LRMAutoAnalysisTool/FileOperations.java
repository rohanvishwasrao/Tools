package LRMAutoAnalysisTool;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.Reader;
import java.util.Scanner;
import java.util.StringTokenizer;

public class FileOperations {

	public String readFromFile(String filePath)
	{
		return null;
	}
	public String readFromAnotherString(String stringToReadFrom, String previousWord)
	{
		try
		{
			StringTokenizer tokens = new StringTokenizer(stringToReadFrom);
			String curr = tokens.nextToken();
			while(curr!=null)
			{
				if(curr.equals(previousWord))
					return tokens.nextToken();
				curr = tokens.nextToken();
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return null;
	}
	public boolean writeResultsToAFile(String outputFileName)
	{
		File opFile = createFileIfNotCreatedAlready(outputFileName);
		return writeDataToFile();
	}
	
	private File createFileIfNotCreatedAlready(String outputFileName)
	{
		File outputFile = new File(outputFileName);
		try {
			if(!outputFile.exists())
			{
				outputFile.createNewFile();
			}
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return outputFile;
	}
	private boolean writeDataToFile()
	{
		return false;
	}
	
	
}
