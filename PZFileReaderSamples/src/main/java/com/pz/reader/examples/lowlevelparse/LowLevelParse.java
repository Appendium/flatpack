package com.pz.reader.examples.lowlevelparse;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

import com.pz.reader.util.ParserUtils;

/*
 * Created on Nov 27, 2005
 *
 */

/**
 * @author zepernick
 *
 */
public class LowLevelParse {

    public static void main(String[] args) {
        BufferedReader br = null;
        FileReader fr = null;
        File textFile = new File("PEOPLE-CommaDelimitedWithQualifier.txt");
        String line = null;
        List elements = null;

        try{
	        fr = new FileReader(textFile);
	        br = new BufferedReader(fr);


	        while ((line = br.readLine()) != null){
	            if (line.trim().length() == 0){
	                continue;
	            }

	            //tell the parser to split using a comma delimiter with a "
	            // text qualifier.  The text qualifier is optional, it can be null
	            // or empty
	            elements = ParserUtils.splitLine(line, "," , "\"");


	            for (int i = 0; i < elements.size(); i++){
	                System.out.println("Column " + i + ": " + (String)elements.get(i));
	            }

	            System.out.println("===========================================================================");

	        }

        }catch (Exception ex){
            ex.printStackTrace();
        }finally{
            try{
	            if (br != null) br.close();
	            if (fr != null) fr.close();
            }catch(Exception ignore){}
        }

    }
}
