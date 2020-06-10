/*
 * This Software is under the MIT License
 * Refer to LICENSE or https://opensource.org/licenses/MIT for more information
 * Copyright (c) 2019, Kohulan Rajan
 */
package org.openscience.decimer;

import java.text.DecimalFormat;

import javax.imageio.ImageIO;
import javax.vecmath.Point2d;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;

import org.openscience.cdk.DefaultChemObjectBuilder;
import org.openscience.cdk.depict.DepictionGenerator;
import org.openscience.cdk.geometry.GeometryTools;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.layout.StructureDiagramGenerator;
import org.openscience.cdk.renderer.generators.BasicSceneGenerator;
import org.openscience.cdk.smiles.SmilesParser;
import org.openscience.cdk.renderer.generators.standard.StandardGenerator;

public class SmilesDepictor {
	String temp = "";
	String moleculeTitle = null;
	int moleculeCount = 0;
	boolean verbose = false;

	public static void main(String[] args) throws Exception {
		IAtomContainer molecule = null;

		System.setProperty("java.awt.headless", "true");
		long startTime = System.nanoTime();
		GraphicsEnvironment.getLocalGraphicsEnvironment();
		System.out.println("Headless mode: " + GraphicsEnvironment.isHeadless());

		// Directories according to your data files
		String input_directory = "/path/to/input_directory/";
		String smiles_directory = "/path/to/smiles_directory/";
		String image_directory = "/path/to/Train_Images/";

		File fileout = new File(smiles_directory + "Selected_SMILES.txt");

		if (!fileout.exists()) {
			fileout.createNewFile();
		}
		BufferedReader input_file = new BufferedReader(new FileReader(input_directory + "All_SMILES.txt"));
		BufferedWriter bufferWriter = new BufferedWriter(new FileWriter(fileout.getAbsoluteFile(), true));

		String line = input_file.readLine();

		while (line != null) {
			
			String[] splitted_line = line.split(",");

			try {

			SmilesParser smi = new SmilesParser(DefaultChemObjectBuilder.getInstance());
			molecule = smi.parseSmiles(splitted_line[1]);

			StructureDiagramGenerator sdg = new StructureDiagramGenerator();
			sdg.setMolecule(molecule);
			sdg.generateCoordinates(molecule);
			molecule = sdg.getMolecule();
			Point2d point = GeometryTools.get2DCenter(molecule);
			DecimalFormat df2 = new DecimalFormat("#");

			// for (double i = 0.0; i < 360.0; i = i + 10.0) {
			double i = Math.random();
			i = i * 360.0 + 0.0;
			GeometryTools.rotate(molecule, point, (i * Math.PI / 180.0));
			DepictionGenerator dptgen = new DepictionGenerator().withSize(299, 299).withAtomValues()
					.withParam(BasicSceneGenerator.ZoomFactor.class, 1.0);
			BufferedImage image = dptgen.depict(molecule).toImg();
			BufferedImage result = new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_BYTE_GRAY);
			result.createGraphics().drawImage(image, 0, 0, Color.WHITE, null);
			String String_i = df2.format(i);
			
			ImageIO.write(result, "png",
					new File(image_directory + splitted_line[0] + ".png"));

			bufferWriter.write(splitted_line[0] + "," + splitted_line[1] + "\n");
		}
			 catch(Exception e) { 
			 	System.out.println("toString(): "  + e.toString());
			 }
			

			line = input_file.readLine();
		}
		input_file.close();
		bufferWriter.close();

		long endTime = System.nanoTime() - startTime;
		double seconds = (double) endTime / 1000000000.0;
		DecimalFormat d = new DecimalFormat(".###");
		System.out.println(
				"Image arrays generated..\nAll images rendered successfully!! Time: " + d.format(seconds) + " seconds");
	}
}
