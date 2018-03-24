package pgm.models;

public final class BayesianModel {
//
//    private final DirectedGraph<TabularCPD> graph;
//
//    private static class BayeseianModelCollector
//            implements Collector<TabularCPD, Collection<TabularCPD>, BayesianModel> {
//
//        @Override
//        public Supplier<Collection<TabularCPD>> supplier() {
//            return ArrayList::new;
//        }
//
//        @Override
//        public BiConsumer<Collection<TabularCPD>, TabularCPD> accumulator() {
//            return Collection::add;
//        }
//
//        @Override
//        public BinaryOperator<Collection<TabularCPD>> combiner() {
//            return (coll1, coll2) -> {
//                coll1.addAll(coll2);
//                return coll1;
//            };
//        }
//
//        @Override
//        public Function<Collection<TabularCPD>, BayesianModel> finisher() {
//            return BayesianModel::new;
//        }
//
//        @Override
//        public Set<Characteristics> characteristics() {
//            return Collections.emptySet();
//        }
//    }
//
//    public BayesianModel(Collection<TabularCPD> tabularCPDs) {
//        this.graph = buildGraph(tabularCPDs);
//    }
//
//    public static BayeseianModelCollector collector() {
//        return new BayeseianModelCollector();
//    }
//
//    private static DirectedGraph<TabularCPD> buildGraph(Collection<TabularCPD> tabularCPDs) {
//        DirectedGraph.Builder<TabularCPD> builder = new DirectedGraph.Builder<>();
//
//        List<TabularCPD> sortedCPDs = tabularCPDs.stream()
//                .sorted(Comparator.comparingInt(o -> o.conditioningVariables().size()))
//                .collect(Collectors.toList());
//
//        Map<RandomVariable, TabularCPD> varToCPD = new HashMap<>();
//        for (TabularCPD tabularCPD : sortedCPDs) {
//            builder.vertex(tabularCPD);
//            varToCPD.put(tabularCPD.randomVariable(), tabularCPD);
//        }
//
//        for (TabularCPD tabularCPD : sortedCPDs) {
//            for (RandomVariable condVar : tabularCPD.conditioningVariables()) {
//                TabularCPD parentCPD = varToCPD.get(condVar);
//                if (parentCPD == null) {
//                    throw new IllegalStateException("Invalid CPD structure, cannot build the graph");
//                }
//                builder.edge(parentCPD, tabularCPD);
//            }
//        }
//
//        return builder.build();
//    }
//
//    @Override
//    public boolean equals(@Nullable final Object obj) {
//        if (this == obj) {
//            return true;
//        }
//
//        if (obj == null || getClass() != obj.getClass()) {
//            return false;
//        }
//
//        BayesianModel model = (BayesianModel) obj;
//
//        return Objects.equals(graph, model.graph);
//    }
//
//    @Override
//    public int hashCode() {
//        return Objects.hash(graph);
//    }
}
