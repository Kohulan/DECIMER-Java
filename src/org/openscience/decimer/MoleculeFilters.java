/* 
 *This Software is under the MIT License
 * Refer to LICENSE or https://opensource.org/licenses/MIT for more information
 * Copyright (c) 2019, Kohulan Rajan 
 */

/*This script was written to filter out molecules from larger database which follows these rules;
 * - Bond Count < 3.
 * - Bond Count > 40.
 * - Molecular weight > 1500 daltons.
 * - Disconnected molecules.
 * - Molecules hwhich has elements rather than these = {C,H,O,N,P,S,Cl,F,I,Br,B,Se}.
 * - Molecules with Hydrogen isoptopes D and T.
 */

package org.openscience.decimer;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.openscience.cdk.config.IsotopeFactory;
import org.openscience.cdk.config.Isotopes;
import org.openscience.cdk.graph.ConnectivityChecker;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IAtomContainerSet;
import org.openscience.cdk.interfaces.IElement;
import org.openscience.cdk.interfaces.IMolecularFormula;
import org.openscience.cdk.io.iterator.IteratingSDFReader;
import org.openscience.cdk.qsar.descriptors.molecular.WeightDescriptor;
import org.openscience.cdk.qsar.result.IDescriptorResult;
import org.openscience.cdk.silent.SilentChemObjectBuilder;
import org.openscience.cdk.tools.manipulator.AtomContainerManipulator;
import org.openscience.cdk.tools.manipulator.MolecularFormulaManipulator;

public class MoleculeFiltersunwanted {
	public static void main(String[] args) throws Exception {
		IAtomContainer molecule;
		int x;

		// Directories according to your data files
		String input_directory = "/beegfs/ce85vof/2019/After_Meeting_201905/Pubchem_Work/Data_from_Pubchem20190612/AllPubchem20190612/Unwanted/";
		String report_directory = "/beegfs/ce85vof/2019/After_Meeting_201905/Pubchem_Work/Data_from_Pubchem20190612/AllPubchem20190612/Filters/MoleculeFilters/";

		File folder = new File(input_directory);
		File[] listOfFiles = folder.listFiles();

		File fileout_2 = new File(report_directory + "Rejected_Mols_unwanted.txt");

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
						IAtomContainer mol_new = AtomContainerManipulator.removeHydrogens(molecule);
						IDescriptorResult weight = new WeightDescriptor().calculate(mol_new).getValue();
						Object fragments = ConnectivityChecker.partitionIntoMolecules(mol_new);
						int fragmentCount = ((IAtomContainerSet) fragments).getAtomContainerCount();

						// Molecule Filtering rules
						if (mol_new.getBondCount() < 3 || mol_new.getBondCount() > 40
								|| Double.valueOf(weight.toString()) >= 1500.0 || fragmentCount > 1
								|| SelectedElements(mol_new) == true || HIsotope(mol_new) == true) {
							bufferWriter2.write(filename + "\n");
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

		System.out.println("All process successfully completed!!");
	}

	// Function to check selected Elements
	public static boolean SelectedElements(IAtomContainer molecule) {
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
					@SuppressWarnings("unused")
					int x = 0;
				} else {
					return true;

				}
			}

		} catch (Exception e) {
			System.err.println(" Error");
			e.printStackTrace();
		}
		return false;

	}

	// Function to check Hydrogen Isotopes
	public static boolean HIsotope(IAtomContainer molecule) throws Exception {
		// configure Isotopes
		IsotopeFactory iso_H = Isotopes.getInstance();

		// Print file name with Hydrogen Isotopes
		try {
			iso_H.configureAtoms(molecule);
			for (IAtom atom : molecule.atoms()) {
				if (atom.getSymbol() == "H") {
					if (atom.getMassNumber() > 1) {
						return true;

					}
				}
			}

		} catch (Exception e) {
			System.err.println(" Error");
			e.printStackTrace();
		}
		return false;
	}

}
