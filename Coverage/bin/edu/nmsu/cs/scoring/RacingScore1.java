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
		// method is supposed to determine
		// smallest score from the three
		// integers provided then it
		// subtracts the number from their
		// total sum to therefore get the
		// sum of the largest two numbers

		int s;
		//if (score1 < score2) WRONG
		// if condition had been set up like this
		// this would then make s be set as the
		// bigger one of the two numbers and it would confuse
		// the calculation at the end, subtracting some number
		// that is more than a different one but then less than
		// another one
		if(score1 > score2)
			s = score2;
		else
			s = score1;
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

		if (s1 < 0 || s1 > 50 || s2 < 0 || s2 > 50 || s3 < 0 || s3 > 50)
		{
			System.err.println("Error: scores must be between 0 and 50!");
			return;
		}

		RacingScore1 score = new RacingScore1();
		score.recordScores(s1, s2, s3);
		System.out.println("Overall score: " + score.overallScore());
		return;
	}

} // end class
