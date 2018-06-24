package io.pgm.discrete.core;

import org.junit.Assert;
import org.junit.Test;

@SuppressWarnings({"checkstyle:magicnumber", "checkstyle:designforextension"})
public class RandomVariableConstructionTest {

    private enum VarType {
        INTELLIGENCE
    }

    private enum IntelligenceType {
        LOW,
        HIGH
    }

    @Test
    public void testConstruction() {
        RandomVariable<VarType, IntelligenceType> a = RandomVariable.builder()
                .id(VarType.INTELLIGENCE)
                .events(IntelligenceType.values())
                .build();

        Assert.assertEquals(2, a.cardinality());
    }
}
