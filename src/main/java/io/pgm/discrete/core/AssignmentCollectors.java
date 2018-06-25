package io.pgm.discrete.core;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
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

public final class AssignmentCollectors {

    private AssignmentCollectors() {
    }

    public static Collector<Assignment, ?, AssignmentStream> multiplying() {
        return new AssignmentFunctionEvaluator((a, b) -> a * b, 1d);
    }

    public static Collector<Assignment, ?, AssignmentStream> summing() {
        return new AssignmentFunctionEvaluator((a, b) -> a + b, 0d);
    }

    public static Collector<Assignment, ?, AssignmentStream> normalizing() {
        return new AssignmentSumNormalization();
    }

    public static Collector<Assignment, ?, AssignmentStream> lognormalizing() {
        return new AssignmentLogSumNormalization();
    }

    private static final class AssignmentFunctionEvaluator implements
            Collector<Assignment, AssignmentFunctionEvaluator, AssignmentStream> {

        private final ToDoubleBiFunction<Double, Double> fn;
        private final Set<RandomVariable> vars = new TreeSet<>();
        private final List<Double> values = new ArrayList<>();
        private final double initVal;

        private AssignmentFunctionEvaluator(final ToDoubleBiFunction<Double, Double> biFunction, final double val) {
            fn = biFunction;
            initVal = val;
        }

        private void mergeVars(final Collection<? extends RandomVariable> randomVars) {
            if (randomVars.isEmpty()) {
                return;
            }

            if (!randomVars.equals(vars)) {
                Set<RandomVariable> oldVariables = new HashSet<>(vars);
                vars.addAll(randomVars);

                int offset = AssignmentUtils.cardinality(oldVariables);
                int length = AssignmentUtils.cardinality(vars);

                for (int i = offset; i < length; i++) {
                    values.add(initVal);
                }

                if (!oldVariables.isEmpty()) {
                    new MultiVarAssignmentSpliterator(vars).forEachRemaining(newAssignment -> {
                        MultiVarAssignment oldAssignment = newAssignment.retainAll(oldVariables);

                        int newIndex = AssignmentUtils.assignmentIndex(newAssignment);
                        int oldIndex = AssignmentUtils.assignmentIndex(oldAssignment);

                        values.set(newIndex, values.get(oldIndex));
                    });
                }
            }
        }

        @Override
        public Supplier<AssignmentFunctionEvaluator> supplier() {
            return () -> new AssignmentFunctionEvaluator(fn, initVal);
        }

        @Override
        public BiConsumer<AssignmentFunctionEvaluator, Assignment> accumulator() {
            return (evaluator, a) -> {
                evaluator.mergeVars(a.randomVariables());

                for (int i : AssignmentUtils.subAssignmentIndexes(a.varAssignments(), evaluator.vars)) {
                    evaluator.values.set(i, evaluator.fn.applyAsDouble(evaluator.values.get(i), a.value()));
                }
            };
        }

        @Override
        public BinaryOperator<AssignmentFunctionEvaluator> combiner() {
            return (left, right) -> {
                left.mergeVars(right.vars);

                new MultiVarAssignmentSpliterator(right.vars).forEachRemaining(accAssignment -> {
                    for (int i : AssignmentUtils.subAssignmentIndexes(accAssignment, left.vars)) {
                        double accVal = right.values.get(accAssignment.index());
                        double selfVal = left.values.get(i);

                        left.values.set(i, left.fn.applyAsDouble(selfVal, accVal));
                    }
                });

                return left;
            };
        }

        @Override
        public Function<AssignmentFunctionEvaluator, AssignmentStream> finisher() {
            return (evaluator) ->
                AssignmentStream.builder()
                    .variables(evaluator.vars)
                    .values(evaluator.values)
                    .build();
        }

        @Override
        public Set<Characteristics> characteristics() {
            return Collections.emptySet();
        }
    }

    static class AssignmentSumNormalization implements
            Collector<Assignment, AssignmentSumNormalization, AssignmentStream> {

        private final List<Assignment> assignments = new ArrayList<>();
        private double sum;

        @Override
        public Supplier<AssignmentSumNormalization> supplier() {
            return AssignmentSumNormalization::new;
        }

        @Override
        public BiConsumer<AssignmentSumNormalization, Assignment> accumulator() {
            return (normalization, assignment) -> {
                normalization.sum += assignment.value();
                normalization.assignments.add(assignment);
            };
        }

        @Override
        public BinaryOperator<AssignmentSumNormalization> combiner() {
            return (left, right) -> {
                left.sum += right.sum;
                left.assignments.addAll(right.assignments);
                return left;
            };
        }

        @Override
        public Function<AssignmentSumNormalization, AssignmentStream> finisher() {
            return (normalization) -> new DefaultAssignmentStream(normalization.assignments.stream()
                .map(assignment -> assignment.divide(normalization.sum)));
        }

        @Override
        public Set<Characteristics> characteristics() {
            return Collections.emptySet();
        }
    }

    private static class AssignmentLogSumNormalization implements
            Collector<Assignment, AssignmentLogSumNormalization, AssignmentStream> {

        private final List<Assignment> assignments = new ArrayList<>();
        private double max = Double.NEGATIVE_INFINITY;

        @Override
        public Supplier<AssignmentLogSumNormalization> supplier() {
            return AssignmentLogSumNormalization::new;
        }

        @Override
        public BiConsumer<AssignmentLogSumNormalization, Assignment> accumulator() {
            return (normalization, assignment) -> {
                normalization.max = Math.max(normalization.max, assignment.value());
                normalization.assignments.add(assignment);
            };
        }

        @Override
        public BinaryOperator<AssignmentLogSumNormalization> combiner() {
            return (left, right) -> {
                left.max = Math.max(left.max, right.max);
                left.assignments.addAll(right.assignments);
                return left;
            };
        }

        @Override
        public Function<AssignmentLogSumNormalization, AssignmentStream> finisher() {
            return (normalization) -> {
                double logsum = normalization.max + Math.log(normalization.assignments.stream()
                    .mapToDouble(assignment -> Math.exp(assignment.value() - normalization.max)).sum());

                return new DefaultAssignmentStream(normalization.assignments.stream()
                    .map(assignment -> assignment.minus(logsum)));
            };
        }

        @Override
        public Set<Characteristics> characteristics() {
            return Collections.emptySet();
        }
    }
}
