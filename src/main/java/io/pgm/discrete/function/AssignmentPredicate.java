package io.pgm.discrete.function;

import io.pgm.discrete.core.Assignment;

@FunctionalInterface
public interface AssignmentPredicate {

    boolean test(Assignment assignment);
}
