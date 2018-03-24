package pgm.factors.discrete.functions.transformers;

import pgm.core.discrete.Assignment;

import java.util.function.Function;
import java.util.stream.Stream;

public final class NormalizationTransformer implements
        Function<Stream<? extends Assignment>, Stream<? extends Assignment>> {

    private static NormalizationTransformer transformer = new NormalizationTransformer();

    public static NormalizationTransformer instance() {
        return transformer;
    }

    @Override
    public Stream<? extends Assignment> apply(final Stream<? extends Assignment> assignments) {
        double normalizationConstant = assignments.mapToDouble(Assignment::value).sum();
        return assignments.map(assignment -> assignment.value(assignment.value() / normalizationConstant));
    }
}
