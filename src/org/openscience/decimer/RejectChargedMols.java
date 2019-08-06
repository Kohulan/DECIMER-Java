/*
 * This Software is under the MIT License
 * Refer to LICENSE or https://opensource.org/licenses/MIT for more information
 * Copyright (c) 2019, Kohulan Rajan
 */

package org.openscience.decimer;

import java.text.DecimalFormat;

import java.awt.*;
import java.io.*;

import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.io.iterator.IteratingSDFReader;
import org.openscience.cdk.silent.SilentChemObjectBuilder;
import org.openscience.cdk.smiles.SmilesGenerator;
import org.openscience.cdk.smiles.SmiFlavor;
import org.openscience.cdk.tools.manipulator.AtomContainerManipulator;

public class RejectChargedMols {
	String temp = "";
	String moleculeTitle = null;
	int moleculeCount = 0;
	boolean verbose = false;

	public static void main(String[] args) throws Exception {
		IAtomContainer molecule;
		int x;

		System.setProperty("java.awt.headless", "true");
		long startTime = System.nanoTime();
		GraphicsEnvironment.getLocalGraphicsEnvironment();
		System.out.println("Headless mode: " + GraphicsEnvironment.isHeadless());

		// Directories according to your data files
		String input_directory = "/Path/to/Directory/";
		String report_directory = "/Path/to/Directory/";

		File folder = new File(input_directory);
		File[] listOfFiles = folder.listFiles();

		File fileout_2 = new File(report_directory + "Filename.txt");

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
				String filename = file.getName();
				filename = filename.substring(0, filename.length() - 4);
				while (sdf.hasNext()) {
					molecule = sdf.next();

					BufferedWriter bufferWriter2 = new BufferedWriter(
							new FileWriter(fileout_2.getAbsoluteFile(), true));

					try {

						if (AtomContainerManipulator.getTotalFormalCharge(molecule) == 0.0
								&& AtomContainerManipulator.getTotalNegativeFormalCharge(molecule) == 0.0
								&& AtomContainerManipulator.getTotalPositiveFormalCharge(molecule) == 0.0) {
							IAtomContainer mol_new = AtomContainerManipulator.removeHydrogens(molecule);
							SmilesGenerator sg = new SmilesGenerator(SmiFlavor.Generic);
							String smi = sg.create(mol_new);
							bufferWriter2.write(filename + "\t\t" + smi + "\t\t" + smi.length() + "\n");
						}
					} catch (Exception e) {
						System.err.println(filename + " Error");
						e.printStackTrace();
					}

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
				"Molecules cointaining charges rejected!\n Time: " + d.format(seconds) + " seconds");
	}
}
