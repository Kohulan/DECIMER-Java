/*
 * This Software is under the MIT License
 * Refer to LICENSE or https://opensource.org/licenses/MIT for more information
 * Copyright (c) 2018, Kohulan Rajan
 */

//This code is written to generate chemical structures as 8-bit Grayscale Images using random rotations.
//User can specify how many Imges the code should generate per molecule.

package org.openscience.decimer;

import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.Collections;

import javax.imageio.ImageIO;
import javax.vecmath.Point2d;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.io.*;

import org.openscience.cdk.depict.DepictionGenerator;
import org.openscience.cdk.geometry.GeometryTools;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.io.iterator.IteratingSDFReader;
import org.openscience.cdk.layout.StructureDiagramGenerator;
import org.openscience.cdk.renderer.generators.BasicSceneGenerator;
import org.openscience.cdk.qsar.DescriptorValue;
import org.openscience.cdk.qsar.descriptors.molecular.BondCountDescriptor;
import org.openscience.cdk.silent.SilentChemObjectBuilder;

public class Image2ArrayRandom {
	String temp = "";
	String moleculeTitle = null;
	int moleculeCount = 0;
	boolean verbose = false;

	public static void main(String[] args) throws Exception {
		IAtomContainer molecule;
		StructureDiagramGenerator sdg = null;
		int x;

		System.setProperty("java.awt.headless", "true");
		long startTime = System.nanoTime();
		GraphicsEnvironment.getLocalGraphicsEnvironment();
		System.out.println("Headless mode: " + GraphicsEnvironment.isHeadless());

		// Directories according to your data files
		String input_directory = "/Path/to/input/directory";
		String report_directory = "/Path/to/report/directory";
		String image_directory = "/Path/to/image/directory";

		File folder = new File(input_directory);
		File[] listOfFiles = folder.listFiles();

		File fileout = new File(report_directory + "Potential_train.txt");
		if (!fileout.exists()) {
			fileout.createNewFile();
		}
		File fileout_2 = new File(report_directory + "Potentialtrain_labels.csv");

		if (!fileout_2.exists()) {
			fileout_2.createNewFile();
		}

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
				DecimalFormat df2 = new DecimalFormat("#.##");
				while (sdf.hasNext()) {
					molecule = sdf.next();
					molecule.setStereoElements(Collections.emptyList());
					sdg.setMolecule(molecule);
					sdg.generateCoordinates();
					molecule = sdg.getMolecule();
					Point2d point = GeometryTools.get2DCenter(molecule);

					BufferedWriter bufferWriter = new BufferedWriter(new FileWriter(fileout.getAbsoluteFile(), true));
					BufferedWriter bufferWriter2 = new BufferedWriter(
							new FileWriter(fileout_2.getAbsoluteFile(), true));

					try {
						for (int j = 0; j < 8; j = j + 1) {
							double i = Math.random();
							i = i * 360.0 + 0.0;
							// Rotating the molecule by 45 Degree and rendering the structure
							GeometryTools.rotate(molecule, point, (i * Math.PI / 180.0));

							// System.out.println("Random Rotation "+df2.format(i));
							bufferWriter.write(filename + "_R_" + df2.format(i) + ",");
							bufferWriter2.write(filename + "_R_" + df2.format(i) + ",");
							DescriptorValue bondcount = new BondCountDescriptor().calculate(molecule);
							bufferWriter2.write(Integer.parseInt(bondcount.getValue().toString()) - 5 + "\n");
							DepictionGenerator dptgen = new DepictionGenerator().withSize(256, 256).withAtomValues()
									.withParam(BasicSceneGenerator.ZoomFactor.class, 0.7);
							BufferedImage image = dptgen.depict(molecule).toImg();
							BufferedImage result = new BufferedImage(image.getWidth(), image.getHeight(),
									BufferedImage.TYPE_BYTE_GRAY);
							result.createGraphics().drawImage(image, 0, 0, Color.WHITE, null);

							// Save images inside the directory
							ImageIO.write(result, "png",
									new File(image_directory + filename + "_R_" + df2.format(i) + ".png"));

							// Image to arrays
							byte[] pixels = ((DataBufferByte) result.getRaster().getDataBuffer()).getData();
							int[] int_pixel = new int[pixels.length];
							double[] bin_pixel = new double[pixels.length];
							int im;
							for (im = 0; im < pixels.length; im++) {
								int_pixel[im] = (int) pixels[im] & 0xff;
								bin_pixel[im] = (1 - (int_pixel[im] / 255.0));
							}

							bufferWriter.write(Arrays.toString(int_pixel) + "\n");

						}
					} catch (Exception e) {
						System.err.println(filename + " Error");
						e.printStackTrace();
					}
					bufferWriter.close();
					bufferWriter2.close();
					continue;

				}

				sdf.close();

			}
		}

		long endTime = System.nanoTime() - startTime;
		double seconds = (double) endTime / 1000000000.0;
		DecimalFormat d = new DecimalFormat(".###");
		System.out.println(
				"Image arrays generated..\nAll images rendered successfully!! Time: " + d.format(seconds) + " seconds");
	}
}