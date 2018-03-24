package pgm.factors.discrete.functions;

//import org.junit.Test;
//import pgm.core.discrete.Assignments;
//import pgm.core.discrete.FactorCollectors;
//import pgm.core.discrete.RandomVariable;
//import pgm.factors.discrete.Factor;
//import pgm.factors.discrete.functions.queries.ProbabilityQuery;
//
//import java.util.Arrays;

public class ProbabilityQueryTest {

//    @Test
//    @SuppressWarnings({"checkstyle:magicnumber", "checkstyle:designforextension"})
//    public void testProbabilityQuerySimple() {
//        RandomVariable<String, String> a = RandomVariable.builder()
//                .id("a")
//                .event("one")
//                .event("two")
//                .build();
//
//        RandomVariable<String, Integer> b = RandomVariable.builder()
//                .id("b")
//                .event(1)
//                .event(2)
//                .build();
//
//        Factor factor = Assignments.of(Arrays.asList(0.6d, 0.4d, 0.7d, 0.3d), a, b).stream()
//                .collect(FactorCollectors.toFactor());
//
//        ProbabilityQuery probabilityQuery = ProbabilityQuery.builder()
//                .assignment(a.assign("one"))
//                .evidence(b.assign(2))
//                .build();
//    }
}
