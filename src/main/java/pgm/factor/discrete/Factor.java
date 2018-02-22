package pgm.factor.discrete;

import org.checkerframework.checker.nullness.qual.Nullable;
import pgm.factor.discrete.interfaces.AssignmentTransformer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.TreeSet;
import java.util.function.BiConsumer;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public final class Factor {

    private final Set<? extends RandomVariable> randomVariables;
    private final Set<? extends Assignment> assignments;

    private static class FactorCollector implements Collector<Assignment, Collection<Assignment>, Factor> {

        @Override
        public Supplier<Collection<Assignment>> supplier() {
            return ArrayList::new;
        }

        @Override
        public BiConsumer<Collection<Assignment>, Assignment> accumulator() {
            return Collection::add;
        }

        @Override
        public BinaryOperator<Collection<Assignment>> combiner() {
            return (coll1, coll2) -> {
                coll1.addAll(coll2);
                return coll1;
            };
        }

        @Override
        public Function<Collection<Assignment>, Factor> finisher() {
            return Factor::new;
        }

        @Override
        public Set<Characteristics> characteristics() {
            return Collections.emptySet();
        }
    }

    public Factor(final Collection<? extends Assignment> assignmentColl) {
        if (assignmentColl.isEmpty()) {
            throw new IllegalArgumentException("Assignments cannot be empty");
        }

        Set<Assignment> assignmentSet = new TreeSet<>(assignmentColl);
        validateAssignments(assignmentSet);

        Assignment first = assignmentColl.iterator().next();
        this.randomVariables = Collections.unmodifiableSet(first.index().randomVariables());
        this.assignments = Collections.unmodifiableSet(assignmentSet);
    }

    private static void validateAssignments(final Set<? extends Assignment> assignments) {
        Assignment first = assignments.iterator().next();
        Set<? extends RandomVariable> randomVariables = first.index().randomVariables();

        boolean hasSameVariables = assignments.stream()
                .map(assignment -> assignment.index().randomVariables())
                .allMatch(randomVariables::equals);

        if (!hasSameVariables) {
            throw new IllegalArgumentException("Random variables mismatch, cannot construct factor");
        }

        Collection<AssignmentIndex> indexes = AssignmentIndexes.of(randomVariables);

        boolean hasCorrectAssignments = assignments.stream()
                .map(Assignment::index)
                .collect(Collectors.toList())
                .equals(indexes);

        if (!hasCorrectAssignments) {
            throw new IllegalArgumentException("Invalid assignment indexes, cannot construct factor");
        }
    }

    private Spliterator<Assignment> spliterator() {
        return Spliterators.spliterator(assignments, 0);
    }

    public Stream<Assignment> stream() {
        return StreamSupport.stream(spliterator(), false);
    }

    public Factor multiply(final Factor ... factors) {
        if (factors.length == 0) {
            throw new IllegalArgumentException("Factors cannot be empty");
        }

        List<Factor> allFactors = new ArrayList<>();
        allFactors.add(this);
        allFactors.addAll(Arrays.asList(factors));

        Set<RandomVariable> unionVars = new TreeSet<>(randomVariables);
        for (Factor factor : factors) {
            unionVars.addAll(factor.randomVariables);
        }

        return AssignmentIndexes.of(unionVars).stream()
                .map(index -> {
                    double value = allFactors.stream()
                            .map(Factor::assignments)
                            .flatMap(Collection::stream)
                            .filter(assignment -> index.contains(assignment.index()))
                            .mapToDouble(Assignment::value)
                            .reduce(1d, (a, b) -> a * b);
                    return new Assignment(index, value);
                })
                .collect(collector());
    }

    public double assignment(final Integer ... indexes) {
        if (indexes.length == 0) {
            throw new IllegalArgumentException("Indexes cannot be empty");
        }

        AssignmentIndex index = new AssignmentIndex(randomVariables, Arrays.asList(indexes));

        Optional<? extends Assignment> result = assignments.stream()
                .filter(assignment -> assignment.index().equals(index))
                .findFirst();

        if (!result.isPresent()) {
            throw new IllegalArgumentException("Invalid assignment");
        }

        return result.get().value();
    }

    public Factor marginalize(final Set<? extends RandomVariable> margVars) {
        if (margVars.isEmpty()) {
            throw new IllegalArgumentException("Marginalization variables cannot be emtpy");
        }

        Map<AssignmentIndex, Double> assignmentMap = assignments.stream()
                .map(assignment -> {
                    AssignmentIndex assignmentIndex = assignment.index().remove(margVars);

                    if (assignmentIndex.isEmpty()) {
                        throw new IllegalArgumentException("Cannot produce empty factor");
                    }

                    return assignment.index(assignmentIndex.sort());

                }).collect(Collectors.groupingBy(Assignment::index,
                        Collectors.summingDouble(Assignment::value)));

        return assignmentMap.entrySet().stream()
                .map(entry -> new Assignment(entry.getKey(), entry.getValue()))
                .collect(collector());
    }

    public static FactorCollector collector() {
        return new FactorCollector();
    }

    public Factor transform(final AssignmentTransformer transformer) {
        return stream().map(transformer::transform).collect(collector());
    }

    public Collection<Assignment> assignments() {
        return new ArrayList<>(assignments);
    }

    public Set<RandomVariable> randomVariables() {
        return new LinkedHashSet<>(randomVariables);
    }

    @Override
    public boolean equals(@Nullable final Object obj) {
        if (this == obj) {
            return true;
        }

        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }

        Factor factor = (Factor) obj;

        return Objects.equals(randomVariables, factor.randomVariables)
                && Objects.equals(assignments, factor.assignments);
    }

    @Override
    public int hashCode() {
        return Objects.hash(randomVariables, assignments);
    }
}
