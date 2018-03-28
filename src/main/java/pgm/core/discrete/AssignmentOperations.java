package pgm.core.discrete;

import pgm.factors.discrete.functions.operations.Marginalize;
import pgm.factors.discrete.functions.operations.Product;
import pgm.factors.discrete.functions.operations.Renormalize;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.EnumSet;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collector;
import java.util.stream.Stream;

public final class AssignmentOperations {

    private AssignmentOperations() {
    }

    private static <R> Collector<Assignment, Collection<Assignment>, R> operation(
            final Supplier<Function<Collection<? extends Assignment>, R>> fn) {

        return new FactorCollectors.CollectorImpl<>(ArrayList::new,
                Collection::add,
                (r1, r2) -> {
                    r1.addAll(r2);
                    return r1;
                },
                (assignments) -> fn.get().apply(assignments),
                Collections.unmodifiableSet(EnumSet.of(Collector.Characteristics.UNORDERED)));

    }

    public static Collector<Assignment, Collection<Assignment>, Stream<? extends Assignment>> renormalize() {
        return operation(Renormalize::new);
    }

    public static Collector<Assignment, Collection<Assignment>, Stream<? extends Assignment>> product() {
        return operation(Product::new);
    }

    public static Collector<Assignment, Collection<Assignment>, Stream<? extends Assignment>> marginalize(
            final Collection<? extends RandomVariable> randomVariables) {

        return operation(() -> new Marginalize(randomVariables));
    }
}
