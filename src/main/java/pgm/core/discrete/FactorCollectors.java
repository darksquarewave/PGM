package pgm.core.discrete;

import pgm.factors.discrete.CPD;
import pgm.factors.discrete.Factor;
import pgm.factors.discrete.JointDistribution;

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

    private static class AssignmentCollector<E extends AbstractAssignmentSet> implements
            Collector<Assignment, AbstractAssignmentSet.AbstractBuilder<E>, E> {

        private final Supplier<AbstractAssignmentSet.AbstractBuilder<E>> builderSupplier;

        AssignmentCollector(final Supplier<AbstractAssignmentSet.AbstractBuilder<E>> suppler) {
            this.builderSupplier = suppler;
        }

        @Override
        public Supplier<AbstractAssignmentSet.AbstractBuilder<E>> supplier() {
            return builderSupplier;
        }

        @Override
        public BiConsumer<AbstractAssignmentSet.AbstractBuilder<E>, Assignment> accumulator() {
            return AbstractAssignmentSet.AbstractBuilder::add;
        }

        @Override
        public BinaryOperator<AbstractAssignmentSet.AbstractBuilder<E>> combiner() {
            return AbstractAssignmentSet.AbstractBuilder::combine;
        }

        @Override
        public Function<AbstractAssignmentSet.AbstractBuilder<E>, E> finisher() {
            return AbstractAssignmentSet.AbstractBuilder::build;
        }

        @Override
        public Set<Characteristics> characteristics() {
            return EnumSet.of(Characteristics.UNORDERED);
        }
    }

    public static AssignmentCollector<JointDistribution> toJoint() {
        return new AssignmentCollector<>(JointDistribution.Builder::new);
    }

    public static AssignmentCollector<CPD> toCPD() {
        return new AssignmentCollector<>(CPD.Builder::new);
    }

    public static AssignmentCollector<Factor> toFactor() {
        return new AssignmentCollector<>(Factor.Builder::new);
    }
}
