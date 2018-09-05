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

    private static final RandomVariable<StudentNetwork, Difficulty> DIFFICULTY_RANDOM_VARIABLE;
    private static final RandomVariable<StudentNetwork, Intelligence> INTELLIGENCE_RANDOM_VARIABLE;
    private static final RandomVariable<StudentNetwork, Grade> GRADE_RANDOM_VARIABLE;
    private static final RandomVariable<StudentNetwork, SATScore> SAT_SCORE_RANDOM_VARIABLE;
    private static final RandomVariable<StudentNetwork, Letter> LETTER_RANDOM_VARIABLE;

    private static final ProbTable STUDENT_TABLE;

    static {
        DIFFICULTY_RANDOM_VARIABLE = RandomVariable.label(StudentNetwork.DIFFICULTY)
            .events(Difficulty.values())
            .build();

        INTELLIGENCE_RANDOM_VARIABLE = RandomVariable.label(StudentNetwork.INTELLIGENCE)
            .events(Intelligence.values())
            .build();

        GRADE_RANDOM_VARIABLE = RandomVariable.label(StudentNetwork.GRADE)
            .events(Grade.values())
            .build();

        SAT_SCORE_RANDOM_VARIABLE = RandomVariable.label(StudentNetwork.SAT)
            .events(SATScore.values())
            .build();

        LETTER_RANDOM_VARIABLE = RandomVariable.label(StudentNetwork.LETTER)
            .events(Letter.values())
            .build();

        ProbTable difficultyTable = ProbTable.builder()
            .variable(DIFFICULTY_RANDOM_VARIABLE)
            .value(0.6d)
            .value(0.4d)
            .build();

        ProbTable intelligenceTable = ProbTable.builder()
            .variable(INTELLIGENCE_RANDOM_VARIABLE)
            .value(0.7d)
            .value(0.3d)
            .build();

        ProbTable gradeTable = ProbTable.builder()
            .variables(GRADE_RANDOM_VARIABLE, DIFFICULTY_RANDOM_VARIABLE, INTELLIGENCE_RANDOM_VARIABLE)
            .values(0.3d, 0.4d, 0.3d)
            .values(0.05d, 0.25d, 0.7d)
            .values(0.9d, 0.08d, 0.02d)
            .values(0.5d, 0.3d, 0.2d)
            .build();

        ProbTable satTable = ProbTable.builder()
            .variables(SAT_SCORE_RANDOM_VARIABLE, INTELLIGENCE_RANDOM_VARIABLE)
            .values(0.95d, 0.05d)
            .values(0.2d, 0.8d)
            .build();

        ProbTable letterTable = ProbTable.builder()
            .variables(LETTER_RANDOM_VARIABLE, GRADE_RANDOM_VARIABLE)
            .values(0.1d, 0.9d)
            .values(0.4d, 0.6d)
            .values(0.99d, 0.01d)
            .build();

        STUDENT_TABLE = difficultyTable.stream().concat(
            intelligenceTable.stream(),
            gradeTable.stream(),
            satTable.stream(),
            letterTable.stream()).product().collect(AssignmentCollectors.toProbTable());
    }

    private double eventProbability(final MultiVarAssignment event, final MultiVarAssignment evidences) {
        return STUDENT_TABLE.stream()
            .evidence(evidences)
            .norm()
            .filter(assignment -> assignment.varAssignments().contains(event))
            .mapToDouble(Assignment::value)
            .sum();
    }

    private double eventProbability(final MultiVarAssignment event) {
        return STUDENT_TABLE.stream()
            .norm()
            .filter(assignment -> assignment.varAssignments().contains(event))
            .mapToDouble(Assignment::value)
            .sum();
    }

    @Test
    public void testStrongLetter() {
        Assert.assertEquals(0.502d, eventProbability(LETTER_RANDOM_VARIABLE.set(Letter.STRONG)), 0.001d);
    }

    @Test
    @SuppressWarnings({"checkstyle:magicnumber", "checkstyle:designforextension"})
    public void testStrongLetterGivenLowIntelligence() {
        Assert.assertEquals(0.389, eventProbability(
            LETTER_RANDOM_VARIABLE.set(Letter.STRONG),
            INTELLIGENCE_RANDOM_VARIABLE.set(Intelligence.LOW)),
            0.001d);
    }

    @Test
    @SuppressWarnings({"checkstyle:magicnumber", "checkstyle:designforextension"})
    public void testStrongLetterGivenLowIntelligenceAndEasyDifficulty() {
        Assert.assertEquals(0.513d, eventProbability(LETTER_RANDOM_VARIABLE.set(Letter.STRONG),
            INTELLIGENCE_RANDOM_VARIABLE.set(Intelligence.LOW).and(DIFFICULTY_RANDOM_VARIABLE.set(Difficulty.EASY))),
            0.001d);
    }

    @Test
    @SuppressWarnings({"checkstyle:magicnumber", "checkstyle:designforextension"})
    public void testHighIntelligenceGivenCGrade() {
        Assert.assertEquals(0.078d, eventProbability(INTELLIGENCE_RANDOM_VARIABLE.set(Intelligence.HIGH),
            GRADE_RANDOM_VARIABLE.set(Grade.C)), 0.001d);
    }

    @Test
    @SuppressWarnings({"checkstyle:magicnumber", "checkstyle:designforextension"})
    public void testHighIntelligenceGivenWeakLetter() {
        Assert.assertEquals(0.14d, eventProbability(INTELLIGENCE_RANDOM_VARIABLE.set(Intelligence.HIGH),
            LETTER_RANDOM_VARIABLE.set(Letter.WEAK)), 0.001d);
    }

    @Test
    @SuppressWarnings({"checkstyle:magicnumber", "checkstyle:designforextension"})
    public void testHighIntelligenceGivenWeakLetterAndGradeC() {
        Assert.assertEquals(0.079d, eventProbability(INTELLIGENCE_RANDOM_VARIABLE.set(Intelligence.HIGH),
            GRADE_RANDOM_VARIABLE.set(Grade.C).and(LETTER_RANDOM_VARIABLE.set(Letter.WEAK))), 0.001d);
    }

    @Test
    @SuppressWarnings({"checkstyle:magicnumber", "checkstyle:designforextension"})
    public void testHighIntelligenceGivenSatScoreIsHighAndGradeC() {
        Assert.assertEquals(0.578d, eventProbability(INTELLIGENCE_RANDOM_VARIABLE.set(Intelligence.HIGH),
            GRADE_RANDOM_VARIABLE.set(Grade.C).and(SAT_SCORE_RANDOM_VARIABLE.set(SATScore.HIGH))), 0.001d);
    }

    @Test
    @SuppressWarnings({"checkstyle:magicnumber", "checkstyle:designforextension"})
    public void testHighIntelligenceGivenGradeCAndDifficultClass() {
        Assert.assertEquals(0.11d, eventProbability(INTELLIGENCE_RANDOM_VARIABLE.set(Intelligence.HIGH),
            GRADE_RANDOM_VARIABLE.set(Grade.C).and(DIFFICULTY_RANDOM_VARIABLE.set(Difficulty.HARD))), 0.001d);
    }

    @Test
    @SuppressWarnings({"checkstyle:magicnumber", "checkstyle:designforextension"})
    public void testHighIntelligenceGivenGradeBAndDifficultClass() {
        Assert.assertEquals(0.34d, eventProbability(INTELLIGENCE_RANDOM_VARIABLE.set(Intelligence.HIGH),
            GRADE_RANDOM_VARIABLE.set(Grade.B).and(DIFFICULTY_RANDOM_VARIABLE.set(Difficulty.HARD))), 0.001d);
    }

    @Test
    @SuppressWarnings({"checkstyle:magicnumber", "checkstyle:designforextension"})
    public void testHighIntelligenceAndWeakLetterGivenGradeBAndDifficultClass() {
        Assert.assertEquals(0.135d, eventProbability(
            INTELLIGENCE_RANDOM_VARIABLE.set(Intelligence.HIGH).and(LETTER_RANDOM_VARIABLE.set(Letter.WEAK)),
            GRADE_RANDOM_VARIABLE.set(Grade.B).and(DIFFICULTY_RANDOM_VARIABLE.set(Difficulty.HARD))), 0.001d);
    }
}
