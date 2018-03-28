package pgm.factors.discrete;

import org.junit.Assert;
import org.junit.Test;
import pgm.core.discrete.Assignments;
import pgm.core.discrete.FactorCollectors;
import pgm.core.discrete.RandomVariable;
import pgm.models.BayesianModel;

import java.util.stream.Stream;

public class BayesianModelTest {

    @Test
    @SuppressWarnings({"checkstyle:magicnumber", "checkstyle:designforextension"})
    public void testConstruction() {
        RandomVariable<String, Boolean> pollution = RandomVariable.builder()
                .id("Pollution")
                .events(true, false)
                .build();

        RandomVariable<String, Boolean> smoker = RandomVariable.builder()
                .id("Smoker")
                .events(true, false)
                .build();

        RandomVariable<String, Boolean> cancer = RandomVariable.builder()
                .id("Cancer")
                .events(true, false)
                .build();

        RandomVariable<String, Boolean> xray = RandomVariable.builder()
                .id("XRay")
                .events(true, false)
                .build();

        RandomVariable<String, Boolean> dyspnea = RandomVariable.builder()
                .id("Dyspnoea")
                .events(true, false)
                .build();

        CPD pollutionCPD = Assignments.builder()
                .variable(pollution)
                .values(0.9, 0.1)
                .toCPD();

        CPD smokerCPD = Assignments.builder()
                .variable(smoker)
                .values(0.3, 0.7)
                .toCPD();

        CPD cancerCPD = Assignments.builder()
                .variables(cancer, smoker, pollution)
                .values(0.03d, 0.97d, 0.05d, 0.95d, 0.001d, 0.999d, 0.02d, 0.98d)
                .toCPD();

        CPD xrayCPD = Assignments.builder()
                .variables(xray, cancer)
                .values(0.9d, 0.1d, 0.2d, 0.8d)
                .toCPD();

        CPD dyspneaCPD = Assignments.builder()
                .variables(dyspnea, cancer)
                .values(0.65d, 0.35d, 0.3d, 0.7d)
                .toCPD();

        BayesianModel cancerModel = Stream.of(pollutionCPD, smokerCPD, cancerCPD, xrayCPD, dyspneaCPD)
                .collect(FactorCollectors.toBayesianModel());

        Assert.assertNotNull(cancerModel);
    }
}
