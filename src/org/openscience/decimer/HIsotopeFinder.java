/*
 * This Software is under the MIT License
 * Refer to LICENSE or https://opensource.org/licenses/MIT for more information
 * Copyright (c) 2019, Kohulan Rajan
 * ABOUT: This program reads SDF files inside a folder and gives a list of molecules with Hydrogen isotopes (D and T).
 */
package org.openscience.decimer;

import java.awt.GraphicsEnvironment;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;

import org.openscience.cdk.config.IsotopeFactory;
import org.openscience.cdk.config.Isotopes;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.io.iterator.IteratingSDFReader;
import org.openscience.cdk.silent.SilentChemObjectBuilder;

public class HIsotopeFinder {
	String temp = "";
	String moleculeTitle = null;
	int moleculeCount = 0;
	boolean verbose = false;

	public static void main(String[] args) throws Exception {
		IAtomContainer molecule;
		int x;

		System.setProperty("java.awt.headless", "true");
		GraphicsEnvironment.getLocalGraphicsEnvironment();
		System.out.println("Headless mode: " + GraphicsEnvironment.isHeadless());

		// Directories according to your data files
		String input_directory = "/Users/kohulanrajan/Desktop/Project Folder 2019/HIgher_Bonds/Test_Images/test/";
		String report_directory = "/Users/kohulanrajan/Desktop/Project Folder 2019/HIgher_Bonds/Test_Images/test/";

		File folder = new File(input_directory);
		File[] listOfFiles = folder.listFiles();
		File fileout = new File(report_directory + "Selected_Molecules.txt");
		if (!fileout.exists()) {
			fileout.createNewFile();
		}
		BufferedWriter bufferWriter = new BufferedWriter(new FileWriter(fileout.getAbsoluteFile(), true));
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
					// configure Isotopes

					IsotopeFactory iso_H = Isotopes.getInstance();
					iso_H.configureAtoms(molecule);

					// Print file name with Hydrogen Isotopes
					try {
						for (IAtom atom : molecule.atoms()) {
							if (atom.getSymbol() == "H") {
								if (atom.getMassNumber() > 1) {
									System.out.println(filename + " Hydrogen isotope detected");
									break;
								}
							}
						}

					} catch (Exception e) {
						System.err.println(filename + " Error");
						e.printStackTrace();
					}

					continue;

				}

				sdf.close();

			}

		}
		bufferWriter.close();
	}
}
