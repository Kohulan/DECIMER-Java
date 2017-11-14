import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.text.DecimalFormat;

import org.openscience.cdk.CDKConstants;
import org.openscience.cdk.depict.DepictionGenerator;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.io.iterator.IteratingSDFReader;
import org.openscience.cdk.layout.StructureDiagramGenerator;
import org.openscience.cdk.silent.SilentChemObjectBuilder;

public class StructureDepictor_eclipse {
	String temp = "";
	String moleculeTitle = null;
	int moleculeCount = 0;
	boolean verbose = false;

	public static void main(String[] args) throws CDKException, IOException {
		long startTime = System.nanoTime();
		File folder = new File("D:\\Project\\Chembl_Data\\test\\");
		File[] listOfFiles = folder.listFiles();
		int x;
		for (x = 0; x < listOfFiles.length; x++) {
			File file = listOfFiles[x];
			if (file.isFile() && file.getName().endsWith(".sdf")) { // change .pdb if you are using a different file
																	// format

				FileReader sdfile = null;
				try {
					sdfile = new FileReader(file);
				} catch (FileNotFoundException e) {
					System.err.println(file + " not found");
					System.exit(1);
				}
				IteratingSDFReader sdf = new IteratingSDFReader(sdfile, SilentChemObjectBuilder.getInstance());
				IAtomContainer ac = SilentChemObjectBuilder.getInstance().newAtomContainer();
				StructureDiagramGenerator sdg = new StructureDiagramGenerator();
				sdg.setMolecule(ac);
				sdg.generateCoordinates();
				IAtomContainer layedOutMol = sdg.getMolecule();
				String filename = file.toString();
				filename = filename.substring(0, filename.length() - 4);
				while (sdf.hasNext()) {
					layedOutMol = sdf.next();
					layedOutMol.setProperty(CDKConstants.TITLE, file);
					DepictionGenerator dptgen = new DepictionGenerator().withSize(2048,2048).withAtomColors()
							.withAtomValues();
					dptgen.depict(layedOutMol).writeTo(filename+"_M2.png");
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
