package pgm.factors.discrete.functions;

import org.junit.Assert;
import org.junit.Test;
import pgm.core.discrete.Assignments;
import pgm.core.discrete.RandomVariable;
import pgm.factors.discrete.Factor;

public class FactorMarginalizationTest {

    @Test
    @SuppressWarnings({"checkstyle:magicnumber", "checkstyle:designforextension"})
    public void testMarginalization() {
        RandomVariable<Integer, Integer> a = RandomVariable.builder()
                .id(1)
                .events(1, 2)
                .build();

        RandomVariable<Integer, Integer> b = RandomVariable.builder()
                .id(2)
                .events(1, 2)
                .build();

        Factor factorA = Assignments.builder()
                .variables(b, a)
                .values(0.59d, 0.41d, 0.22d, 0.78d)
                .toFactor();

        Factor factorB = Assignments.builder()
                .variables(a)
                .values(1d, 1d)
                .toFactor();

        Assert.assertEquals(factorB, factorA.marginalize(b));
    }

    @Test
    @SuppressWarnings({"checkstyle:magicnumber", "checkstyle:designforextension"})
    public void testMarginalization2() {
        RandomVariable<Integer, Integer> a = RandomVariable.builder()
                .id(1)
                .events(1, 2, 3)
                .build();

        RandomVariable<Integer, Integer> b = RandomVariable.builder()
                .id(2)
                .events(1, 2)
                .build();

        RandomVariable<Integer, Integer> c = RandomVariable.builder()
                .id(3)
                .events(1, 2)
                .build();

        Factor x = Assignments.builder()
                .variables(a, c)
                .values(0.33d, 0.05d, 0.24d, 0.51d, 0.07d, 0.39d)
                .toFactor();

        Factor z = Assignments.builder()
                .variables(c, b, a)
                .values(0.25d, 0.35d, 0.08d, 0.16d, 0.05d, 0.07d)
                .values(0.00d, 0.00d, 0.15d, 0.21d, 0.09d, 0.18d)
                .toFactor();

        Assert.assertEquals(x, z.marginalize(b));
    }

}
