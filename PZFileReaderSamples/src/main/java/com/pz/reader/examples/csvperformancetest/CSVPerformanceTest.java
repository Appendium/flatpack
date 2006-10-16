package com.pz.reader.examples.csvperformancetest;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.HashMap;
import java.util.Map;

import com.pz.reader.DataError;
import com.pz.reader.DataSet;

/*
 * Created on Dec 1, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */

/**
 * @author zepernick
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class CSVPerformanceTest {

    public static void main(String[] args) {

        DataSet ds = null;
        String[] colNames = null;
        Map settings = null;

        try{


            settings = readSettings();

	        //delimited by a comma
	        //text qualified by double quotes
	        //ignore first record
            long timeStarted = System.currentTimeMillis();
	        ds = new DataSet(new File((String)settings.get("csvFile")),",","\"",false);
	        long timeFinished = System.currentTimeMillis();


	        String timeMessage = "";

	        if (timeFinished - timeStarted < 1000){
	            timeMessage = (timeFinished - timeStarted) + " Milleseconds..." ;
	        }else{
	            timeMessage = ((timeFinished - timeStarted) / 1000) + " Seconds...";
	        }

	        System.out.println("");
	        System.out.println("********FILE PARSED IN: " + timeMessage  + " ******");
	        Thread.sleep(2000); //sleep for a couple seconds to the message above can be read



	        if (Boolean.valueOf((String)settings.get("verbose")).booleanValue()){
	            timeStarted = System.currentTimeMillis();
		        colNames = ds.getColumns();

		        while (ds.next()){
		            for (int i = 0; i < colNames.length; i++){
		                System.out.println("COLUMN NAME: " + colNames[i] + " VALUE: " + ds.getString(colNames[i]));
		            }

		            System.out.println("===========================================================================");
		        }
		        timeFinished = System.currentTimeMillis();

		        if (timeFinished - timeStarted < 1000){
		            timeMessage = (timeFinished - timeStarted) + " Milleseconds..." ;
		        }else{
		            timeMessage = ((timeFinished - timeStarted) / 1000) + " Seconds...";
		        }

		        System.out.println("");
		        System.out.println("********Displayed Data To Console In: " + timeMessage  + " ******");

	        }


	        if (ds.getErrors() != null && ds.getErrors().size() > 0){
	            System.out.println("FOUND ERRORS IN FILE....");
	            for (int i = 0; i < ds.getErrors().size(); i++){
	                DataError de = (DataError)ds.getErrors().get(i);
	                System.out.println("Error: " + de.getErrorDesc() + " Line: " + de.getLineNo());
	            }
	        }

	        //clear out the DataSet object for the JVM to collect
	        ds.freeMemory();
        }catch(Exception ex){
            ex.printStackTrace();
        }


    }



    private static Map readSettings() throws Exception{
        Map result = new HashMap();
        FileReader fr = null;
        BufferedReader br = null;
        String line = null;

        try{
            fr = new FileReader("settings.properties");
            br = new BufferedReader(fr);

            while ((line = br.readLine()) != null){
                if (line.trim().length() == 0 || line.startsWith("#") || line.indexOf("=") == -1){
                    continue;
                }

                result.put(line.substring(0,line.indexOf("=")), line.substring(line.indexOf("=") + 1));
            }
        }finally{
            if (fr != null) fr.close();
            if (br != null) br.close();
        }

        return result;

    }

}
