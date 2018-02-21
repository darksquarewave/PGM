package pgm.factor.discrete.factor.transformers;

import pgm.factor.discrete.factor.Assignment;
import pgm.factor.discrete.factor.AssignmentIndex;
import pgm.factor.discrete.factor.interfaces.AssignmentTransformer;

public class ObserveEvidenceTransformer implements AssignmentTransformer {

    private final AssignmentIndex evidenceIndex;

    public ObserveEvidenceTransformer(final AssignmentIndex index) {
        //todo check dimensions
        this.evidenceIndex = index;
    }

    @Override
    public final Assignment transform(final Assignment assignment) {
        AssignmentIndex index = assignment.index();

        if (!index.contains(evidenceIndex.randomVariables())) {
            return assignment;
        }

        if (!index.intersects(evidenceIndex)) {
            return assignment.value(0d);
        } else {
            return assignment;
        }
    }
}
