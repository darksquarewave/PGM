package io.pgm.discrete.core;

import java.util.ArrayList;
import java.util.Collection;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.function.BiConsumer;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.function.ToDoubleBiFunction;
import java.util.stream.Collector;

import static java.util.stream.Collector.Characteristics.UNORDERED;

public final class AssignmentCollectors {

    private AssignmentCollectors() {
    }

    public static Collector<Assignment, ?, AssignmentStream> summing() {
        return new AssignmentStreamOperationCollector((a, b) -> a + b, 0d);
    }

    public static Collector<Assignment, ?, AssignmentStream> multiplying() {
        return new AssignmentStreamOperationCollector((a, b) -> a * b, 1d);
    }

    public static Collector<Assignment, ?, AssignmentStream> normalizing() {
        return Collector.of(
            SumNormalization::new,
            (norm, a) -> {
                norm.sum += a.value();
                norm.assignments.add(a);
            },
            (left, right) -> {
                left.sum += right.sum;
                left.assignments.addAll(right.assignments);
                return left;
            },
            (norm) -> new DefaultAssignmentStream(norm.assignments.stream().map(a -> a.divide(norm.sum))),
            UNORDERED);
    }

    public static Collector<Assignment, ?, AssignmentStream> lognormalizing() {
        return Collector.of(
            LogSumNormalization::new,
            (norm, a) -> {
                norm.max = Math.max(norm.max, a.value());
                norm.assignments.add(a);
            },
            (left, right) -> {
                left.max = Math.max(left.max, right.max);
                left.assignments.addAll(right.assignments);
                return left;
            },
            (norm) -> {
                double z = norm.max + Math.log(norm.assignments.stream()
                    .mapToDouble(a -> Math.exp(a.value() - norm.max)).sum());

                return new DefaultAssignmentStream(norm.assignments.stream().map(a -> a.minus(z)));
            },
            UNORDERED);
    }

    public static Collector<Assignment, ?, ProbTable> toProbTable() {
        return Collector.of(
            ProbTable::builder,
            ProbTable.Builder::assignment,
            ProbTable.Builder::add,
            ProbTable.Builder::build);
    }

    private static class AssignmentStreamOperationCollector implements
        Collector<Assignment, AssignmentStreamOperationAccumulator, AssignmentStream> {

        private final ToDoubleBiFunction<Double, Double> toDoubleFunction;
        private final double initVal;

        AssignmentStreamOperationCollector(final ToDoubleBiFunction<Double, Double> function, final double init) {
            toDoubleFunction = function;
            initVal = init;
        }

        @Override
        public Supplier<AssignmentStreamOperationAccumulator> supplier() {
            return AssignmentStreamOperationAccumulator::new;
        }

        @Override
        public BiConsumer<AssignmentStreamOperationAccumulator, Assignment> accumulator() {
            return (acc, a) -> {
                acc.addVars(a.randomVariables(), initVal);
                acc.addAssignment(a, toDoubleFunction);
            };
        }

        @Override
        public BinaryOperator<AssignmentStreamOperationAccumulator> combiner() {
            return (left, right) -> {
                left.addVars(right.vars, initVal);
                new AssignmentSpliterator(right.vars, right.vals).forEachRemaining(assignment -> {
                    left.addAssignment(assignment, toDoubleFunction);
                });
                return left;
            };
        }

        @Override
        public Function<AssignmentStreamOperationAccumulator, AssignmentStream> finisher() {
            return (acc) ->
                ProbTable.builder()
                    .variables(acc.vars)
                    .values(acc.vals)
                    .build()
                    .stream();
        }

        @Override
        public Set<Characteristics> characteristics() {
            return EnumSet.of(UNORDERED);
        }
    }

    private static class AssignmentStreamOperationAccumulator {

        private final Set<RandomVariable> vars = new TreeSet<>();
        private final List<Double> vals = new ArrayList<>();

        void addVars(final Collection<? extends RandomVariable> randomVars, final double init) {
            if (randomVars.isEmpty()) {
                return;
            }
            if (!randomVars.equals(vars)) {
                Set<RandomVariable> old = new HashSet<>(vars);
                vars.addAll(randomVars);

                int offset = AssignmentUtils.cardinality(old);
                int length = AssignmentUtils.cardinality(vars);

                for (int i = offset; i < length; i++) {
                    vals.add(init);
                }

                if (!old.isEmpty()) {
                    new MultiVarAssignmentSpliterator(vars).forEachRemaining(newAssignment -> {
                        MultiVarAssignment oldAssignment = newAssignment.retainAll(old);
                        int newIndex = AssignmentUtils.assignmentIndex(newAssignment);
                        int oldIndex = AssignmentUtils.assignmentIndex(oldAssignment);
                        vals.set(newIndex, vals.get(oldIndex));
                    });
                }
            }

        }

        void addAssignment(final Assignment a, final ToDoubleBiFunction<Double, Double> fn) {
            for (int i : AssignmentUtils.subAssignmentIndexes(a.varAssignments(), vars)) {
                vals.set(i, fn.applyAsDouble(vals.get(i), a.value()));
            }
        }
    }

    private static class SumNormalization {
        private final List<Assignment> assignments = new ArrayList<>();
        private double sum;
    }

    private static class LogSumNormalization {
        private final List<Assignment> assignments = new ArrayList<>();
        private double max = Double.NEGATIVE_INFINITY;
    }
}
