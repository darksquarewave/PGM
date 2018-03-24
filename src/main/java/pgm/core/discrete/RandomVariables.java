package pgm.core.discrete;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

public final class RandomVariables {

    private RandomVariables() {
    }

    public static Set<? extends RandomVariable> of(final RandomVariable var0, final RandomVariable ... others) {
        List<RandomVariable> randomVars = new ArrayList<>();
        randomVars.add(var0);
        if (others.length > 0) {
            randomVars.addAll(Arrays.asList(others));
        }

        return construct(randomVars);
    }

    private static Set<? extends RandomVariable> construct(final Collection<? extends RandomVariable> randomVars) {
        return new LinkedHashSet<>(randomVars);
    }
}
