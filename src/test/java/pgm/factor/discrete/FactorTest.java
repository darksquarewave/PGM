package pgm.factor.discrete;

import org.junit.Assert;
import org.junit.Test;
import pgm.factor.discrete.factor.AssignmentIndex;
import pgm.factor.discrete.factor.Assignments;
import pgm.factor.discrete.factor.Factor;
import pgm.factor.discrete.factor.transformers.ObserveEvidenceTransformer;

import java.util.Arrays;
import java.util.Collections;
import java.util.stream.Stream;

public class FactorTest {

    @Test
    @SuppressWarnings({"checkstyle:magicnumber", "checkstyle:designforextension"})
    public void testConstruction() {
        RandomVariable<String> a = new RandomVariable<>("a", 2);
        RandomVariable<String> b = new RandomVariable<>("b", 2);

        Factor factor = Assignments.of(RandomVariables.of(a, b), Arrays.asList(0.2d, 0.8d, 0.5d, 0.5d)).stream()
                .collect(Factor.collector());

        Assert.assertNotNull(factor);
    }

    @Test
    @SuppressWarnings({"checkstyle:magicnumber", "checkstyle:designforextension"})
    public void testGetAssignment() {
        RandomVariable<Integer> a = new RandomVariable<>(1, 2);
        RandomVariable<Integer> b = new RandomVariable<>(2, 3);
        RandomVariable<Integer> c = new RandomVariable<>(3, 2);

        Factor factor = Assignments.of(RandomVariables.of(a, b, c),
                Arrays.asList(0.2d, 0.8d, 0.1d, 0.9d, 0.4d, 0.6d, 0.3d, 0.7d, 0.9d, 0.1d, 0.2d, 0.8d)).stream()
                .collect(Factor.collector());

        final double delta = 1e-9;

        Assert.assertEquals(0.2d, factor.assignment(1, 1, 1), delta);
        Assert.assertEquals(0.1d, factor.assignment(1, 2, 1), delta);
        Assert.assertEquals(0.4d, factor.assignment(1, 3, 1), delta);
        Assert.assertEquals(0.8d, factor.assignment(2, 1, 1), delta);
        Assert.assertEquals(0.9d, factor.assignment(2, 2, 1), delta);
        Assert.assertEquals(0.6d, factor.assignment(2, 3, 1), delta);
        Assert.assertEquals(0.3d, factor.assignment(1, 1, 2), delta);
        Assert.assertEquals(0.9d, factor.assignment(1, 2, 2), delta);
        Assert.assertEquals(0.2d, factor.assignment(1, 3, 2), delta);
        Assert.assertEquals(0.7d, factor.assignment(2, 1, 2), delta);
        Assert.assertEquals(0.1d, factor.assignment(2, 2, 2), delta);
        Assert.assertEquals(0.8d, factor.assignment(2, 3, 2), delta);
    }

    @Test
    @SuppressWarnings({"checkstyle:magicnumber", "checkstyle:designforextension"})
    public void testMultiply() {
        RandomVariable<Integer> a = new RandomVariable<>(1, 2);
        RandomVariable<Integer> b = new RandomVariable<>(2, 2);

        Factor factorA = Assignments.of(RandomVariables.of(a),
                Arrays.asList(0.11d, 0.89d)).stream()
                .collect(Factor.collector());

        Factor factorB = Assignments.of(RandomVariables.of(b, a),
                Arrays.asList(0.59d, 0.41d, 0.22d, 0.78d)).stream()
                .collect(Factor.collector());

        Factor factorC = Assignments.of(RandomVariables.of(a, b),
                Arrays.asList(0.0649d, 0.1958d, 0.0451d, 0.6942d)).stream()
                .collect(Factor.collector());

        Assert.assertEquals(factorC, factorA.multiply(factorB));
    }

    @Test
    @SuppressWarnings({"checkstyle:magicnumber", "checkstyle:designforextension"})
    public void testMultiply2() {
        RandomVariable<Integer> a = new RandomVariable<>(1, 3);
        RandomVariable<Integer> b = new RandomVariable<>(2, 2);
        RandomVariable<Integer> c = new RandomVariable<>(3, 2);

        Factor x = Assignments.of(RandomVariables.of(b, a),
                Arrays.asList(0.5d, 0.8d, 0.1d, 0d, 0.3d, 0.9d))
                .stream().collect(Factor.collector());

        Factor y = Assignments.of(RandomVariables.of(c, b),
                Arrays.asList(0.5d, 0.7d, 0.1d, 0.2d))
                .stream().collect(Factor.collector());

        Factor z = Assignments.of(RandomVariables.of(a, b, c),
                Arrays.asList(0.25d, 0.05d, 0.15d, 0.08d, 0d, 0.09d, 0.35d, 0.07d, 0.21d, 0.16d, 0d, 0.18d))
                .stream().collect(Factor.collector());

        Assert.assertEquals(z, x.multiply(y));
    }

    @Test
    @SuppressWarnings({"checkstyle:magicnumber", "checkstyle:designforextension"})
    public void testMultiply3() {
        RandomVariable<Integer> a = new RandomVariable<>(1, 3);
        RandomVariable<Integer> b = new RandomVariable<>(2, 2);
        RandomVariable<Integer> c = new RandomVariable<>(3, 2);
        RandomVariable<Integer> d = new RandomVariable<>(4, 2);
        RandomVariable<Integer> e = new RandomVariable<>(5, 3);
        RandomVariable<Integer> f = new RandomVariable<>(6, 2);
        RandomVariable<Integer> g = new RandomVariable<>(7, 2);

        Factor x = Assignments.of(RandomVariables.of(b, a), Arrays.asList(0.5d, 0.8d, 0.1d, 0d, 0.3d, 0.9d))
                .stream().collect(Factor.collector());

        Factor y = Assignments.of(RandomVariables.of(d, c), Arrays.asList(0.5d, 0.7d, 0.1d, 0.2d))
                .stream().collect(Factor.collector());

        Factor z = Assignments.of(RandomVariables.of(e, f, g),
                Arrays.asList(0.25d, 0.05d, 0.15d, 0.08d, 0d, 0.09d, 0.35d, 0.07d, 0.21d, 0.16d, 0d, 0.18d))
                .stream().collect(Factor.collector());

        Factor result = Assignments.of(RandomVariables.of(a, b, c, d, e, f, g),
                Arrays.asList(0.06250, 0.01250, 0.03750, 0.10000, 0.00000, 0.11250, 0.01250, 0.00250, 0.00750, 0.02000,
                        0.00000, 0.02250, 0.08750, 0.01750, 0.05250, 0.14000, 0.00000, 0.15750, 0.02500, 0.00500,
                        0.01500, 0.04000, 0.00000, 0.04500, 0.01250, 0.00250, 0.00750, 0.02000, 0.00000, 0.02250,
                        0.00250, 0.00050, 0.00150, 0.00400, 0.00000, 0.00450, 0.01750, 0.00350, 0.01050, 0.02800,
                        0.00000, 0.03150, 0.00500, 0.00100, 0.00300, 0.00800, 0.00000, 0.00900, 0.03750, 0.00750,
                        0.02250, 0.06000, 0.00000, 0.06750, 0.00750, 0.00150, 0.00450, 0.01200, 0.00000, 0.01350,
                        0.05250, 0.01050, 0.03150, 0.08400, 0.00000, 0.09450, 0.01500, 0.00300, 0.00900, 0.02400,
                        0.00000, 0.02700, 0.02000, 0.00400, 0.01200, 0.03200, 0.00000, 0.03600, 0.00400, 0.00080,
                        0.00240, 0.00640, 0.00000, 0.00720, 0.02800, 0.00560, 0.01680, 0.04480, 0.00000, 0.05040,
                        0.00800, 0.00160, 0.00480, 0.01280, 0.00000, 0.01440, 0.00000, 0.00000, 0.00000, 0.00000,
                        0.00000, 0.00000, 0.00000, 0.00000, 0.00000, 0.00000, 0.00000, 0.00000, 0.00000, 0.00000,
                        0.00000, 0.00000, 0.00000, 0.00000, 0.00000, 0.00000, 0.00000, 0.00000, 0.00000, 0.00000,
                        0.02250, 0.00450, 0.01350, 0.03600, 0.00000, 0.04050, 0.00450, 0.00090, 0.00270, 0.00720,
                        0.00000, 0.00810, 0.03150, 0.00630, 0.01890, 0.05040, 0.00000, 0.05670, 0.00900, 0.00180,
                        0.00540, 0.01440, 0.00000, 0.01620, 0.08750, 0.01750, 0.05250, 0.14000, 0.00000, 0.15750,
                        0.01750, 0.00350, 0.01050, 0.02800, 0.00000, 0.03150, 0.12250, 0.02450, 0.07350, 0.19600,
                        0.00000, 0.22050, 0.03500, 0.00700, 0.02100, 0.05600, 0.00000, 0.06300, 0.01750, 0.00350,
                        0.01050, 0.02800, 0.00000, 0.03150, 0.00350, 0.00070, 0.00210, 0.00560, 0.00000, 0.00630,
                        0.02450, 0.00490, 0.01470, 0.03920, 0.00000, 0.04410, 0.00700, 0.00140, 0.00420, 0.01120,
                        0.00000, 0.01260, 0.05250, 0.01050, 0.03150, 0.08400, 0.00000, 0.09450, 0.01050, 0.00210,
                        0.00630, 0.01680, 0.00000, 0.01890, 0.07350, 0.01470, 0.04410, 0.11760, 0.00000, 0.13230,
                        0.02100, 0.00420, 0.01260, 0.03360, 0.00000, 0.03780, 0.04000, 0.00800, 0.02400, 0.06400,
                        0.00000, 0.07200, 0.00800, 0.00160, 0.00480, 0.01280, 0.00000, 0.01440, 0.05600, 0.01120,
                        0.03360, 0.08960, 0.00000, 0.10080, 0.01600, 0.00320, 0.00960, 0.02560, 0.00000, 0.02880,
                        0.00000, 0.00000, 0.00000, 0.00000, 0.00000, 0.00000, 0.00000, 0.00000, 0.00000, 0.00000,
                        0.00000, 0.00000, 0.00000, 0.00000, 0.00000, 0.00000, 0.00000, 0.00000, 0.00000, 0.00000,
                        0.00000, 0.00000, 0.00000, 0.00000, 0.04500, 0.00900, 0.02700, 0.07200, 0.00000, 0.08100,
                        0.00900, 0.00180, 0.00540, 0.01440, 0.00000, 0.01620, 0.06300, 0.01260, 0.03780, 0.10080,
                        0.00000, 0.11340, 0.01800, 0.00360, 0.01080, 0.02880, 0.00000, 0.03240))
                .stream().collect(Factor.collector());

        Assert.assertEquals(result, Stream.of(x, y).reduce(z, Factor::multiply));
    }

    @Test
    @SuppressWarnings({"checkstyle:magicnumber", "checkstyle:designforextension"})
    public void testMarginalization() {
        RandomVariable<Integer> a = new RandomVariable<>(1, 2);
        RandomVariable<Integer> b = new RandomVariable<>(2, 2);

        Factor factorA = Assignments.of(RandomVariables.of(b, a),
                Arrays.asList(0.59d, 0.41d, 0.22d, 0.78d)).stream()
                .collect(Factor.collector());

        Factor factorB = Assignments.of(RandomVariables.of(a),
                Arrays.asList(1d, 1d)).stream()
                .collect(Factor.collector());

        Assert.assertEquals(factorB, factorA.marginalize(RandomVariables.of(b)));
    }

    @Test
    @SuppressWarnings({"checkstyle:magicnumber", "checkstyle:designforextension"})
    public void testMarginalization2() {
        RandomVariable<Integer> a = new RandomVariable<>(1, 3);
        RandomVariable<Integer> b = new RandomVariable<>(2, 2);
        RandomVariable<Integer> c = new RandomVariable<>(3, 2);

        Factor x = Assignments.of(RandomVariables.of(a, c),
                Arrays.asList(0.33d, 0.05d, 0.24d, 0.51d, 0.07d, 0.39d))
                .stream().collect(Factor.collector());

        Factor z = Assignments.of(RandomVariables.of(c, b, a),
                Arrays.asList(0.25d, 0.35d, 0.08d, 0.16d, 0.05d, 0.07d, 0d, 0d, 0.15d, 0.21d, 0.09d, 0.18d))
                .stream().collect(Factor.collector());

        Assert.assertEquals(x, z.marginalize(RandomVariables.of(b)));
    }

    @Test
    @SuppressWarnings({"checkstyle:magicnumber", "checkstyle:designforextension"})
    public void testObserveEvidence() {
        RandomVariable<Integer> a = new RandomVariable<>(1, 2);
        RandomVariable<Integer> b = new RandomVariable<>(2, 2);

        Factor factorA = Assignments.of(RandomVariables.of(b, a),
                Arrays.asList(0.59d, 0.41d, 0.22d, 0.78d)).stream()
                .collect(Factor.collector());

        Factor factorB = Assignments.of(RandomVariables.of(b, a),
                Arrays.asList(0.59d, 0d, 0.22d, 0d)).stream()
                .collect(Factor.collector());

        AssignmentIndex evidence = new AssignmentIndex(RandomVariables.of(b), Collections.singletonList(1));

        Assert.assertEquals(factorB, factorA.transform(new ObserveEvidenceTransformer(evidence)));
    }
}
