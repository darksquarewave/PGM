package pgm.models;

import org.checkerframework.checker.nullness.qual.Nullable;
import pgm.core.discrete.RandomVariable;
import pgm.factors.discrete.CPD;
import pgm.graph.DirectedGraph;

import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

public final class BayesianModel {

    private final DirectedGraph<CPD> graph;

    public static BayesianModel build(final Collection<CPD> tabularCPDs) {
        return new BayesianModel(tabularCPDs);
    }

    private BayesianModel(final Collection<CPD> tabularCPDs) {
        this.graph = buildGraph(tabularCPDs);
    }

    private static DirectedGraph<CPD> buildGraph(final Collection<CPD> tabularCPDs) {
        DirectedGraph.Builder<CPD> builder = new DirectedGraph.Builder<>();

        List<CPD> sortedCPDs = tabularCPDs.stream()
                .sorted(Comparator.comparingInt(o -> o.conditioningVariables().size()))
                .collect(Collectors.toList());

        Map<RandomVariable, CPD> varToCPD = new HashMap<>();
        for (CPD tabularCPD : sortedCPDs) {
            builder.vertex(tabularCPD);
            varToCPD.put(tabularCPD.randomVariable(), tabularCPD);
        }

        for (CPD tabularCPD : sortedCPDs) {
            for (RandomVariable condVar : tabularCPD.conditioningVariables()) {
                CPD parentCPD = varToCPD.get(condVar);
                if (parentCPD == null) {
                    throw new IllegalStateException("Invalid CPD structure, cannot build the graph");
                }
                builder.edge(parentCPD, tabularCPD);
            }
        }

        return builder.build();
    }

    @Override
    public boolean equals(@Nullable final Object obj) {
        if (this == obj) {
            return true;
        }

        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }

        BayesianModel model = (BayesianModel) obj;

        return Objects.equals(graph, model.graph);
    }

    @Override
    public int hashCode() {
        return Objects.hash(graph);
    }
}
