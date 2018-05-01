package com.worthent.foundation.util.metadata;

import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * Factory for declaring new {@link DataType}s.
 * 
 * @author Erik K. Worth
 */
public interface DataTypeFactory {

    /**
     * Declares a <code>Struct</code> from the list of provided fields. The
     * order of the fields is preserved.
     * 
     * @param fields the fields for the struct
     * @return a <code>Struct</code> from the list of provided fields
     */
    DataType declareStruct(NamedType... fields);

    /**
     * Declares a <code>Struct</code> from the list of provided fields. The
     * order of the fields is preserved. Insert the type into the dictionary
     * with the specified type ID.
     * 
     * @param typeId the unique ID of the type in the dictionary
     * @param dictionary the dictionary to which the type is added
     * @param fields the fields for the struct
     * @return a <code>Struct</code> from the list of provided fields
     */
    DataType declareStruct(
        String typeId,
        TypeDictionary dictionary,
        NamedType... fields);

    /**
     * Declares a <code>Struct</code> from the list of provided fields. The
     * order of the fields is preserved.
     * 
     * @param fields the fields for the struct
     * @return a <code>Struct</code> from the list of provided fields
     */
    DataType declareStruct(List<NamedType> fields);

    /**
     * Declares a <code>Struct</code> from the list of provided fields. The
     * order of the fields is preserved. Insert the type into the dictionary
     * with the specified type ID.
     * 
     * @param typeId the unique ID of the type in the dictionary
     * @param dictionary the dictionary to which the type is added
     * @param fields the fields for the struct
     * @return a <code>Struct</code> from the list of provided fields
     */
    DataType declareStruct(
        String typeId,
        TypeDictionary dictionary,
        List<NamedType> fields);

    /**
     * Declares a new <code>Struct</code> by extending another with additional
     * fields. The additional fields are always added to the end.
     * 
     * @param struct the <code>Struct</code> to extend
     * @param fields the additional fields
     * @return a new <code>Struct</code> by extending another with additional
     *         fields
     * @throws ClassCastException thrown when the specified struct is not a
     *         <code>Struct</code> type
     */
    DataType extendStruct(DataType struct, NamedType... fields);

    /**
     * Declares a new <code>Struct</code> by extending another with additional
     * fields. The additional fields are always added to the end. Insert the new
     * type into the dictionary with the specified type ID.
     * 
     * @param typeId the unique ID of the type in the dictionary
     * @param dictionary the dictionary to which the type is added
     * @param struct the <code>Struct</code> to extend
     * @param fields the additional fields
     * @return a new <code>Struct</code> by extending another with additional
     *         fields
     * @throws ClassCastException thrown when the specified struct is not a
     *         <code>Struct</code> type
     */
    DataType extendStruct(
        String typeId,
        TypeDictionary dictionary,
        DataType struct,
        NamedType... fields);

    /**
     * Declares a new <code>Struct</code> by extending another with additional
     * fields. The additional fields are always added to the end.
     * 
     * @param struct the <code>Struct</code> to extend
     * @param fields the additional fields
     * @return a new <code>Struct</code> by extending another with additional
     *         fields
     * @throws ClassCastException thrown when the specified struct is not a
     *         <code>Struct</code> type
     */
    DataType extendStruct(DataType struct, List<NamedType> fields);

    /**
     * Declares a new <code>Struct</code> by extending another with additional
     * fields. The additional fields are always added to the end. Insert the new
     * type into the dictionary with the specified type ID.
     * 
     * @param typeId the unique ID of the type in the dictionary
     * @param dictionary the dictionary to which the type is added
     * @param struct the <code>Struct</code> to extend
     * @param fields the additional fields
     * @return a new <code>Struct</code> by extending another with additional
     *         fields
     * @throws ClassCastException thrown when the specified struct is not a
     *         <code>Struct</code> type
     */
    DataType extendStruct(
        String typeId,
        TypeDictionary dictionary,
        DataType struct,
        List<NamedType> fields);

    /**
     * Declares a <code>Map</code> with <code>String</code> keys and elements of
     * the specified type.
     *
     * @param concreteMapClass the fully qualified name of the concrete map class
     * @param elementType the data type of the elements of the map
     * @return a <code>Struct</code> from the list of provided fields
     */
    DataType declareMap(String concreteMapClass, DataType elementType);

    /**
     * Declares a <code>Map</code> with <code>String</code> keys and elements of
     * the specified type. Insert the new type into the dictionary with the
     * specified type ID.
     * 
     * @param typeId the unique ID of the type in the dictionary
     * @param dictionary the dictionary to which the type is added
     * @param concreteMapClass the fully qualified name of the concrete map class
     * @param elementType the data type of the elements of the map
     * @return a <code>Struct</code> from the list of provided fields
     */
    DataType declareMap(
        String typeId,
        TypeDictionary dictionary,
        String concreteMapClass,
        DataType elementType);

    /**
     * Declare a reference to a type in the specified dictionary. The definition
     * does not need to exist in the dictionary when the reference is created.
     * The corresponding type will behave as the referenced type.
     *
     * @param refId the reference identifier
     * @param dictionary the type dictionary to hold the referenced type
     * @return the new reference type definition
     */
    DataType declareReference(String refId, TypeDictionary dictionary);

    /**
     * Declare a <code>java.util.List</code> type with a specific child type and
     * a specific concrete implementation of the list.
     *
     * @param concreteListClass the fully qualified class name of the concrete list type
     * @param memberType the type definition for the list element
     * @return the list type definition
     */
    DataType declareList(String concreteListClass, DataType memberType);

    /**
     * Declare a <code>java.util.List</code> type with a specific child type and
     * a specific concrete implementation of the list. Insert the new type into the dictionary with the
     * specified type ID.
     * 
     * @param typeId the unique ID of the type in the dictionary
     * @param dictionary the dictionary to which the type is added
     * @param concreteListClass the fully qualified class name of the concrete list type
     * @param memberType the element type of the list
     * @return the list type definition
     *
     */
    DataType declareList(
        String typeId,
        TypeDictionary dictionary,
        String concreteListClass, 
        DataType memberType);

    /**
     * Declare an enumeration of string values.
     * 
     * @param choices the specific choices allowed for the enumerated type
     * @return the enumerated type definition
     */
    DataType declareEnumeration(Collection<String> choices);

    /**
     * Declare an enumeration of string values and insert the type into the
     * dictionary with the specified type ID.
     * 
     * @param typeId the unique ID of the type in the dictionary
     * @param dictionary the dictionary to which the type is added
     * @param choices the specific choices allowed for the enumerated type
     * @return the enumerated type definition
     */
    DataType declareEnumeration(
        String typeId,
        TypeDictionary dictionary,
        Collection<String> choices);

    /**
     * Declare an enumeration for all the values of an enumerated type.
     * @param values the specific values allowed for the enumerated type
     * @return the enumerated type definition
     */
    DataType declareEnumeration(Enum<?>[] values);

    /**
     * Declare an enumeration of string values and insert the type into the
     * dictionary with the specified type ID.
     * 
     * @param typeId the unique ID of the type in the dictionary
     * @param dictionary the dictionary to which the type is added
     * @param values the specific choices allowed for the enumerated type
     * @return the enumerated type definition
     */
    DataType declareEnumeration(
        String typeId,
        TypeDictionary dictionary,
        Enum<?>[] values);

    /**
     * Returns a copy of the provided data type with the specified validators
     * registered on it.
     * 
     * @param validators the list of validators to add to the copy
     * @param toType the data type to copy
     * @return the copy of the type definition with the specified validators
     */
    DataType addValidators(List<Validator> validators, DataType toType);

    /**
     * Returns a copy of the provided data type with the specified converters
     * registered on it.
     *
     * @param converters the converters to register on the data type by
     *        converter type
     * @param toType the data type to copy
     * @return the copy of the type definition with the specified converters
     */
    DataType setConverters(
            Map<ConverterType, Converter> converters,
            DataType toType);

    /**
     * Restore a type definition from a data object holding its state.
     * 
     * @param dataObject the data object holding all the information required to
     *        restore the type definition
     * @return the type definition from a data object holding its state
     * @throws MetadataException thrown when there is an error restoring the
     *         data type
     */
    DataType restoreFromStateObject(DataGetter dataObject)
        throws MetadataException;

}
