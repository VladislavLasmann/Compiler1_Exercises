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
package mavlc.ast.dot;

import mavlc.ast.type.Type;

public class VisualizerNode {
	
	private int id;
	private String label;
	private Type type;
	
	public VisualizerNode(int id, String label, Type type){
		this.id = id;
		this.label = label;
		this.type = type;
	}
	
	public String getName(){
		return "node"+id;
	}
	
	public Type getType() {
		return type;
	}
	
	@Override
	public String toString() {
		return "node"+id+" [label=\""+label+"\"];";
	}
}
