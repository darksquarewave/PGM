package io.pgm.discrete.function;

import io.pgm.discrete.core.Assignment;

@FunctionalInterface
public interface AssignmentConsumer {

    void accept(Assignment assignment);
}
