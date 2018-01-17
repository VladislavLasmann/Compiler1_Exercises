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
 * Matrix type.
 */
public class MatrixType extends StructType {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -2053459590024751252L;

	protected final int xDim;
	
	protected final int yDim;
	
	/**
	 * Constructor.
	 * @param elementType The type of the elements in the matrix type.
	 * @param xDimension The number of rows in the matrix type.
	 * @param yDimension The number of columns in the matrix type.
	 */
	public MatrixType(ScalarType elementType, int xDimension, int yDimension){
		super(elementType);
		xDim = xDimension;
		yDim = yDimension;
	}

	/**
	 * Get the number of rows of this matrix type.
	 * @return Number of rows.
	 */
	public int getxDimension() {
		return xDim;
	}

	/**
	 * Get the number of columns in this matrix type.
	 * @return Number of columns.
	 */
	public int getyDimension() {
		return yDim;
	}

	@Override
	public String toString() {
		return "MATRIX<"+elemType.toString()+"> ["+xDim+"]["+yDim+"]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((elemType == null) ? 0 : elemType.hashCode());
		result = prime * result + xDim;
		result = prime * result + yDim;
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
		MatrixType other = (MatrixType) obj;
		if (elemType == null) {
			if (other.elemType != null)
				return false;
		} else if (!elemType.equals(other.elemType))
			return false;
		if (xDim != other.xDim)
			return false;
		if (yDim != other.yDim)
			return false;
		return true;
	}

	@Override
	public int wordSize() {
		return xDim * yDim;
	}
	
	

}
