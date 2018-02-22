package pgm.factor.discrete;

import java.util.Collection;
import java.util.Iterator;
import java.util.Set;
import java.util.stream.Collectors;

public final class Assignments {

    private Assignments() {
    }

    public static Collection<Assignment> of(final Set<? extends RandomVariable> randomVariables,
                                            final Collection<? extends Double> values) {

        if (randomVariables.isEmpty()) {
            throw new IllegalArgumentException("Random variables cannot be empty");
        }

        if (AssignmentIndexes.card(randomVariables) != values.size()) {
            throw new IllegalArgumentException("Dimensionality mismatch between values and variables");
        }

        Iterator<? extends Double> iterator = values.iterator();

        return AssignmentIndexes.of(randomVariables).stream()
                .map(index -> new Assignment(index, iterator.next()))
                .collect(Collectors.toList());
    }
}
