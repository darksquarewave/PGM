package pgm.factors.discrete.functions.queries;

import pgm.core.discrete.Assignment;
import pgm.core.discrete.VariableAssignment;
import pgm.core.discrete.VariableGroupAssignment;
import pgm.factors.discrete.functions.transformers.EvidenceTransformer;
import pgm.factors.discrete.functions.transformers.NormalizationTransformer;

import java.util.function.Function;
import java.util.stream.Stream;

public final class ProbabilityQuery implements Function<Stream<? extends Assignment>, Double> {

    private final VariableAssignment target;
    private final VariableGroupAssignment evidences;

    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder {
        private Builder() {
        }

        public EvidenceBuilder assignment(final VariableAssignment target) {
            return new EvidenceBuilder(target);
        }
    }

    public static final class EvidenceBuilder {

        private final VariableAssignment targetAssignment;
        private VariableGroupAssignment evidenceAssignments;

        EvidenceBuilder(final VariableAssignment assignment) {
            targetAssignment = assignment;
            evidenceAssignments = VariableGroupAssignment.emptyAssignment();
        }

        public EvidenceBuilder evidence(final VariableGroupAssignment evidences) {
            this.evidenceAssignments = evidences;
            return this;
        }

        public ProbabilityQuery build() {
            return new ProbabilityQuery(this);
        }
    }

    private ProbabilityQuery(final EvidenceBuilder builder) {
        target = builder.targetAssignment;
        evidences = builder.evidenceAssignments;
    }

    @Override
    public Double apply(final Stream<? extends Assignment> assignments) {
        return Stream.of(assignments)
            .map(NormalizationTransformer.instance())
            .flatMap(Function.identity())
            .map(EvidenceTransformer.of(evidences))
            .filter(a -> a.varAssignments().contains(target))
            .mapToDouble(Assignment::value)
            .sum();
    }
}
