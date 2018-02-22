package pgm.factor.discrete;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;

public final class AssignmentIndexes {

    private AssignmentIndexes() {
    }

    static int card(final Set<? extends RandomVariable> randomVariables) {
        if (randomVariables.size() == 0) {
            throw new IllegalArgumentException("Random variables cannot be empty");
        }

        return randomVariables.stream()
                .map(RandomVariable::cardinality)
                .reduce(1, (a, b) -> a * b);
    }

    static int toIndex(final AssignmentIndex assignmentIndex) {
        List<Integer> indexList = assignmentIndex.indexes();
        List<Integer> cardList = assignmentIndex.cardinalities();

        for (int i = 0; i < indexList.size(); i++) {
            int index = indexList.get(i);
            if (index <= 0 || index > cardList.get(i)) {
                throw new IllegalArgumentException("Invalid assignment");
            }
        }

        int cumprod = 1;
        int index = indexList.get(0) - 1;

        for (int i = 1; i < cardList.size(); i++) {
            cumprod *= cardList.get(i - 1);
            index += cumprod * (indexList.get(i) - 1);
        }

        return index;
    }

    public static Collection<AssignmentIndex> of(final Set<? extends RandomVariable> randomVariables) {
        if (randomVariables.size() == 0) {
            throw new IllegalArgumentException("Variables cannot be empty");
        }

        List<AssignmentIndex> assignmentIndices = new ArrayList<>();
        List<RandomVariable> varList = new ArrayList<>(randomVariables);
        int cardinality = card(randomVariables);

        for (int i = 0; i < cardinality; i++) {
            int cumprod = 1;

            List<Integer> indices = new ArrayList<>();
            indices.add(i % varList.get(0).cardinality() + 1);

            for (int j = 1; j < randomVariables.size(); j++) {
                cumprod *= varList.get(j - 1).cardinality();
                indices.add((i / cumprod) % varList.get(j).cardinality() + 1);
            }

            assignmentIndices.add(new AssignmentIndex(randomVariables, indices));
        }

        return assignmentIndices;
    }
}
