package pgm.factors.discrete.functions.operations;

import pgm.core.discrete.Assignment;

import java.util.Collection;
import java.util.function.Function;
import java.util.stream.Stream;

public final class Renormalize implements Function<Collection<? extends Assignment>, Stream<? extends Assignment>> {

    @Override
    public Stream<? extends Assignment> apply(final Collection<? extends Assignment> assignments) {
        double normConstant = assignments.stream().mapToDouble(Assignment::value).sum();
        return assignments.stream().map(assignment -> assignment.value(assignment.value() / normConstant));
    }
}
