import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;
import edu.nmsu.cs.scoring.RacingScore2;

public class RacingScore2Test {

  @Test
  public void testOverallScoreWithDistinctScores() {
    RacingScore2 score = new RacingScore2();
    score.recordScores(10, 20, 30);
    assertEquals(50, score.overallScore());
  }

  @Test
  public void testOverallScoreWithTwoEqualScores() {
    RacingScore2 score = new RacingScore2();
    score.recordScores(10, 20, 20);
    assertEquals(40, score.overallScore());
  }

  @Test
  public void testOverallScoreWithThreeEqualScores() {
    RacingScore2 score = new RacingScore2();
    score.recordScores(20, 20, 20);
    assertEquals(40, score.overallScore());
  }

  @Test
  public void testOverallScoreWithOneLowestScore() {
    RacingScore2 score = new RacingScore2();
    score.recordScores(30, 40, 0);
    assertEquals(70, score.overallScore());
  }

  @Test
  public void testOverallScoreWithTwoLowestScores() {
    RacingScore2 score = new RacingScore2();
    score.recordScores(40, 0, 0);
    assertEquals(40, score.overallScore());
  }
  
  try {
    int s1 = Integer.parseInt(args[0]);
    int s2 = Integer.parseInt(args[1]);
    int s3 = Integer.parseInt(args[2]);
    System.out.println("Recorded scores: " + s1 + ", " + s2 + ", " + s3);
  } 
  catch (NumberFormatException e) {
    System.err.println("Error: arguments must be integers!");
  }
}