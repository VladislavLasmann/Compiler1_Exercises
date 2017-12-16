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
package mavlc.frontend.ast;

import static org.junit.Assert.assertThat;

import java.io.File;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;
import org.xmlunit.matchers.CompareMatcher;

import mavlc.Main;
import mavlc.ast.nodes.module.Module;
import mavlc.ast.serialization.ASTSerialization;
import mavlc.ast.serialization.ASTXMLSerialization;
import mavlc.test_wrapper.ASTTestWrapper;

@RunWith(Parameterized.class)
public class ASTCompareTest {

	protected final File wrapperFile;

	public ASTCompareTest(File testWrapperFile, String name) {
		wrapperFile = testWrapperFile;
	}

	@Test
	public void testExample() {
		ASTSerialization serializer = new ASTXMLSerialization();
		ASTTestWrapper wrapper = (ASTTestWrapper) serializer.deserialize(wrapperFile.getAbsolutePath());
		String path = wrapperFile.getParentFile().getAbsolutePath();
		String sourceInput = wrapper.getTestFile();
		boolean onlySyntax = wrapper.isOnlySyntax();
		Module referenceASt = wrapper.getAST();
		String expected = new ASTXMLSerialization().serialize(referenceASt);
		
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
		System.err.println("Running testcase "+sourceInput);
		Main.main(args);
		assertThat(new File(outputFile), CompareMatcher.isSimilarTo(expected).ignoreComments()
											.ignoreWhitespace()
											.normalizeWhitespace()
											.throwComparisonFailure()
											.withComparisonFormatter(new ASTComparisonFormatter()));
		System.err.println("-->Successful");
	}

	@Parameters(name="{1}")
	public static Collection<Object[]> data() {
		/*
		 * Iterate over all XML-files in the testcases/trees directory to
		 * collect testcases
		 */
		List<Object[]> testcases = new LinkedList<>();
		List<File> inputFiles = getAllXMLFiles(new File("src"+File.separator+"test"+File.separator+"testcases"+File.separator+"trees"));
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
