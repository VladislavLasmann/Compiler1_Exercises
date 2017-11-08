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
package mavlc.ast.serialization;

import mavlc.ast.nodes.ASTNode;

public abstract class ASTSerialization {
	
	public abstract void serialize(ASTNode node, String fileName);
	
	public abstract String serialize(ASTNode node);
	
	public abstract ASTNode deserialize(String fileName);
	
}
