/*******************************************************************************
 * Copyright (C) 2016, 2017 Embedded Systems and Applications Group
 * Department of Computer Science, Technische Universitaet Darmstadt,
 * Hochschulstr. 10, 64289 Darmstadt, Germany.
 * 
 * All rights reserved.
 * 
 * This software is provided free for educational use only.
 * It may not be used for commercial purposes without the
 * prior written permission of the authors.
 ******************************************************************************/

package mavlc.codegen.output;

import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import MTAM.OutputSerializer;
import MTAM.TestInterpreter;
import mavlc.Main;

@RunWith(Parameterized.class)
public class OutputCompareTest {
	
	protected final File inputFile;
	
	protected final String testcaseName;
	
	public OutputCompareTest(File testInputFile, String name){
		inputFile = testInputFile;
		testcaseName = name;
	}
	
	@Test
	public void testExample(){
		System.out.println(inputFile);
		String referenceOutput =inputFile.getPath().substring(0, inputFile.getPath().lastIndexOf('.'))+".txt";
		System.out.println(referenceOutput);
		List<String> cliArguments = new LinkedList<>();
		String input = inputFile.getPath();
		cliArguments.add(input);
		String outputDir = "build"+File.separator+"test-output";
		new File(outputDir).mkdirs(); // ensure output directory exists
		String outputFile = outputDir+File.separator+"a.tam";
		cliArguments.add("-o");
		cliArguments.add(outputFile);
		String [] args = cliArguments.toArray(new String[cliArguments.size()]);
		System.err.println("Running testcase "+testcaseName);
		Main.main(args);
		
		cliArguments.clear();
		cliArguments.add("-f");
		cliArguments.add(outputFile);
		args = cliArguments.toArray(new String[cliArguments.size()]);
		System.err.println("Executing binary!");
		TestInterpreter.main(args);
		
		File reference = new File(referenceOutput);
		File output = new File(OutputSerializer.outputFile);
		
		try {
			StringBuilder sb = new StringBuilder("Resulting output does not match reference:\n");
			sb.append("Reference:\n").append(FileUtils.readFileToString(reference)).append("\n");
			sb.append("Output:\n").append(FileUtils.readFileToString(output)).append("\n");
			assertTrue(sb.toString(), FileUtils.contentEqualsIgnoreEOL(reference, output, "UTF-8"));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	@Parameters(name="{1}")
	public static Collection<Object[]> data(){
		/*
		 * Iterate over all XML-files in the testcases/trees directory to
		 * collect testcases
		 */
		List<Object[]> testcases = new LinkedList<>();
		List<File> inputFiles = getAllMAVLFiles(new File("src"+File.separator+"test"+File.separator+"testcases"+File.separator+"executables"));
		System.out.println(inputFiles);
		for(File input : inputFiles){
			String name = input.getName().substring(0, input.getName().indexOf('.'));
			testcases.add(new Object[]{input, name});
		}
		return testcases;
	}
	
	private static List<File> getAllMAVLFiles(File directory) {
		List<File> result = new LinkedList<>();
		File[] listOfFiles = directory.listFiles();
		for (File entry : listOfFiles) {
			if (entry.isFile()) {
				if (entry.getName().endsWith(".mavl")) {
					result.add(entry);
				}
			} else if (entry.isDirectory()) {
				result.addAll(getAllMAVLFiles(entry));
			}
		}
		return result;
	}
}
