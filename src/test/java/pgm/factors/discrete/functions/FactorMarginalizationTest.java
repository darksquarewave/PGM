package pgm.factors.discrete.functions;

//import org.junit.Assert;
//import org.junit.Test;
//import pgm.core.discrete.old.Assignments;
//import pgm.core.discrete.old.FactorCollectors;
//import pgm.core.discrete.old.RandomVariable;
//import pgm.core.discrete.old.RandomVariables;
//import pgm.factors.discrete.Factor;
//
//import java.util.Arrays;

public class FactorMarginalizationTest {

//    @Test
//    @SuppressWarnings({"checkstyle:magicnumber", "checkstyle:designforextension"})
//    public void testMarginalization() {
//        RandomVariable<Integer> a = new RandomVariable<>(1, 2);
//        RandomVariable<Integer> b = new RandomVariable<>(2, 2);
//
//        Factor factorA = Assignments.of(RandomVariables.of(b, a),
//                Arrays.asList(0.59d, 0.41d, 0.22d, 0.78d))
//                .collect(FactorCollectors.toFactor());
//
//        Factor factorB = Assignments.of(RandomVariables.of(a),
//                Arrays.asList(1d, 1d))
//                .collect(FactorCollectors.toFactor());
//
//        Assert.assertEquals(factorB, factorA.marginalize(RandomVariables.of(b)));
//    }
//
//    @Test
//    @SuppressWarnings({"checkstyle:magicnumber", "checkstyle:designforextension"})
//    public void testMarginalization2() {
//        RandomVariable<Integer> a = new RandomVariable<>(1, 3);
//        RandomVariable<Integer> b = new RandomVariable<>(2, 2);
//        RandomVariable<Integer> c = new RandomVariable<>(3, 2);
//
//        Factor x = Assignments.of(RandomVariables.of(a, c),
//                Arrays.asList(0.33d, 0.05d, 0.24d, 0.51d, 0.07d, 0.39d))
//                .collect(FactorCollectors.toFactor());
//
//        Factor z = Assignments.of(RandomVariables.of(c, b, a),
//                Arrays.asList(0.25d, 0.35d, 0.08d, 0.16d, 0.05d, 0.07d, 0d, 0d, 0.15d, 0.21d, 0.09d, 0.18d))
//                .collect(FactorCollectors.toFactor());
//
//        Assert.assertEquals(x, z.marginalize(RandomVariables.of(b)));
//    }
}
