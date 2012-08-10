package jplayground.clipboard;

import java.awt.Image;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;
import java.io.Serializable;

import jplayground.etc.Null;


/**
 *	Universal transferable class . Can transfer mainly everything . Auto-filters some types , if no filter found then creates new MimeType . 
 */
public class TransferableX implements Transferable {
	
    public Object i;
    public boolean jvmonly;
    public DataFlavor flavor;
	
    /**
     * Simple constructor for simple uses .
     * @param i Object to transfer
     */
    public TransferableX(Object i) {
        this(i, false, null);
    }
    
    /**
     * Simple constructor for simple uses .
     * @param i Object to transfer
     * @param jvmonly true if transfering only inside of JVM , false when transfering everywhere .
     */
    public TransferableX(Object i, boolean jvmonly) {
    	this(i, jvmonly, null);
    }
    
    /**
     * Simple constructor for simple uses .
     * @param i Object to transfer
     * @param jvmonly true if transfering only inside of JVM , false when transfering everywhere .
     * @param flavor Flavor to use if no other works or for special tasks .
     */
    public TransferableX(Object i, DataFlavor flavor) {
    	this(i, false, flavor);
    }
    
    /**
     * Simple constructor for simple uses .
     * @param i Object to transfer
     * @param jvmonly true if transfering only inside of JVM , false when transfering everywhere .
     * @param flavor Flavor to use if no other works or for special tasks .
     */
    public TransferableX(Object i, boolean jvmonly, DataFlavor flavor) {
    	this.i = i;
    	this.jvmonly = jvmonly;
    	this.flavor = flavor;
    }
    
    public Object getTransferData(DataFlavor flavor) throws UnsupportedFlavorException {
        if (flavor.equals(getDataFlavorFor(i)) && i != null ) {
            return i;
        } else {
            throw new UnsupportedFlavorException(flavor);
        }
    }
    
    public DataFlavor[] getTransferDataFlavors() {
        DataFlavor[] flavors = new DataFlavor[1];
        flavors[0] = getDataFlavorFor(i);
        return flavors;
    }
    
    /**
     * Gets the dataflavor for the given object .
     * @param i Object to get flavor for
     * @return DataFlavor for the given object
     */
    public final DataFlavor getDataFlavorFor(Object i) {
    	if (flavor != null) {
    		return flavor;
    	}
    	if (i == null) {
        	String name = "null";
        	String nameFull = "etc.Null";
        	String mimeType = "application/x-java-trx-"+name+";class="+nameFull;
        	return createConstant(mimeType, null);
    	}
    	if (i instanceof java.lang.String) {
    		return DataFlavor.stringFlavor;
    	}
    	if (i instanceof java.awt.Image) {
    		return DataFlavor.imageFlavor;
    	}
    	if (i instanceof java.util.List) {
    		java.util.List list = (java.util.List) i;
    		boolean isFileList = true;
    		for (Object o : list) {
    			if (!(o instanceof java.io.File)) {
    				isFileList = false;
    			}
    		}
    		if (isFileList) {
    			return DataFlavor.javaFileListFlavor;
    		}
    	}
    	String name = i.getClass().getSimpleName().toLowerCase();
    	String nameFull = i.getClass().getName();
    	if (name.isEmpty()) {
    		name = "null";
    		nameFull = "etc.Null";
    	}
    	String mimeType = "application/x-java-trx-"+name+";class="+nameFull;
    	if (i instanceof java.rmi.Remote) {
    		createConstant(DataFlavor.javaRemoteObjectMimeType+";class="+nameFull, null);
    	}
    	if (i instanceof Serializable) {
    		return createConstant(DataFlavor.javaSerializedObjectMimeType+";class="+nameFull, null);
    	}
    	if (jvmonly) {
    		return createConstant(DataFlavor.javaJVMLocalObjectMimeType+";class="+nameFull, null);
    	}
    	return createConstant(mimeType, null);
	}
    
    /**
     * @see DataFlavor#DataFlavor(String, String)
     */
    public static DataFlavor createConstant(String mimeType, String humanPresentableName) {
        try {
            return new DataFlavor(mimeType, humanPresentableName);
        } catch (Exception e) {
            return null;
        }
    }
    
	public boolean isDataFlavorSupported(DataFlavor flavor) {
        DataFlavor[] flavors = getTransferDataFlavors();
        for (int i = 0; i < flavors.length; i++) {
            if (flavor.equals(flavors[i])) {
                return true;
            }
        }
        return false;
    }
    
}
