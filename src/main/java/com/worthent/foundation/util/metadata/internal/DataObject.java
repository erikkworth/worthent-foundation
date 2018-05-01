package com.worthent.foundation.util.metadata.internal;

import java.text.Format;
import java.util.Collection;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

import com.worthent.foundation.util.lang.NamedValue;
import com.worthent.foundation.util.lang.StringUtils;
import com.worthent.foundation.util.metadata.DataGetter;
import com.worthent.foundation.util.metadata.DataSetter;
import com.worthent.foundation.util.metadata.DataType;
import com.worthent.foundation.util.metadata.MetadataException;
import com.worthent.foundation.util.recorder.DataErrorRecorder;

import static com.worthent.foundation.util.condition.Preconditions.checkNotNull;

/**
 * Provides a basic wrapper around data that is described by metadata.
 * 
 * @author Erik K. Worth
 */
public class DataObject implements DataGetter {

    /** Serial Version ID */
    private static final long serialVersionUID = -2607365404601426271L;

    /** Helper class used to represent string data */
    static class NamedString extends NamedValue<String>{
        NamedString(final String name, final String value) {
            super(name, value);
        }

        void setName(final String name) {
            this.name = name;
        }
    }

    /** The metadata describing this data object */
    protected final AbstractDataType type;

    /** The backing data this data object wraps */
    protected Object data;

    /**
     * Construct with type metadata and that backing data.
     * 
     * @param type the type metadata describing this object
     * @param data the backing data wrapped by this object
     */
    protected DataObject(final AbstractDataType type, final Object data) {
        this.type = type;
        this.data = data;
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> T get() throws MetadataException {
        return (T) data;
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> T get(final String path) throws MetadataException {
        return (T) type.get(path, data);
    }

    @Override
    public String getAsString() throws MetadataException {
        return type.asString(null, data, null);
    }

    @Override
    public String getAsString(final Format format) throws MetadataException {
        return type.asString(null, data, format);
    }

    @Override
    public String getAsString(final String path) throws MetadataException {
        return type.asString(path, data, null);
    }

    @Override
    public String getAsString(final String path, final Format format)
        throws MetadataException {
        return type.asString(path, data, format);
    }

    @Override
    public DataSetter getDeepCopy() throws MetadataException {
        final Object copy = type.deepCopy(data);
        return new EditableDataObject(type, copy);
    }

    @Override
    public Collection<String> getMapElementNames() throws MetadataException {
        return type.getMapElementNames(data);
    }

    @Override
    public Collection<String> getMapElementNames(final String path)
        throws MetadataException {
        return type.getMapElementNames(path, data);
    }

    @Override
    public DataGetter getDataGetter(final String path) throws MetadataException {
        if (StringUtils.isBlank(path)) {
            return this;
        }
        return type.getDataObject(path, data);
    }

    @Override
    public DataType getType() {
        return type;
    }

    @Override
    public DataType getType(String path) throws MetadataException {
        return type.getType(path);
    }

    @Override
    public int size() throws MetadataException {
        return type.getSize(data);
    }

    @Override
    public int size(final String path) throws MetadataException {
        return type.getSize(path, data);
    }

    @Override
    public DataSetter setStringData(
        final Map<String, String> values,
        final DataErrorRecorder errRecorder) {
        checkNotNull(errRecorder, "errRecorder must not be null");
        final List<NamedString> strData = checkNotNull(values, "values must not be null").entrySet().stream()
                .map(entry -> new NamedString(entry.getKey(), entry.getValue()))
                .sorted(NamedString.NAME_COMPARATOR)
                .collect(Collectors.toList());

        final Object copy;
        final DataSetter dataSetter;
        try {
            dataSetter = getDeepCopy();
            copy = dataSetter.get();
        } catch (final MetadataException exc) {
            // Error locating a type at the specified path
            final String msg = MetadataRsrc.INTERNAL_ERROR.localize(Locale.getDefault());
            errRecorder.reportError(msg, exc);
            return null;
        }

        // Update the copy from the string data.
        type.setStringData(copy, strData, errRecorder);
        return dataSetter;
    }

}
