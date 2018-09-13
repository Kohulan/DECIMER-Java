/*
 * This Software is under the MIT License
 * Refer to LICENSE or https://opensource.org/licenses/MIT for more information
 * Copyright (c) 2018, Kohulan Rajan
 */

package org.openscience.decimer;

import java.text.DecimalFormat;
import java.util.Arrays;

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
import org.openscience.cdk.silent.SilentChemObjectBuilder;

public class Image2Array {
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
		String input_directory = "D:\\Project\\Diverse_Divide_10_1\\network_test_dataset20180807\\";
		String report_directory = "D:\\Project\\Diverse_Divide_10_1\\network_test_dataset20180807\\";
		String output_directory = "D:\\\\Project\\\\Diverse_Divide_10_1\\\\network_test_dataset20180807\\";

		File folder = new File(input_directory);
		File[] listOfFiles = folder.listFiles();

		File fileout = new File(report_directory + "Image_array_255.txt");
		File fileout_2 = new File(report_directory + "Image_array_binary.txt");
		if (!fileout.exists()) {
			fileout.createNewFile();
		}
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
				while (sdf.hasNext()) {
					molecule = sdf.next();
					sdg.setMolecule(molecule);
					sdg.generateCoordinates();
					molecule = sdg.getMolecule();
					Point2d point = GeometryTools.get2DCenter(molecule);

					BufferedWriter bufferWriter = new BufferedWriter(new FileWriter(fileout.getAbsoluteFile(), true));
					BufferedWriter bufferWriter2 = new BufferedWriter(
							new FileWriter(fileout_2.getAbsoluteFile(), true));

					try {

						for (double i = 0.0; i < 360.0; i = i + 45.0) {
							bufferWriter.write(filename + "_R_" + i + ",");
							bufferWriter2.write(filename + "_R_" + i + ",");
							DepictionGenerator dptgen = new DepictionGenerator().withSize(1024, 1024).withAtomValues();
							BufferedImage image = dptgen.depict(molecule).toImg();
							BufferedImage result = new BufferedImage(image.getWidth(), image.getHeight(),
									BufferedImage.TYPE_BYTE_GRAY);
							result.createGraphics().drawImage(image, 0, 0, Color.WHITE, null);

							//Image to arrays
							byte[] pixels = ((DataBufferByte) result.getRaster().getDataBuffer()).getData();
							int[] int_pixel = new int[pixels.length];
							double[] bin_pixel = new double[pixels.length];
							int im;
							for (im = 0; im < pixels.length; im++) {
								int_pixel[im] = (int) pixels[im] & 0xff;
								//Arrays converted to 0.95-0.05
								//bin_pixel[im] = Math.round((0.95 - 0.9 / 255 * (255 - (int_pixel[im]))) * 100);
								bin_pixel[im] = (1.0- (int_pixel[im]/255.0));

							}

							bufferWriter.write(Arrays.toString(int_pixel) + "\n");
							bufferWriter2.write(Arrays.toString(bin_pixel) + "\n");

							ImageIO.write(result, "png", new File(output_directory + filename + "_" + i + ".png"));

							// Rotating the molecule by 45 Degree and rendering the structure
							GeometryTools.rotate(molecule, point, (45 * Math.PI / 180.0));
						}
						// }
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
		System.out.println("Image arrays generated..\nAll images rendered successfully!! Time: " + d.format(seconds) + " seconds");
	}
}
