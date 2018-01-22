package pgm.factor;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.TreeSet;

public final class DiscreteFactor {

    private static final DiscreteFactor EMPTY_FACTOR = new DiscreteFactor();

    private final Set<Integer> variables;
    private final List<Integer> cardinalities;
    private final List<Double> values;

    public static class Builder {

        private final Set<Integer> vars;
        private final List<Integer> cards;
        private final List<Double> vals;

        public Builder() {
            this.vars = new LinkedHashSet<>();
            this.cards = new ArrayList<>();
            this.vals = new ArrayList<>();
        }

        public Builder variables(int variable, int ... variables) {
            this.vars.add(variable);
            if (variables != null && variables.length > 0) {
                for (int v : variables) {
                    this.vars.add(v);
                }
            }
            return this;
        }

        public Builder variables(Collection<Integer> variables) {
            this.vars.addAll(variables);
            return this;
        }

        public Builder card(int cardinality, int ... cardinalities) {
            this.cards.add(cardinality);
            if (cardinalities != null && cardinalities.length > 0) {
                for (int c : cardinalities) {
                    this.cards.add(c);
                }
            }
            return this;
        }

        public Builder card(Collection<Integer> cardinalities) {
            this.cards.addAll(cardinalities);
            return this;
        }

        public Builder values(double ... values) {
            if (values == null) {
                throw new IllegalArgumentException("Values cannot be null");
            }
            for (double value : values) {
                this.vals.add(value);
            }
            return this;
        }

        public Builder values(Collection<Double> values) {
            if (values == null) {
                throw new IllegalArgumentException("Values cannot be null");
            }
            this.vals.addAll(values);
            return this;
        }

        public DiscreteFactor build() {
            if (vars.size() != cards.size()) {
                throw new IllegalStateException("Dimensionality mismatch beween random variables and cardinalities");
            }
            if (getCardinality(cards) != vals.size()) {
                throw new IllegalStateException("Dimensionality mismatch beween values and cardinalities");
            }
            if (vars.size() == 0 && cards.size() == 0 && vals.size() == 0) {
                return EMPTY_FACTOR;
            }
            return new DiscreteFactor(this);
        }
    }

    private DiscreteFactor() {
        variables = Collections.emptySet();
        cardinalities = Collections.emptyList();
        values = Collections.emptyList();
    }

    private DiscreteFactor(Builder builder) {
        variables = Collections.unmodifiableSet(builder.vars);
        cardinalities = Collections.unmodifiableList(builder.cards);
        values = Collections.unmodifiableList(builder.vals);
    }

    private static int getCardinality(Collection<Integer> cardinalities) {
        if (cardinalities.size() == 0) {
            return 0;
        }

        int cardinality = 1;
        for (int c : cardinalities) {
            cardinality *= c;
        }

        return cardinality;
    }

    private static int assignmentToIndex(List<Integer> assignment, Collection<Integer> cardinalities) {
        if (assignment.size() != cardinalities.size()) {
            throw new IllegalArgumentException("invalid assignment");
        }

        List<Integer> cardinalityList = new ArrayList<>(cardinalities);

        for (int i = 0; i < assignment.size(); i++) {
            int assignmentIndex = assignment.get(i);
            if (assignmentIndex <= 0 || assignmentIndex > cardinalityList.get(i)) {
                throw new IllegalArgumentException("Invalid assignment");
            }
        }

        int cumprod = 1;
        int assignmentIndex = assignment.get(0) - 1;

        for (int i = 1; i < cardinalityList.size(); i++) {
            cumprod *= cardinalityList.get(i - 1);
            assignmentIndex += cumprod * (assignment.get(i) - 1);
        }

        return assignmentIndex;
    }

    private static List<Integer> indexToAssignment(int index, Collection<Integer> cardinalities) {
        if (index < 0) {
            throw new IllegalArgumentException("Index cannot be less than zero");
        }
        if (cardinalities == null || cardinalities.size() == 0) {
            throw new IllegalArgumentException("Empty cardinialities");
        }

        int cumprod = 1;
        List<Integer> cardinalityList = new ArrayList<>(cardinalities);
        List<Integer> assignment = new ArrayList<>(cardinalities.size());
        assignment.add(index % cardinalityList.get(0) + 1);

        for (int i = 1; i < cardinalities.size(); i++) {
            cumprod *= cardinalityList.get(i - 1);
            assignment.add((index / cumprod) % cardinalityList.get(i) + 1);
        }

        return assignment;
    }

    public DiscreteFactor product(DiscreteFactor ... factors) {
        if (factors == null || factors.length == 0) {
            throw new IllegalArgumentException("Factors cannot be empty");
        }

        List<DiscreteFactor> factorList = new ArrayList<>();
        factorList.add(this);

        TreeSet<Integer> union = new TreeSet<>(variables);
        for (DiscreteFactor factor : factors) {
            factorList.add(factor);
            union.addAll(factor.variables);
        }

        List<Integer> unionVarList = new ArrayList<>(union);
        Map<Integer, Integer> cardMap = new HashMap<>();

        List<List<Integer>> indexMapList = new ArrayList<>();
        for (DiscreteFactor factor : factorList) {
            List<Integer> varIndexes = new ArrayList<>(factor.variables.size());
            int varIndex = 0;
            for (int factorVar : factor.variables) {
                int unionVarIndex = unionVarList.indexOf(factorVar);
                if (unionVarIndex >= 0) {
                    cardMap.put(unionVarIndex, factor.cardinalities.get(varIndex));
                    varIndexes.add(unionVarIndex);
                }
                if (varIndexes.size() == factor.variables.size()) {
                    break;
                }
                varIndex++;
            }

            indexMapList.add(varIndexes);
        }

        List<Double> resultVals = new ArrayList<>();
        int resultCard = getCardinality(cardMap.values());

        for (int i = 0; i < resultCard; i++) {
            List<Integer> assignment = indexToAssignment(i, cardMap.values());
            double product = 1;

            for (int j = 0; j < indexMapList.size(); j++) {
                DiscreteFactor factor = factorList.get(j);
                List<Integer> factorIndexMap = indexMapList.get(j);
                List<Integer> factorAssignment = new ArrayList<>();

                for (int varAssignmentIndex : factorIndexMap) {
                    factorAssignment.add(assignment.get(varAssignmentIndex));
                }
                int index = assignmentToIndex(factorAssignment, factor.cardinalities);
                product *= factor.values.get(index);
            }

            resultVals.add(product);
        }

        return new Builder()
            .variables(union)
            .card(cardMap.values())
            .values(resultVals)
            .build();
    }

    public double assignment(int ... indexes) {
        List<Integer> indexesList = new ArrayList<>();
        for (int index : indexes) {
            indexesList.add(index);
        }
        int assignmentIndex = assignmentToIndex(indexesList, this.cardinalities);
        return this.values.get(assignmentIndex);
    }

    public List<Double> values() {
        return new ArrayList<>(values);
    }

    public Set<Integer> variables() {
        return new LinkedHashSet<>(variables);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Discreate Random Factor");
        int i = 0;
        for (int variable : variables) {
            sb.append(" | ");
            sb.append(variable);
            sb.append(" (");
            sb.append(cardinalities.get(i));
            sb.append(") ");
            i++;
        }
        return sb.toString();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }

        DiscreteFactor that = (DiscreteFactor) obj;
        if (!Objects.equals(variables, that.variables)) {
            return false;
        }
        if (values.size() != that.values.size()) {
            return false;
        }
        for (int i = 0; i < values.size(); i++) {
            if (Math.abs(values.get(i) - that.values.get(i)) > 1e-9) {
                return false;
            }
        }

        return true;
    }

    @Override
    public int hashCode() {
        return Objects.hash(variables);
    }
}
