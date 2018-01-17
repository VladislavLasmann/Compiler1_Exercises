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
package mavlc.codegen;

import java.io.DataOutputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import MTAM.Instruction;
import MTAM.Machine;
import mavlc.ast.nodes.expression.Compare.Comparison;
import mavlc.ast.nodes.function.Function;
import mavlc.ast.nodes.statement.Declaration;

public class TAMAssembler {
	
	public enum Register{
		LB(Machine.LBr),
		ST(Machine.STr);
		
		
		private int id;
		Register(int ID){
			id = ID;
		}
		
		int getID(){
			return id;
		}
	}
	
	protected final ArrayList<Instruction> code = new ArrayList<Instruction>();

	protected int nextInstAddr = Machine.CB;
	
	protected final Map<String, Integer> constantPool = new HashMap<>();
	
	protected int nextConstIndex = 0;
	
	protected int nextOffset;
	
	public int getNextOffset() {
		return nextOffset;
	}

	public void setNextOffset(int nextOffset) {
		this.nextOffset = nextOffset;
	}

	protected final Instruction initialJump;
	
	public TAMAssembler(){
		initialJump = new Instruction(Machine.CALLop, Machine.CBr, 0, -1);
		addInstruction(initialJump);
	}
	
	/**
	 * Write the Object Code to a UTF Encoded File.
	 * @param fileName Path to the Outputfile
	 */
	public void writeObjectProgram(String fileName){
		try(DataOutputStream writer = new DataOutputStream(new FileOutputStream(fileName))){
			/*
			 * Write the constant pool: First emit the index of a string, 
			 * then the string itself.
			 */
			if(!constantPool.isEmpty()){
				writer.writeUTF(".data");
				for(Entry<String, Integer> constant : constantPool.entrySet()){
					writer.writeUTF(constant.getKey());
					writer.writeInt(constant.getValue());
				}
			}
			/*
			 * Write the instructions
			 */
			writer.writeUTF(".text");
			for(Instruction inst : code){
				inst.write(writer);
			}
		} catch (FileNotFoundException e) {
			throw new RuntimeException(e);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	private void addInstruction(Instruction inst){
		code.add(inst);
		++nextInstAddr;
	}
	
	private int getStringIndex(String string){
		if(constantPool.containsKey(string)){
			return constantPool.get(string);
		}
		else{
			constantPool.put(string, nextConstIndex);
			return nextConstIndex++;
		}
	}
	
	protected final Map<Function, List<Instruction>> referencedFunctions = new HashMap<>();
	
	/**
	 * Add a function to the programcode. If the added function is the main function,
	 * the initial jump is automatically patched.
	 * @param function Function to add to the code
	 */
	public void addNewFunction(Function function){
		/*
		 * The new function starts at the next instruction address.
		 */
		function.setSourceCodeOffset(nextInstAddr);
		/*
		 * Back-patch already emitted call instructions referencing 
		 * to this function.
		 */
		if(referencedFunctions.containsKey(function)){
			for(Instruction call : referencedFunctions.get(function)){
				call.d = nextInstAddr;
			}
		}
		/*
		 * If the new function is the main method, patch the 
		 * initial jump to point to this function.
		 */
		if(function.getName().equals("main")){
			initialJump.d = nextInstAddr;
		}
		/*
		 * Function-local data start at offset 2 
		 * relative to LB because of the link data.
		 */
		nextOffset = 2;
		
	}
	
	/**
	 * Emit a Call to a Function.
	 * @param callee Function to call
	 */
	public void emitFunctionCall(Function callee){
		if(callee.getSourceCodeOffset()>0){
			addInstruction(new Instruction(Machine.CALLop, Machine.CBr, 0, callee.getSourceCodeOffset()));
		}
		else{
			/*
			 * Code generation for the called function has not yet taken place, store call instruction 
			 * for later back-patch.
			 */
			Instruction call = new Instruction(Machine.CALLop, Machine.CBr, 0, callee.getSourceCodeOffset()); 
			addInstruction(call);
			addToMapSet(callee, call);
		}
	}
	
	/**
	 * Emit a return Instruction.
	 * @param resultSize WordSize of the Result
	 * @param argSize Size of the Function Arguments
	 */
	public void emitReturn(int resultSize, int argSize){
		addInstruction(new Instruction(Machine.RETURNop, 0, resultSize, argSize));
	}

	/**
	 * Emit a Halt Instruction, which terminates the programm execution.
	 */
	public void emitHaltInstruction() {
		addInstruction(new Instruction(Machine.HALTop, 0, 0, 0));
	}
	
	private void addToMapSet(Function key, Instruction value){
		if(!referencedFunctions.containsKey(key)){
			referencedFunctions.put(key, new LinkedList<>());
		}
		referencedFunctions.get(key).add(value);
	}
	
	/**
	 * Add a Variable or Constant to the Local Scope.
	 * @param decl Declared Entity to add
	 */
	public void addDeclaredEntity(Declaration decl){
		decl.setLocalBaseOffset(nextOffset);
		nextOffset += decl.getType().wordSize();
	}
	
	/**
	 * Emit a load instruction, to load a single Boolean Value onte the Stack.
	 * @param value Value of the boolean
	 */
	public void loadBooleanValue(boolean value){
		int literal = (value) ? Machine.trueRep : Machine.falseRep;
		addInstruction(new Instruction(Machine.LOADLop, 0, 0, literal));
	}
	
	/**
	 * Emit a load instruction, to load a single Integer Value onte the Stack.
	 * @param value Value of the int
	 */
	public void loadIntegerValue(int value){
		addInstruction(new Instruction(Machine.LOADLop, 0, 0, value));
	}
	
	/**
	 * Emit a load instruction, to load a single Float Value onte the Stack.
	 * @param value Value of the float
	 */
	public void loadFloatValue(float value){
		addInstruction(new Instruction(Machine.LOADLop, 0, 0, Float.floatToIntBits(value)));
	}
	
	/**
	 * Emit a load instruction, which loads a String onto the Stack.
	 * @param value Value of the StringValue
	 */
	public void loadStringValue(String value){
		int literal = getStringIndex(value);
		addInstruction(new Instruction(Machine.LOADLop, 0, 0, literal));
	}
	
	/**
	 * Emit a load instruction for an entity local to the function. 
	 * Addressing on the stack will be relative to register LB.
	 * @param wordSize Number of words to load.
	 * @param offset Offset from LB.
	 */
	public void loadLocalValue(int wordSize, int offset){
		addInstruction(new Instruction(Machine.LOADop, Machine.LBr, wordSize, offset));
	}
	
	/**
	 * Emit a load instruction for an entity.
	 * Addressing on the stack will be relative to the given register.
	 * @param register register used for the adrressing
	 * @param wordSize Number of words to load
	 * @param offset Offset from the given Register
	 */
	public void loadValue(Register register, int wordSize, int offset){
		addInstruction(new Instruction(Machine.LOADop, register.getID(), wordSize, offset));
	}
	
	/**
	 * Load an address relative to a register onto the stack.
	 * Loaded address equals register value + offset.
	 * @param register Base register.
	 * @param offset Offset from the register.
	 */
	public void loadAddress(Register register, int offset){
		addInstruction(new Instruction(Machine.LOADAop, register.getID(), 0, offset));
	}
	
	/**
	 * Emit a load instruction which uses the value on top 
	 * of the stack as address and loads the given number of words.
	 * @param wordSize The number of words to load.
	 */
	public void loadFromStackAddress(int wordSize){
		addInstruction(new Instruction(Machine.LOADIop, 0, wordSize, 0));
	}
	
	/**
	 * Emit a store instruction which stores the given number 
	 * of words to the address on top of the stack.
	 * @param wordSize The number of words to store.
	 */
	public void storeToStackAddress(int wordSize){
		addInstruction(new Instruction(Machine.STOREIop, 0, wordSize, 0));
	}
	
	/**
	 * Emit a store instruction for an entity local to the function. 
	 * Addressing on the stack will be relative to register LB.
	 * @param wordSize Number of words to store.
	 * @param offset Offset from LB.
	 */
	public void storeLocalValue(int wordSize, int offset){
		addInstruction(new Instruction(Machine.STOREop, Machine.LBr, wordSize, offset));
	}
	
	/**
	 * Emit a store instruction for an entity.
	 * Addressing on the stack will be relative to a given register.
	 * @param register Register
	 * @param wordSize Number of words to store
	 * @param offset Offset from given register
	 */
	public void storeValue(Register register, int wordSize, int offset){
		addInstruction(new Instruction(Machine.STOREop, register.getID(), wordSize, offset));
	}
	
	/**
	 * Emit a pop instruction. The pop instruction pops a result of size 
	 * resultSize from the stack, deletes another popSize words from the
	 * stack and pushes the result back on the stack. 
	 * @param resultSize Size of the result.
	 * @param popSize Number of words to delete.
	 */
	public void emitPop(int resultSize, int popSize){
		addInstruction(new Instruction(Machine.POPop, 0, resultSize, popSize));
	}
	
	/**
	 * Emit a push instruction pushing size uninitialized words on the stack.
	 * @param size Number of words to push.
	 */
	public void emitPush(int size){
		addInstruction(new Instruction(Machine.PUSHop, 0, 0, size));
	}
	
	/**
	 * Emit an unconditional jump to the given code address.
	 * @param address The target code address.
	 * @return The jump instruction.
	 */
	public Instruction emitJump(int address){
		Instruction jump = new Instruction(Machine.JUMPop, Machine.CBr, 0, address);
		addInstruction(jump);
		return jump;
	}
	
	/**
	 * Emit a conditional jump to the given code address 
	 * under the given condition.
	 * @param condition The condition of the jump.
	 * @param address The target code address.
	 * @return The jump instruction.
	 */
	public Instruction emitConditionalJump(boolean condition, int address){
		int n = (condition) ? Machine.trueRep : Machine.falseRep;
		Instruction jump = new Instruction(Machine.JUMPIFop, Machine.CBr, n, address);
		addInstruction(jump);
		return jump;
	}
	
	/**
	 * Get the code address of the next instruction emitted.
	 * @return The next free code address.
	 */
	public int getNextInstructionAddress(){
		return nextInstAddr;
	}
	
	/**
	 * Patch a jump-instruction to point to a new address.
	 * @param inst The jump instruction to be modified.
	 * @param newAddress The new address.
	 */
	public void backPatchJump(Instruction inst, int newAddress){
		if(!(inst.op==Machine.JUMPIFop || inst.op==Machine.JUMPop)){
			throw new UnsupportedOperationException("Can only backpatch jump instruction!");
		}
		inst.d = newAddress;
	}
	
	/**
	 * Emits an Integer Negation, which consumes the upmost Element of the Stack, and pushes
	 * its Negation onto the Stack.
	 * The consumed Element is interpreted as Integer Number.
	 */
	public void emitIntegerNegation(){
		addInstruction(new Instruction(Machine.CALLop, Machine.PBr, 0, Machine.negDisplacement));
	}
	
	/**
	 * Emits an Integer Multiplication, which consumes the two upmost Elements of the Stack,
	 * multiplies them and pushed the result onto the Stack.
	 * The consumed Elements are interpreted as Integer Numbers.
	 */
	public void emitIntegerMultiplication(){
		addInstruction(new Instruction(Machine.CALLop, Machine.PBr, 0, Machine.multDisplacement));
	}
	
	/**
	 * Emits an Integer Division, which consumes the two upmost Elements of the Stack, divides the
	 * lower one by the upper one, and pushes the result onto the Stack.
	 * The consumed Elements are interpreted as Integer Numbers.
	 */
	public void emitIntegerDivision(){
		addInstruction(new Instruction(Machine.CALLop, Machine.PBr, 0, Machine.divDisplacement));
	}
	
	/**
	 * Emits an Integer Addition, which consumes the two upmost Elements of the Stack, adds them up
	 * and pushes the result onto the Stack.
	 * The consumed Elements are interpreted as Integer Numbers.
	 */
	public void emitIntegerAddition(){
		addInstruction(new Instruction(Machine.CALLop, Machine.PBr, 0, Machine.addDisplacement));
	}
	
	/**
	 * Emits an Integer Subtraction, which consumes the two upmost Elements of the Stack, subtracts
	 * the upper one from the lower one and pushes the result to the Stack.
	 * The consumed Elements are interpreted as Integer Numbers.
	 */
	public void emitIntegerSubtraction(){
		addInstruction(new Instruction(Machine.CALLop, Machine.PBr, 0, Machine.subDisplacement));
	}
	
	/**
	 * Emits an Integer Exponentiation, which consumes the two upmost Elements of the Stack.
	 * The upmost is interpreted as the power, the one below as base. 
	 * The result is than pushed onto the Stack.
	 * The consumed Elements are interpreted as Integer Numbers.
	 */
	public void emitIntegerExponentiation(){
		addInstruction(new Instruction(Machine.CALLop, Machine.PBr, 0, Machine.powIntDisplacement));
	}

	/**
	 * Emits an Integer Comparison Operation, which consumes the two upmost Elements of the Stack,
	 * and pushes the Comparison Result onto the Stack.
	 * The consumed Elements are interpreted as Integer Numbers.
	 * @param comparison Comparison Operator
	 */
	public void emitIntegerComparison(Comparison comparison){
		int displacement = 0;
		switch(comparison){
		case EQUAL:
			displacement = Machine.eqDisplacement;
			break;
		case GREATER:
			displacement = Machine.gtDisplacement;
			break;
		case GREATER_EQUAL:
			displacement = Machine.geDisplacement;
			break;
		case LESS:
			displacement = Machine.ltDisplacement;
			break;
		case LESS_EQUAL:
			displacement = Machine.leDisplacement;
			break;
		case NOT_EQUAL:
			displacement = Machine.neDisplacement;
			break;
		default:
			break;
		}
		addInstruction(new Instruction(Machine.CALLop, Machine.PBr, 0, displacement));
	}
	
	
	/**
	 * Emits a Floating Point Negation, which consumes the upmost Element of the Stack, and pushes
	 * its Negation onto the Stack.
	 * The consumed Elements are interpreted as Floating Point Numbers.
	 */
	public void emitFloatNegation(){
		addInstruction(new Instruction(Machine.CALLop, Machine.PBr, 0, Machine.floatNegDisplacement));
	}
	
	/**
	 * Emits a Floating Point Multiplication, which consumes the two upmost Elements of the Stack,
	 * multiplies them and pushes the result onto the Stack.
	 * The consumed Elements are interpreted as Floating Point Numbers.
	 */
	public void emitFloatMultiplication(){
		addInstruction(new Instruction(Machine.CALLop, Machine.PBr, 0, Machine.floatMulDisplacement));
	}
	
	/**
	 * Emits a Floating Point Division, which consumes the two upmost Elements of the Stack, divides
	 * the lower one by the upper one and pushes the result onto the Stack.
	 * The consumed Elements are interpreted as Floating Point Numbers.
	 */
	public void emitFloatDivision(){
		addInstruction(new Instruction(Machine.CALLop, Machine.PBr, 0, Machine.floatDivDisplacement));
	}
	
	/**
	 * Emits a Floating Point Addition, which consumes the two upmost Elements of the Stack, adds them
	 * up and pushes the result onto the Stack.
	 * The consumed Elements are interpreted as Floating Point Numbers.
	 */
	public void emitFloatAddition(){
		addInstruction(new Instruction(Machine.CALLop, Machine.PBr, 0, Machine.floatAddDisplacement));
	}
	
	/**
	 * Emits a Floating Point Subtraction, which consumes the two upmost Elements of the Stack, subtracts
	 * the upper one from the lower one and pushes the result onto the stack.
	 * The consumed Elements are interpreted as Floating Point Numbers.
	 */
	public void emitFloatSubtraction(){
		addInstruction(new Instruction(Machine.CALLop, Machine.PBr, 0, Machine.floatSubDisplacement));
	}
	
	/**
	 * Emits an Float Exponentiation, which consumes the two upmost Elements of the Stack.
	 * The upmost is interpreted as the power, the one below as base. 
	 * The result is than pushed onto the Stack.
	 * The consumed Elements are interpreted as Float Numbers.
	 */
	public void emitFloatExponentiation(){
		addInstruction(new Instruction(Machine.CALLop, Machine.PBr, 0, Machine.powFloatDisplacement));
	}
	
	/**
	 * Emits a Floating Point Comparison Operation, that consumes the two upmost Stack elements  
	 * and pushes the Comparison result onto the Stack.
	 * The consumed Elements are interpreted as Floating Point Numbers.
	 * @param comparison Comparision Operator
	 */
	public void emitFloatComparison(Comparison comparison){
		int displacement = 0;
		switch(comparison){
		case EQUAL:
			displacement = Machine.floatEQDisplacement;
			break;
		case GREATER:
			displacement = Machine.floatGTDisplacement;
			break;
		case GREATER_EQUAL:
			displacement = Machine.floatGEDisplacement;
			break;
		case LESS:
			displacement = Machine.floatLTDisplacement;
			break;
		case LESS_EQUAL:
			displacement = Machine.floatLEDisplacement;
			break;
		case NOT_EQUAL:
			displacement = Machine.floatNEDisplacement;
			break;
		default:
			break;
		}
		addInstruction(new Instruction(Machine.CALLop, Machine.PBr, 0, displacement));
	}
	
	/**
	 * Emit a Call to the Matrix Multiplication Primitive for ints.
	 */
	public void emitIntegerMatrixMultiplication(){
		addInstruction(new Instruction(Machine.CALLop, Machine.PBr, 0, Machine.matrixIntMultDisplacement));
	}
	
	/**
	 * Emit a Call to the Matrix Multiplication Primitive for floats.
	 */	
	public void emitFloatMatrixMultiplication(){
		addInstruction(new Instruction(Machine.CALLop, Machine.PBr, 0, Machine.matrixFloatMultDisplacement));
	}
	
	
	/**
	 * Emit a logical Not Operation that consumes the upmost element of the Stack, and replaces it with
	 * its negation.
	 */	
	public void emitLogicalNot(){
		addInstruction(new Instruction(Machine.CALLop, Machine.PBr, 0, Machine.notDisplacement));
	}
	
	/**
	 * Emit a logical Or Operation that consumes the two upmost Stack elements, applies the Logical
	 * Or Operation, and pushes the result to the Stack.
	 */
	public void emitLogicalOr(){
		addInstruction(new Instruction(Machine.CALLop, Machine.PBr, 0, Machine.orDisplacement));
	}
	
	/**
	 * Emit a logical And Operation that consumes the two upmost Stack elements, applies the Logical
	 * And Operation, and pushes the result to the Stack.
	 */
	public void emitLogicalAnd(){
		addInstruction(new Instruction(Machine.CALLop, Machine.PBr, 0, Machine.andDisplacement));
	}

	/**
	 * Returns the Program Code as List of Instructions.
	 * @return the code
	 */
	public ArrayList<Instruction> getCode() {
		return code;
	}

	/**
	 * Returns the Constant Pool, which contains all constants.
	 * @return the constantPool
	 */
	public Map<Integer, String> getConstantPool() {
		Map<Integer, String> cp = new HashMap<>();
		for(Entry<String, Integer> constant : constantPool.entrySet()){
			cp.put(constant.getValue(), constant.getKey());
		}
		return cp;
	}


}
