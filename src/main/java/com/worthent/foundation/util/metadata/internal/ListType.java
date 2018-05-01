package com.worthent.foundation.util.metadata.internal;

import com.worthent.foundation.util.metadata.DataGetter;
import com.worthent.foundation.util.metadata.DataType;
import com.worthent.foundation.util.metadata.MetadataException;
import com.worthent.foundation.util.metadata.TypeCode;

/**
 * Type definition for a list data type.
 * 
 * @author Erik K. Worth
 */
public class ListType extends AbstractListType {

    /** Serial Version ID */
    private static final long serialVersionUID = 5993194548638564295L;
    
    /** Used for internalization purposes only */
    @SuppressWarnings("unused")
    private ListType() {
        super();
    }

    /**
     * Construct the list type from a concrete list class name and an element
     * data type
     * 
     * @param javaClassName the full java class name for a class that extends
     *        java.util.List
     * @param elementType the list element data type
     */
    protected ListType(final String javaClassName, final DataType elementType) {
        super(TypeCode.LIST, javaClassName, elementType);
    }
    
    /**
     * Copy constructor.
     */
   private ListType(final ListType other) {
       super(other);
   }

   /**
    * Construct from data object
    *
    * @param dataObject the data object holding the information used to define this type
    */
   protected ListType(final DataGetter dataObject)
       throws MetadataException {
       super(dataObject);
   }
   
    /*
     * (non-Javadoc)
     * 
     * @see com.worthent.foundation.service.metadata.DataType#deepCopy()
     */
    @Override
    public DataType deepCopy() {
        return new ListType(this);
    }

}
