package io.pgm.discrete.core;

import io.pgm.discrete.function.AssignmentConsumer;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.function.ToDoubleBiFunction;
import java.util.stream.Collector;

public final class AssignmentCollectors {

    private AssignmentCollectors() {
    }

    //todo: refac, move combine and finish to evaluator
    public static Collector<Assignment, ?, AssignmentStream> multiplying() {
        return Collector.of(
            () -> new AssignmentFunctionEvaluator((a, b) -> a * b, 1d),
            AssignmentFunctionEvaluator::accept,
            (left, right) -> {
                left.combine(right);
                return left;
            },
            evaluator -> AssignmentStream.builder()
                .variables(evaluator.vars)
                .values(evaluator.values)
                .build());
    }

    public static Collector<Assignment, ?, AssignmentStream> summing() {
        return Collector.of(
            () -> new AssignmentFunctionEvaluator((a, b) -> a + b, 0d),
            AssignmentFunctionEvaluator::accept,
            (left, right) -> {
                left.combine(right);
                return left;
            },
            evaluator -> AssignmentStream.builder()
                .variables(evaluator.vars)
                .values(evaluator.values)
                .build());
    }

    public static Collector<Assignment, ?, AssignmentStream> normalizing() {
        return Collector.of(
            AssignmentSumNormalization::new,
            AssignmentSumNormalization::accept,
            AssignmentSumNormalization::combine,
            AssignmentSumNormalization::finish);
    }

    public static Collector<Assignment, ?, AssignmentStream> lognormalizing() {
        return Collector.of(
            AssignmentLogSumNormalization::new,
            AssignmentLogSumNormalization::accept,
            AssignmentLogSumNormalization::combine,
            AssignmentLogSumNormalization::finish);
    }

    private static final class AssignmentFunctionEvaluator implements AssignmentConsumer {

        private final ToDoubleBiFunction<Double, Double> evaluator;
        private final double initVal;
        private final Set<RandomVariable> vars;
        private final List<Double> values;

        private AssignmentFunctionEvaluator(final ToDoubleBiFunction<Double, Double> fn, final double val) {
            evaluator = fn;
            initVal = val;
            vars = new TreeSet<>();
            values = new ArrayList<>();
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
        public void accept(final Assignment a) {
            mergeVars(a.randomVariables());

            for (int i : AssignmentUtils.subAssignmentIndexes(a.varAssignments(), vars)) {
                values.set(i, evaluator.applyAsDouble(values.get(i), a.value()));
            }
        }

        void combine(final AssignmentFunctionEvaluator other) {
            mergeVars(other.vars);

            new MultiVarAssignmentSpliterator(other.vars).forEachRemaining(accAssignment -> {
                for (int i : AssignmentUtils.subAssignmentIndexes(accAssignment, vars)) {
                    double accVal = other.values.get(accAssignment.index());
                    double selfVal = values.get(i);

                    values.set(i, evaluator.applyAsDouble(selfVal, accVal));
                }
            });
        }
    }

    static class AssignmentSumNormalization implements AssignmentConsumer {

        private double sum;
        private final List<Assignment> assignments = new ArrayList<>();

        @Override
        public void accept(final Assignment assignment) {
            sum += assignment.value();
            assignments.add(assignment);
        }

        static AssignmentSumNormalization combine(final AssignmentSumNormalization that,
                                                  final AssignmentSumNormalization other) {
            that.sum += other.sum;
            that.assignments.addAll(other.assignments);
            return that;
        }

        AssignmentStream finish() {
            return new DefaultAssignmentStream(assignments.stream()
                .map(assignment -> assignment.divide(sum)));
        }
    }

    private static class AssignmentLogSumNormalization implements AssignmentConsumer {

        private double max = Double.NEGATIVE_INFINITY;
        private final List<Assignment> assignments = new ArrayList<>();

        @Override
        public void accept(final Assignment assignment) {
            max = Math.max(max, assignment.value());
            assignments.add(assignment);
        }

        static AssignmentLogSumNormalization combine(final AssignmentLogSumNormalization that,
                                                     final AssignmentLogSumNormalization other) {
            that.max = Math.max(that.max, other.max);
            that.assignments.addAll(other.assignments);
            return that;
        }

        AssignmentStream finish() {
            double logsum = max + Math.log(assignments.stream()
                .mapToDouble(assignment -> Math.exp(assignment.value() - max)).sum());

            return new DefaultAssignmentStream(assignments.stream()
                .map(assignment -> assignment.minus(logsum)));
        }
    }
}
