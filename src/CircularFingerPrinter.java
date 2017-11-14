import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.BitSet;
import java.util.Map;

import org.openscience.cdk.CDKConstants;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.fingerprint.CircularFingerprinter;
import org.openscience.cdk.fingerprint.IBitFingerprint;
import org.openscience.cdk.fingerprint.ICountFingerprint;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.io.iterator.IteratingSDFReader;

import org.openscience.cdk.silent.SilentChemObjectBuilder;

public class CircularFingerPrinter {

	public static void main(String[] args) throws IOException, CDKException {
		FileReader sdfile = null;
		try {
			sdfile = new FileReader(new File("CHEMBL404963.sdf"));
		} catch (FileNotFoundException e) {
			System.err.println("file" + " not found");
			System.exit(1);
		}
		
		IteratingSDFReader sdf = new IteratingSDFReader(sdfile, SilentChemObjectBuilder.getInstance());
		IAtomContainer ac = SilentChemObjectBuilder.getInstance().newAtomContainer();

		while (sdf.hasNext()) {
			ac = sdf.next();
			ac.setProperty(CDKConstants.TITLE, sdfile);
			 CircularFingerprinter fp = new CircularFingerprinter();
//BitSet fpcount =fp.getBitFingerprint(ac).asBitSet();
			
			 			
			 
			System.out.println("Finger Print count "+);
		}
		sdf.close();
	}

}
