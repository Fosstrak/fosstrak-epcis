package org.accada.epcis.repository;

/**
 * Miscellaneous utility functions.
 * @author Sean Wellington
 */
public class Functions {

	/**
	 * @return true if both o1 and o2 are null; or o1 and o2 are both non-null, 
	 * and equal according to their equals() methods.
	 */
	public static final boolean eq(Object o1, Object o2) {
		return (o1 == null && o2 == null) || (o1 != null && o2 != null && o1.equals(o2));
	}
	
	/**
	 * @return o.hashCode() or 0 if o is null.
	 */
	public static final int hc(Object o) {
		return o != null ? o.hashCode() : 0;
	}
	
}

