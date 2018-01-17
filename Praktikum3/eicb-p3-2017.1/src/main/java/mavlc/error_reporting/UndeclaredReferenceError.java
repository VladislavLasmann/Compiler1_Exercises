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
package mavlc.error_reporting;

/**
 * Error class to signal a reference to a non-declared entity.
 */
public class UndeclaredReferenceError extends CompilationError {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4928305525480235378L;
	
	private final String ref;
	
	/**
	 * Constructor.
	 * @param referencedName The name of the referenced entity.
	 */
	public UndeclaredReferenceError(String referencedName){
		ref = referencedName;
		
		this.message = "Identifier "+ref+" has not been declared!";
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((ref == null) ? 0 : ref.hashCode());
		return result;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		UndeclaredReferenceError other = (UndeclaredReferenceError) obj;
		if (ref == null) {
			if (other.ref != null)
				return false;
		} else if (!ref.equals(other.ref))
			return false;
		return true;
	}

}
