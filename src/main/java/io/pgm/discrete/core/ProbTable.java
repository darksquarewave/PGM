package io.pgm.discrete.core;

import org.checkerframework.checker.nullness.qual.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

public final class ProbTable {

    private static final double DELTA = 1e-9;
    private final Set<? extends RandomVariable> randomVariables;
    private final List<? extends Double> values;

    private ProbTable(final Builder builder) {
        randomVariables = Collections.unmodifiableSet(builder.randomVars);
        values = Collections.unmodifiableList(builder.vals);

        if (AssignmentUtils.cardinality(randomVariables) != values.size()) {
            throw new IllegalArgumentException();
        }
    }

    public AssignmentStream stream() {
        return new DefaultAssignmentStream(new AssignmentSpliterator(randomVariables, values), false);
    }

    public List<? extends Double> values() {
        return values;
    }

    public Set<? extends RandomVariable> randomVariables() {
        return randomVariables;
    }

    @Override
    public boolean equals(@Nullable final Object obj) {
        if (this == obj) {
            return true;
        }

        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }

        ProbTable table = (ProbTable) obj;

        if (!Objects.equals(randomVariables, table.randomVariables)) {
            return false;
        }

        if (values.size() != table.values.size()) {
            return false;
        }

        Iterator<? extends Double> thisValues = values.iterator();
        Iterator<? extends Double> thatValues = table.values().iterator();

        while (thisValues.hasNext()) {
            double thisVal = thisValues.next();
            double thatVal = thatValues.next();
            if (Math.abs(thisVal - thatVal) > DELTA) {
                return false;
            }
        }

        return true;
    }

    @Override
    public int hashCode() {
        return Objects.hash(randomVariables);
    }

    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder {

        private final Set<RandomVariable> randomVars = new LinkedHashSet<>();
        private final List<Double> vals = new ArrayList<>();

        private Builder() {
        }

        public Builder variables(final Collection<? extends RandomVariable> vars) {
            randomVars.addAll(vars);
            return this;
        }

        public Builder variables(final RandomVariable ... vars) {
            randomVars.addAll(Arrays.asList(vars));
            return this;
        }

        public Builder variable(final RandomVariable randomVar) {
            randomVars.add(randomVar);
            return this;
        }

        public Builder values(final Collection<Double> values) {
            vals.addAll(values);
            return this;
        }

        public Builder values(final double ... values) {
            for (double val : values) {
                vals.add(val);
            }
            return this;
        }

        public Builder assignment(final Assignment assignment) {
            if (randomVars.isEmpty()) {
                randomVars.addAll(assignment.randomVariables());
            } else if (!randomVars.equals(assignment.randomVariables())) {
                throw new IllegalArgumentException();
            }
            vals.add(assignment.value());
            return this;
        }

        public Builder add(final Builder builder) {
            if (!randomVars.equals(builder.randomVars)) {
                throw new IllegalArgumentException();
            }
            vals.addAll(builder.vals);
            return this;
        }

        public Builder value(final double value) {
            vals.add(value);
            return this;
        }

        public ProbTable build() {
            return new ProbTable(this);
        }
    }
}
