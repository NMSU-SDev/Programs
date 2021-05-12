package testing;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.io.*;
import java.lang.*;
public class testing {

	public static void main(String [] args) {
	Path path = Paths.get("test.txt");
	String string = path.toAbsolutePath().toString();
	char c=string.charAt(0);
	String string2 ="";
	int i=0;
	while( i<string.length()) {
		System.out.println(i);
		c=string.charAt(i);
		if(c=='/') {
    		string2=string2+'/';
    		
    	}
		
			string2 = string2+c;
		

		System.out.print(string2);
		i++;
    }
	
	System.out.println("string1 :"+string);

	System.out.println("string2 :"+string2);
	}
}
