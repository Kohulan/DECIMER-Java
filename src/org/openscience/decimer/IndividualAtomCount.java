/*
 * This Software is under the MIT License
 * Refer to LICENSE or https://opensource.org/licenses/MIT for more information
 * Copyright (c) 2017, Kohulan Rajan
 */

package org.openscience.decimer;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import org.openscience.cdk.exception.CDKException;

import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.io.iterator.IteratingSDFReader;
import org.openscience.cdk.qsar.DescriptorValue;
import org.openscience.cdk.qsar.descriptors.molecular.AtomCountDescriptor;
import org.openscience.cdk.qsar.descriptors.molecular.BondCountDescriptor;
import org.openscience.cdk.silent.SilentChemObjectBuilder;

public class IndividualAtomCount {

	public static void main(String[] args) throws CDKException, IOException {
		IAtomContainer molecule;
		int x;

		String input_directory = "D:\\Project\\Chembl_Data\\Chembl_23_7\\Sorted";

		File folder = new File(input_directory);
		File[] listOfFiles = folder.listFiles();
		File fileout = new File("D:\\Project\\Chembl_Data\\Chembl_23_7\\Atomcount.csv");
		if (!fileout.exists()) {
			fileout.createNewFile();
		}

		FileWriter fw = new FileWriter(fileout.getAbsoluteFile(), true);
		BufferedWriter bufferWriter = new BufferedWriter(fw);
		bufferWriter.write("Compound,C_Count,N_Count,O_Count,H_Count\n");
		
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

				// Assigning parameters
				String c = "C";
				String n = "N";
				String o = "O";
				String h = "H";

				Object[] param_c = { c };
				Object[] param_n = { n };
				Object[] param_o = { o };
				Object[] param_h = { h };

				AtomCountDescriptor atomcount_C = new AtomCountDescriptor();
				AtomCountDescriptor atomcount_N = new AtomCountDescriptor();
				AtomCountDescriptor atomcount_O = new AtomCountDescriptor();
				AtomCountDescriptor atomcount_H = new AtomCountDescriptor();

				atomcount_C.setParameters(param_c);
				atomcount_H.setParameters(param_h);
				atomcount_N.setParameters(param_n);
				atomcount_O.setParameters(param_o);

				

				while (sdf.hasNext()) {

					molecule = sdf.next();
					DescriptorValue atomc = atomcount_C.calculate(molecule);
					DescriptorValue atomh = atomcount_H.calculate(molecule);
					DescriptorValue atomn = atomcount_N.calculate(molecule);
					DescriptorValue atomo = atomcount_O.calculate(molecule);
					DescriptorValue bondcount = new BondCountDescriptor().calculate(molecule);

					bufferWriter.write(filename + "," + atomc.getValue() + "," + atomn.getValue() + ","
							+ atomo.getValue() + "," + atomh.getValue() + "," + bondcount.getValue() + "\n");

				}
				sdf.close();

			}
		}
		bufferWriter.close();
		System.out.println("Labels Created\n\n");
	}
}
