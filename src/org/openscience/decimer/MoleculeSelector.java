/*
 * This Software is under the MIT License
 * Refer to LICENSE or https://opensource.org/licenses/MIT for more information
 * Copyright (c) 2019, Kohulan Rajan
 * ABOUT: This program was written in order to get a list of molecules with desired atoms (CHNOPS, F, Cl, Br, I, Se, B) stores in a folder as SDF files
 */

package org.openscience.decimer;

import java.awt.GraphicsEnvironment;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IElement;
import org.openscience.cdk.interfaces.IMolecularFormula;
import org.openscience.cdk.io.iterator.IteratingSDFReader;
import org.openscience.cdk.silent.SilentChemObjectBuilder;
import org.openscience.cdk.tools.manipulator.MolecularFormulaManipulator;

public class MoleculeSelector {
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
		String input_directory = "/home/kohulan/Data_test/PotentialPub/NewDataSet_20190416/New_Train";
		String report_directory = "/home/kohulan/Data_test/PotentialPub/NewDataSet_20190416/";

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

					IMolecularFormula mols = MolecularFormulaManipulator.getMolecularFormula(molecule);
					List<IElement> elements = MolecularFormulaManipulator.elements(mols);
					List<String> String_elements = new ArrayList<String>();
					for (IElement i : elements) {
						String_elements.add(i.getSymbol());
					}

					HashMap<String, String> Selected_Elements = new HashMap<>();
					Selected_Elements.put("C", "Atom Symbol");
					Selected_Elements.put("H", "Atom Symbol");
					Selected_Elements.put("N", "Atom Symbol");
					Selected_Elements.put("O", "Atom Symbol");
					Selected_Elements.put("P", "Atom Symbol");
					Selected_Elements.put("S", "Atom Symbol");
					Selected_Elements.put("F", "Atom Symbol");
					Selected_Elements.put("Cl", "Atom Symbol");
					Selected_Elements.put("Br", "Atom Symbol");
					Selected_Elements.put("I", "Atom Symbol");
					Selected_Elements.put("Se", "Atom Symbol");
					Selected_Elements.put("B", "Atom Symbol");

					try {
						for (String element : String_elements) {
							if (Selected_Elements.containsKey(element)) {
								int temp = 0;
							} else {
								bufferWriter.write(filename + "\n");

							}
						}
						// }
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
