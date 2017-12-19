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

import org.openscience.cdk.depict.DepictionGenerator;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.io.iterator.IteratingSDFReader;
import org.openscience.cdk.layout.StructureDiagramGenerator;
import org.openscience.cdk.silent.SilentChemObjectBuilder;

public class StructureDepictorGrayscale {
	String temp = "";
	String moleculeTitle = null;
	int moleculeCount = 0;
	boolean verbose = false;

	public static void main(String[] args) throws CDKException, IOException {
		IAtomContainer molecule;
		StructureDiagramGenerator sdg = null;
		int x;		
		
		long startTime = System.nanoTime();
		File folder = new File("D:\\Project\\Chembl_Data\\test\\");
		File[] listOfFiles = folder.listFiles();
	
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
				String filename = file.toString();
				filename = filename.substring(0, filename.length() - 4);
				while (sdf.hasNext()) {
					molecule = sdf.next();
					sdg.setMolecule(molecule);
					sdg.generateCoordinates();
					molecule = sdg.getMolecule();
					DepictionGenerator dptgen = new DepictionGenerator().withSize(1048,1048).withAtomValues();
					dptgen.depict(molecule).writeTo(filename+"_M1_G.png");
					}
				sdf.close();
				
			}
		}

		long endTime = System.nanoTime() - startTime;
		double seconds = (double) endTime / 1000000000.0;
		DecimalFormat d = new DecimalFormat(".###");
		System.out.println("All images rendered successfully!! Time: "+ d.format(seconds)+" seconds");
	}
}

