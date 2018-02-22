package pgm.factor.discrete.interfaces;

import pgm.factor.discrete.Assignment;

@FunctionalInterface
public interface AssignmentTransformer {

    Assignment transform(Assignment assignment);
}
