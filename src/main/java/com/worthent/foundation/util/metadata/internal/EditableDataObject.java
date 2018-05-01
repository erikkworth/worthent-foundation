package com.worthent.foundation.util.metadata.internal;

import com.worthent.foundation.util.lang.StringUtils;
import com.worthent.foundation.util.metadata.DataGetter;
import com.worthent.foundation.util.metadata.DataSetter;
import com.worthent.foundation.util.metadata.MetadataException;
import com.worthent.foundation.util.recorder.DataErrorRecorder;
import com.worthent.foundation.util.recorder.RecorderFactory;

/**
 * A data object that supports set methods.
 * 
 * @author Erik K. Worth
 */
public class EditableDataObject extends DataObject implements DataSetter {

    /** Serial Version ID */
    private static final long serialVersionUID = -5183074896224458182L;

    /**
     * Construct with the type and backing data
     * 
     * @param type the data type metadata
     * @param data the backing data that may be edited
     */
    EditableDataObject(final AbstractDataType type, final Object data) {
        super(type, data);
    }

    @Override
    public void addElement(final String path, final Object element)
        throws MetadataException {
        final Object toAdd =
            (element instanceof DataGetter)
                ? ((DataGetter) element).get()
                : element;
        type.addElement(path, data, toAdd);
    }

    @Override
    public void assertValidData() throws MetadataException {
        type.assertValid(data);
    }

    @Override
    public DataSetter getDataSetter(final String path) throws MetadataException {
        return type.getEditableDataObject(path, data);
    }

    @Override
    public Object removeElement(String path) throws MetadataException {
        return type.removeElement(path, data);
    }

    @Override
    public void set(final Object value) throws MetadataException {
        final Object toSet =
            (value instanceof DataGetter) ? ((DataGetter) value).get() : value;
        type.assertValid(toSet);
        data = toSet;
    }

    @Override
    public void set(final String path, final Object value)
        throws MetadataException {
        final Object toSet =
            (value instanceof DataGetter) ? ((DataGetter) value).get() : value;
        if (StringUtils.isBlank(path)) {
            set(toSet);
        } else {
            type.set(path, data, toSet);
        }
    }

    @Override
    public void setFromString(final String value) throws MetadataException {
        final DataErrorRecorder errRecorder =
            RecorderFactory.newDataErrorRecorder();
        final Object obj = type.convertFromString(value, errRecorder);
        if (errRecorder.hasErrors()) {
            throw new MetadataException("Error setting string value, '" +
                value + "', into data object: " + errRecorder.toString());
        }
        data = obj;
    }

    @Override
    public void setFromString(final String path, final String value)
        throws MetadataException {
        final DataErrorRecorder errRecorder =
            RecorderFactory.newDataErrorRecorder();
        type.setFromString(path, data, value, errRecorder);
        if (errRecorder.hasErrors()) {
            throw new MetadataException("Error setting string value, '" +
                value + "', into data object with the path, '" + path + "': " +
                errRecorder.toString());
        }
    }

    @Override
    public void setFromString(
        final String path,
        final String value,
        final DataErrorRecorder errRecorder) throws MetadataException {
        type.setFromString(path, data, value, errRecorder);
    }

    @Override
    public void validate(final DataErrorRecorder errRecorder)
        throws MetadataException {
        type.deepValidate(data, errRecorder);
    }
}
