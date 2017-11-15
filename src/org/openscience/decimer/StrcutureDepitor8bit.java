/* 
 *This Software is under the MIT License
 * Refer to LICENSE or https://opensource.org/licenses/MIT for more information
 * Copyright (c) 2017, Kohulan Rajan 
 */
package org.openscience.decimer;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.text.DecimalFormat;

import org.openscience.cdk.CDKConstants;
import org.openscience.cdk.depict.DepictionGenerator;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.io.iterator.IteratingSDFReader;
import org.openscience.cdk.layout.StructureDiagramGenerator;
import org.openscience.cdk.silent.SilentChemObjectBuilder;

import ij.IJ;
import ij.ImagePlus;
import ij.process.ImageConverter;

public class StrcutureDepitor8bit {
	String temp = "";
	String moleculeTitle = null;
	int moleculeCount = 0;
	boolean verbose = false;

	public static void main(String[] args) throws CDKException, IOException {
		long startTime = System.nanoTime();
		File folder = new File("D:\\Project\\Chembl_Data\\Test2\\");
		File[] listOfFiles = folder.listFiles();
		int x;
		// omp parallel for
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
				IAtomContainer ac = SilentChemObjectBuilder.getInstance().newAtomContainer();
				StructureDiagramGenerator sdg = new StructureDiagramGenerator();
				sdg.setMolecule(ac);
				sdg.generateCoordinates();
				IAtomContainer layedOutMol = sdg.getMolecule();
				String filename = file.toString();
				filename = filename.substring(0, filename.length() - 4);
				while (sdf.hasNext()) {
					layedOutMol = sdf.next();
					layedOutMol.setProperty(CDKConstants.TITLE, file);
					DepictionGenerator dptgen = new DepictionGenerator().withSize(1048, 1048).withAtomValues();
					dptgen.depict(layedOutMol).writeTo(filename + "_M1_G.png");

					// Image conversion to 8 bit using ImagePlus API
					IJ.open(filename + "_M1_G.png"); // Input of image depicted using CDk Depiction generator
					ImagePlus img = IJ.getImage();
					ImageConverter iconv = new ImageConverter(img);
					iconv.convertToGray8();
					img.updateAndDraw();
					IJ.save(filename + "_M1_G.png"); // 8 bit grayscaled image replacing the original image

				}
				sdf.close();

			}
		}

		long endTime = System.nanoTime() - startTime;
		double seconds = (double) endTime / 1000000000.0;
		DecimalFormat d = new DecimalFormat(".###");
		System.out.println("All images rendered successfully!! \n\nTime: " + d.format(seconds) + " seconds");
	}
}
