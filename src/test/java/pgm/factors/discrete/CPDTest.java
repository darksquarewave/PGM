package pgm.factors.discrete;

import org.junit.Assert;
import org.junit.Test;
import pgm.core.discrete.Assignments;
import pgm.core.discrete.RandomVariable;

import java.util.Arrays;
import java.util.Collections;

public class CPDTest {

    @Test
    @SuppressWarnings({"checkstyle:magicnumber", "checkstyle:designforextension"})
    public void testConstruction() {
        RandomVariable<String, String> a = RandomVariable.builder().id("a").event("1").event("2").build();

        CPD cpd = Assignments.builder()
                .variable(a)
                .values(0.5d, 0.5d)
                .toCPD();

        Assert.assertEquals(a, cpd.randomVariable());
        Assert.assertEquals(Collections.emptyList(), cpd.conditioningVariables());
    }

    @Test(expected = IllegalArgumentException.class)
    @SuppressWarnings({"checkstyle:magicnumber", "checkstyle:designforextension"})
    public void testInvalidConstruction() {
        RandomVariable<String, String> a = RandomVariable.builder().id("a").event("1").event("2").build();

        CPD cpd = Assignments.builder()
                .variable(a)
                .values(0.5d, 0.6d)
                .toCPD();

        Assert.assertEquals(a, cpd.randomVariable());
        Assert.assertEquals(Collections.emptyList(), cpd.conditioningVariables());
    }

    @Test
    @SuppressWarnings({"checkstyle:magicnumber", "checkstyle:designforextension"})
    public void testConstruction2() {
        RandomVariable<String, String> a = RandomVariable.builder().id("a").event("1").event("2").build();
        RandomVariable<String, String> b = RandomVariable.builder().id("b").event("1").event("2").build();
        RandomVariable<String, String> c = RandomVariable.builder().id("c").event("1").event("2").build();

        CPD cpd = Assignments.builder()
                .variable(a)
                .variable(c)
                .variable(b)
                .values(0.5d, 0.5d)
                .values(0.5d, 0.5d)
                .values(0.5d, 0.5d)
                .values(0.5d, 0.5d)
                .toCPD();

        Assert.assertEquals(a, cpd.randomVariable());
        Assert.assertEquals(Arrays.asList(c, b), cpd.conditioningVariables());
    }
}
