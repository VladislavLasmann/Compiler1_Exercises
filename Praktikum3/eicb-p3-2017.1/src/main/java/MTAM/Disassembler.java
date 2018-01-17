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
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import mavlc.ast.nodes.function.Function;

/**
 * Disassembles the TAM code in the given file, and displays the
 * instructions on standard output.
 *
 * For example:
 * <pre>
 *   java MTAM.Disassembler obj.tam
 * </pre>
 *
 */
public class Disassembler {

	static String objectName;

	static int CT;
	private static HashMap<Integer, Function> offsetToFunctionMap;

	/**
	 * Writes the r-field of an instruction in the form "l<I>reg</I>r", where
	 * l and r are the bracket characters to use.
	 * @param leftbracket  the character to print before the register.
	 * @param r            the number of the register.
	 * @param rightbracket the character to print after the register.
	 * @param stream       the output stream for the disassembled code.
	 */
	private static void writeR (char leftbracket, int r, char rightbracket, PrintStream stream) {

		stream.print(leftbracket);
		switch (r) {
		case Machine.CBr:
			stream.print ("CB");
			break;
		case Machine.CTr:
			stream.print ("CT");
			break;
		case Machine.PBr:
			stream.print ("PB");
			break;
		case Machine.PTr:
			stream.print ("PT");
			break;
		case Machine.SBr:
			stream.print ("SB");
			break;
		case Machine.STr:
			stream.print ("ST");
			break;
		case Machine.HBr:
			stream.print ("HB");
			break;
		case Machine.HTr:
			stream.print ("HT");
			break;
		case Machine.LBr:
			stream.print ("LB");
			break;
		case Machine.CPr:
			stream.print ("CP");
			break;
		}
		stream.print (rightbracket);
	}

	/**
	 * Writes a void n-field of an instruction.
	 * 
	 * @param stream the output stream for the disassembled code.
	 */
	private static void blankN(PrintStream stream) {
		stream.print ("      ");
	}

	/**
	 * Writes the n-field of an instruction in the form "(n)".
	 * @param n      the integer to write.
	 * @param stream the output stream for the disassembled code.
	 */
	private static void writeN (int n, PrintStream stream) {
		stream.print ("(" + n + ") ");
		if (n < 10)
			stream.print ("  ");
		else if (n < 100)
			stream.print (" ");
	}

	/**
	 * Writes the d-field of an instruction.
	 * @param d      the integer to write.
	 * @param stream the output stream for the disassembled code.
	 */
	private static void writeD (int d, PrintStream stream) {
		stream.print (d);
	}

	/**
	 * Writes the name of primitive routine with relative address d.
	 * @param d      the displacment of the primitive routine.
	 * @param stream the output stream for the disassembled code.
	 */
	private static void writePrimitive (int d, PrintStream stream) {
		stream.print(Machine.primnames[d]);
	}

	/**
	 * Writes the given instruction in assembly-code format.
	 * @param instr  the instruction to display.
	 * @param stream the output stream for the disassembled code.
	 */
	public static void writeInstruction (Instruction instr, PrintStream stream) {
		switch (instr.op) {
		case Machine.LOADop:
			stream.print ("LOAD  ");
			writeN(instr.n, stream);
			writeD(instr.d, stream);
			writeR('[', instr.r, ']', stream);
			break;

		case Machine.LOADAop:
			stream.print ("LOADA ");
			blankN(stream);
			writeD(instr.d, stream);
			writeR('[', instr.r, ']', stream);
			break;

		case Machine.LOADIop:
			stream.print ("LOADI ");
			writeN(instr.n, stream);
			break;

		case Machine.LOADLop:
			stream.print ("LOADL ");
			blankN(stream);
			writeD(instr.d, stream);
			break;

		case Machine.STOREop:
			stream.print ("STORE ");
			writeN(instr.n, stream);
			writeD(instr.d, stream);
			writeR('[', instr.r, ']', stream);
			break;

		case Machine.STOREIop:
			stream.print ("STOREI");
			writeN(instr.n, stream);
			break;

		case Machine.CALLop:
			if (instr.r == Machine.PBr) {
				writePrimitive(instr.d, stream);
			} else {
				stream.print ("CALL  ");
				writeR('(', instr.n, ')', stream);
				stream.print ("  ");
				writeD(instr.d, stream);
				writeR('[', instr.r, ']', stream);
				if(offsetToFunctionMap!=null){
					stream.print("\t" + offsetToFunctionMap.get(instr.d).getName());
				}
			}
			break;

		case Machine.CALLIop:
			stream.print ("CALLI ");
			break;

		case Machine.RETURNop:
			stream.print ("RETURN");
			writeN(instr.n, stream);
			writeD(instr.d, stream);
			break;

		case Machine.PUSHop:
			stream.print ("PUSH  ");
			blankN(stream);
			writeD(instr.d, stream);
			break;

		case Machine.POPop:
			stream.print ("POP   ");
			writeN(instr.n, stream);
			writeD(instr.d, stream);
			break;

		case Machine.JUMPop:
			stream.print ("JUMP  ");
			blankN(stream);
			writeD(instr.d, stream);
			writeR('[', instr.r, ']', stream);
			break;

		case Machine.JUMPIop:
			stream.print ("JUMPI ");
			break;

		case Machine.JUMPIFop:
			stream.print ("JUMPIF");
			writeN(instr.n, stream);
			writeD(instr.d, stream);
			writeR('[', instr.r, ']', stream);
			break;

		case Machine.HALTop:
			stream.print ("HALT  ");
		}
	}

	/**
	 * Writes all instructions of the program in code store.
	 * 
	 * @param stream       the output stream for the disassembled code.
	 * @param constantPool the constant pool.
	 * @param code         the code to disassemble.
	 * @param env          the module environment, i.e. a collection of known functions.
	 */
	public static void disassembleProgram(PrintStream stream, Map<Integer, String> constantPool, List<Instruction> code, Collection<Function> env) {
		offsetToFunctionMap = new HashMap<>();
		for (Function function : env)
			offsetToFunctionMap.put(function.getSourceCodeOffset(), function);

		int addr = Machine.CB;
		if(!constantPool.isEmpty()){
			stream.println(".data");
			for(Entry<Integer, String> string : constantPool.entrySet()){
				stream.println(string.getKey()+"->"+string.getValue());
			}
		}
		stream.println(".text");
		for(Instruction inst : code){
			if (offsetToFunctionMap.containsKey(addr)) {
				stream.println();
				stream.print("# " + offsetToFunctionMap.get(addr).getSignature() + "\n");
			}
			stream.print(String.format("%04X:  ", addr));
			writeInstruction(inst, stream);
			stream.println();
			++addr;
		}
	}


	// LOADING

	static Map<Integer, String> loadConstantPool(DataInputStream reader){
		Map<Integer, String> constantPool = new HashMap<>();
		try {
			String text = reader.readUTF();
			if(text.equals(".data")){
				while(true){
					text = reader.readUTF();
					if(text.equals(".text")){
						break;
					}
					int index = reader.readInt();
					constantPool.put(index, text);
				}
			}
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		return constantPool;
	}

	static List<Instruction> loadObjectCode (DataInputStream reader) {
		try{
			List<Instruction> code = new ArrayList<>();
			while(true){
				Instruction inst = Instruction.read(reader);
				if(inst!=null){
					code.add(inst); 
				}
				else{
					break;
				}
			}
			return code;
		}
		catch(IOException e){
			throw new RuntimeException(e);
		}
	}


	// DISASSEMBLE

	public static void main(String[] args) {
		System.out.println ("********** MTAM Disassembler **********");

		if (args.length == 1)
			objectName = args[0];
		else
			objectName = "obj.tam";

		try(DataInputStream reader = new DataInputStream(new FileInputStream(objectName))){
			Map<Integer, String> constantPool = loadConstantPool(reader);
			List<Instruction> code = loadObjectCode(reader);
			// Sadly, functions and symbols are not known at this point :(
			disassembleProgram(System.out, constantPool, code, new LinkedList<>());
		} catch (FileNotFoundException e) {
			throw new RuntimeException(e);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
}
