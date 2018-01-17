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
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.IOException;

public class Instruction {

	public Instruction() {
		op = 0;
		r = 0;
		n = 0;
		d = 0;
	}

	public Instruction(int op, int r, int n, int d) {
		this.op = op;
		this.r = r;
		this.n = n;
		this.d = d;
	}

	// Java has no type synonyms, so the following representations are
	// assumed:
	//
	//  type
	//    OpCode = 0..15;  {4 bits unsigned}
	//    Length = 0..255;  {8 bits unsigned}
	//    Operand = -32767..+32767;  {16 bits signed}

	// Represents TAM instructions.
	public int op; // OpCode
	public int r;  // RegisterNumber
	public int n;  // Length
	public int d;  // Operand

	public void write(DataOutputStream output) throws IOException {
		output.writeInt (op);
		output.writeInt (r);
		output.writeInt (n);
		output.writeInt (d);
	}

	public static Instruction read(DataInputStream input) throws IOException {
		Instruction inst = new Instruction();
		try {
			inst.op = input.readInt();
			inst.r = input.readInt();
			inst.n = input.readInt();
			inst.d = input.readInt();
			return inst;
		} catch (EOFException s) {
			return null;
		}
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + op;
		result = prime * result + r;
		result = prime * result + n;
		result = prime * result + d;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Instruction other = (Instruction) obj;
		if (op != other.op)
			return false;
		if (r != other.r)
			return false;
		if (n != other.n)
			return false;
		if (d != other.d)
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "" + op + "(" + n + ") " + d + "[" + r + "]";
	}
}
