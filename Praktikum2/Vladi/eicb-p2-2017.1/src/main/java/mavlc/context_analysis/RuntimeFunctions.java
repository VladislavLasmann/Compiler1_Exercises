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
		runtimeFunctions.put("readImage", readImage);
		
		// Write image
		Function writeImage = new Function(-1, -1, "writeImage", Type.getVoidType());
		writeImage.addParameter(new FormalParameter(0, 0, "fileName", Type.getStringType()));
		writeImage.addParameter(new FormalParameter(0, 0, "image", matrixInt512));
		runtimeFunctions.put("writeImage", writeImage);
		
		/*
		 * Read integer matrices
		 */
		Function readIntMatrix64 = new Function(-1, -1, "readIntMatrix64", matrixInt64);
		readIntMatrix64.addParameter(new FormalParameter(0, 0, "fileName", Type.getStringType()));
		runtimeFunctions.put("readIntMatrix64", readIntMatrix64);
		
		Function readIntMatrix16 = new Function(-1, -1, "readIntMatrix16", matrixInt16);
		readIntMatrix16.addParameter(new FormalParameter(0, 0, "fileName", Type.getStringType()));
		runtimeFunctions.put("readIntMatrix16", readIntMatrix16);
		
		Function readIntMatrix9 = new Function(-1, -1, "readIntMatrix9", matrixInt9);
		readIntMatrix9.addParameter(new FormalParameter(0, 0, "fileName", Type.getStringType()));
		runtimeFunctions.put("readIntMatrix9", readIntMatrix9);
		
		/*
		 * Write integer matrices
		 */
		Function writeIntMatrix64 = new Function(0, 0, "writeIntMatrix64", Type.getVoidType());
		writeIntMatrix64.addParameter(new FormalParameter(0, 0, "fileName", Type.getStringType()));
		writeIntMatrix64.addParameter(new FormalParameter(0, 0, "matrix", matrixInt64));
		runtimeFunctions.put("writeIntMatrix64", writeIntMatrix64);
		
		Function writeIntMatrix16 = new Function(0, 0, "writeIntMatrix16", Type.getVoidType());
		writeIntMatrix16.addParameter(new FormalParameter(0, 0, "fileName", Type.getStringType()));
		writeIntMatrix16.addParameter(new FormalParameter(0, 0, "matrix", matrixInt16));
		runtimeFunctions.put("writeIntMatrix16", writeIntMatrix16);
		
		Function writeIntMatrix9 = new Function(0, 0, "writeIntMatrix9", Type.getVoidType());
		writeIntMatrix9.addParameter(new FormalParameter(0, 0, "fileName", Type.getStringType()));
		writeIntMatrix9.addParameter(new FormalParameter(0, 0, "matrix", matrixInt9));
		runtimeFunctions.put("writeIntMatrix9", writeIntMatrix9);
		
		/*
		 * Read floating point matrices
		 */
		Function readFloatMatrix64 = new Function(0,0, "readFloatMatrix64", matrixFloat64);
		readFloatMatrix64.addParameter(new FormalParameter(0, 0, "fileName", Type.getStringType()));
		runtimeFunctions.put("readFloatMatrix64", readFloatMatrix64);
		
		Function readFloatMatrix16 = new Function(0,0, "readFloatMatrix16", matrixFloat16);
		readFloatMatrix16.addParameter(new FormalParameter(0, 0, "fileName", Type.getStringType()));
		runtimeFunctions.put("readFloatMatrix16", readFloatMatrix16);
		
		Function readFloatMatrix9 = new Function(0,0, "readFloatMatrix9", matrixFloat9);
		readFloatMatrix9.addParameter(new FormalParameter(0, 0, "fileName", Type.getStringType()));
		runtimeFunctions.put("readFloatMatrix9", readFloatMatrix9);
		
		/*
		 * Write floating point matrices
		 */
		Function writeFloatMatrix64 = new Function(0, 0, "writeFloatMatrix64", Type.getVoidType());
		writeFloatMatrix64.addParameter(new FormalParameter(0, 0, "fileName", Type.getStringType()));
		writeFloatMatrix64.addParameter(new FormalParameter(0, 0, "matrix", matrixFloat64));
		runtimeFunctions.put("writeFloatMatrix64", writeFloatMatrix64);
		
		Function writeFloatMatrix16 = new Function(0, 0, "writeFloatMatrix16", Type.getVoidType());
		writeFloatMatrix16.addParameter(new FormalParameter(0, 0, "fileName", Type.getStringType()));
		writeFloatMatrix16.addParameter(new FormalParameter(0, 0, "matrix", matrixFloat16));
		runtimeFunctions.put("writeFloatMatrix16", writeFloatMatrix16);
		
		Function writeFloatMatrix9 = new Function(0, 0, "writeFloatMatrix9", Type.getVoidType());
		writeFloatMatrix9.addParameter(new FormalParameter(0, 0, "fileName", Type.getStringType()));
		writeFloatMatrix9.addParameter(new FormalParameter(0, 0, "matrix", matrixFloat9));
		runtimeFunctions.put("writeFloatMatrix9", writeFloatMatrix9);
		
		/*
		 * Math
		 */
		Function powInt = new Function(0, 0, "powInt", Type.getIntType());
		powInt.addParameter(new FormalParameter(0, 0, "base", Type.getIntType()));
		powInt.addParameter(new FormalParameter(0, 0, "exp", Type.getIntType()));
		runtimeFunctions.put("powInt", powInt);
		
		Function powFloat = new Function(0, 0, "powFloat", Type.getFloatType());
		powFloat.addParameter(new FormalParameter(0, 0, "base", Type.getFloatType()));
		powFloat.addParameter(new FormalParameter(0, 0, "exp", Type.getFloatType()));
		runtimeFunctions.put("powFloat", powFloat);
		
		Function sqrtInt = new Function(0, 0, "sqrtInt", Type.getIntType());
		sqrtInt.addParameter(new FormalParameter(0, 0, "num", Type.getIntType()));
		runtimeFunctions.put("sqrtInt", sqrtInt);
		
		Function sqrtFloat = new Function(0, 0, "sqrtFloat", Type.getFloatType());
		sqrtFloat.addParameter(new FormalParameter(0, 0, "num", Type.getFloatType()));
		runtimeFunctions.put("sqrtFloat", sqrtFloat);
		
		Function modulo = new Function(0, 0, "modulo", Type.getIntType());
		modulo.addParameter(new FormalParameter(0, 0, "num", Type.getIntType()));
		modulo.addParameter(new FormalParameter(0, 0, "divisor", Type.getIntType()));
		runtimeFunctions.put("modulo", modulo);
		
		/*
		 * Input & Output
		 */
		Function printInt = new Function(0, 0, "printInt", Type.getVoidType());
		printInt.addParameter(new FormalParameter(0, 0, "num", Type.getIntType()));
		runtimeFunctions.put("printInt", printInt);
		
		Function printFloat = new Function(0, 0, "printFloat", Type.getVoidType());
		printFloat.addParameter(new FormalParameter(0, 0, "num", Type.getFloatType()));
		runtimeFunctions.put("printFloat", printFloat);
		
		Function printBool = new Function(0, 0, "printBool", Type.getVoidType());
		printBool.addParameter(new FormalParameter(0, 0, "b", Type.getBoolType()));
		runtimeFunctions.put("printBool", printBool);
		
		Function printString = new Function(0, 0, "printString", Type.getVoidType());
		printString.addParameter(new FormalParameter(0, 0, "text", Type.getStringType()));
		runtimeFunctions.put("printString", printString);
		
		Function printLine = new Function(0, 0, "printLine", Type.getVoidType());
		runtimeFunctions.put("printLine", printLine);
		
		Function readInt = new Function(0, 0, "readInt", Type.getIntType());
		runtimeFunctions.put("readInt", readInt);
		
		Function readFloat = new Function(0, 0, "readFloat", Type.getFloatType());
		runtimeFunctions.put("readFloat", readFloat);
		
		Function readBool = new Function(0, 0, "readBool", Type.getBoolType());
		runtimeFunctions.put("readBool", readBool);
		
		/*
		 * Type conversions
		 */
		Function float2int = new Function(0, 0, "float2int", Type.getIntType());
		float2int.addParameter(new FormalParameter(0, 0, "num", Type.getFloatType()));
		runtimeFunctions.put("float2int", float2int);
		
		Function int2float = new Function(0, 0, "int2float", Type.getFloatType());
		int2float.addParameter(new FormalParameter(0, 0, "num", Type.getIntType()));
		runtimeFunctions.put("int2float", int2float);
		
		
		return runtimeFunctions;
	}

}
