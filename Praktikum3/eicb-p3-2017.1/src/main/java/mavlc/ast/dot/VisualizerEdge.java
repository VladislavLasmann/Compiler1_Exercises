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

public class VisualizerEdge {
	
	public VisualizerNode a,b;
	private String style;
	
	public VisualizerEdge(VisualizerNode first, VisualizerNode second, String style){
		a = first;
		b = second;
		if(style==null){
			style ="";
		}else {
			this.style = style;
		}
	}
	
	public VisualizerEdge(VisualizerNode first, VisualizerNode second, String style, String color){
		a = first;
		b = second;
		if(style==null){
			style ="";
		}else {
			this.style = style+", color="+color;
		}
	}
	
	public String toASTString(){
		return a.getName() + " -> " + b.getName();
	}

	public String toDASTString(){
		if(style==null){
			if(b.getType()==null)return a.getName() + " -> " + b.getName();
			else return a.getName() + " -> " + b.getName() + "[label=\""+b.getType().toString()+"\", fontcolor=springgreen3]";
		}else{
			if(b.getType()==null)return a.getName() + " -> " + b.getName() + " [style="+style+", constraint=false]";
			else return a.getName() + " -> " + b.getName() + "[style="+style+", constraint=false]";
		}
	}
	
	@Override
	public String toString() {
		if(style==null){
			if(b.getType()==null)return a.getName() + " -> " + b.getName();
			else return a.getName() + " -> " + b.getName() + "[label=\""+b.getType().toString()+"\"]";
		}else{
			if(b.getType()==null)return a.getName() + " -> " + b.getName() + " [style="+style+", constraint=false, splines=ortho]";
			else return a.getName() + " -> " + b.getName() + "[style="+style+", constraint=false, splines=ortho]";
		}
		
	}
	
}
