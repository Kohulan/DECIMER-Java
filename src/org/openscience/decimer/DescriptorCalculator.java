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
import java.text.DecimalFormat;

import javax.vecmath.Point2d;

import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.geometry.GeometryTools;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.io.iterator.IteratingSDFReader;
import org.openscience.cdk.layout.StructureDiagramGenerator;
import org.openscience.cdk.qsar.DescriptorValue;
import org.openscience.cdk.qsar.descriptors.molecular.APolDescriptor;
import org.openscience.cdk.qsar.descriptors.molecular.AromaticAtomsCountDescriptor;
import org.openscience.cdk.qsar.descriptors.molecular.AromaticBondsCountDescriptor;
import org.openscience.cdk.qsar.descriptors.molecular.AtomCountDescriptor;
import org.openscience.cdk.qsar.descriptors.molecular.BPolDescriptor;
import org.openscience.cdk.qsar.descriptors.molecular.BondCountDescriptor;
import org.openscience.cdk.qsar.descriptors.molecular.EccentricConnectivityIndexDescriptor;
import org.openscience.cdk.qsar.descriptors.molecular.HBondAcceptorCountDescriptor;
import org.openscience.cdk.qsar.descriptors.molecular.HBondDonorCountDescriptor;
import org.openscience.cdk.qsar.descriptors.molecular.LargestChainDescriptor;
import org.openscience.cdk.qsar.descriptors.molecular.LargestPiSystemDescriptor;
import org.openscience.cdk.qsar.descriptors.molecular.LongestAliphaticChainDescriptor;
import org.openscience.cdk.qsar.descriptors.molecular.RotatableBondsCountDescriptor;
import org.openscience.cdk.qsar.descriptors.molecular.RuleOfFiveDescriptor;
import org.openscience.cdk.qsar.descriptors.molecular.TPSADescriptor;
import org.openscience.cdk.qsar.descriptors.molecular.WeightDescriptor;
import org.openscience.cdk.qsar.descriptors.molecular.XLogPDescriptor;
import org.openscience.cdk.silent.SilentChemObjectBuilder;

public class DescriptorCalculator {

	public static void main(String[] args) throws CDKException, IOException {
		IAtomContainer molecule;
		StructureDiagramGenerator sdg = null;
		int x;
		long startTime = System.nanoTime();
		File folder = new File("D:\\Project\\Chembl_Data\\Diverse_Subset_20180805\\SDFs");
		File[] listOfFiles = folder.listFiles();

		File fileout = new File("D:\\Project\\Chembl_Data\\Diverse_Subset_20180805\\Diverse_set_descriptors.tsv");
		if (!fileout.exists()) {
			fileout.createNewFile();
		}

		FileWriter fw = new FileWriter(fileout.getAbsoluteFile(), true);
		BufferedWriter bufferWriter = new BufferedWriter(fw);
		bufferWriter.write("Structure_ID\t"
				+ "Molweight\tRotatable_bonds"
				+ "\tAtom_count\tBond_Count"
				+ "\tAtomic_Polarizabilities\tBond_Polarizabilities\tH_Bond_Acceptor\tH_Bond_Donor\tTopological_Polar_Surface_Area\t"
				+ "Eccentric_Connectivity_Index\t"+"No_of_large_chains\tNo_of_largest_pi_systems\tNo_of_largest_aliphatic_chains\tRule_of_Five\tXlogP\n");

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

					// Molecular Descriptors
					DescriptorValue weight = new WeightDescriptor().calculate(molecule);
					DescriptorValue rotor = new RotatableBondsCountDescriptor().calculate(molecule);

					// Constitutional Descriptors
					DescriptorValue atom = new AtomCountDescriptor().calculate(molecule);
					DescriptorValue bondcount = new BondCountDescriptor().calculate(molecule);
					DescriptorValue aroatom = new AromaticAtomsCountDescriptor().calculate(molecule);
					DescriptorValue arobond = new AromaticBondsCountDescriptor().calculate(molecule);
					DescriptorValue ruleof5 = new RuleOfFiveDescriptor().calculate(molecule);
					DescriptorValue xlogp = new XLogPDescriptor().calculate(molecule);

					// Electronic Descriptors
					DescriptorValue apol = new APolDescriptor().calculate(molecule);
					DescriptorValue bpol = new BPolDescriptor().calculate(molecule);
					DescriptorValue hacc = new HBondAcceptorCountDescriptor().calculate(molecule);
					DescriptorValue hdon = new HBondDonorCountDescriptor().calculate(molecule);
					DescriptorValue tpsa = new TPSADescriptor().calculate(molecule);

					// Topological Descriptors
					DescriptorValue ecconnect = new EccentricConnectivityIndexDescriptor().calculate(molecule);
					DescriptorValue largestchain = new LargestChainDescriptor().calculate(molecule);
					DescriptorValue largestpi = new LargestPiSystemDescriptor().calculate(molecule);
					DescriptorValue largestali = new LongestAliphaticChainDescriptor().calculate(molecule);

					for (double i = 0.0; i < 360.0; i = i + 45) {

						bufferWriter.write(filename + "\t" + weight.getValue() + "\t"
								+ rotor.getValue() + "\t" + atom.getValue() + "\t" + bondcount.getValue() + "\t"
								 + "\t" + apol.getValue() + "\t"
								+ bpol.getValue() + "\t" + hacc.getValue() + "\t" + hdon.getValue() + "\t"
								+ tpsa.getValue() + "\t" + ecconnect.getValue() + "\t" + largestchain.getValue() + "\t"
								+ largestpi.getValue() + "\t" + largestali.getValue() + "\t" + ruleof5.getValue()+"\t"+xlogp.getValue()+"\t\n");

						// Rotating the molecule by 45 Degree and rendering the structure
						GeometryTools.rotate(molecule, point, (45 * Math.PI / 180.0));
					}

				}
				sdf.close();

			}
		}
		bufferWriter.close();
		long endTime = System.nanoTime() - startTime;
		double seconds = (double) endTime / 1000000000.0;
		DecimalFormat d = new DecimalFormat(".###");
		System.out.println(" Time: " + d.format(seconds) + " seconds");
	}
}
