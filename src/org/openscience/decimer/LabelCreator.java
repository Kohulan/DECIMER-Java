import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import javax.vecmath.Point2d;

import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.geometry.GeometryTools;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.io.iterator.IteratingSDFReader;
import org.openscience.cdk.qsar.DescriptorValue;
import org.openscience.cdk.qsar.descriptors.molecular.AtomCountDescriptor;
import org.openscience.cdk.qsar.descriptors.molecular.BondCountDescriptor;
import org.openscience.cdk.silent.SilentChemObjectBuilder;

public class LabelCreator {

	public static void main(String[] args) throws CDKException, IOException {
		IAtomContainer molecule;
		int x;

		String input_directory = ""; 
		
		File folder = new File(input_directory);
		File[] listOfFiles = folder.listFiles();
		File fileout = new File("D:\\Project\\Chembl_Data\\test\\Labels_train.csv");
		if (!fileout.exists()) {
			fileout.createNewFile();
		}

		FileWriter fw = new FileWriter(fileout.getAbsoluteFile(), true);
		BufferedWriter bufferWriter = new BufferedWriter(fw);

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
					Point2d point = GeometryTools.get2DCenter(molecule);
					// Constitutional Descriptors
					DescriptorValue atom = new AtomCountDescriptor().calculate(molecule);
					DescriptorValue bondcount = new BondCountDescriptor().calculate(molecule);

					for (double i = 0.0; i < 360.0; i = i + 45.0) {

						bufferWriter
								.write(filename + "_R" + i + "," + atom.getValue() + "," + bondcount.getValue() + "\n");

						// Rotating the molecule by 45 Degree and rendering the structure
						GeometryTools.rotate(molecule, point, (45 * Math.PI / 180.0));
					}

				}
				sdf.close();

			}
		}
		bufferWriter.close();
		System.out.println("Labels Created\n\n");
	}
}
