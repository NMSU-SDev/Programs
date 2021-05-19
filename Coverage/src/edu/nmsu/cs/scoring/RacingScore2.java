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
		//removed s s1 s2 to just return sum of two largest scores
		
		//if, else if, else now filter smallest score out
		if (score1 <= score2 && score1 <= score3)
			return score2 + score3;
		
		else if (score2 < score1 && score2 < score3)
			return score1 + score3; 
		
		else //if (score3 < score1 && score3 < score2)
			return score1 + score2;

	}

	public static void main(String args[])
	{
		int s1, s2, s3;
		final int MAX_S = 50, MIN_S = 0; //to make code readable :/
		
		if (args.length != 3) //deleted args==null since args str array will never be 'null'
		{ //when no args provided, args.length == 0, but no reason to check == 0 since != 3 exist 
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
		if (s1 < MIN_S || s1 > MAX_S || s2 < MIN_S || s2 > MAX_S || s3 < MIN_S || s3 > MAX_S)
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
