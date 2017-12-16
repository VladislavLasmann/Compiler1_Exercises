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
package mavlc;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.util.Deque;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

import mavlc.ast.dot.ASTVisualizerVisitor;
import mavlc.ast.nodes.ASTNode;
import mavlc.ast.nodes.module.Module;
import mavlc.ast.serialization.ASTSerialization;
import mavlc.ast.serialization.ASTXMLSerialization;
import mavlc.context_analysis.ContextualAnalysis;
import mavlc.context_analysis.ModuleEnvironment;
import mavlc.parser.recursive_descent.Parser;
import mavlc.parser.recursive_descent.Scanner;
import mavlc.parser.recursive_descent.Token;

/**
 * The MAVL compiler driver.
 */
public class Main {

	public static void main(String[] args) {
		CommandLineParser cliParser = new DefaultParser();
		Options options = setupCLI();
		boolean dotAfterSyntax = false;
		boolean dotAfterContext = false;
		File outputFile = null;
		File inputFile = null;
		
		try {
			CommandLine cli = cliParser.parse(options, args);
			if(cli.hasOption("h")){
				// Automatically generate the help statement
				HelpFormatter formatter = new HelpFormatter();
				formatter.printHelp("mavlc [OPTIONS] inputfile \n Options:", options );
				System.exit(0);
			}
			if(cli.getArgList().size()<1){
				System.err.println("You must specify the input file!");
				System.exit(1);
			}
			else{
				inputFile = new File(cli.getArgList().get(0));
				if(!Files.exists(inputFile.toPath()) || !Files.isReadable(inputFile.toPath())){
					System.err.println("The specified input file "+inputFile.getAbsolutePath()+" is not accessable!");
					System.exit(1);
				}
			}
			
			if(cli.hasOption("o")){
				outputFile = new File(cli.getOptionValue("o"));
			}
			else{
				outputFile = new File("a.xml");
			}
			if(cli.hasOption("dot")){
				String step = cli.getOptionValue("dot", "all");
				switch (step) {
				case "syntax":
					dotAfterSyntax = true;
					break;
				case "context":
					dotAfterContext = true;
					break;
				case "all":
					dotAfterContext = true;
					dotAfterSyntax = true;
					break;
				default:
					System.err.println("Unknown option value for option \"-dot\": "+step);
					System.exit(1);
				}
			}
		} catch (ParseException e) {
			System.err.print(e);
			System.exit(1);
		}
		
		/*
		 * Syntactical analysis.
		 */
		Module compilationUnit = null;
		ModuleEnvironment env = new ModuleEnvironment();
		System.out.println("Compiling file "+inputFile);
		try {
			Scanner lexer = new Scanner(inputFile);
			Deque<Token> tokens = lexer.scan();
			
			Parser p = new Parser(tokens);
			compilationUnit = p.parse(env); // may throw SyntaxError
			
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(1);
		}
		if(dotAfterSyntax){
			String outputPath = outputFile.getAbsolutePath();
			String path = outputPath.substring(0, outputPath.lastIndexOf('.'));
			PrintWriter out;
			try {
				System.out.println("Dumping AST to "+path+"_ast.dot");
				out = new PrintWriter(new FileWriter(path+"_ast.dot"));
				ASTVisualizerVisitor visualizer = new ASTVisualizerVisitor(false);
				compilationUnit.accept(visualizer, null);
				out.println(visualizer.toDotFormat());
				out.close();
			} catch (FileNotFoundException e) {
				e.printStackTrace();
				System.exit(1);
			} catch (IOException e) {
				e.printStackTrace();
				System.exit(1);
			}		
		}
		
		/*
		 * Contextual analysis.
		 */
		ContextualAnalysis contextAnalysis = new ContextualAnalysis(env);
		compilationUnit.accept(contextAnalysis, null); // may throw CompilationErrors related to context analysis
		
		if(dotAfterContext){
			String outputPath = outputFile.getAbsolutePath();
			String path = outputPath.substring(0, outputPath.lastIndexOf('.'));
			PrintWriter out;
			try {
				System.out.println("Dumping decorated AST to "+path+"_ast.dot");
				out = new PrintWriter(new FileWriter(path+"_decorated_ast.dot"));
				ASTVisualizerVisitor visualizer = new ASTVisualizerVisitor(true);
				compilationUnit.accept(visualizer, null);
				out.println(visualizer.toDotFormat());
				out.close();
			} catch (FileNotFoundException e) {
				e.printStackTrace();
				System.exit(1);
			} catch (IOException e) {
				e.printStackTrace();
				System.exit(1);
			}	
		}
		
		ASTNode target = compilationUnit;
		ASTSerialization serializer = new ASTXMLSerialization();
		System.out.println("Writing output of contextual analysis to "+outputFile);
		serializer.serialize(target, outputFile.getAbsolutePath());
		
		System.out.println("Done.");
	}

	private static Options setupCLI(){
		Options options = new Options();
		
		options.addOption("h", "help", false, "Print this help text");
		
		options.addOption("ctx", "context-only", false, 
				"Stop afer contextual analysis and output XML-serialized D-AST. Syntactical analysis is included.");
		
		Option outputFile = Option.builder("o").argName("output-file")
											.hasArg()
											.desc("Specifies the output file")
											.build();
		options.addOption(outputFile);
		
		Option dotOutput = Option.builder("dot").argName("step")
												.optionalArg(true)
												.numberOfArgs(1)
												.desc("Output the (D)AST in GraphViz dot-format after the specified compilation step:"
														+ "\n syntax : Output the result of the syntactical analysis as dot-tree"
														+ "\n context : Output the result of the contextual analysis as dot-tree"
														+ "\n all [default] : Output after both steps")
												.build();
		options.addOption(dotOutput);
		
		return options;
	}
}
