package pgm.factors.discrete;

import org.checkerframework.checker.nullness.qual.Nullable;
import pgm.core.discrete.Assignment;
import pgm.core.discrete.FactorCollectors;
import pgm.core.discrete.AbstractAssignmentSet;
import pgm.core.discrete.RandomVariable;
import pgm.core.discrete.VariableAssignment;
import pgm.core.discrete.VariableGroupAssignment;
import pgm.factors.discrete.functions.operations.FactorProduct;
import pgm.factors.discrete.functions.transformers.EvidenceTransformer;
import pgm.factors.discrete.functions.transformers.MarginalizationTransformer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

public final class Factor extends AbstractAssignmentSet {

    public static final class Builder extends AbstractAssignmentSet.AbstractBuilder<Factor> {

        @Override
        public Factor build() {
            return new Factor(assignments());
        }
    }

    private Factor(final Collection<? extends Assignment> assignments) {
        super(assignments);
    }

    public CPD toCPD() {
        return stream().collect(FactorCollectors.toCPD());
    }

    public JointDistribution toJointDistribution() {
        return stream().collect(FactorCollectors.toJoint());
    }

    public Factor product(final Factor f) {
        return product(Arrays.asList(this, f));
    }

    public Factor product(final Factor f1, final Factor f2) {
        return product(Arrays.asList(this, f1, f2));
    }

    public static Factor product(final Collection<? extends Factor> operands) {
        return new FactorProduct().apply(operands).collect(FactorCollectors.toFactor());
    }

    public Factor marginalize(final RandomVariable var0, final RandomVariable ... rest) {
        ArrayList<RandomVariable> vars = new ArrayList<>();
        vars.add(var0);
        if (rest.length > 0) {
            vars.addAll(Arrays.asList(rest));
        }
        return marginalize(vars);
    }

    public Factor marginalize(final Collection<? extends RandomVariable> margVars) {
        return MarginalizationTransformer.of(margVars)
                .apply(stream())
                .collect(FactorCollectors.toFactor());
    }

    public Factor marginalize(final Set<? extends RandomVariable> marginalizationVars) {
        return MarginalizationTransformer.of(marginalizationVars)
                .apply(stream())
                .collect(FactorCollectors.toFactor());
    }

    public Factor observeEvidence(final VariableAssignment singleEvidence) {
        return stream().map(EvidenceTransformer.of(singleEvidence.toGroupAssignment()))
                .collect(FactorCollectors.toFactor());
    }

    public Factor observeEvidence(final VariableGroupAssignment groupEvidence) {
        return stream().map(EvidenceTransformer.of(groupEvidence))
                .collect(FactorCollectors.toFactor());
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

        return Objects.equals(randomVariables(), factor.randomVariables())
                && Objects.equals(assignments(), factor.assignments());
    }

    @Override
    public int hashCode() {
        return Objects.hash(randomVariables(), assignments());
    }

    @Override
    public String toString() {
        String vars = randomVariables().stream()
                .map(variable -> variable.id().toString())
                .collect(Collectors.joining(", "));
        return "Factor(variables=" + vars + ")";
    }
}
