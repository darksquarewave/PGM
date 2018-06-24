package io.pgm.discrete.core;

import io.pgm.discrete.function.AssignmentConsumer;
import io.pgm.discrete.function.AssignmentPredicate;
import io.pgm.discrete.function.AssignmentToDoubleFunction;
import io.pgm.discrete.function.ObjAssignmentConsumer;

import java.util.Collection;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.BaseStream;
import java.util.stream.Collector;
import java.util.stream.DoubleStream;
import java.util.stream.Stream;

public interface AssignmentStream extends BaseStream<Assignment, AssignmentStream> {

    AssignmentStream filter(AssignmentPredicate predicate);

    AssignmentStream concat(AssignmentStream ... streams);

    AssignmentStream marginalize(RandomVariable ... margVars);

    AssignmentStream product();

    AssignmentStream sum();

    AssignmentStream norm();

    AssignmentStream lognorm();

    AssignmentStream evidence(VarAssignment evidence);

    AssignmentStream evidence(MultiVarAssignment evidence);

    DoubleStream mapToDouble(AssignmentToDoubleFunction mapper);

    <R> R collect(Supplier<R> supplier,
                  ObjAssignmentConsumer<R> accumulator,
                  BiConsumer<R, R> combiner);

    <R, A> R collect(Collector<? super Assignment, A, R> collector);

    <R> Stream<R> map(Function<? super Assignment, ? extends R> mapper);

    void forEach(AssignmentConsumer action);

    static AssignmentStream of(Assignment ... assignments) {
        return new DefaultAssignmentStream(Stream.of(assignments));
    }

    @SuppressWarnings("unchecked")
    static AssignmentStream of(Collection<? extends Assignment> assignments) {
        return new DefaultAssignmentStream((Stream<Assignment>) assignments.stream());
    }

    static Builder builder() {
        return new DefaultAssignmentStream.BuilderImpl();
    }

    interface Builder {

        Builder variables(Collection<RandomVariable> randomVars);

        Builder variables(RandomVariable ... randomVars);

        Builder variable(RandomVariable randomVar);

        Builder values(Collection<Double> values);

        Builder values(double ... values);

        Builder value(double value);

        AssignmentStream build();
    }
}
