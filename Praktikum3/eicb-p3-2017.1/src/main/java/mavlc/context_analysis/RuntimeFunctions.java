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
package mavlc.context_analysis;

import java.util.HashMap;
import java.util.Map;

import MTAM.Machine;
import mavlc.ast.nodes.function.FormalParameter;
import mavlc.ast.nodes.function.Function;
import mavlc.ast.type.MatrixType;
import mavlc.ast.type.Type;

/**
 * Dummy declarations of all functions defined in the 
 * standard runtime environment.
 */
public class RuntimeFunctions {
	
	public static Map<String, Function> getRuntimeFunctions(){
		Map<String, Function> runtimeFunctions = new HashMap<>();
		
		MatrixType matrixInt512 = new MatrixType(Type.getIntType(), 512, 512);
		MatrixType matrixInt64 = new MatrixType(Type.getIntType(), 64, 64);
		MatrixType matrixInt16 = new MatrixType(Type.getIntType(), 16, 16);
		MatrixType matrixInt9 = new MatrixType(Type.getIntType(), 9, 9);
		
		MatrixType matrixFloat64 = new MatrixType(Type.getFloatType(), 64, 64);
		MatrixType matrixFloat16 = new MatrixType(Type.getFloatType(), 16, 16);
		MatrixType matrixFloat9 = new MatrixType(Type.getFloatType(), 9, 9);
		/*
		 * File-I/O
		 */
		
		// Read image
		Function readImage = new Function(-1, -1, "readImage", matrixInt512);
		readImage.addParameter(new FormalParameter(0, 0, "fileName", Type.getStringType()));
		readImage.setSourceCodeOffset(Machine.PB + Machine.readImageDisplacement);
		runtimeFunctions.put("readImage", readImage);
		
		// Write image
		Function writeImage = new Function(-1, -1, "writeImage", Type.getVoidType());
		writeImage.addParameter(new FormalParameter(0, 0, "fileName", Type.getStringType()));
		writeImage.addParameter(new FormalParameter(0, 0, "image", matrixInt512));
		writeImage.setSourceCodeOffset(Machine.PB + Machine.writeImageDisplacement);
		runtimeFunctions.put("writeImage", writeImage);
		
		/*
		 * Read integer matrices
		 */
		Function readIntMatrix64 = new Function(-1, -1, "readIntMatrix64", matrixInt64);
		readIntMatrix64.addParameter(new FormalParameter(0, 0, "fileName", Type.getStringType()));
		readIntMatrix64.setSourceCodeOffset(Machine.PB + Machine.readIM64Displacement);
		runtimeFunctions.put("readIntMatrix64", readIntMatrix64);
		
		Function readIntMatrix16 = new Function(-1, -1, "readIntMatrix16", matrixInt16);
		readIntMatrix16.addParameter(new FormalParameter(0, 0, "fileName", Type.getStringType()));
		readIntMatrix16.setSourceCodeOffset(Machine.PB + Machine.readIM16Displacement);
		runtimeFunctions.put("readIntMatrix16", readIntMatrix16);
		
		Function readIntMatrix9 = new Function(-1, -1, "readIntMatrix9", matrixInt9);
		readIntMatrix9.addParameter(new FormalParameter(0, 0, "fileName", Type.getStringType()));
		readIntMatrix9.setSourceCodeOffset(Machine.PB + Machine.readIM9Displacement);
		runtimeFunctions.put("readIntMatrix9", readIntMatrix9);
		
		/*
		 * Write integer matrices
		 */
		Function writeIntMatrix64 = new Function(0, 0, "writeIntMatrix64", Type.getVoidType());
		writeIntMatrix64.addParameter(new FormalParameter(0, 0, "fileName", Type.getStringType()));
		writeIntMatrix64.addParameter(new FormalParameter(0, 0, "matrix", matrixInt64));
		writeIntMatrix64.setSourceCodeOffset(Machine.PB + Machine.writeIM64Displacement);
		runtimeFunctions.put("writeIntMatrix64", writeIntMatrix64);
		
		Function writeIntMatrix16 = new Function(0, 0, "writeIntMatrix16", Type.getVoidType());
		writeIntMatrix16.addParameter(new FormalParameter(0, 0, "fileName", Type.getStringType()));
		writeIntMatrix16.addParameter(new FormalParameter(0, 0, "matrix", matrixInt16));
		writeIntMatrix16.setSourceCodeOffset(Machine.PB + Machine.writeIM16Displacement);
		runtimeFunctions.put("writeIntMatrix16", writeIntMatrix16);
		
		Function writeIntMatrix9 = new Function(0, 0, "writeIntMatrix9", Type.getVoidType());
		writeIntMatrix9.addParameter(new FormalParameter(0, 0, "fileName", Type.getStringType()));
		writeIntMatrix9.addParameter(new FormalParameter(0, 0, "matrix", matrixInt9));
		writeIntMatrix9.setSourceCodeOffset(Machine.PB + Machine.writeIM9Displacement);
		runtimeFunctions.put("writeIntMatrix9", writeIntMatrix9);
		
		/*
		 * Read floating point matrices
		 */
		Function readFloatMatrix64 = new Function(0,0, "readFloatMatrix64", matrixFloat64);
		readFloatMatrix64.addParameter(new FormalParameter(0, 0, "fileName", Type.getStringType()));
		readFloatMatrix64.setSourceCodeOffset(Machine.PB + Machine.readFM64Displacement);
		runtimeFunctions.put("readFloatMatrix64", readFloatMatrix64);
		
		Function readFloatMatrix16 = new Function(0,0, "readFloatMatrix16", matrixFloat16);
		readFloatMatrix16.addParameter(new FormalParameter(0, 0, "fileName", Type.getStringType()));
		readFloatMatrix16.setSourceCodeOffset(Machine.PB + Machine.readFM16Displacement);
		runtimeFunctions.put("readFloatMatrix16", readFloatMatrix16);
		
		Function readFloatMatrix9 = new Function(0,0, "readFloatMatrix9", matrixFloat9);
		readFloatMatrix9.addParameter(new FormalParameter(0, 0, "fileName", Type.getStringType()));
		readFloatMatrix9.setSourceCodeOffset(Machine.PB + Machine.readFM9Displacement);
		runtimeFunctions.put("readFloatMatrix9", readFloatMatrix9);
		
		/*
		 * Write floating point matrices
		 */
		Function writeFloatMatrix64 = new Function(0, 0, "writeFloatMatrix64", Type.getVoidType());
		writeFloatMatrix64.addParameter(new FormalParameter(0, 0, "fileName", Type.getStringType()));
		writeFloatMatrix64.addParameter(new FormalParameter(0, 0, "matrix", matrixFloat64));
		writeFloatMatrix64.setSourceCodeOffset(Machine.PB + Machine.writeFM64Displacement);
		runtimeFunctions.put("writeFloatMatrix64", writeFloatMatrix64);
		
		Function writeFloatMatrix16 = new Function(0, 0, "writeFloatMatrix16", Type.getVoidType());
		writeFloatMatrix16.addParameter(new FormalParameter(0, 0, "fileName", Type.getStringType()));
		writeFloatMatrix16.addParameter(new FormalParameter(0, 0, "matrix", matrixFloat16));
		writeFloatMatrix16.setSourceCodeOffset(Machine.PB + Machine.writeFM16Displacement);
		runtimeFunctions.put("writeFloatMatrix16", writeFloatMatrix16);
		
		Function writeFloatMatrix9 = new Function(0, 0, "writeFloatMatrix9", Type.getVoidType());
		writeFloatMatrix9.addParameter(new FormalParameter(0, 0, "fileName", Type.getStringType()));
		writeFloatMatrix9.addParameter(new FormalParameter(0, 0, "matrix", matrixFloat9));
		writeFloatMatrix9.setSourceCodeOffset(Machine.PB + Machine.writeFM9Displacement);
		runtimeFunctions.put("writeFloatMatrix9", writeFloatMatrix9);
		
		/*
		 * Math
		 */
		Function powInt = new Function(0, 0, "powInt", Type.getIntType());
		powInt.addParameter(new FormalParameter(0, 0, "base", Type.getIntType()));
		powInt.addParameter(new FormalParameter(0, 0, "exp", Type.getIntType()));
		powInt.setSourceCodeOffset(Machine.PB + Machine.powIntDisplacement);
		runtimeFunctions.put("powInt", powInt);
		
		Function powFloat = new Function(0, 0, "powFloat", Type.getFloatType());
		powFloat.addParameter(new FormalParameter(0, 0, "base", Type.getFloatType()));
		powFloat.addParameter(new FormalParameter(0, 0, "exp", Type.getFloatType()));
		powFloat.setSourceCodeOffset(Machine.PB + Machine.powFloatDisplacement);
		runtimeFunctions.put("powFloat", powFloat);
		
		Function sqrtInt = new Function(0, 0, "sqrtInt", Type.getIntType());
		sqrtInt.addParameter(new FormalParameter(0, 0, "num", Type.getIntType()));
		sqrtInt.setSourceCodeOffset(Machine.PB + Machine.sqrtIntDisplacement);
		runtimeFunctions.put("sqrtInt", sqrtInt);
		
		Function sqrtFloat = new Function(0, 0, "sqrtFloat", Type.getFloatType());
		sqrtFloat.addParameter(new FormalParameter(0, 0, "num", Type.getFloatType()));
		sqrtFloat.setSourceCodeOffset(Machine.PB + Machine.sqrtFloatDisplacement);
		runtimeFunctions.put("sqrtFloat", sqrtFloat);
		
		Function modulo = new Function(0, 0, "modulo", Type.getIntType());
		modulo.addParameter(new FormalParameter(0, 0, "num", Type.getIntType()));
		modulo.addParameter(new FormalParameter(0, 0, "divisor", Type.getIntType()));
		modulo.setSourceCodeOffset(Machine.PB + Machine.modDisplacement);
		runtimeFunctions.put("modulo", modulo);
		
		/*
		 * Input & Output
		 */
		Function printInt = new Function(0, 0, "printInt", Type.getVoidType());
		printInt.addParameter(new FormalParameter(0, 0, "num", Type.getIntType()));
		printInt.setSourceCodeOffset(Machine.PB + Machine.printIntDisplacement);
		runtimeFunctions.put("printInt", printInt);
		
		Function printFloat = new Function(0, 0, "printFloat", Type.getVoidType());
		printFloat.addParameter(new FormalParameter(0, 0, "num", Type.getFloatType()));
		printFloat.setSourceCodeOffset(Machine.PB + Machine.printFloatDisplacement);
		runtimeFunctions.put("printFloat", printFloat);
		
		Function printBool = new Function(0, 0, "printBool", Type.getVoidType());
		printBool.addParameter(new FormalParameter(0, 0, "b", Type.getBoolType()));
		printBool.setSourceCodeOffset(Machine.PB + Machine.printBoolDisplacement);
		runtimeFunctions.put("printBool", printBool);
		
		Function printString = new Function(0, 0, "printString", Type.getVoidType());
		printString.addParameter(new FormalParameter(0, 0, "text", Type.getStringType()));
		printString.setSourceCodeOffset(Machine.PB + Machine.printStringDisplacement);
		runtimeFunctions.put("printString", printString);
		
		Function printLine = new Function(0, 0, "printLine", Type.getVoidType());
		printLine.setSourceCodeOffset(Machine.PB + Machine.printLineDisplacement);
		runtimeFunctions.put("printLine", printLine);
		
		Function readInt = new Function(0, 0, "readInt", Type.getIntType());
		readInt.setSourceCodeOffset(Machine.PB + Machine.readIntDisplacement);
		runtimeFunctions.put("readInt", readInt);
		
		Function readFloat = new Function(0, 0, "readFloat", Type.getFloatType());
		readFloat.setSourceCodeOffset(Machine.PB + Machine.readFloatDisplacement);
		runtimeFunctions.put("readFloat", readFloat);
		
		Function readBool = new Function(0, 0, "readBool", Type.getBoolType());
		readBool.setSourceCodeOffset(Machine.PB + Machine.readBoolDisplacement);
		runtimeFunctions.put("readBool", readBool);
		
		/*
		 * Type conversions
		 */
		Function float2int = new Function(0, 0, "float2int", Type.getIntType());
		float2int.addParameter(new FormalParameter(0, 0, "num", Type.getFloatType()));
		float2int.setSourceCodeOffset(Machine.PB + Machine.float2intDisplacement);
		runtimeFunctions.put("float2int", float2int);
		
		Function int2float = new Function(0, 0, "int2float", Type.getFloatType());
		int2float.addParameter(new FormalParameter(0, 0, "num", Type.getIntType()));
		int2float.setSourceCodeOffset(Machine.PB + Machine.int2floatDisplacement);
		runtimeFunctions.put("int2float", int2float);
		
		
		return runtimeFunctions;
	}

}
