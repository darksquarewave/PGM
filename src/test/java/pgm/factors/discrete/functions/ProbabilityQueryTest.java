package pgm.factors.discrete.functions;

import org.junit.Assert;
import org.junit.Test;
import pgm.core.discrete.Assignments;
import pgm.core.discrete.RandomVariable;
import pgm.factors.discrete.Factor;
import pgm.factors.discrete.functions.queries.ProbabilityQuery;

public final class ProbabilityQueryTest {

    enum StudentNetwork {
        DIFFICULTY, INTELLIGENCE, GRADE, SAT, LETTER
    }

    enum Difficulty {
        EASY, HARD
    }

    enum Intelligence {
        LOW, HIGH
    }

    enum SATScore {
        LOW, HIGH
    }

    enum Grade {
        A, B, C
    }

    enum Letter {
        WEAK, STRONG
    }

    private final RandomVariable<StudentNetwork, Difficulty> difficulty;
    private final RandomVariable<StudentNetwork, Intelligence> intelligence;
    private final RandomVariable<StudentNetwork, Grade> grade;
    private final RandomVariable<StudentNetwork, SATScore> sat;
    private final RandomVariable<StudentNetwork, Letter> letter;

    private final Factor studentDistribution;

    @SuppressWarnings({"checkstyle:magicnumber"})
    public ProbabilityQueryTest() {
        difficulty = RandomVariable.builder()
                .id(StudentNetwork.DIFFICULTY)
                .events(Difficulty.values())
                .build();

        intelligence = RandomVariable.builder()
                .id(StudentNetwork.INTELLIGENCE)
                .events(Intelligence.values())
                .build();

        grade = RandomVariable.builder()
                .id(StudentNetwork.GRADE)
                .events(Grade.values())
                .build();

        sat = RandomVariable.builder()
                .id(StudentNetwork.SAT)
                .events(SATScore.values())
                .build();

        letter = RandomVariable.builder()
                .id(StudentNetwork.LETTER)
                .events(Letter.values())
                .build();

        Factor difficultyFactor = Assignments.builder()
                .variable(difficulty)
                .values(0.6d, 0.4d)
                .toFactor();

        Factor intelligenceFactor = Assignments.builder()
                .variable(intelligence)
                .values(0.7d, 0.3d)
                .toFactor();

        Factor gradeFactor = Assignments.builder()
                .variables(grade, difficulty, intelligence)
                .values(0.3d, 0.4d, 0.3d)
                .values(0.05d, 0.25d, 0.7d)
                .values(0.9d, 0.08d, 0.02d)
                .values(0.5d, 0.3d, 0.2d)
                .toFactor();

        Factor satFactor = Assignments.builder()
                .variables(sat, intelligence)
                .values(0.95d, 0.05d, 0.2d, 0.8d)
                .toFactor();

        Factor letterFactor = Assignments.builder()
                .variables(letter, grade)
                .values(0.1d, 0.9d, 0.4d, 0.6d, 0.99d, 0.01d)
                .toFactor();

        studentDistribution = Factor.product(difficultyFactor,
                intelligenceFactor,
                gradeFactor,
                satFactor,
                letterFactor);
    }

    @Test
    @SuppressWarnings({"checkstyle:magicnumber", "checkstyle:designforextension"})
    public void testStrongLetter() {
        Assert.assertEquals(0.502d, studentDistribution.query(ProbabilityQuery.builder()
                .assignment(letter.set(Letter.STRONG))
                .build()), 0.001d);
    }

    @Test
    @SuppressWarnings({"checkstyle:magicnumber", "checkstyle:designforextension"})
    public void testStrongLetterGivenLowIntelligence() {
        Assert.assertEquals(0.389d, studentDistribution.query(ProbabilityQuery.builder()
                .assignment(letter.set(Letter.STRONG))
                .evidence(intelligence.set(Intelligence.LOW))
                .build()), 0.001d);
    }

    @Test
    @SuppressWarnings({"checkstyle:magicnumber", "checkstyle:designforextension"})
    public void testStrongLetterGivenLowIntelligenceAndEasyDifficulty() {
        Assert.assertEquals(0.513d, studentDistribution.query(ProbabilityQuery.builder()
                .assignment(letter.set(Letter.STRONG))
                .evidence(intelligence.set(Intelligence.LOW))
                .evidence(difficulty.set(Difficulty.EASY))
                .build()), 0.001d);
    }

    @Test
    @SuppressWarnings({"checkstyle:magicnumber", "checkstyle:designforextension"})
    public void testHighIntelligenceGivenCGrade() {
        Assert.assertEquals(0.078d, studentDistribution.query(ProbabilityQuery.builder()
                .assignment(intelligence.set(Intelligence.HIGH))
                .evidence(grade.set(Grade.C))
                .build()), 0.001d);
    }

    @Test
    @SuppressWarnings({"checkstyle:magicnumber", "checkstyle:designforextension"})
    public void testHighIntelligenceGivenWeakLetter() {
        Assert.assertEquals(0.14d, studentDistribution.query(ProbabilityQuery.builder()
                .assignment(intelligence.set(Intelligence.HIGH))
                .evidence(letter.set(Letter.WEAK))
                .build()), 0.001d);
    }

    @Test
    @SuppressWarnings({"checkstyle:magicnumber", "checkstyle:designforextension"})
    public void testHighIntelligenceGivenWeakLetterAndGradeC() {
        Assert.assertEquals(0.079d, studentDistribution.query(ProbabilityQuery.builder()
                .assignment(intelligence.set(Intelligence.HIGH))
                .evidence(grade.set(Grade.C))
                .evidence(letter.set(Letter.WEAK))
                .build()), 0.001d);
    }

    @Test
    @SuppressWarnings({"checkstyle:magicnumber", "checkstyle:designforextension"})
    public void testHighIntelligenceGivenSatScoreIsHighAndGradeC() {
        Assert.assertEquals(0.578d, studentDistribution.query(ProbabilityQuery.builder()
                .assignment(intelligence.set(Intelligence.HIGH))
                .evidence(grade.set(Grade.C))
                .evidence(sat.set(SATScore.HIGH))
                .build()), 0.001d);
    }

    @Test
    @SuppressWarnings({"checkstyle:magicnumber", "checkstyle:designforextension"})
    public void testHighIntelligenceGivenGradeCAndDifficultClass() {
        Assert.assertEquals(0.11d, studentDistribution.query(ProbabilityQuery.builder()
                .assignment(intelligence.set(Intelligence.HIGH))
                .evidence(grade.set(Grade.C))
                .evidence(difficulty.set(Difficulty.HARD))
                .build()), 0.001d);
    }

    @Test
    @SuppressWarnings({"checkstyle:magicnumber", "checkstyle:designforextension"})
    public void testHighIntelligenceGivenGradeBAndDifficultClass() {
        Assert.assertEquals(0.34d, studentDistribution.query(ProbabilityQuery.builder()
                .assignment(intelligence.set(Intelligence.HIGH))
                .evidence(grade.set(Grade.B))
                .evidence(difficulty.set(Difficulty.HARD))
                .build()), 0.001d);
    }
}
