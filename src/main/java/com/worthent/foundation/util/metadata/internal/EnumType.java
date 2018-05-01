package com.worthent.foundation.util.metadata.internal;

import java.util.ArrayList;
import java.util.Collection;

import com.worthent.foundation.util.metadata.DataGetter;
import com.worthent.foundation.util.metadata.MetadataException;
import com.worthent.foundation.util.metadata.TypeCode;
import com.worthent.foundation.util.metadata.validator.EnumValidator;

/**
 * Represents an enumerated data type.
 * 
 * @author Erik K. Worth
 */
public class EnumType extends SimpleType {

    /** Serial Version ID */
    private static final long serialVersionUID = -1183453415006969324L;
    
    /** Used for internalization purposes only */
    @SuppressWarnings("unused")
    private EnumType() {
        super();
    }

    /**
     * Construct with the enumerated values.
     * 
     * @param choices the specific set of values that instances of this type are
     *        allowed to have
     */
    protected EnumType(final Collection<String> choices) {
        super(TypeCode.ENUMERATED, String.class.getName());
        if (null == choices) {
            throw new IllegalArgumentException("choices must not be null");
        }
        setAttribute(CHOICES, new ArrayList<>(choices));
        addValidator(new EnumValidator(choices));
    }

    /**
     * Construct from a state data object
     * 
     * @param dataObject the data object holding the state of the enumerated
     *        type definition
     * @throws MetadataException thrown when there is an error creating the type
     *         from its state object
     */
    EnumType(final DataGetter dataObject) throws MetadataException {
        super(dataObject);
    }

}
