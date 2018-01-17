/*******************************************************************************
 * Copyright (C) 2016, 2017 Embedded Systems and Applications Group
 * Department of Computer Science, Technische Universitaet Darmstadt,
 * Hochschulstr. 10, 64289 Darmstadt, Germany.
 * 
 * All rights reserved.
 * 
 * This software is provided free for educational use only.
 * It may not be used for commercial purposes without the
 * prior written permission of the authors.
 ******************************************************************************/

package MTAM;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import javax.imageio.ImageIO;

public class FileIO {
	
	public static void readImage(String fileName){
		BufferedImage img;
		try{
			/*
			 * Read image from file.
			 * Image I/O has built-in support for GIF, PNG, JPEG, BMP, and WBMP.
			 */
			img = ImageIO.read(new File(fileName));
			if(img.getHeight()<512 || img.getWidth()<512){
				throw new RuntimeException("The given picture is too small!");
			}
			for(int y=0; y<512; ++y){
				for(int x=0; x<512; ++x){
					Color c = new Color(img.getRGB(x,y));
					int red = (int) (c.getRed() * 0.299);
					int green = (int) (c.getGreen() * 0.587);
					int blue = (int) (c.getBlue() * 0.114);
					int gray = red + blue + green;
					int pos = Interpreter.ST + (y*512) + x;
					Interpreter.data[pos] = gray;
				} 
			}
			Interpreter.ST+= 512*512;
		}
		catch(IOException e){
			throw new RuntimeException(e);
		}
	}
	
	public static void writeImage(){
		Interpreter.ST -= 512*512 + 1; // ST now pointing to the filename		
		String fileName = Machine.constantPool.get(Interpreter.data[Interpreter.ST]);
		++Interpreter.ST; // ST now pointing to the first element of the image
		BufferedImage img = new BufferedImage(512, 512, BufferedImage.TYPE_BYTE_GRAY);
		for(int y=0; y<512; ++y){
			for(int x=0; x<512; ++x){
				int pos = Interpreter.ST + (y*512) + x;
				int gray = Interpreter.data[pos];
				Color pixel = new Color(gray, gray, gray);
				img.setRGB(x, y, pixel.getRGB());
			} 
		}
		String imgType = fileName.substring(fileName.lastIndexOf('.')+1);
		File outputFile = new File(fileName);
		try {
			/*
			 * Write image to file.
			 * Image I/O has built-in support for GIF, PNG, JPEG, BMP, and WBMP.
			 */
			ImageIO.write(img, imgType, outputFile);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		/*
		 * Clear stack
		 */
		Interpreter.ST -= 1;
	}
	
	public static void readIntegerMatrix(String fileName, int size){
		try(BufferedReader reader = new BufferedReader(new FileReader(fileName))){
			int numLines = 0;
			String line = reader.readLine();
			/*
			 * Read matrix from CSV-format.
			 */
			while(line!=null && numLines < size){
				line.trim();
				String [] elements = line.split(",");
				if(elements.length < size){
					throw new RuntimeException("A line in the input file does not contain enough elements!");
				}
				for(int i=0; i<size; ++i){
					int pos = Interpreter.ST + numLines * size + i;
					Interpreter.data[pos] = Integer.parseInt(elements[i].trim());
				}
				line = reader.readLine();
				++numLines;
			}
			if(numLines<size){
				throw new RuntimeException("The input file does not contain enough rows!");
			}
			// Increment stack top
			Interpreter.ST += size*size;
		} catch (FileNotFoundException e) {
			throw new RuntimeException(e);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
	
	public static void writeIntegerMatrix(int size){
		Interpreter.ST -= size * size + 1; // ST now pointing to the filename
		String fileName = Machine.constantPool.get(Interpreter.data[Interpreter.ST]);
		++Interpreter.ST; // ST now pointing to the first element of the matrix.
		try(BufferedWriter writer = new BufferedWriter(new FileWriter(fileName))){
			for(int i=0; i<size; ++i){
				StringBuilder line = new StringBuilder(size*3);
				boolean first = true;
				for(int j=0; j<size; ++j){
					int pos = Interpreter.ST + i * size + j;
					if(!first){
						line.append(", ");
					}
					line.append(Interpreter.data[pos]);
					first = false;
				}
				line.append("\n");
				writer.write(line.toString());
			}
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		// Clear stack
		Interpreter.ST -= 1;
	}
	
	public static void readFloatMatrix(String fileName, int size){
		try(BufferedReader reader = new BufferedReader(new FileReader(fileName))){
			int numLines = 0;
			String line = reader.readLine();
			/*
			 * Read matrix from CSV-format.
			 */
			while(line!=null && numLines < size){
				line.trim();
				String [] elements = line.split(",");
				if(elements.length < size){
					throw new RuntimeException("A line in the input file does not contain enough elements!");
				}
				for(int i=0; i<size; ++i){
					int pos = Interpreter.ST + numLines * size + i;
					float item = Float.parseFloat(elements[i].trim());
					Interpreter.data[pos] = Float.floatToIntBits(item);
				}
				line = reader.readLine();
				++numLines;
			}
			if(numLines<size){
				throw new RuntimeException("The input file does not contain enough rows!");
			}
			// Increment stack top
			Interpreter.ST += size*size;
		} catch (FileNotFoundException e) {
			throw new RuntimeException(e);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
	
	public static void writeFloatMatrix(int size){
		Interpreter.ST -= size * size + 1; // ST now pointing to the filename
		String fileName = Machine.constantPool.get(Interpreter.data[Interpreter.ST]);
		++Interpreter.ST; // ST now pointing to the first element of the matrix.
		try(BufferedWriter writer = new BufferedWriter(new FileWriter(fileName))){
			for(int i=0; i<size; ++i){
				StringBuilder line = new StringBuilder(size*3);
				boolean first = true;
				for(int j=0; j<size; ++j){
					int pos = Interpreter.ST + i * size + j;
					if(!first){
						line.append(", ");
					}
					float item = Float.intBitsToFloat(Interpreter.data[pos]);
					line.append(String.valueOf(item));
					first = false;
				}
				line.append("\n");
				writer.write(line.toString());
			}
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		// Clear stack
		Interpreter.ST -= 1;
	}

	public static void printInt(int text){
		System.out.print(String.valueOf(text));
	}
	
	public static void printFloat(float text){
		System.out.print(String.valueOf(text));
	}
	
	public static void printBool(boolean text){
		System.out.print(String.valueOf(text));
	}
	
	public static void printString(String text){
		System.out.print(text);
	}
	
	public static void printLine(){
		System.out.println();
	}
}
