package io.pgm.discrete.core;

import org.junit.Assert;
import org.junit.Test;

@SuppressWarnings({"checkstyle:magicnumber", "checkstyle:designforextension"})
public class ProbabilityQueryTest {

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

    private final AssignmentStream studentDistribution;

    @SuppressWarnings({"checkstyle:magicnumber"})
    public ProbabilityQueryTest() {
        difficulty = RandomVariable.id(StudentNetwork.DIFFICULTY)
            .events(Difficulty.values())
            .build();

        intelligence = RandomVariable.id(StudentNetwork.INTELLIGENCE)
            .events(Intelligence.values())
            .build();

        grade = RandomVariable.id(StudentNetwork.GRADE)
            .events(Grade.values())
            .build();

        sat = RandomVariable.id(StudentNetwork.SAT)
            .events(SATScore.values())
            .build();

        letter = RandomVariable.id(StudentNetwork.LETTER)
            .events(Letter.values())
            .build();

        AssignmentStream difficultyAssignments = AssignmentStream.builder()
            .variable(difficulty)
            .value(0.6d)
            .value(0.4d)
            .build();

        AssignmentStream intelligenceAssignments = AssignmentStream.builder()
            .variable(intelligence)
            .value(0.7d)
            .value(0.3d)
            .build();

        AssignmentStream gradeAssignments = AssignmentStream.builder()
            .variables(grade, difficulty, intelligence)
            .values(0.3d, 0.4d, 0.3d)
            .values(0.05d, 0.25d, 0.7d)
            .values(0.9d, 0.08d, 0.02d)
            .values(0.5d, 0.3d, 0.2d)
            .build();

        AssignmentStream satAssignments = AssignmentStream.builder()
            .variables(sat, intelligence)
            .values(0.95d, 0.05d)
            .values(0.2d, 0.8d)
            .build();

        AssignmentStream letterAssignments = AssignmentStream.builder()
            .variables(letter, grade)
            .values(0.1d, 0.9d)
            .values(0.4d, 0.6d)
            .values(0.99d, 0.01d)
            .build();

        studentDistribution = difficultyAssignments.concat(
            intelligenceAssignments,
            gradeAssignments,
            satAssignments,
            letterAssignments).product();
    }

    private double eventProbability(final MultiVarAssignment event, final MultiVarAssignment evidences) {
        return studentDistribution.evidence(evidences)
            .norm()
            .filter(assignment -> assignment.varAssignments().contains(event))
            .mapToDouble(Assignment::value)
            .sum();
    }

    private double eventProbability(final MultiVarAssignment event) {
        return studentDistribution
            .norm()
            .filter(assignment -> assignment.varAssignments().contains(event))
            .mapToDouble(Assignment::value)
            .sum();
    }

    @Test
    public void testStrongLetter() {
        Assert.assertEquals(0.502d, eventProbability(letter.set(Letter.STRONG)), 0.001d);
    }

    @Test
    @SuppressWarnings({"checkstyle:magicnumber", "checkstyle:designforextension"})
    public void testStrongLetterGivenLowIntelligence() {
        Assert.assertEquals(0.389, eventProbability(
            letter.set(Letter.STRONG),
            intelligence.set(Intelligence.LOW)),
            0.001d);
    }

    @Test
    @SuppressWarnings({"checkstyle:magicnumber", "checkstyle:designforextension"})
    public void testStrongLetterGivenLowIntelligenceAndEasyDifficulty() {
        Assert.assertEquals(0.513d, eventProbability(letter.set(Letter.STRONG),
            intelligence.set(Intelligence.LOW).and(difficulty.set(Difficulty.EASY))),
            0.001d);
    }

    @Test
    @SuppressWarnings({"checkstyle:magicnumber", "checkstyle:designforextension"})
    public void testHighIntelligenceGivenCGrade() {
        Assert.assertEquals(0.078d, eventProbability(intelligence.set(Intelligence.HIGH),
            grade.set(Grade.C)), 0.001d);
    }

    @Test
    @SuppressWarnings({"checkstyle:magicnumber", "checkstyle:designforextension"})
    public void testHighIntelligenceGivenWeakLetter() {
        Assert.assertEquals(0.14d, eventProbability(intelligence.set(Intelligence.HIGH),
            letter.set(Letter.WEAK)), 0.001d);
    }

    @Test
    @SuppressWarnings({"checkstyle:magicnumber", "checkstyle:designforextension"})
    public void testHighIntelligenceGivenWeakLetterAndGradeC() {
        Assert.assertEquals(0.079d, eventProbability(intelligence.set(Intelligence.HIGH),
            grade.set(Grade.C).and(letter.set(Letter.WEAK))), 0.001d);
    }

    @Test
    @SuppressWarnings({"checkstyle:magicnumber", "checkstyle:designforextension"})
    public void testHighIntelligenceGivenSatScoreIsHighAndGradeC() {
        Assert.assertEquals(0.578d, eventProbability(intelligence.set(Intelligence.HIGH),
            grade.set(Grade.C).and(sat.set(SATScore.HIGH))), 0.001d);
    }

    @Test
    @SuppressWarnings({"checkstyle:magicnumber", "checkstyle:designforextension"})
    public void testHighIntelligenceGivenGradeCAndDifficultClass() {
        Assert.assertEquals(0.11d, eventProbability(intelligence.set(Intelligence.HIGH),
            grade.set(Grade.C).and(difficulty.set(Difficulty.HARD))), 0.001d);
    }

    @Test
    @SuppressWarnings({"checkstyle:magicnumber", "checkstyle:designforextension"})
    public void testHighIntelligenceGivenGradeBAndDifficultClass() {
        Assert.assertEquals(0.34d, eventProbability(intelligence.set(Intelligence.HIGH),
            grade.set(Grade.B).and(difficulty.set(Difficulty.HARD))), 0.001d);
    }

    @Test
    @SuppressWarnings({"checkstyle:magicnumber", "checkstyle:designforextension"})
    public void testHighIntelligenceAndWeakLetterGivenGradeBAndDifficultClass() {
        Assert.assertEquals(0.135d, eventProbability(
            intelligence.set(Intelligence.HIGH).and(letter.set(Letter.WEAK)),
            grade.set(Grade.B).and(difficulty.set(Difficulty.HARD))), 0.001d);
    }
}
