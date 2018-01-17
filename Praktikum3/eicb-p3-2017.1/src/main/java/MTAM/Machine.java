/*******************************************************************************
 * Copyright (C) 1999, 2003 D.A. Watt and D.F. Brown
 * Dept. of Computing Science, University of Glasgow, Glasgow G12 8QQ Scotland
 * and School of Computer and Math Sciences, The Robert Gordon University,
 * St. Andrew Street, Aberdeen AB25 1HG, Scotland.
 * 
 * Modifications for the Matrix and Vector Language (MAVL):
 * 
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

import java.util.HashMap;
import java.util.Map;

public final class Machine {

	public final static int fileNameLength = 20; // all filenames are array fileNameLength of Char, chr(0) to indicate end-of-string

	public final static int
	maxRoutineLevel = 7;

	/*
	 *  WORDS AND ADDRESSES
	 *  
	 *  1 word = 32 bit integer
	 *  int = 1 word
	 *  bool = 1 word
	 *  float = 1 word
	 *  
	 */


	// INSTRUCTIONS

	// Operation codes
	public final static int
	LOADop = 0,
	LOADAop = 1,
	LOADIop = 2,
	LOADLop = 3,
	STOREop = 4,
	STOREIop = 5,
	CALLop = 6,
	CALLIop = 7,
	RETURNop = 8,
	PUSHop = 10,
	POPop = 11,
	JUMPop = 12,
	JUMPIop = 13,
	JUMPIFop = 14,
	HALTop = 15,
	LASTop = HALTop;

	public final static String[] mnemonics = {
			"LOAD", "LOADA", "LOADI", "LOADL", "STORE", "STOREI", "CALL", "CALLI", "RETURN", "ILL", "PUSH", "POP", "JUMP", "JUMPI",
			"JUMPIF", "HALT"
	};

	// CODE STORE
	public final static int maxPrimitives = 59;     // number of user defined primitives

	public final static int maxCodeSize = 32767-maxPrimitives;   // max size of user code store in words  

	public static Instruction[] code = new Instruction[maxCodeSize];


	// CODE STORE REGISTERS

	public final static int
	CB = 0,
	PB = maxCodeSize,  // = upper bound of code array + 1
	PT = PB+maxPrimitives;

	// REGISTER NUMBERS

	public final static int
	CBr = 0,
	CTr = 1,
	PBr = 2,
	PTr = 3,
	SBr = 4,
	STr = 5,
	HBr = 6,
	HTr = 7,
	LBr = 8,
	CPr = 15;


	// DATA REPRESENTATION

	public final static int
	booleanSize = 1,
	characterSize = 1,
	integerSize = 1,
	addressSize = 1,
	closureSize = 2 * addressSize,

	linkDataSize = 3 * addressSize,

	falseRep = 0,
	trueRep = 1;

	/*
	 * Constant pool
	 */
	public final static Map<Integer, String> constantPool = new HashMap<>();


	// ADDRESSES OF PRIMITIVE ROUTINES

	public final static int
	idDisplacement = 1,
	notDisplacement = 2,
	andDisplacement = 3,
	orDisplacement = 4,
	succDisplacement = 5,
	predDisplacement = 6,
	negDisplacement = 7,
	addDisplacement = 8,
	subDisplacement = 9,
	multDisplacement = 10,
	divDisplacement = 11,
	modDisplacement = 12,
	ltDisplacement = 13,
	leDisplacement = 14,
	geDisplacement = 15,
	gtDisplacement = 16,
	eqDisplacement = 17,
	neDisplacement = 18,
	floatNegDisplacement = 19,
	floatAddDisplacement = 20,
	floatSubDisplacement = 21,
	floatMulDisplacement = 22,
	floatDivDisplacement = 23,
	floatLTDisplacement = 24,
	floatLEDisplacement = 25,
	floatGEDisplacement = 26,
	floatGTDisplacement = 27,
	floatEQDisplacement = 28,
	floatNEDisplacement = 29,
	readImageDisplacement = 30,
	writeImageDisplacement = 31,
	readIM64Displacement = 32,
	readIM16Displacement = 33,
	readIM9Displacement = 34,
	writeIM64Displacement = 35,
	writeIM16Displacement = 36,
	writeIM9Displacement = 37,
	readFM64Displacement = 38,
	readFM16Displacement = 39,
	readFM9Displacement = 40,
	writeFM64Displacement = 41,
	writeFM16Displacement = 42,
	writeFM9Displacement = 43,
	powIntDisplacement = 44,
	powFloatDisplacement = 45,
	sqrtIntDisplacement = 46,
	sqrtFloatDisplacement = 47,
	printIntDisplacement = 48,
	printFloatDisplacement = 49,
	printBoolDisplacement = 50,
	printStringDisplacement = 51,
	printLineDisplacement = 52,
	readIntDisplacement = 53,
	readFloatDisplacement = 54,
	readBoolDisplacement = 55,
	int2floatDisplacement = 56,
	float2intDisplacement = 57,
	matrixIntMultDisplacement = 58,
	matrixFloatMultDisplacement = maxPrimitives; // 59


	public static final String[] primnames = {
			"err", "id", "not", "and", "or", "succ", "pred", "neg", "add", "sub", "mult", "div", "mod", "lt", "le", "ge",
			"gt", "eq", "ne", "floatNeg", "floatAdd", "floatSub", "floatMul", "floatDiv", "floatLT", "floatLE", "floatGE", 
			"floatGT", "floatEQ", "floatNE", "readImage", "writeImage", "readIM64", "readIM16", "readIM9", "writeIM64", 
			"writeIM16", "writeIM9", "readFM64", "readFM16", "readFM9", "writeFM64", "writeFM16", "writeFM9", "powInt", 
			"powFloat", "sqrtInt", "sqrtFloat", "printInt", "printFloat", "printBool", "printString", "printLine", 
			"readInt", "readFloat", "readBool", "int2float", "float2int", "matrixIntMult", "matrixFloatMult"
	};

	// for dynamic profiling
	public final static int[] execProfile=new int[LASTop+1];
	public final static int[] primProfile=new int[maxPrimitives+2];

}
