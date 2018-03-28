package pgm.core.discrete;

import pgm.factors.discrete.CPD;
import pgm.factors.discrete.Factor;
import pgm.factors.discrete.JointDistribution;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

public final class Assignments {

    private Assignments() {
    }

    public static final class Builder {

        private final List<Double> values = new ArrayList<>();
        private final List<RandomVariable> randomVariables = new ArrayList<>();

        public Builder variable(final RandomVariable randomVariable) {
            randomVariables.add(randomVariable);
            return this;
        }

        public Builder variables(final RandomVariable randomVariable, final RandomVariable ... rest) {
            randomVariables.add(randomVariable);
            if (rest.length > 0) {
                randomVariables.addAll(Arrays.asList(rest));
            }

            return this;
        }

        public Builder value(final Double value) {
            values.add(value);
            return this;
        }

        public Builder values(final Double value, final Double ... rest) {
            values.add(value);
            if (rest.length > 0) {
                values.addAll(Arrays.asList(rest));
            }

            return this;
        }

        public Collection<? extends Assignment> build() {
            if (randomVariables.isEmpty()) {
                throw new IllegalArgumentException("Random variables cannot be empty");
            }

            int sumCardinality = randomVariables.stream()
                    .map(RandomVariable::cardinality)
                    .reduce(1, (a, b) -> a * b);

            if (sumCardinality != values.size()) {
                throw new IllegalArgumentException("Dimensionality mismatch between values Operations variables");
            }

            Iterator<? extends Double> valIterator = values.iterator();

            return VariableGroupAssignments.of(randomVariables).stream()
                    .map(groupAssignment -> new Assignment(groupAssignment, valIterator.next()))
                    .collect(Collectors.toList());
        }

        public Factor toFactor() {
            return build().stream().collect(FactorCollectors.toFactor());
        }

        public CPD toCPD() {
            return build().stream().collect(FactorCollectors.toCPD());
        }

        public JointDistribution toJoint() {
            return build().stream().collect(FactorCollectors.toJoint());
        }
    }

    public static Builder builder() {
        return new Builder();
    }
}
