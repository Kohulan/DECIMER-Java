package org.openscience.decimer;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DecimalFormat;

import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.io.iterator.IteratingSDFReader;
import org.openscience.cdk.qsar.DescriptorValue;
import org.openscience.cdk.qsar.descriptors.molecular.WeightDescriptor;
import org.openscience.cdk.silent.SilentChemObjectBuilder;

public class MolWeightCalculator {
	public static void main(String[] args) throws CDKException, IOException {
		long startTime = System.nanoTime();
		File folder = new File("D:\\Project\\Chembl_Data\\Chembl_23_10\\");
		File[] listOfFiles = folder.listFiles();
		int x;
		File fileout = new File("D:\\Project\\Chembl_Data\\test\\Chembl_23_10.csv");
				if (!fileout.exists()) {
					fileout.createNewFile();
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
				IAtomContainer ac = SilentChemObjectBuilder.getInstance().newAtomContainer();
				String filename = file.getName();
				//filename = filename.substring(0, filename.length()-4);
				
				FileWriter fw = new FileWriter(fileout.getAbsoluteFile(), true);
				
				BufferedWriter bufferWriter = new BufferedWriter(fw);
				
				while (sdf.hasNext()) {
					ac = sdf.next();
					
					WeightDescriptor wei = new WeightDescriptor();
					DescriptorValue weight = wei.calculate(ac);
					bufferWriter.write( filename + "," + weight.getValue() +  "\n");								
					}
				sdf.close();
				bufferWriter.close();
			}
		}

		long endTime = System.nanoTime() - startTime;
		double seconds = (double) endTime / 1000000000.0;
		DecimalFormat d = new DecimalFormat(".###");
		System.out.println(" Time: "+ d.format(seconds)+" seconds");
	}
}
