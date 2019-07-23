package com.hansung.ard;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.math.BigDecimal;


public class RSR {
	
	public String FORMOSAT_RES6S = "/formosat_res6S.dat";
	public String KOMPSAT3_RSR = "/KOMPSAT_3_Spectral_response_function.csv";
	public String output = "kompsat3_res6S.dat";
	
	public RSR(){
		FORMOSAT_RES6S = getClass().getResource(FORMOSAT_RES6S).getPath();
		KOMPSAT3_RSR = getClass().getResource(KOMPSAT3_RSR).getPath();
		
		String[][] source = csvReader(FORMOSAT_RES6S, " ");
		String[][] target = csvReader(KOMPSAT3_RSR, ",");
		String[][] newRSR = new String[source.length][target[0].length + 1];
		int i=0, j=0;
		for(i=0; i<source.length; i++) {
				BigDecimal  wavelength = new BigDecimal(source[i][0]).setScale(6, BigDecimal.ROUND_DOWN);
				String  solar_spectrum = source[i][1];
				 
				for(int a=0; a<target.length; a++) {
					BigDecimal  target_wavelength = new BigDecimal(target[a][0]);
					target_wavelength = target_wavelength.multiply(new BigDecimal(0.001)).setScale(6, BigDecimal.ROUND_DOWN); 
					if (target_wavelength.compareTo(wavelength) ==0) { 						
						newRSR[i][0] = String.valueOf(wavelength);
						newRSR[i][1] = solar_spectrum;
						for (int c=1; c<target[0].length; c++) {
							newRSR[i][c+1] = String.valueOf(
												new BigDecimal(target[a][c]).setScale(4, BigDecimal.ROUND_DOWN)
												);
						} 
						break;
					} else if (target_wavelength.compareTo(wavelength) == 1 ) {
						
						newRSR[i][0] = String.valueOf(wavelength);
						newRSR[i][1] = solar_spectrum;
						for (int c=1; c<target[0].length; c++) {
							newRSR[i][c+1] = String.valueOf(
												average(new BigDecimal(target[a-1][c]), new BigDecimal(target[a+1][c]))
											);
						} 
						break;
					}
				}		
		}
		/*
		for(i=0; i<newRSR.length; i++) {
			for (j=0; j<newRSR[0].length; j++) {
				System.out.print(newRSR[i][j] + " ");
			}
			System.out.println("");
		}
		*/
		try {
			String dat_path = "d:/" + output;
			File file = new File(dat_path);
			if (file.exists()) {
				file.delete();
			}
			BufferedWriter writer = new BufferedWriter(
					new FileWriter(dat_path, true)
			);
			
			for (i=0; i<newRSR.length; i++) {
				for (j=0; j<newRSR[0].length; j++) {
//					System.out.print(newRSR[i][j] + " ");
					writer.write(newRSR[i][j] + " ");
				}
				writer.newLine();
			}
			writer.flush();
			writer.close();
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	public BigDecimal average(BigDecimal a, BigDecimal b) {
		return a.add(b).divide(new BigDecimal(2)).setScale(4, BigDecimal.ROUND_DOWN);
	}
	
	public String[][] csvReader(String file_path,String token_type){
		String[][] result = null;
		try{
			File csv = new File(file_path);
			//BufferedReader br = new BufferedReader(new FileReader(csv));
			BufferedReader br = new BufferedReader(
					   new InputStreamReader(
			                      new FileInputStream(file_path), "UTF8"));	
			String line="";
			int row=0, i;
			int col=0;
			while (( line = br.readLine()) != null) {
				String[] token = line.split(token_type, -1);
				if (col == 0){
					col = token.length;
				}	 			
				row++;
			}
			br.close();  
			br =  new BufferedReader(
					   new InputStreamReader(
			                      new FileInputStream(file_path), "UTF8"));
			result = new String[row][col];
			line="";
			row=0;
			while (( line = br.readLine()) != null) {
				line = line.trim().replaceAll(" +", " ");
				String[] token = line.split(token_type, -1); 
				for(i=0; i<col; i++){
					if (!token[i].equals(null)) {
						result[row][i] = token[i];
					}					
				} /*
				for(i=0; i<col; i++){
					System.out.print(result[row][i] + " ");
				}
				System.out.println("");
				 		*/
				row++;
			}
		} catch(FileNotFoundException e){
			e.printStackTrace();
		} catch(IOException e){
			e.printStackTrace();
		} 	 
		return result;
	}
	
	
	
	

}
