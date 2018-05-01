package com.worthent.foundation.util.metadata.internal;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import com.worthent.foundation.util.metadata.Converter;
import com.worthent.foundation.util.metadata.ConverterType;
import com.worthent.foundation.util.metadata.DataGetter;
import com.worthent.foundation.util.metadata.DataType;
import com.worthent.foundation.util.metadata.DataTypeFactory;
import com.worthent.foundation.util.metadata.MetadataException;
import com.worthent.foundation.util.metadata.NamedType;
import com.worthent.foundation.util.metadata.TypeCode;
import com.worthent.foundation.util.metadata.TypeDictionary;
import com.worthent.foundation.util.metadata.Validator;

/**
 * Implements the data type factory interface. This class creates data type
 * definitions for the more complex types. Get the simple types from the type
 * dictionary.
 * 
 * @author Erik K. Worth
 */
public final class DataTypeFactoryImpl implements DataTypeFactory {

    /** Single instance of this class */
    public static final DataTypeFactoryImpl INSTANCE = new DataTypeFactoryImpl();

    @Override
    public DataType declareEnumeration(final Collection<String> choices) {
        return new EnumType(choices);
    }

    @Override
    public DataType declareEnumeration(
        final String typeId,
        final TypeDictionary dictionary,
        final Collection<String> choices) {
        final DataType type = declareEnumeration(choices);
        dictionary.putType(typeId, type);
        return type;
    }

    @Override
    public DataType declareEnumeration(final Enum<?>[] values) {
        if (null == values) {
            throw new IllegalArgumentException("values must not be null");
        }
        final List<String> choices = new ArrayList<>(values.length);
        for (final Enum<?> value : values) {
            choices.add(value.name());
        }
        return new EnumType(choices);
    }

    @Override
    public DataType declareEnumeration(
        final String typeId,
        final TypeDictionary dictionary,
        final Enum<?>[] values) {
        final DataType type = declareEnumeration(values);
        dictionary.putType(typeId, type);
        return type;
    }

    @Override
    public DataType declareList(
        final String concreteListClass,
        final DataType memberType) {
        return new ListType(concreteListClass, memberType);
    }

    @Override
    public DataType declareList(
        final String typeId,
        final TypeDictionary dictionary,
        final String concreteListClass,
        final DataType memberType) {
        final DataType type = declareList(concreteListClass, memberType);
        dictionary.putType(typeId, type);
        return type;
    }

    @Override
    public DataType declareMap(
        final String conceteMapClass,
        final DataType elementType) {
        return new MapType(conceteMapClass, elementType);
    }

    @Override
    public DataType declareMap(
        final String typeId,
        final TypeDictionary dictionary,
        final String conceteMapClass,
        final DataType elementType) {
        final DataType type = declareMap(conceteMapClass, elementType);
        dictionary.putType(typeId, type);
        return type;
    }

    @Override
    public DataType declareReference(
        final String refId,
        final TypeDictionary dictionary) {
        return new RefType(refId, new LocalDictionaryReference(dictionary));
    }

    @Override
    public DataType declareStruct(final NamedType... fields) {
        return this.declareStruct(Arrays.asList(fields));
    }

    @Override
    public DataType declareStruct(
        final String typeId,
        final TypeDictionary dictionary,
        final NamedType... fields) {
        final DataType type = declareStruct(Arrays.asList(fields));
        dictionary.putType(typeId, type);
        return type;
    }

    @Override
    public DataType declareStruct(final List<NamedType> fields) {
        return new StructType(fields);
    }

    @Override
    public DataType declareStruct(
        final String typeId,
        final TypeDictionary dictionary,
        final List<NamedType> fields) {
        final DataType type = declareStruct(fields);
        dictionary.putType(typeId, type);
        return type;
    }

    @Override
    public DataType extendStruct(
        final DataType struct,
        final NamedType... fields) {
        return this.extendStruct(struct, Arrays.asList(fields));
    }

    @Override
    public DataType extendStruct(
        final String typeId,
        final TypeDictionary dictionary,
        final DataType struct,
        final NamedType... fields) {
        final DataType type = extendStruct(struct, fields);
        dictionary.putType(typeId, type);
        return type;
    }

    @Override
    public DataType extendStruct(
        final DataType struct,
        final List<NamedType> fields) {
        AbstractDataType.assertIsClass(struct, StructType.class);
        final StructType toExtend = (StructType) struct;
        return new StructType(toExtend, fields);
    }

    @Override
    public DataType extendStruct(
        final String typeId,
        final TypeDictionary dictionary,
        final DataType struct,
        final List<NamedType> fields) {
        final DataType type = extendStruct(struct, fields);
        dictionary.putType(typeId, type);
        return type;
    }

    @Override
    public DataType addValidators(
        final List<Validator> validators,
        final DataType toType) {
        if ((null == validators) || validators.isEmpty()) {
            // There are no validators to add, so just return what was given
            return toType;
        }
        final AbstractDataType copy = (AbstractDataType) toType.deepCopy();
        for (final Validator validator : validators) {
            copy.addValidator(validator);
        }
        return copy;
    }

    @Override
    public DataType setConverters(
        final Map<ConverterType, Converter> converters,
        final DataType toType) {
        if ((null == converters) || converters.isEmpty()) {
            // No converters to add
            return toType;
        }
        final AbstractDataType copy = (AbstractDataType) toType.deepCopy();
        for (Map.Entry<ConverterType, Converter> entry : converters.entrySet()) {
            copy.setConverter(entry.getKey(), entry.getValue());
        }
        return copy;
    }

    @Override
    public DataType restoreFromStateObject(final DataGetter dataObject)
        throws MetadataException {
        final MetadataMetadata metaMeta = MetadataMetadata.getInstance();
        final TypeCode typeCode = metaMeta.getTypeCode(dataObject);
        switch (typeCode) {
        case BOOLEAN:
        case NUMERIC:
        case STRING:
            return new SimpleType(dataObject);
        case DATE:
            return new DateType(dataObject);
        case ENUMERATED:
            return new EnumType(dataObject);
        case LIST:
            return new ListType(dataObject);
        case MAP:
            return new MapType(dataObject);
        case STRUCT:
            return new StructType(dataObject);
        case REFERENCE:
            return new RefType(dataObject);
        default:
            throw new MetadataException(
                "Unable to restore data objects of type, " + typeCode);
        }
    }

    /**
     * Hide the factory to enforce the singleton pattern.
     */
    private DataTypeFactoryImpl() {
        // Empty
    }

}
