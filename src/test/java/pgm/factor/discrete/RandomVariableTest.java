package pgm.factor.discrete;

import org.junit.Assert;
import org.junit.Test;

public class RandomVariableTest {

    @Test
    @SuppressWarnings({"checkstyle:magicnumber", "checkstyle:designforextension"})
    public void testConstruction() {
        RandomVariable<Integer> a = new RandomVariable<>(1, 2);
        Assert.assertNotNull(a);
    }

    @Test(expected = IllegalStateException.class)
    @SuppressWarnings({"checkstyle:magicnumber", "checkstyle:designforextension"})
    public void testInvalidRandomVariables() {
        RandomVariable<Integer> a = new RandomVariable<>(1, 2);
        RandomVariable<Integer> b = new RandomVariable<>(1, 3);

        Assert.assertNotEquals(a, b);
    }
}
