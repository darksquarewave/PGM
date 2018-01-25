package pgm.factor;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Set;
import java.util.TreeMap;
import java.util.stream.Collectors;

public final class DiscreteFactor {

    private static final DiscreteFactor EMPTY_FACTOR = new DiscreteFactor();

    private final Map<Integer, Integer> varToCardMap;
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

        public Builder var(int variable, int ... variables) {
            this.vars.add(variable);
            if (variables != null && variables.length > 0) {
                for (int v : variables) {
                    this.vars.add(v);
                }
                if (this.vars.size() != variables.length + 1) {
                    throw new IllegalArgumentException("Variables must be unique");
                }
            }
            return this;
        }

        public Builder var(Collection<Integer> variables) {
            this.vars.addAll(variables);
            if (this.vars.size() != variables.size()) {
                throw new IllegalArgumentException("Variables must be unique");
            }
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

        public Builder val(double ... values) {
            if (values == null) {
                throw new IllegalArgumentException("Values cannot be null");
            }
            for (double value : values) {
                this.vals.add(value);
            }
            return this;
        }

        public Builder val(Collection<Double> values) {
            if (values == null) {
                throw new IllegalArgumentException("Values cannot be null");
            }
            this.vals.addAll(values);
            return this;
        }

        public DiscreteFactor build() {
            if (vars.size() != cards.size()) {
                throw new IllegalArgumentException("Dimensionality mismatch beween variables and cardinalities");
            }
            if (getCardSize(cards) != vals.size()) {
                throw new IllegalArgumentException("Dimensionality mismatch beween values and cardinalities");
            }
            if (cards.stream().anyMatch(c -> c == null || c <= 0)) {
                throw new IllegalArgumentException("Cardinality must be not null and strictly positive");
            }
            if (vals.stream().anyMatch(v -> v == null || v < 0)) {
                throw new IllegalArgumentException("Value must be not null and non negative");
            }
            if (vars.contains(null)) {
                throw new IllegalArgumentException("Variable cannot be null");
            }

            if (vars.size() == 0 && cards.size() == 0 && vals.size() == 0) {
                return EMPTY_FACTOR;
            }

            return new DiscreteFactor(this);
        }
    }

    private static class Assignment {

        private Set<Integer> randomVars;
        private Collection<Integer> cardinalities;
        private Collection<Integer> indexes;

        private Assignment(Collection<Integer> indexes, Set<Integer> randomVars, Collection<Integer> cardinalities) {
            this.indexes = indexes;
            this.randomVars = randomVars;
            this.cardinalities = cardinalities;
        }

        private Assignment mapAssignment(Set<Integer> mapVars) {
            List<Integer> randomVarList = new ArrayList<>(randomVars);
            List<Integer> mapIndexes = new ArrayList<>();
            List<Integer> mapCardinalities = new ArrayList<>();
            List<Integer> indexList = new ArrayList<>(indexes);
            List<Integer> cardList = new ArrayList<>(cardinalities);

            for (int mapVar : mapVars) {
                int i = randomVarList.indexOf(mapVar);
                if (i >= 0) {
                    mapIndexes.add(indexList.get(i));
                    mapCardinalities.add(cardList.get(i));
                }
            }

            return new Assignment(mapIndexes, mapVars, mapCardinalities);
        }

        private int toIndex() {
            if (indexes == null || indexes.size() == 0) {
                throw new IllegalArgumentException("Assignment cannot be empty");
            }
            if (cardinalities == null || cardinalities.size() == 0) {
                throw new IllegalArgumentException("Cardinalities cannot be empty");
            }
            if (indexes.size() != cardinalities.size()) {
                throw new IllegalArgumentException("invalid assignment");
            }

            List<Integer> indexList = new ArrayList<>(indexes);
            List<Integer> cardList = new ArrayList<>(cardinalities);

            for (int i = 0; i < indexList.size(); i++) {
                int assignmentIndex = indexList.get(i);
                if (assignmentIndex <= 0 || assignmentIndex > cardList.get(i)) {
                    throw new IllegalArgumentException("Invalid assignment");
                }
            }

            int cumprod = 1;
            int assignmentIndex = indexList.get(0) - 1;

            for (int i = 1; i < cardList.size(); i++) {
                cumprod *= cardList.get(i - 1);
                assignmentIndex += cumprod * (indexList.get(i) - 1);
            }

            return assignmentIndex;
        }

        private static Assignment toAssignment(int index, Set<Integer> variables, Collection<Integer> cardinalities) {
            if (index < 0) {
                throw new IllegalArgumentException("Index cannot be less than zero");
            }
            if (cardinalities == null || cardinalities.size() == 0) {
                throw new IllegalArgumentException("Cardinalities cannot be empty");
            }

            List<Integer> cardList = new ArrayList<>(cardinalities);

            int cumprod = 1;
            List<Integer> indexes = new ArrayList<>(cardinalities.size());
            indexes.add(index % cardList.get(0) + 1);

            for (int i = 1; i < cardinalities.size(); i++) {
                cumprod *= cardList.get(i - 1);
                indexes.add((index / cumprod) % cardList.get(i) + 1);
            }

            return new Assignment(indexes, variables, cardinalities);
        }
    }

    private static class AssignmentIterator implements Iterable<Assignment> {

        private final Collection<Integer> cardinalities;
        private final Set<Integer> variables;
        private final int size;
        private int i = 0;

        private AssignmentIterator(Set<Integer> variables, Collection<Integer> cardinalities)  {
            this.variables = variables;
            this.cardinalities = cardinalities;
            this.size = getCardSize(cardinalities);
        }

        @Override
        public Iterator<Assignment> iterator() {
            return new Iterator<Assignment>() {

                @Override
                public boolean hasNext() {
                    return i < size;
                }

                @Override
                public Assignment next() {
                    if (!hasNext()) {
                        throw new NoSuchElementException();
                    }

                    Assignment result = Assignment.toAssignment(i, variables, cardinalities);
                    i++;
                    return result;
                }
            };
        }
    }

    private DiscreteFactor() {
        varToCardMap = Collections.emptyMap();
        values = Collections.emptyList();
    }

    private DiscreteFactor(Builder builder) {
        Map<Integer, Integer> map = new LinkedHashMap<>();
        int i = 0;
        for (int variable : builder.vars) {
            map.put(variable, builder.cards.get(i));
            i++;
        }

        this.varToCardMap = Collections.unmodifiableMap(map);
        this.values = Collections.unmodifiableList(builder.vals);
    }

    private static AssignmentIterator assignments(Map<Integer, Integer> varToCardMap) {
        return new AssignmentIterator(varToCardMap.keySet(), varToCardMap.values());
    }

    private static int getCardSize(Collection<Integer> cardinalities) {
        if (cardinalities.size() == 0) {
            return 0;
        }

        return cardinalities.stream().reduce(1, (a, b) -> a * b);
    }

    public DiscreteFactor product(DiscreteFactor ... factors) {
        if (factors == null || factors.length == 0) {
            throw new IllegalArgumentException("Factors cannot be empty");
        }

        List<DiscreteFactor> allFactors = new ArrayList<>();
        allFactors.add(this);
        Collections.addAll(allFactors, factors);

        allFactors = allFactors.stream().filter(factor -> factor != EMPTY_FACTOR).collect(Collectors.toList());
        if (allFactors.size() == 0) {
            return EMPTY_FACTOR;
        }
        else if (allFactors.size() == 1) {
            return allFactors.get(0);
        }

        Map<Integer, Integer> union = new TreeMap<>();
        for (DiscreteFactor factor : allFactors) {
            for (int factorVar : factor.varToCardMap.keySet()) {
                if (union.containsKey(factorVar) && !union.get(factorVar).equals(factor.varToCardMap.get(factorVar))) {
                    throw new IllegalArgumentException("Dimensionality mismatch in factors");
                }
            }

            union.putAll(factor.varToCardMap);
        }

        List<Double> resultVals = new ArrayList<>();

        for (Assignment assignment : assignments(union)) {
            double result = 1;
            for (DiscreteFactor factor : allFactors) {
                Assignment factorAssignment = assignment.mapAssignment(factor.variables());
                result *= factor.values.get(factorAssignment.toIndex());
            }
            resultVals.add(result);
        }

        return new Builder()
            .var(union.keySet())
            .card(union.values())
            .val(resultVals)
            .build();
    }

    public double assignment(int ... indexes) {
        List<Integer> indexesList = new ArrayList<>();
        for (int index : indexes) {
            indexesList.add(index);
        }
        Assignment assignment = new Assignment(indexesList, varToCardMap.keySet(), varToCardMap.values());
        return this.values.get(assignment.toIndex());
    }

    public DiscreteFactor marginalize(int marginalizeVar, int ... marginalizeVars) {
        Set<Integer> varSet = new LinkedHashSet<>();
        varSet.add(marginalizeVar);
        if (marginalizeVars != null) {
            for (int margVar : marginalizeVars) {
                varSet.add(margVar);
            }
        }
        return marginalize(varSet);
    }

    public DiscreteFactor marginalize(Set<Integer> marginalizeVars) {
        Map<Integer, Integer> margVarToCardMap = new TreeMap<>(varToCardMap);

        margVarToCardMap.keySet().removeAll(marginalizeVars);
        Set<Integer> margVars = margVarToCardMap.keySet();
        Collection<Integer> margCards = margVarToCardMap.values();

        if (margVars.size() == 0) {
            throw new IllegalArgumentException("Resultant factor has empty scope");
        }

        AssignmentIterator assignments = assignments(varToCardMap);

        int size = getCardSize(margCards);
        List<Double> resultVals = new ArrayList<>(Collections.nCopies(size, 0d));

        for (Assignment assignment : assignments) {
            Assignment margAssignment = assignment.mapAssignment(margVars);
            int i = margAssignment.toIndex();
            int j = assignment.toIndex();
            double val = resultVals.get(i) + values.get(j);
            resultVals.set(i, val);
        }

        return new Builder()
            .var(margVars)
            .card(margCards)
            .val(resultVals)
            .build();
    }

    public List<Double> values() {
        return new ArrayList<>(values);
    }

    public Set<Integer> variables() {
        return new LinkedHashSet<>(varToCardMap.keySet());
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Discrete Random Factor");
        for (Map.Entry<Integer, Integer> entry : varToCardMap.entrySet()) {
            sb.append(" | ");
            sb.append(entry.getKey());
            sb.append("(");
            sb.append(entry.getValue());
            sb.append(") ");
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
        if (!Objects.equals(varToCardMap, that.varToCardMap)) {
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
        return Objects.hash(varToCardMap);
    }
}
