package edu.nmsu.cs.scoring;

/***
 * Olympic Dragon Racing Scoring Class
 *
 * For the Summer Olympics dragon racing event there are three judges, each of which gives a score
 * from 0 to 50 (inclusive), but the lowest score is thrown out and the competitor's overall score
 * is just the sum of the two highest scores. This class supports the recording of the three judge's
 * scores, and the computing of the competitor's overall score.
 *
 ***/

/**
 * Brock Middleton - 5/1/23
 * 
 * 	Found Issue: in case - (score3 > score1) - the lowest score is added to overall.
 * 		Fixed by setting var 's' to s1 rather than s2 (line 51) after comparing.
 * 
 * 	Found Issue: in case - (score > 50) - cutoff value of '50' is not enforced. 
 * 		Functional error - invalid input - NOT FIXEDout
 * 
 * 	Found Issue: in case - (score < 0) - scores with negative value are still considered. 	
 * 		Functional error - invalid input - NOT FIXED
 */

public class RacingScore1
{

	int	score1;

	int	score2;

	int	score3;

	public RacingScore1()
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
		// Store lowest score 
		int s;
		if (score1 < score2)
			// s1 < s2, store s1 rather than s2
			s = score1;
		else
			s = score2;
		if (s > score3)
			s = score3;
		s = (score1 + score2 + score3) - s;
		return s;
	}

	public static void main(String args[])
	{
		int s1, s2, s3;
		if (args.length != 3)
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
		RacingScore1 score = new RacingScore1();
		score.recordScores(s1, s2, s3);
		System.out.println("Overall score: " + score.overallScore());
		return;
	}

} // end class
