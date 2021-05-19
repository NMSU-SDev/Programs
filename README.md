# Changes for Lab1
* L1: fork and clone to local computer. Programs had /Circles, /Coverage, /SimpleWebServer with /src and package struct as edu.nmsu.cs...
* Changes made:
	* Hello: added project folder /HelloWorld with /src and package struct, added file called helloworld.java under /HelloWorld
	* Questions: added new folder in root dir named /Questions, added 2 .txt to /Questions named p1Questions.txt, p2Questions.txt 
	* Ant Buildfiles: made build.xml for /Circles, /SimpleWebServer and touched
	* readMe: updated.
	_forked from toupsz_
	* MyID: jsy4

:upside_down_face: https://github.com/jsy4/Programs


# Programs
Base code for program assignments in Software Development (C S 371). 

## Program 1 and Program 2
These assignments rely on the SimpleWebServer program. Each gets graded using tags in a cloned repository.

* Changes made for Program 1: 
	* Webworker.java: serves HTML files according to GET request from the url. Checks if the file exists and displays the HTML file(200.OK) or defaults to HTML page displaying error with status code 404. When reading and outputing HTML, it substitutes tags. 
	* build.xml: updated to <ant> (default: clean, compile) and <ant run> (runs program on port 8080) 
	* p1.Questions.txt are answered.
	* readMe: updated.

* Changes made for Program 2:
	* Webworker.java: now changed to serve image file types GIF, JPEG, PNG.
	* www subfolder: has the image files that are less than 10KB (jpgTest.jpg, pngTest.png, gifTest.gif, favicon.ico). It also contains two test HTML files test1.html, test2.html, and these files in www uses the image files using html img tag. Also included /res/acc/test.html for testing in www subdirectory.
	* build.xml: the ant command itself will now clean, compile, run. ant gets the source code from the root directory SimpleWebServer and runs the code in subdirectory www, creating bin directory there to run. P2's http://localhost:8080/res/acc/test.html is same as P1's http://localhost:8080/www/res/acc/test.html
	* p2.Questions.txt are answered.
	* readMe: updated.


## Program 3
This assignment relies on the Coverage programs. 

* Changes made for Program 3:
	* /lib: added a folder to contain compiled libraries for Jacoco's 7 jar files https://www.jacoco.org/jacoco/trunk/index.html (Copyright © 2009, 2021 Mountainminds GmbH & Co. KG and Contributors)
	* gitignore: added to ignoring of target directory being committed to github
	* build.xml: ant clean command will now delete the bin directory along with classfiles. 100% coverage testing of RacingScore1.java, RacingScore2.java is written with corresponding echo messages.
	* RacingScore1 and RacingScore2 now correctly finds sum of 2 largest score out of 3 int arguments given.
	* RacingScore1.java: corrected overallScore() to compare score1 and score2 to find smallest s. Made final variable for the score range 0-50(inclusive) and added in main to check if args are in range.
	* RacingScore2.java: corrected overallScore() to include correct case when 3 scores are same and removed unnecessary variables. Made final variable for the score range 0-50(inclusive) to avoid magic nums. Removed if(args==null) since String args[] is empty string array instead of null ref.
	* readMe: Updated.

## Program 4
This assignment relies on the Circles programs. 

* Changes made for Program 4:
	* /lib: added folder for junit.jars and hamcrest https://junit.org/junit4/license.html (Copyright © 2002-2021 JUnit. All Rights Reserved.),  https://search.maven.org/artifact/org.hamcrest/hamcrest-core/1.3/jar (Copyright ©2017-present Sonatype, Inc.)
	* .java classes: fixed.
	* build.xml: clean(rm bin dir), compile(javac all java to make jar), run(CircleRun.java only), test(depends on testCircle1,2), testCircle1(tests Circle1.java), testCircle2(tests Circle2.java).
	* readMe: Updated.

