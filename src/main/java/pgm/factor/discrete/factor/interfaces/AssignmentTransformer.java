package pgm.factor.discrete.factor.interfaces;

import pgm.factor.discrete.factor.Assignment;

@FunctionalInterface
public interface AssignmentTransformer {

    Assignment transform(Assignment assignment);
}
