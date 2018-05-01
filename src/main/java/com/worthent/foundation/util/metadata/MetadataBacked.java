package com.worthent.foundation.util.metadata;

/**
 * Interface mixed into objects that are able to expose metadata that describes
 * their persistent state.  This allows them to be externalized and 
 * internalized to and from XML or other supported representations.
 * 
 * @author Erik K. Worth
 */
public interface MetadataBacked {

    /** @return the metadata that describes the state of this object */
    DataType getStateMetadata();
    
    /** @return a data object holding the state of this object */
    DataGetter getState() throws MetadataException;
}
