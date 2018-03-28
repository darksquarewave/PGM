package pgm.factors.discrete;

import org.junit.Assert;
import org.junit.Test;
import pgm.core.discrete.RandomVariable;

public class RandomVariableTest {

    private enum VarType {
        INTELLIGENCE
    }

    private enum IntelligenceType {
        LOW,
        HIGH
    }

    @Test
    @SuppressWarnings({"checkstyle:magicnumber", "checkstyle:designforextension"})
    public void testConstruction() {
        RandomVariable<String, Integer> varA = RandomVariable.builder()
                .id("Variable A")
                .event(1)
                .event(2)
                .event(3)
                .build();

        RandomVariable<VarType, IntelligenceType> varB = RandomVariable.builder()
                .id(VarType.INTELLIGENCE)
                .events(IntelligenceType.values())
                .build();

        Assert.assertEquals(3, varA.cardinality());
        Assert.assertEquals(2, varB.cardinality());
    }


    @Test
    @SuppressWarnings({"checkstyle:magicnumber", "checkstyle:designforextension"})
    public void testSetConstruction() {
        RandomVariable<String, Integer> a = RandomVariable.builder()
                .id("A")
                .event(1)
                .event(2)
                .event(3)
                .build();

        RandomVariable<VarType, IntelligenceType> b = RandomVariable.builder()
                .id(VarType.INTELLIGENCE)
                .events(IntelligenceType.values())
                .build();

        Assert.assertEquals(2, a.and(b).toSet().size());
    }
}
