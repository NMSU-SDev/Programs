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
 * 
 ***/

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

	//No data validation is completed. Thus, the program will take in invalid inputs.
	//This is for invalid inputs and was not fixed.
	public void recordScores(int s1, int s2, int s3)
	{
		score1 = s1;
		score2 = s2;
		score3 = s3;
	}
	
	/*
	 *s needs to be the smallest of the scores which is subtracted off later for the final score
	 *previously, s was not always the smallest score, so I switched s = score2 to be s = score1
	 *in the first if block and s = score1 to s = score2 in the first else block. This ensures
	 *s is the smallest value amongs all the scores.
	 */
	public int overallScore()
	{
		int s;
		if (score1 < score2)
			s = score1;
		else
			s = score2;
		if (s > score3)
			s = score3;
		s = (score1 + score2 + score3) - s;
		return s;
	}

	//No data validation is completed. Thus, the program will take in invalid inputs.
	//This is for invalid inputs and was not fixed.
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
