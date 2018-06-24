package io.pgm.discrete.core;

import io.pgm.core.UtilitySpliterators;
import io.pgm.discrete.function.AssignmentConsumer;
import io.pgm.discrete.function.AssignmentPredicate;
import io.pgm.discrete.function.AssignmentToDoubleFunction;
import io.pgm.discrete.function.ObjAssignmentConsumer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Spliterator;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collector;
import java.util.stream.DoubleStream;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public final class DefaultAssignmentStream implements AssignmentStream {

    private final Stream<Assignment> stream;

    DefaultAssignmentStream(final Spliterator<Assignment> spliterator) {
        this(StreamSupport.stream(spliterator, false));
    }

    DefaultAssignmentStream(final Stream<Assignment> delegate) {
        stream = delegate;
    }

    @Override
    public AssignmentStream filter(final AssignmentPredicate predicate) {
        return new DefaultAssignmentStream(stream.filter(predicate::test));
    }

    @Override
    public AssignmentStream concat(final AssignmentStream ... streams) {
        boolean parallel = isParallel();

        Spliterator[] spliterators = new Spliterator[streams.length + 1];
        spliterators[0] = spliterator();

        for (int i = 0; i < streams.length; i++) {
            spliterators[i + 1] = streams[i].spliterator();
            parallel |= streams[i].isParallel();
        }

        @SuppressWarnings("unchecked")
        Spliterator<Assignment> concat = new UtilitySpliterators.ConcatSpliterator<>(spliterators);

        return new DefaultAssignmentStream(StreamSupport.stream(concat, parallel));
    }

    @Override
    public AssignmentStream marginalize(final RandomVariable ... margVars) {
        return new DefaultAssignmentStream(map(assignment ->
            assignment.varAssignments()
                .excludeAll(Arrays.asList(margVars))
                .toAssignment(assignment.value())));
    }

    @Override
    public AssignmentStream product() {
        return collect(AssignmentCollectors.multiplying());
    }

    @Override
    public AssignmentStream sum() {
        return collect(AssignmentCollectors.summing());
    }

    @Override
    public AssignmentStream norm() {
        return collect(AssignmentCollectors.normalizing());
    }

    @Override
    public AssignmentStream lognorm() {
        return collect(AssignmentCollectors.lognormalizing());
    }

    @Override
    public AssignmentStream evidence(final MultiVarAssignment evidence) {
        return new DefaultAssignmentStream(map(assignment -> {
//todo: add test
            if (Collections.disjoint(evidence.randomVariables(), assignment.randomVariables())) {
                throw new IllegalArgumentException("Invalid evidence");
            }

            if (assignment.varAssignments().contains(evidence)) {
                return assignment;
            } else {
                return assignment.value(0d);
            }
        }));
    }

    @Override
    public DoubleStream mapToDouble(final AssignmentToDoubleFunction mapper) {
        return stream.mapToDouble(mapper::applyAsDouble);
    }

    @Override
    public <R> R collect(final Supplier<R> supplier,
                         final ObjAssignmentConsumer<R> accumulator,
                         final BiConsumer<R, R> combiner) {
        return stream.collect(supplier, accumulator::accept, combiner);
    }

    @Override
    public <R, A> R collect(final Collector<? super Assignment, A, R> collector) {
        return stream.collect(collector);
    }

    @Override
    public <R> Stream<R> map(final Function<? super Assignment, ? extends R> mapper) {
        return stream.map(mapper);
    }

    @Override
    public void forEach(final AssignmentConsumer action) {
        stream.forEach(action::accept);
    }

    @Override
    public Iterator<Assignment> iterator() {
        return stream.iterator();
    }

    @Override
    public Spliterator<Assignment> spliterator() {
        return stream.spliterator();
    }

    @Override
    public boolean isParallel() {
        return stream.isParallel();
    }

    @Override
    public AssignmentStream sequential() {
        return new DefaultAssignmentStream(stream.sequential());
    }

    @Override
    public AssignmentStream parallel() {
        return new DefaultAssignmentStream(stream.parallel());
    }

    @Override
    public AssignmentStream unordered() {
        return new DefaultAssignmentStream(stream.unordered());
    }

    @Override
    public AssignmentStream onClose(final Runnable closeHandler) {
        return new DefaultAssignmentStream(stream.onClose(closeHandler));
    }

    @Override
    public void close() {
        stream.close();
    }

    static class BuilderImpl implements Builder {

        private final List<RandomVariable> randomVars = new ArrayList<>();
        private final List<Double> values = new ArrayList<>();

        @Override
        public Builder variable(final RandomVariable randomVar) {
            randomVars.add(randomVar);
            return this;
        }

        @Override
        public Builder variables(final Collection<RandomVariable> vars) {
            randomVars.addAll(vars);
            return this;
        }

        @Override
        public Builder variables(final RandomVariable ... vars) {
            randomVars.addAll(Arrays.asList(vars));
            return this;
        }

        @Override
        public Builder value(final double value) {
            values.add(value);
            return this;
        }

        @Override
        public Builder values(final Collection<Double> vals) {
            values.addAll(vals);
            return this;
        }

        @Override
        public Builder values(final double ... vals) {
            for (double val : vals) {
                values.add(val);
            }
            return this;
        }

        @Override
        public AssignmentStream build() {
            MultiVarAssignmentSpliterator mvSpliterator = new MultiVarAssignmentSpliterator(randomVars);
            AssignmentSpliterator assignmentSpliterator = new AssignmentSpliterator(mvSpliterator, values);
            return new DefaultAssignmentStream(assignmentSpliterator);
        }
    }
}
