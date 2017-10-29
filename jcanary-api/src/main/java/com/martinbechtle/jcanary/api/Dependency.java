package com.martinbechtle.jcanary.api;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import static com.martinbechtle.jrequire.Require.notEmpty;
import static com.martinbechtle.jrequire.Require.notNull;

/**
 * Represents a dependency of the system. It's identified with a name, a {@link DependencyType} and {@link DependencyImportance}.
 *
 * @author Martin Bechtle
 */
public class Dependency {

    private final DependencyImportance importance;

    private final DependencyType type;

    private final String name;

    public Dependency(DependencyImportance importance, DependencyType type, String name) {

        this.importance = notNull(importance);
        this.type = notNull(type);
        this.name = notEmpty(name);
    }

    public DependencyImportance getImportance() {

        return importance;
    }

    public DependencyType getType() {

        return type;
    }

    public String getName() {

        return name;
    }

    @Override
    public boolean equals(Object o) {

        if (this == o) {
            return true;
        }

        if (!(o instanceof Dependency)) {
            return false;
        }

        Dependency that = (Dependency) o;

        return new EqualsBuilder()
                .append(importance, that.importance)
                .append(type, that.type)
                .append(name, that.name)
                .isEquals();
    }

    @Override
    public int hashCode() {

        return new HashCodeBuilder(17, 37)
                .append(importance)
                .append(type)
                .append(name)
                .toHashCode();
    }

    @Override
    public String toString() {

        return "Dependency{" +
                "importance=" + importance +
                ", type=" + type +
                ", name='" + name + '\'' +
                '}';
    }
}
