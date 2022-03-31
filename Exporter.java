/*****************************************************************************
 * Name : Mike
 * Date : 30 Mar 2022
 * File : Exporter.java
 *****************************************************************************/

import ghidra.app.util.Option;
import ghidra.app.util.OptionException;
import ghidra.app.util.exporter.CppExporter;
import ghidra.app.util.exporter.ExporterException;
import ghidra.app.util.headless.HeadlessScript;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Exporter extends HeadlessScript
{
	@Override
	public void run()
	{
		if (!isRunningHeadless())
		{
			printerr("This script is intended for use in headless mode only !");
			return;
		}
		
		String[] strArgumentsArray = getScriptArgs();
		if (strArgumentsArray.length == 0)
		{
			printerr("No output filepath was provided !");
			return;
		}

		String strOutputFilepath = strArgumentsArray[0];
		if (strOutputFilepath.length() == 0 || strOutputFilepath.length() > 255)
		{
			printerr("The provided output filepath '" + strOutputFilepath + "' is invalid !");
			return;
		}

		File fileOutput = new File(strOutputFilepath);
		try
		{
			if (fileOutput.exists())
			{
				printerr("The file '" + strOutputFilepath + "' already exists !");
				return;
			}
		}
		catch (SecurityException securityException)
		{
			printerr("Unable to access the file '" + strOutputFilepath + "', permission denied !");
			return;
		}

		CppExporter cppExporter = new CppExporter();
		cppExporter.setExporterServiceProvider(state.getTool());
		
		List<Option> optionsList = new ArrayList<Option>();
		optionsList.add(new Option(CppExporter.CREATE_C_FILE, true));
		try
		{
			cppExporter.setOptions(optionsList);
		}
		catch (OptionException optionException)
		{
			printerr("Failed to set the options for the object 'CppExporter' !");
			return;
		}
		
		try
		{
			cppExporter.export(fileOutput, currentProgram, null, monitor);
		}
		catch (ExporterException exporterException)
		{
			printerr("Failed to export the C code from the input file !");
			return;
		}
		catch (IOException ioException)
		{
			printerr("Failed to save the exported C code to the file '" + strOutputFilepath + "' !");
			return;
		}

		printf("Successfully saved the exported C code to the file '%s' !\n", strOutputFilepath);
	}
}
