package pgm.core.discrete;

import pgm.factors.discrete.CPD;
import pgm.factors.discrete.Factor;
import pgm.factors.discrete.JointDistribution;
import pgm.models.BayesianModel;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.EnumSet;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collector;

public final class FactorCollectors {

    private FactorCollectors() {
    }

    static final class CollectorImpl<T, A, R> implements Collector<T, A, R> {

        private final Supplier<A> supplier;
        private final BiConsumer<A, T> accumulator;
        private final BinaryOperator<A> combiner;
        private final Function<A, R> finisher;
        private final Set<Characteristics> characteristics;

        CollectorImpl(final Supplier<A> inputSupplier,
                      final BiConsumer<A, T> inputAccumulator,
                      final BinaryOperator<A> inputCombiner,
                      final Function<A, R> inputFinisher,
                      final Set<Characteristics> inputCharacteristics) {

            supplier = inputSupplier;
            accumulator = inputAccumulator;
            combiner = inputCombiner;
            finisher = inputFinisher;
            characteristics = inputCharacteristics;
        }

        @Override
        public Supplier<A> supplier() {
            return supplier;
        }

        @Override
        public BiConsumer<A, T> accumulator() {
            return accumulator;
        }

        @Override
        public BinaryOperator<A> combiner() {
            return combiner;
        }

        @Override
        public Function<A, R> finisher() {
            return finisher;
        }

        @Override
        public Set<Characteristics> characteristics() {
            return characteristics;
        }
    }

    private static <T, R> Collector<T, Collection<T>, R> collector(final Function<Collection<T>, R> finisher) {
        return new CollectorImpl<>(ArrayList::new,
                Collection::add,
                (r1, r2) -> {
                    r1.addAll(r2);
                    return r2;
                },
                finisher,
                Collections.unmodifiableSet(EnumSet.of(Collector.Characteristics.UNORDERED)));
    }

    public static Collector<Assignment, Collection<Assignment>, CPD> toCPD() {
        return collector(CPD::build);
    }

    public static Collector<Assignment, Collection<Assignment>, Factor> toFactor() {
        return collector(Factor::build);
    }

    public static Collector<Assignment, Collection<Assignment>, JointDistribution> toJoint() {
        return collector(JointDistribution::build);
    }

    public static Collector<CPD, Collection<CPD>, BayesianModel> toBayesianModel() {
        return collector(BayesianModel::build);
    }
}
