import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import org.openscience.cdk.CDKConstants;
import org.openscience.cdk.depict.DepictionGenerator;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.io.iterator.IteratingSDFReader;
import org.openscience.cdk.layout.StructureDiagramGenerator;
import org.openscience.cdk.silent.SilentChemObjectBuilder;

public class StructureDepictor {

	String temp = "";
	String moleculeTitle = null;
	int moleculeCount = 0;
	boolean verbose = false;

	public static void main(String[] args) throws CDKException, IOException {

		for (int x = 0; x < args.length; x++) {

			FileReader sdfile = null;
			try {
				sdfile = new FileReader(new File(args[x]));
			} catch (FileNotFoundException e) {
				System.err.println(args[x] + " not found");
				System.exit(1);
			}

			IteratingSDFReader sdf = new IteratingSDFReader(sdfile, SilentChemObjectBuilder.getInstance());
			IAtomContainer ac = SilentChemObjectBuilder.getInstance().newAtomContainer();
			StructureDiagramGenerator sdg = new StructureDiagramGenerator();
			sdg.setMolecule(ac);
			sdg.generateCoordinates();
			IAtomContainer layedOutMol = sdg.getMolecule();

			while (sdf.hasNext()) {
				layedOutMol = sdf.next();
				layedOutMol.setProperty(CDKConstants.TITLE, args[x]);
				DepictionGenerator dptgen = new DepictionGenerator().withSize(512, 512).withAtomColors()
						.withAtomValues().withFillToFit();
				dptgen.depict(layedOutMol).writeTo(args[x] + ".png");
			}
			sdf.close();
			System.out.println("All images rendered successfully!!");
		}

	}

}
