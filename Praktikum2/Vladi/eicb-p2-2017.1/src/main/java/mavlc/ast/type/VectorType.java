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
package mavlc.ast.type;

/**
 * Vector type.
 */
public class VectorType extends StructType {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 2761244243130460154L;
	
	protected final int dim;
	
	/**
	 * Constructor.
	 * @param elementType The type of elements in the vector type.
	 * @param dimension The number of elements in the vector type.
	 */
	public VectorType(ScalarType elementType, int dimension){
		super(elementType);
		dim = dimension;
	}

	/**
	 * Get the number elements in the vector type.
	 * @return Number of elements.
	 */
	public int getDimension() {
		return dim;
	}

	@Override
	public String toString() {
		return "VECTOR <"+elemType.toString()+"> ["+dim+"]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + dim;
		result = prime * result + ((elemType == null) ? 0 : elemType.hashCode());
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
		VectorType other = (VectorType) obj;
		if (dim != other.dim)
			return false;
		if (elemType == null) {
			if (other.elemType != null)
				return false;
		} else if (!elemType.equals(other.elemType))
			return false;
		return true;
	}

	@Override
	public int wordSize() {
		return dim;
	}
	
	

}
