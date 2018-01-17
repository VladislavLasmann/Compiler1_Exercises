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

package MTAM;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class OutputSerializer {
	
	public static String outputFile = "build"+File.separator+"test-output"+File.separator+"output.txt";
	
	public static BufferedWriter output;
	
	static{
		try{
			output = new BufferedWriter(new FileWriter(outputFile));
		} 
		catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static void readImage(String fileName){
		throw new UnsupportedOperationException("Reading of images is not supported!");
	}
	
	public static void writeImage(){
		throw new UnsupportedOperationException("Writing of images is not supported!");
	}
	
	public static void readIntegerMatrix(String fileName, int size){
		throw new UnsupportedOperationException("Reading of matrices is not supported!");
	}
	
	public static void writeIntegerMatrix(int size){
		TestInterpreter.ST -= size * size + 1; // ST now pointing to the filename
		++TestInterpreter.ST; // ST now pointing to the first element of the matrix.
		try{
			for(int i=0; i<size; ++i){
				StringBuilder line = new StringBuilder(size*3);
				boolean first = true;
				for(int j=0; j<size; ++j){
					int pos = TestInterpreter.ST + i * size + j;
					if(!first){
						line.append(", ");
					}
					line.append(TestInterpreter.data[pos]);
					first = false;
				}
				line.append("\n");
				output.write(line.toString());
			}
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		// Clear stack
		TestInterpreter.ST -= 1;
	}
	
	public static void readFloatMatrix(String fileName, int size){
		throw new UnsupportedOperationException("Reading of matrices is not supported!");
	}
	
	public static void writeFloatMatrix(int size){
		TestInterpreter.ST -= size * size + 1; // ST now pointing to the filename
		++TestInterpreter.ST; // ST now pointing to the first element of the matrix.
		try{
			for(int i=0; i<size; ++i){
				StringBuilder line = new StringBuilder(size*3);
				boolean first = true;
				for(int j=0; j<size; ++j){
					int pos = TestInterpreter.ST + i * size + j;
					if(!first){
						line.append(", ");
					}
					float item = Float.intBitsToFloat(TestInterpreter.data[pos]);
					line.append(String.valueOf(item));
					first = false;
				}
				line.append("\n");
				output.write(line.toString());
			}
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		// Clear stack
		TestInterpreter.ST -= 1;
	}

	public static void printInt(int text){
		try {
			output.write(String.valueOf(text));
			output.newLine();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static void printFloat(float text){
		try {
			output.write(String.valueOf(text));
			output.newLine();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static void printBool(boolean text){
		try {
			output.write(String.valueOf(text));
			output.newLine();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static void printString(String text){
		try {
			output.write(text);
			output.newLine();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static void printLine(){
		try {
			output.newLine();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
