import org.junit.Test;
import static org.junit.Assert.*;

public class RacingScore1Test {

    @Test
    public void testOverallScore_allEqual() {
        RacingScore1 score = new RacingScore1();
        score.recordScores(40, 40, 40);
        assertEquals(80, score.overallScore());
    }

    @Test
    public void testOverallScore_score1Lowest() {
        RacingScore1 score = new RacingScore1();
        score.recordScores(10, 30, 40);
        assertEquals(70, score.overallScore());
    }

    @Test
    public void testOverallScore_score2Lowest() {
        RacingScore1 score = new RacingScore1();
        score.recordScores(30, 10, 40);
        assertEquals(70, score.overallScore());
    }

    @Test
    public void testOverallScore_score3Lowest() {
        RacingScore1 score = new RacingScore1();
        score.recordScores(30, 40, 10);
        assertEquals(70, score.overallScore());
    }

    @Test
    public void testOverallScore_score1AndScore2Lowest() {
        RacingScore1 score = new RacingScore1();
        score.recordScores(10, 20, 40);
        assertEquals(60, score.overallScore());
    }

    @Test
    public void testOverallScore_score1AndScore3Lowest() {
        RacingScore1 score = new RacingScore1();
        score.recordScores(10, 40, 20);
        assertEquals(60, score.overallScore());
    }

    @Test
    public void testOverallScore_score2AndScore3Lowest() {
        RacingScore1 score = new RacingScore1();
        score.recordScores(40, 10, 20);
        assertEquals(60, score.overallScore());
    }

    try {
      s1 = Integer.parseInt(args[0]);
       System.out.println("Score 1: " + s1);
      s2 = Integer.parseInt(args[1]);
      System.out.println("Score 2: " + s2);
       s3 = Integer.parseInt(args[2]);
       System.out.println("Score 3: " + s3);
    }
    catch (Exception e) {
       System.err.println("Error: arguments must be integers!");
       return;
    }
}
