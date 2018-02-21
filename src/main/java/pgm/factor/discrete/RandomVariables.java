package pgm.factor.discrete;

import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.Set;

public final class RandomVariables {

    private RandomVariables() {
    }

    public static Set<RandomVariable> of(
            final RandomVariable ... randomVariables) {
        if (randomVariables.length == 0) {
            throw new IllegalArgumentException("Random variables cannot be empty");
        }
        return new LinkedHashSet<>(Arrays.asList(randomVariables));
    }
}
