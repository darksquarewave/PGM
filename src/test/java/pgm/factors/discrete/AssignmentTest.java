package pgm.factors.discrete;

import org.junit.Assert;
import org.junit.Test;
import pgm.core.discrete.RandomVariable;

public class AssignmentTest {

    @Test
    @SuppressWarnings({"checkstyle:magicnumber", "checkstyle:designforextension"})
    public void testFactorConstruction() {
        RandomVariable<String, Integer> a = RandomVariable.builder()
                .id("A")
                .event(1)
                .event(2)
                .build();

        RandomVariable<String, Boolean> b = RandomVariable.builder()
                .id("B")
                .event(true)
                .event(false)
                .build();

        Factor f = a.set(1).and(b.set(true)).toAssignment(0.5d)
                .and(a.set(1).and(b.set(false)).toAssignment(0.5d))
                .and(a.set(2).and(b.set(true)).toAssignment(0.5d))
                .and(a.set(2).and(b.set(false)).toAssignment(0.5d))
                .toFactor();

        Assert.assertEquals(4, f.cardinality());
    }

    @Test
    @SuppressWarnings({"checkstyle:magicnumber", "checkstyle:designforextension"})
    public void testGroupAssignmentConstruction() {
        RandomVariable<String, Integer> a = RandomVariable.builder()
                .id("A")
                .event(1)
                .event(2)
                .event(3)
                .build();

        RandomVariable<String, Integer> b = RandomVariable.builder()
                .id("B")
                .event(1)
                .event(2)
                .event(3)
                .build();

        Assert.assertEquals(2, a.set(2).and(b.set(3))
                .toGroupAssignment()
                .randomVariables()
                .size());
    }
}
