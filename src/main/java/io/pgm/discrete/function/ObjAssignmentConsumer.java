package io.pgm.discrete.function;

import io.pgm.discrete.core.Assignment;

@FunctionalInterface
public interface ObjAssignmentConsumer<T> {

    void accept(T t, Assignment assignment);
}
