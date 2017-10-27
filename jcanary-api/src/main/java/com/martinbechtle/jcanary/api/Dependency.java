package com.martinbechtle.jcanary.api;

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
}
