package io.pgm.discrete.function;

import io.pgm.discrete.core.Assignment;

@FunctionalInterface
public interface AssignmentBinaryOperator {

    Assignment applyAsAssignment(Assignment left, Assignment right);
}
