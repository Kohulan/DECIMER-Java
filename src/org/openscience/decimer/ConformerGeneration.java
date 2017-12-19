/*
 * This Software is under the MIT License
 * Refer to LICENSE or https://opensource.org/licenses/MIT for more information
 * Copyright (c) 2017, Kohulan Rajan
 */

package org.openscience.decimer;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.text.DecimalFormat;
import javax.vecmath.Point2d;

import org.openscience.cdk.depict.DepictionGenerator;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.geometry.GeometryTools;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.io.iterator.IteratingSDFReader;
import org.openscience.cdk.layout.StructureDiagramGenerator;
import org.openscience.cdk.silent.SilentChemObjectBuilder;

import ij.IJ;
import ij.ImagePlus;
import ij.process.ImageConverter;

public class ConformerGeneration {
	String temp = "";
	String moleculeTitle = null;
	int moleculeCount = 0;
	boolean verbose = false;

	public static void main(String[] args) throws CDKException, IOException {
		IAtomContainer ac;
		StructureDiagramGenerator sdg = null;
		int x;
		
		
		long startTime = System.nanoTime();
		File folder = new File("D:\\Project\\Chembl_Data\\test45");
		File[] listOfFiles = folder.listFiles();
		
		for (x = 0; x < listOfFiles.length; x++) {
			File file = listOfFiles[x];
			if (file.isFile() && file.getName().endsWith(".sdf")) {
				FileReader sdfile = null;
				try {
					sdfile = new FileReader(file);
				} catch (FileNotFoundException e) {
					System.err.println(file + " not found");
					System.exit(1);
				}
				IteratingSDFReader sdf = new IteratingSDFReader(sdfile, SilentChemObjectBuilder.getInstance());
				sdg = new StructureDiagramGenerator();
				String filename = file.getName();
				filename = filename.substring(0, filename.length() - 4);
				while (sdf.hasNext()) {
					ac = sdf.next();
					sdg.setMolecule(ac);
					sdg.generateCoordinates();
					ac = sdg.getMolecule();
					Point2d point = GeometryTools.get2DCenter(ac);

					for (double i = 0.0; i < 360.0; i = i + 45) {
						// Initial Depiction 32 Bit
						DepictionGenerator dptgen = new DepictionGenerator().withSize(1024, 1024).withAtomValues();
						dptgen.depict(ac).writeTo("D:\\Project\\Chembl_Data\\test45\\images\\"+filename + "_R" + i + ".png");

						// Image conversion to 8 bit using ImagePlus API
						IJ.open("D:\\Project\\Chembl_Data\\test45\\images\\"+filename + "_R" + i + ".png"); // Input of image depicted using CDk Depiction generator
						ImagePlus img = IJ.getImage();
						ImageConverter iconv = new ImageConverter(img);
						iconv.convertToGray8();
						img.updateAndDraw();
						IJ.save("D:\\Project\\Chembl_Data\\test45\\images\\"+filename + "_R" + i + ".png"); // 8 bit grayscaled image replacing the original image

						// Rotating the molecule by 45 Degree and rendering the structure
						GeometryTools.rotate(ac, point, (45 * Math.PI / 180.0));
					}

				}
				sdf.close();

			}
		}

		long endTime = System.nanoTime() - startTime;
		double seconds = (double) endTime / 1000000000.0;
		DecimalFormat d = new DecimalFormat(".###");
		System.out.println("All images rendered successfully!! Time: " + d.format(seconds) + " seconds");
	}
}
