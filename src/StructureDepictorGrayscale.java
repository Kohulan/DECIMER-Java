

import java.awt.Graphics;
import java.awt.color.ColorSpace;
import java.awt.image.BufferedImage;
import java.awt.image.ColorConvertOp;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.text.DecimalFormat;

import javax.imageio.ImageIO;
import javax.imageio.stream.ImageInputStream;
import javax.imageio.stream.ImageOutputStream;

import org.openscience.cdk.CDKConstants;
import org.openscience.cdk.depict.DepictionGenerator;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.io.iterator.IteratingSDFReader;
import org.openscience.cdk.layout.StructureDiagramGenerator;
import org.openscience.cdk.silent.SilentChemObjectBuilder;

import ij.IJ;
import ij.ImagePlus;
import ij.process.ImageConverter;

public class StructureDepictorGrayscale {
	String temp = "";
	String moleculeTitle = null;
	int moleculeCount = 0;
	boolean verbose = false;

	public static void main(String[] args) throws CDKException, IOException {
		long startTime = System.nanoTime();
		File folder = new File("D:\\Project\\Chembl_Data\\Test2\\");
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
				//System.out.println(filename);
				filename = filename.substring(0, filename.length() - 4);
				while (sdf.hasNext()) {
					layedOutMol = sdf.next();
					layedOutMol.setProperty(CDKConstants.TITLE, file);
					DepictionGenerator dptgen = new DepictionGenerator().withSize(1048,1048).withAtomValues();
					dptgen.depict(layedOutMol).writeTo(filename+"_M1_G.png");
				    
					/*
					IJ.open(filename+"_M1_G.png");
					ImagePlus img = IJ.getImage();
					ImageConverter iconv = new ImageConverter(img);
					iconv.convertToGray8();
					img.updateAndDraw();
					IJ.save(filename + "_M1_G.png");
					**/
					
					BufferedImage in = ImageIO.read(new File(filename+"_M1_G.png"));
					BufferedImage out = new BufferedImage(in.getWidth(), in.getHeight(), BufferedImage.TYPE_INT_ARGB);
					ColorConvertOp op = new ColorConvertOp(in.getColorModel().getColorSpace(), ColorSpace.getInstance(ColorSpace.CS_GRAY),  null);
					op.filter(in, out);
									
					ImageIO.write(out, "png", new File(filename+"_M1_G.png"));
					
					}
				sdf.close();
				//System.out.println(filename+"_M1_G.png");
				
				
					
			}
		}

		long endTime = System.nanoTime() - startTime;
		double seconds = (double) endTime / 1000000000.0;
		DecimalFormat d = new DecimalFormat(".###");
		System.out.println("All images rendered successfully!! Time: "+ d.format(seconds)+" seconds");
	}
}


//BufferedImage buf = new BufferedImage(1048, 1048, BufferedImage.TYPE_3BYTE_BGR);
//dptgen.depict(layedOutMol).writeTo(filename+"_M1_G.png")