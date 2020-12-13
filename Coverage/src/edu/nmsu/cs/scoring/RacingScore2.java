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

	// In this function I fixed two functional errors.
	public int overallScore()
	{
		int s, s1 = 0, s2 = 0; // Initialized variables s1 and s2 to 0.
		if (score1 < score2 && score1 < score3)
		{
			s1 = score2;
			s2 = score3;
		}
		else if (score2 < score1 && score2 < score3)
		{
			s1 = score1;
			s2 = score3; // Switch the stored number from score2 to score3 since this else if statement indicates score2 being the smallest score.
		}
		else if (score3 < score1 && score3 < score2)
		{
			s1 = score1;
			s2 = score2;
		}
		else
		{
			// Created an if statement that checks if score1 == score2 then sets the numbers accordingly.
			if(score1 == score2) {
				s1 = score1;
				s2 = score3;
			} // end of if
		    
			// Created an if statement that checks if score2 == score3 then sets the numbers accordingly.
			if(score2 == score3) {
				s1 = score2;
				s2 = score1;	
			} // end of if
			
			// Created an if statement that checks if score3 == score1 then sets the numbers accordingly.
			if(score3 == score1) {
				s1 = score1;
				s2 = score2;
			} // end of if		
		}
		s = s1 + s2;
		return s;
	}

	// In the main function I fixed one functional error.
	public static void main(String args[])
	{
		int s1, s2, s3;
		
		// I removed the null case due to it never being possible to execute.
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
		RacingScore2 score = new RacingScore2();
		score.recordScores(s1, s2, s3);
		System.out.println("Overall score: " + score.overallScore());
		return;
	}

} // end class