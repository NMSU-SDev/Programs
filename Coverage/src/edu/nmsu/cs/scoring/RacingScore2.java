package edu.nmsu.cs.scoring;

/***
 * Olympic Dragon Racing Scoring Class
 *
 * For the Summer Olympics dragon racing event there are three judges, each of which gives a score
 * from 0 to 50 (inclusive), but the lowest score is thrown out and the competitor's overall score
 * is just the sum of the two highest scores. This class supports the recording of the three judge's
 * scores, and the computing of the competitor's overall score.
 * 
 * @author Jon Cook, Ph.D.
 ***/

public class RacingScore2
{

	int	score1;

	int	score2;

	int	score3;

	public RacingScore2()
	{
		score1 = 0; 
		score2 = 0;
		score3 = 0;
	}

	public void recordScores(int s1, int s2, int s3)
	{
		score1 = s1;
		score2 = s2;
		score3 = s3;
	}

	public int overallScore()
	{
		/*in the original program when three arguments supplied are all the same, it sets the score to 198.
		when the first and last arguments are the same, and highest the output would be wrong. The program adds the second argument,
		 the lowest, and the first argument.
        When the first and last arguments are the same, and they are lower than the second argument then the score would be 198. 
		*/
		int s, s1, s2;
		if (score1 < score2 && score1 < score3)
		{
			s1 = score2;
			s2 = score3;
		}
		else if (score2 < score1 && score2 < score3)
		{
			//changing s2 to be score 3 to get correct output
			s1 = score1;
			s2 = score3;
		}
		else if (score3 < score1 && score3 < score2)
		{
			s1 = score1;
			s2 = score2;
		}
		//when it gets here and s1 and s2 are not decided then two numbers are equal
		else if(score1 == score2 && score1 < score3)
		{
			//making s1 = score 1 and s2 = score 2 to get correct output
			s1 = score1;
			s2 = score3;
		}
		else if(score2 == score3 && score2 < score1)
		{
			//making s1 = score 1 and s2 = score 2 to get correct output
			s1 = score1;
			s2 = score3;
		}
		else {
			s1 = score1;
			s2 = score2;
		}
		s = s1 + s2;
		return s;
	}

	public static void main(String args[])
	{
		int s1, s2, s3;
		if (args.length == 0 || args.length != 3)
		{
			System.err.println("Error: must supply three arguments!");
			return;
		}
		try
		{
			s1 = Integer.parseInt(args[0]);
			s2 = Integer.parseInt(args[1]);
			s3 = Integer.parseInt(args[2]);
		}
		catch (Exception e)
		{
			System.err.println("Error: arguments must be integers!");
			return;
		}
		if (s1 < 0 || s1 > 50 || s2 < 0 || s2 > 50 || s3 < 0 || s3 > 50)
		{
			System.err.println("Error: scores must be between 0 and 50!");
			return;
		}
		RacingScore2 score = new RacingScore2();
		score.recordScores(s1, s2, s3);
		System.out.println("Overall score: " + score.overallScore());
		return;
	}

} // end class
