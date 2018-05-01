package com.worthent.foundation.util.lang;

import com.worthent.foundation.util.annotation.NotNull;
import com.worthent.foundation.util.annotation.Nullable;

import java.io.Serializable;
import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import static com.worthent.foundation.util.condition.Preconditions.checkNotNull;

/**
 * Helper class used to create static maps from an array of named values. It
 * provides a capability for maps that the Arrays.asList() method does for
 * lists.
 * 
 * @param <E> the element type
 * 
 *        Usage:
 * 
 *        <pre>
 * // Define a named value class for a Class&lt;?&gt; value type
 * private static class NamedClass extends NamedValue&lt;Class&lt;?&gt;&gt; {
 *     private NamedClass(final String serviceId, final Class&lt;?&gt; impl) {
 *         super(serviceId, impl);
 *     }
 * }
 * 
 * // Declare an array of named classes
 * private static final NamedClass[] REMOTE_CLIENTS =
 *     new NamedClass[] { new NamedClass(
 *         RepositoryService.SERVICE_ID,
 *         RepositoryServiceClientImpl.class) };
 * // Declare a map constant from the array.
 * private static final Map&lt;String, Class&lt;?&gt;&gt; REMOTE_CLIENT_TABLE =
 *     NamedValue.asMap(REMOTE_CLIENTS);
 * </pre>
 * 
 * @author Erik K. Worth
 */
public class NamedValue<E> {

    /** Single instance of this class */
    public static final NameComparator NAME_COMPARATOR = new NameComparator();

    /** Comparator that compares the names for named values. */
    public static final class NameComparator implements Comparator<NamedValue<?>>, Serializable {

        /** Serial ID */
        private static final long serialVersionUID = 333903909889588389L;

        /*
         * (non-Javadoc)
         * 
         * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
         */
        @Override
        public int compare(final NamedValue<?> nv1, final NamedValue<?> nv2) {
            final String name1 = nv1.getName();
            return name1.compareTo(nv2.getName());
        }

        /** Hide the constructor */
        private NameComparator() { }
    } // NameComparator

    /**
     * Returns a map from the provided array of named values.
     * 
     * @param <E> the map element (value) type
     * @param items the array of named values.
     * @return a map from the provided array of named values
     */
    public static <E> Map<String, E> asMap(@NotNull final NamedValue<E>... items) {
        return Arrays.stream(checkNotNull(items, "items must not be null"))
                .collect(Collectors.toMap(NamedValue::getName, NamedValue::getValue));
    }

    /** The map key */
    protected String name;

    /** The map value */
    protected E value;

    /**
     * Construct with name and value
     * 
     * @param name the map key
     * @param value the map value
     */
    public NamedValue(@NotNull final String name, @Nullable final E value) {
        this.name = checkNotNull(name, "name must not be null");
        this.value = value;
    }

    @Override
    public String toString() {
        return "NamedValue{" +
                "name='" + name + '\'' +
                ", value=" + value +
                '}';
    }

    @Override
    public boolean equals(final Object other) {
        if (this == other) {
            return true;
        }
        if (other == null || getClass() != other.getClass()) {
            return false;
        }
        final NamedValue<?> that = (NamedValue<?>) other;
        return Objects.equals(name, that.name) &&
                Objects.equals(value, that.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, value);
    }

    /** @return the map key */
    @NotNull
    public String getName() {
        return name;
    }

    /** @return the map value */
    @Nullable
    public E getValue() {
        return value;
    }
}
