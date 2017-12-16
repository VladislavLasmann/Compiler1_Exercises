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
package mavlc.frontend.error;

import static org.junit.Assert.assertTrue;

import java.io.File;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import mavlc.Main;
import mavlc.ast.serialization.ErrorXMLSerialization;
import mavlc.error_reporting.CompilationError;
import mavlc.test_wrapper.ErrorTestWrapper;

@RunWith(Parameterized.class)
public class ErrorCompareTest {

	protected final File wrapperFile;

	public ErrorCompareTest(File testWrapperFile, String name) {
		wrapperFile = testWrapperFile;
	}

	@Test
	public void testExample() {
		ErrorXMLSerialization serializer = new ErrorXMLSerialization();
		ErrorTestWrapper wrapper = (ErrorTestWrapper) serializer.deserialize(wrapperFile.getAbsolutePath());
		String path = wrapperFile.getParentFile().getAbsolutePath();
		String sourceInput = wrapper.getTestFile();
		boolean onlySyntax = wrapper.isOnlySyntax();
		CompilationError referenceError = wrapper.getError();
		
		List<String> cliArguments = new LinkedList<>();
		String inputFile = path+File.separator+sourceInput;
		cliArguments.add(inputFile);
		String outputDir = "build"+File.separator+"test-output";
		new File(outputDir).mkdirs(); // ensure output directory exists
		String outputFile = outputDir+File.separator+sourceInput+".test.xml";
		cliArguments.add("-o");
		cliArguments.add(outputFile);
		if(onlySyntax){
			cliArguments.add("-syn");
		}
		else{
			cliArguments.add("-ctx");
		}
		String [] args = cliArguments.toArray(new String[cliArguments.size()]);
		CompilationError error = null;
		System.err.println("Running testcase "+sourceInput);
		try{
			Main.main(args);
		}
		catch(CompilationError e){
			error = e;
		}
		assertTrue("Expected an error but got none!", error!=null);
		StringBuilder sb = new StringBuilder();
		sb.append("Error thrown by the compiler does not match the expectation!\n");
		sb.append("Expected error: \n");
		sb.append(referenceError.getMessage());
		sb.append("\nError thrown by compiler:\n");
		sb.append(error.getMessage());
		assertTrue(sb.toString(), referenceError.getMessage().equals(error.getMessage()));
		System.err.println("-->Successful");

	}

	@Parameters(name="{1}")
	public static Collection<Object[]> data() {
		/*
		 * Iterate over all XML-files in the testcases/trees directory to
		 * collect testcases
		 */
		List<Object[]> testcases = new LinkedList<>();
		List<File> inputFiles = getAllXMLFiles(new File("src"+File.separator+"test"+File.separator+"testcases"+File.separator+"errors"));
		System.out.println(inputFiles);
		for(File input : inputFiles){
			String name = input.getName().substring(0, input.getName().indexOf('.'));
			testcases.add(new Object[]{input, name});
		}
		return testcases;
	}

	private static List<File> getAllXMLFiles(File directory) {
		List<File> result = new LinkedList<>();
		File[] listOfFiles = directory.listFiles();
		for (File entry : listOfFiles) {
			if (entry.isFile()) {
				if (entry.getName().endsWith(".xml")) {
					result.add(entry);
				}
			} else if (entry.isDirectory()) {
				result.addAll(getAllXMLFiles(entry));
			}
		}
		return result;
	}

}
