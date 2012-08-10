package jplayground.etc;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * Almost empty class representing null .
 *
 */
public final class Null {
	
    /**
     * The Class object representing the pseudo-type corresponding to
     * the keyword null.
     */
    public static final Class<Null> TYPE = type();
    
    private static final Class<Null> type() {
		try {
			Method method = Class.class.getMethod("getPrimitiveClass");
			return (Class<Null>) method.invoke(null, "null");
		} catch (IllegalArgumentException e) {
		} catch (IllegalAccessException e) {
		} catch (InvocationTargetException e) {
		} catch (NullPointerException e) {
		} catch (ExceptionInInitializerError e) {
		} catch (SecurityException e) {
		} catch (NoSuchMethodException e) {
		}
    	return null;
    }
	
}
