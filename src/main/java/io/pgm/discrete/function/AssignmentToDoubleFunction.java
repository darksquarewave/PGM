package io.pgm.discrete.function;

import io.pgm.discrete.core.Assignment;

@FunctionalInterface
public interface AssignmentToDoubleFunction {

    double applyAsDouble(Assignment value);
}
