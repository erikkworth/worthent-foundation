package com.worthent.foundation.util.state.etc.obj;

import com.worthent.foundation.util.annotation.NotNull;
import com.worthent.foundation.util.annotation.Nullable;
import com.worthent.foundation.util.state.StateExeException;

import java.util.Collection;

import static com.worthent.foundation.util.condition.Preconditions.checkNotNull;

/**
 * Utility used to create lists and populate them.
 *
 * @author Erik K. Worth
 */
public class ListBuilder extends BaseBuilder {

    /** The metadata for field parameter captured from a tag on a constructor argument */
    private final ConstructorParameter listParameter;

    /** The collection being built */
    private final Collection<Object> collection;

    /**
     * Construct from elements
     *
     * @param name the name of the list entity being built
     * @param listParameter the metadata for field parameter captured from a tag on a constructor argument
     */
    public ListBuilder(@Nullable final String name, @NotNull final ConstructorParameter listParameter) {
        super(BuilderType.LIST_BUILDER, name);
        this.listParameter = checkNotNull(listParameter, "listParameter must not be null");
        if (!listParameter.isCollectionType()) {
            throw new IllegalArgumentException("The provided constructor parameter is not for a list field");
        }
        collection = listParameter.newCollection();
    }

    @Nullable
    @Override
    public BaseBuilder getFieldBuilder(final String name) {
        final ConstructionWorker constructionWorker = listParameter.getComplexListElementConstructionWorker();
        if (null != constructionWorker) {
            return new ObjectBuilder(name, constructionWorker);
        }
        return null;
   }

    @Override
    public void set(@NotNull final String name, @Nullable final Object value) {
        if (null != value) {
            collection.add(value);
        }
    }

    @Override
    @NotNull
    public Object build() {
        return collection;
    }
}
