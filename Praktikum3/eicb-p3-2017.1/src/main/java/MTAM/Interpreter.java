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

import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

public class Interpreter {

	static String objectName;

	// DATA STORE

	final static int maxDataSize = 33554432; // max 128MiB = 33554432 words in user data space (stack+heap)
	static int[] data = new int[maxDataSize];

	// DATA STORE REGISTERS AND OTHER REGISTERS

	final static int CB = 0, SB = 0, HB = maxDataSize; // = upper bound of data array + 1

	static int CT, CP, ST, HT, LB, status;

	// status values
	final static int running = 0, halted = 1, failedDataStoreFull = 2,
			failedInvalidCodeAddress = 3, failedInvalidInstruction = 4,
			failedOverflow = 5, failedZeroDivide = 6, failedIOError = 7;

	static long accumulator;

	static float floatAccumulator;

	static boolean debug;
	static boolean showStackIntel;

	// For command line input
	static Scanner cli = new Scanner(System.in);

	static int content(int r) {
		// Returns the current content of register r,
		// even if r is one of the pseudo-registers L1..L6.

		switch (r) {
		case Machine.CBr:
			return CB;
		case Machine.CTr:
			return CT;
		case Machine.PBr:
			return Machine.PB;
		case Machine.PTr:
			return Machine.PT;
		case Machine.SBr:
			return SB;
		case Machine.STr:
			return ST;
		case Machine.HBr:
			return HB;
		case Machine.HTr:
			return HT;
		case Machine.LBr:
			return LB;
		case Machine.CPr:
			return CP;
		default:
			return 0;
		}
	}

	// PROGRAM STATUS

	static void dump() {
		// Writes a summary of the machine state.
		int addr, dynamicLink;
		System.out.flush();
		System.out.println("");
		System.out.println("State of data store and registers:");
		System.out.println("");
		if (HT == HB)
			System.out.println("            |--------|          (heap is empty)");
		else {
			System.out.println("       HB-->");
			System.out.println("            |--------|");
			for (addr = HB - 1; addr >= HT; addr--) {
				System.out.print(addr + ":");
				if (addr == HT)
					System.out.print(" HT-->");
				else
					System.out.print("      ");
				System.out.println("|" + data[addr] + "|");
			}
			System.out.println("            |--------|");
		}
		System.out.println("            |////////|");
		System.out.println("            |////////|");
		if (ST == SB)
			System.out.println("            |--------|          (stack is empty)");
		else {
			dynamicLink = LB;
			System.out.println("      ST--> |////////|");
			System.out.println("            |--------|");
			for (addr = ST - 1; addr >= SB; addr--) {
				System.out.print(addr + ":");
				if (addr == SB)
					System.out.print(" SB-->");
				else if (addr == dynamicLink) {
					System.out.print(" LB-->");
				} else
					System.out.print("      ");
				if ((addr == dynamicLink) && (dynamicLink != SB))
					System.out.print("|SL=" + data[addr] + "|");
				else if ((addr == dynamicLink + 1) && (dynamicLink != SB))
					System.out.print("|DL=" + data[addr] + "|");
				else if ((addr == dynamicLink + 2) && (dynamicLink != SB))
					System.out.print("|RA=" + data[addr] + "|");
				else
					System.out.print("|" + data[addr] + "|");
				System.out.println("");
				if (addr == dynamicLink) {
					System.out.println("            |--------|");
					dynamicLink = data[addr + 1];
				}
			}
		}
		System.out.println("");
	}

	static void showStatus() {
		// Writes an indication of whether and why the program has terminated.
		System.out.println("");
		switch (status) {
		case running:
			System.out.println("Program is running.");
			break;
		case halted:
			System.out.println("Program has halted normally.");
			break;
		case failedDataStoreFull:
			System.out.println("Program has failed due to exhaustion of Data Store.");
			break;
		case failedInvalidCodeAddress:
			System.out.println("Program has failed due to an invalid code address.");
			break;
		case failedInvalidInstruction:
			System.out.println("Program has failed due to an invalid instruction.");
			break;
		case failedOverflow:
			System.out.println("Program has failed due to overflow.");
			break;
		case failedZeroDivide:
			System.out.println("Program has failed due to division by zero.");
			break;
		case failedIOError:
			System.out.println("Program has failed due to an IO error.");
			break;
		}
		if (status != halted)
			dump();
	}

	// INTERPRETATION

	static void checkSpace(int spaceNeeded) {
		// Signals failure if there is not enough space to expand the stack or
		// heap by spaceNeeded.

		if (HT - ST < spaceNeeded)
			status = failedDataStoreFull;
	}

	static boolean isTrue(int datum) {
		// Tests whether the given datum represents true.
		return (datum == Machine.trueRep);
	}

	static boolean equal(int size, int addr1, int addr2) {
		// Tests whether two multi-word objects are equal, given their common
		// size and their base addresses.

		boolean eq;
		int index;

		eq = true;
		index = 0;
		while (eq && (index < size))
			if (data[addr1 + index] == data[addr2 + index])
				index = index + 1;
			else
				eq = false;
		return eq;
	}

	static int overflowChecked(long datum) {
		// Signals failure if the datum is too large to fit into a single word,
		// otherwise returns the datum as a single word.

		if ((-Integer.MIN_VALUE <= datum) && (datum <= Integer.MAX_VALUE))
			return (int) datum;
		else {
			status = failedOverflow;
			return 0;
		}
	}

	static int toInt(boolean b) {
		return b ? Machine.trueRep : Machine.falseRep;
	}

	static int currentChar;

	static int readInt() throws java.io.IOException {
		int temp = 0;
		int sign = 1;

		do {
			currentChar = System.in.read();
		} while (Character.isWhitespace((char) currentChar));

		if ((currentChar == '-') || (currentChar == '+'))
			do {
				sign = (currentChar == '-') ? -1 : 1;
				currentChar = System.in.read();
			} while ((currentChar == '-') || currentChar == '+');

		if (Character.isDigit((char) currentChar))
			do {
				temp = temp * 10 + (currentChar - '0');
				currentChar = System.in.read();
			} while (Character.isDigit((char) currentChar));

		return sign * temp;
	}

	static void callPrimitive(int primitiveDisplacement) {
		// Invokes the given primitive routine.

		// for dynamic profiling of primitives
		++Machine.primProfile[primitiveDisplacement];

		String fileName;

		switch (primitiveDisplacement) {
		case Machine.idDisplacement:
			break; // nothing to be done
		case Machine.notDisplacement:
			data[ST - 1] = toInt(!isTrue(data[ST - 1]));
			break;
		case Machine.andDisplacement:
			ST = ST - 1;
			data[ST - 1] = toInt(isTrue(data[ST - 1]) & isTrue(data[ST]));
			break;
		case Machine.orDisplacement:
			ST = ST - 1;
			data[ST - 1] = toInt(isTrue(data[ST - 1]) | isTrue(data[ST]));
			break;
		case Machine.succDisplacement:
			data[ST - 1] = overflowChecked(data[ST - 1] + 1);
			break;
		case Machine.predDisplacement:
			data[ST - 1] = overflowChecked(data[ST - 1] - 1);
			break;
		case Machine.negDisplacement:
			data[ST - 1] = -data[ST - 1];
			break;
		case Machine.addDisplacement:
			ST = ST - 1;
			accumulator = data[ST - 1];
			data[ST - 1] = overflowChecked(accumulator + data[ST]);
			break;
		case Machine.subDisplacement:
			ST = ST - 1;
			accumulator = data[ST - 1];
			data[ST - 1] = overflowChecked(accumulator - data[ST]);
			break;
		case Machine.multDisplacement:
			ST = ST - 1;
			accumulator = data[ST - 1];
			data[ST - 1] = overflowChecked(accumulator * data[ST]);
			break;
		case Machine.divDisplacement:
			ST = ST - 1;
			accumulator = data[ST - 1];
			if (data[ST] != 0)
				data[ST - 1] = (int) (accumulator / data[ST]);
			else
				status = failedZeroDivide;
			break;
		case Machine.modDisplacement:
			ST = ST - 1;
			accumulator = data[ST - 1];
			if (data[ST] != 0)
				data[ST - 1] = (int) (accumulator % data[ST]);
			else
				status = failedZeroDivide;
			break;
		case Machine.ltDisplacement:
			ST = ST - 1;
			data[ST - 1] = toInt(data[ST - 1] < data[ST]);
			break;
		case Machine.leDisplacement:
			ST = ST - 1;
			data[ST - 1] = toInt(data[ST - 1] <= data[ST]);
			break;
		case Machine.geDisplacement:
			ST = ST - 1;
			data[ST - 1] = toInt(data[ST - 1] >= data[ST]);
			break;
		case Machine.gtDisplacement:
			ST = ST - 1;
			data[ST - 1] = toInt(data[ST - 1] > data[ST]);
			break;
		case Machine.eqDisplacement:
			ST = ST - 1;
			data[ST - 1] = toInt(data[ST - 1] == data[ST]);
			break;
		case Machine.neDisplacement:
			ST = ST - 1;
			data[ST - 1] = toInt(data[ST - 1] != data[ST]);
			break;
		case Machine.floatNegDisplacement:
			float num = Float.intBitsToFloat(data[ST-1]);
			data[ST-1] = Float.floatToIntBits(num*-1.0f);
			break;
		case Machine.floatAddDisplacement:
			ST = ST - 1;
			floatAccumulator = Float.intBitsToFloat(data[ST-1]);
			data[ST-1] = Float.floatToIntBits(floatAccumulator + Float.intBitsToFloat(data[ST]));
			break;
		case Machine.floatSubDisplacement:
			ST = ST - 1;
			floatAccumulator = Float.intBitsToFloat(data[ST-1]);
			data[ST-1] = Float.floatToIntBits(floatAccumulator - Float.intBitsToFloat(data[ST]));
			break;
		case Machine.floatMulDisplacement:
			ST = ST - 1;
			floatAccumulator = Float.intBitsToFloat(data[ST-1]);
			data[ST-1] = Float.floatToIntBits(floatAccumulator * Float.intBitsToFloat(data[ST]));
			break;
		case Machine.floatDivDisplacement:
			ST = ST - 1;
			floatAccumulator = Float.intBitsToFloat(data[ST-1]);
			float divisor = Float.intBitsToFloat(data[ST]);
			if(divisor != 0){
				data[ST-1] = Float.floatToIntBits(floatAccumulator / divisor);
			}
			else{
				status = failedZeroDivide;
			}
			break;
		case Machine.floatLTDisplacement:
			ST = ST - 1;
			floatAccumulator = Float.intBitsToFloat(data[ST-1]);
			data[ST-1] = toInt(floatAccumulator < Float.intBitsToFloat(data[ST]));
			break;
		case Machine.floatLEDisplacement:
			ST = ST - 1;
			floatAccumulator = Float.intBitsToFloat(data[ST-1]);
			data[ST-1] = toInt(floatAccumulator <= Float.intBitsToFloat(data[ST]));
			break;
		case Machine.floatGEDisplacement:
			ST = ST - 1;
			floatAccumulator = Float.intBitsToFloat(data[ST-1]);
			data[ST-1] = toInt(floatAccumulator >= Float.intBitsToFloat(data[ST]));
			break;
		case Machine.floatGTDisplacement:
			ST = ST - 1;
			floatAccumulator = Float.intBitsToFloat(data[ST-1]);
			data[ST-1] = toInt(floatAccumulator > Float.intBitsToFloat(data[ST]));
			break;
		case Machine.floatEQDisplacement:
			ST = ST - 1;
			floatAccumulator = Float.intBitsToFloat(data[ST-1]);
			data[ST-1] = toInt(floatAccumulator == Float.intBitsToFloat(data[ST]));
			break;
		case Machine.floatNEDisplacement:
			ST = ST - 1;
			floatAccumulator = Float.intBitsToFloat(data[ST-1]);
			data[ST-1] = toInt(floatAccumulator != Float.intBitsToFloat(data[ST]));
			break;
		case Machine.readImageDisplacement:
			ST = ST - 1;
			fileName = Machine.constantPool.get(data[ST]);
			FileIO.readImage(fileName);
			break; 
		case Machine.writeImageDisplacement:
			FileIO.writeImage();
			break; 
		case Machine.readIM64Displacement:
			ST = ST - 1;
			fileName = Machine.constantPool.get(data[ST]);
			FileIO.readIntegerMatrix(fileName, 64);
			break; 
		case Machine.readIM16Displacement:
			ST = ST - 1;
			fileName = Machine.constantPool.get(data[ST]);
			FileIO.readIntegerMatrix(fileName, 16);
			break;
		case Machine.readIM9Displacement:
			ST = ST - 1;
			fileName = Machine.constantPool.get(data[ST]);
			FileIO.readIntegerMatrix(fileName, 9);
			break;
		case Machine.writeIM64Displacement:
			FileIO.writeIntegerMatrix(64);
			break; 
		case Machine.writeIM16Displacement:
			FileIO.writeIntegerMatrix(16);
			break; 
		case Machine.writeIM9Displacement:
			FileIO.writeIntegerMatrix(9);
			break; 
		case Machine.readFM64Displacement:
			ST = ST - 1;
			fileName = Machine.constantPool.get(data[ST]);
			FileIO.readFloatMatrix(fileName, 64);
			break;
		case Machine.readFM16Displacement:
			ST = ST - 1;
			fileName = Machine.constantPool.get(data[ST]);
			FileIO.readFloatMatrix(fileName, 16);
			break; 
		case Machine.readFM9Displacement:
			ST = ST - 1;
			fileName = Machine.constantPool.get(data[ST]);
			FileIO.readFloatMatrix(fileName, 9);
			break; 
		case Machine.writeFM64Displacement:
			FileIO.writeFloatMatrix(64);
			break; 
		case Machine.writeFM16Displacement:
			FileIO.writeFloatMatrix(16);
			break; 
		case Machine.writeFM9Displacement:
			FileIO.writeFloatMatrix(9);
			break; 
		case Machine.powIntDisplacement:
			ST = ST - 1;
			accumulator = (long) Math.pow(data[ST-1], data[ST]);
			data[ST-1] = overflowChecked(accumulator);
			break; 
		case Machine.powFloatDisplacement:
			ST = ST - 1;
			floatAccumulator = (float) Math.pow(Float.intBitsToFloat(data[ST-1]), 
					Float.intBitsToFloat(data[ST]));
			data[ST-1] = Float.floatToIntBits(floatAccumulator);
			break; 
		case Machine.sqrtIntDisplacement:
			accumulator = (long) Math.sqrt(data[ST-1]);
			data[ST-1] = overflowChecked(accumulator);
			break; 
		case Machine.sqrtFloatDisplacement:
			floatAccumulator = (float) Math.sqrt(Float.intBitsToFloat(data[ST-1]));
			data[ST-1] = Float.floatToIntBits(floatAccumulator);
			break; 
		case Machine.printIntDisplacement:
			ST = ST-1;
			System.out.print(String.valueOf(data[ST]));
			break; 
		case Machine.printFloatDisplacement:
			ST = ST-1;
			System.out.print(String.valueOf(Float.intBitsToFloat(data[ST])));
			break; 
		case Machine.printBoolDisplacement:
			ST = ST-1;
			System.out.print(String.valueOf((data[ST]==Machine.trueRep) ? true : false));
			break; 
		case Machine.printStringDisplacement:
			ST = ST-1;
			int index = data[ST];
			System.out.print(Machine.constantPool.get(index));
			break; 
		case Machine.printLineDisplacement:
			System.out.println();
			break; 
		case Machine.readIntDisplacement:
			System.out.println(">");
			accumulator = Integer.parseInt(cli.nextLine());
			data[ST] = overflowChecked(accumulator);
			ST = ST + 1;
			break; 
		case Machine.readFloatDisplacement:
			System.out.println(">");
			accumulator = Float.floatToIntBits(Float.parseFloat(cli.nextLine()));
			data[ST] = overflowChecked(accumulator);
			ST = ST + 1;
			break;  
		case Machine.readBoolDisplacement:
			System.out.println(">");
			accumulator = (Boolean.parseBoolean(cli.nextLine()) ? Machine.trueRep : Machine.falseRep);
			data[ST] = overflowChecked(accumulator);
			ST = ST + 1;
			break;  
		case Machine.int2floatDisplacement:
			floatAccumulator = (float) data[ST-1];
			data[ST-1] = Float.floatToIntBits(floatAccumulator);
			break; 
		case Machine.float2intDisplacement:
			accumulator = (int) Float.intBitsToFloat(data[ST-1]);
			data[ST-1] = overflowChecked(accumulator);
			break;
		case Machine.matrixFloatMultDisplacement:{		
			int l = data[ST-3];
			int m = data[ST-2];
			int n = data[ST-1];
			int MatBSize = m*n;
			int MatBBegin = ST-3-MatBSize;
			float[] MatB = new float[m*n];
			for(int i = MatBBegin; i < MatBBegin+MatBSize; i++){
				MatB[i - MatBBegin] = Float.intBitsToFloat(data[i]);
			}
			int MatASize = l*m;
			int MatABegin = ST-3-MatBSize-MatASize;
			float[] MatA = new float[l*m];
			for(int i = MatABegin; i < MatABegin+MatASize; i++){
				MatA[i - MatABegin] = Float.intBitsToFloat(data[i]);
			}
			float[] Res = new float[l*n];
			for(int i = 0; i < l; i++){
				for(int k = 0; k < n; k++){
					float accu = 0;
					for(int j = 0; j< m; j++){
						accu += MatA[i*m+j] * MatB[n*j+k];
					}
					Res[i*n+k] = accu;
				}
			}
			ST = MatABegin + l*n;
			for(int i = 0; i < l*n;i++){
				data[MatABegin+i] = Float.floatToIntBits(Res[i]);
			}
			}
			break;
		case Machine.matrixIntMultDisplacement:{
			/**
			 * Executing a matrix multiplication.
			 * Expects the following values on the stack:
			 * MatA, MatB, MatA.yDim, MatA.xDim, MatB.yDim
			 * Leaves the stack containing only the resulting Matrix.
			 */
			int l = data[ST-3];
			int m = data[ST-2];
			int n = data[ST-1];
			int MatBSize = m*n;
			int MatBBegin = ST-3-MatBSize;
			int[] MatB = new int[m*n];
			for(int i = MatBBegin; i < MatBBegin+MatBSize; i++){
				MatB[i - MatBBegin] = data[i];
			}
			int MatASize = l*m;
			int MatABegin = ST-3-MatBSize-MatASize;
			int[] MatA = new int[l*m];
			for(int i = MatABegin; i < MatABegin+MatASize; i++){
				MatA[i - MatABegin] = data[i];
			}
			int[] Res = new int[l*n];
			//All needed Values are now ready;
			for(int i = 0; i < l; i++){
				for(int k = 0; k < n; k++){
					int accu = 0;
					for(int j = 0; j< m; j++){
						accu += MatA[i*m+j] * MatB[n*j+k];
					}
					Res[i*n+k] = accu;
				}
			}
			ST = MatABegin + l*n;
			for(int i = 0; i < l*n;i++){
				data[MatABegin+i] = Res[i];
			}	
			}
			break;
		}

	}

	static void interpretProgram() {
		// Runs the program in code store.

		Instruction currentInstr;
		int op, r, n, d, addr, index;

		// Initialize registers ...
		ST = SB;
		HT = HB;
		LB = SB;
		CP = CB;
		status = running;
		do {
			// Fetch instruction ...
			currentInstr = Machine.code[CP];
			// Decode instruction ...
			op = currentInstr.op;
			r = currentInstr.r;
			n = currentInstr.n;
			d = currentInstr.d;
			if(debug){
				Disassembler.writeInstruction(currentInstr, System.out);
				System.out.print(", ST = " + ST);
				System.out.print("\n");
				System.out.flush();
			}
			if(showStackIntel){
				writeStack(data, ST);				
			}

			// dynamic profiling
			++Machine.execProfile[op];
			// Execute instruction ...
			switch (op) {
			case Machine.LOADop:
				addr = d + content(r);
				checkSpace(n);
				for (index = 0; index < n; index++)
					data[ST + index] = data[addr + index];
				ST = ST + n;
				CP = CP + 1;
				break;
			case Machine.LOADAop:
				addr = d + content(r);
				checkSpace(1);
				data[ST] = addr;
				ST = ST + 1;
				CP = CP + 1;
				break;
			case Machine.LOADIop:
				ST = ST - 1;
				addr = data[ST];
				checkSpace(n);
				for (index = 0; index < n; index++)
					data[ST + index] = data[addr + index];
				ST = ST + n;
				CP = CP + 1;
				break;
			case Machine.LOADLop:
				checkSpace(1);
				data[ST] = d;
				ST = ST + 1;
				CP = CP + 1;
				break;
			case Machine.STOREop:
				addr = d + content(r);
				ST = ST - n;
				for (index = 0; index < n; index++)
					data[addr + index] = data[ST + index];
				CP = CP + 1;
				break;
			case Machine.STOREIop:
				ST = ST - 1;
				addr = data[ST];
				ST = ST - n;
				for (index = 0; index < n; index++)
					data[addr + index] = data[ST + index];
				CP = CP + 1;
				break;
			case Machine.CALLop:
				addr = d + content(r);
				if (addr >= Machine.PB) {
					callPrimitive(addr - Machine.PB);
					CP = CP + 1;
				} else {
					checkSpace(2);
					data[ST + 0] = LB; // dynamic link
					data[ST + 1] = CP + 1; // return address
					LB = ST;
					ST = ST + 2;
					CP = addr;
				}
				break;
			case Machine.CALLIop:
				ST = ST - 2;
				addr = data[ST + 1];
				if (addr >= Machine.PB) {
					callPrimitive(addr - Machine.PB);
					CP = CP + 1;
				} else {
					// data[ST] = static link already
					data[ST + 1] = LB; // dynamic link
					data[ST + 2] = CP + 1; // return address
					LB = ST;
					ST = ST + 3;
					CP = addr;
				}
				break;
			case Machine.RETURNop:
				addr = LB - d;
				CP = data[LB + 1];
				LB = data[LB + 0];
				ST = ST - n;
				for (index = 0; index < n; index++)
					data[addr + index] = data[ST + index];
				ST = addr + n;
				break;
			case Machine.PUSHop:
				checkSpace(d);
				int oldST = ST;
				ST = ST + d;
				for(int i=oldST; i<ST; ++i){
					data[i] = 0;
				}
				CP = CP + 1;
				break;
			case Machine.POPop:
				addr = ST - n - d;
				ST = ST - n;
				for (index = 0; index < n; index++)
					data[addr + index] = data[ST + index];
				ST = addr + n;
				CP = CP + 1;
				break;
			case Machine.JUMPop:
				CP = d + content(r);
				break;
			case Machine.JUMPIop:
				ST = ST - 1;
				CP = data[ST];
				break;
			case Machine.JUMPIFop:
				ST = ST - 1;
				if (data[ST] == n)
					CP = d + content(r);
				else
					CP = CP + 1;
				break;
			case Machine.HALTop:
				status = halted;
				break;
			}
			if ((CP < CB) || (CP >= CT))
				status = failedInvalidCodeAddress;
		} while (status == running);
	}

	// LOADING

	static void loadObjectProgram(String objectName) {
		try(DataInputStream reader = new DataInputStream(new FileInputStream(objectName))){
			/*
			 * Load and initialize constant pool.
			 */
			Map<Integer, String> constantPool = Disassembler.loadConstantPool(reader);
			Machine.constantPool.putAll(constantPool);
			/*
			 * Load program code.
			 */
			List<Instruction> code = Disassembler.loadObjectCode(reader);
			int addr = Machine.CB;
			for(Instruction inst : code){
				Machine.code[addr] = inst;
				++addr;
			}
			//TODO This fixes something! Not sure if this belongs here!
			CT = addr;
		} catch (FileNotFoundException e) {
			throw new RuntimeException(e);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	// DUMP PROFILE

	static void dumpProfile() {
		System.out.flush();

		System.err.println("\n");
		System.err.println("=== Dynamic Execution Profile ===");
		System.err.println();
		System.err.println("Instructions\n");
		int instrs=0, prims=0;
		for (int i=0; i <= Machine.LASTop; ++i) {
			System.err.format("%1$8s: %2$8d", Machine.mnemonics[i], Machine.execProfile[i]);
			instrs += Machine.execProfile[i];
			if ((i+1) % 4 == 0)
				System.err.println();
		}
		System.err.println("\nIntrinsic Functions\n");
		for (int i=0; i <= Machine.maxPrimitives; ++i) {
			System.err.format("%1$8s: %2$8d", Machine.primnames[i], Machine.primProfile[i]);
			prims += Machine.primProfile[i];
			if ((i+1) % 4 == 0)
				System.err.println();
		}
		System.err.format("\n\nTotal instructions: %1$7d    primitives: %2$7d\n", instrs, prims);
	}

	// RUNNING

	public static void main(String[] args) {
		System.out.println("********** MTAM Interpreter **********");

		debug = false;
		showStackIntel = false;
		objectName = "obj.tam";
		if(args.length!= 0){
			try{
				CommandLineParser cliParser = new DefaultParser();
				Options options = setupCLI();
				CommandLine cli = cliParser.parse(options, args);
				if(cli.hasOption("h")){
					HelpFormatter help = new HelpFormatter();
					help.printHelp("interpreter [OPTIONS]\n Options:",options);
					System.exit(0);
				}
				if(cli.hasOption("stack")){
					showStackIntel = true;
				}
				if(cli.hasOption("debug")){
					debug = true;
				}
				if(cli.hasOption("i")){
					objectName = cli.getOptionValue("i");
				}
			} catch (ParseException e) {
				System.err.print(e);
				System.exit(1);
			}
		}
		loadObjectProgram(objectName);
		if (CT != CB) {
			interpretProgram();
			showStatus();

			if(debug||showStackIntel){
				try {
					Thread.sleep(100);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			dumpProfile();
			cli.close();
			System.exit(status == halted ? 0 : 1 );
		}
	}

	private static void writeStack(int[] stack, int stackTop){
		String stackStr = "Current Stack: [";
		for(int i = 0; i < stackTop; i++){
			stackStr += stack[i];
			if(i!= stackTop-1){
				stackStr+= ", ";
			}
		}
		stackStr+= "]";
		System.out.println(stackStr);
		System.out.flush();
	}

	private static Options setupCLI(){
		Options options = new Options();
		options.addOption("h",false, "Print this help text");
		options.addOption("stack", false, "Print stack state during execution");
		options.addOption("debug",false, "Print all executed Instructions during execution");
		Option op = Option.builder("i").argName("input-file")
				.hasArg().numberOfArgs(1).desc("Use specified file for execution [else \"obj.tam\" is used]")
				.build();
		options.addOption(op);

		return options;
	}

}
