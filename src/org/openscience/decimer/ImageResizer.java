

import java.text.DecimalFormat;

import javax.imageio.ImageIO;
import javax.vecmath.Point2d;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;

import org.openscience.cdk.depict.DepictionGenerator;
import org.openscience.cdk.geometry.GeometryTools;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.io.iterator.IteratingSDFReader;
import org.openscience.cdk.layout.StructureDiagramGenerator;
import org.openscience.cdk.silent.SilentChemObjectBuilder;

public class ImageResizer {
	String temp = "";
	String moleculeTitle = null;
	int moleculeCount = 0;
	boolean verbose = false;

	public static void main(String[] args) throws Exception {
		IAtomContainer molecule;
		StructureDiagramGenerator sdg = null;
		int x;

		System.setProperty("java.awt.headless", "true");
		long startTime = System.nanoTime();
		GraphicsEnvironment.getLocalGraphicsEnvironment();
		System.out.println("Headless mode: " + GraphicsEnvironment.isHeadless());

		//Directories according to your data files
		String input_directory = "D:\\Project\\Chembl_Data\\test45\\"; 
		String report_directory = "D:\\Project\\Chembl_Data\\test45\\";
		String output_directory = "D:\\Project\\Chembl_Data\\test45\\";
		
		File folder = new File(input_directory);
		File[] listOfFiles = folder.listFiles();

		File fileout = new File(report_directory+"report.csv");
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
				sdg = new StructureDiagramGenerator();
				String filename = file.getName();
				filename = filename.substring(0, filename.length() - 4);
				while (sdf.hasNext()) {
					molecule = sdf.next();
					sdg.setMolecule(molecule);
					sdg.generateCoordinates();
					molecule = sdg.getMolecule();
					Point2d point = GeometryTools.get2DCenter(molecule);
					FileWriter fw = new FileWriter(fileout.getAbsoluteFile(), true);

					BufferedWriter bufferWriter = new BufferedWriter(fw);
					bufferWriter.write(filename + ", Success" + "\n");
					try {
						//for (double magnify = 1; magnify<=2.0; magnify=magnify+1) {
								for (double i = 0.0; i < 360.0; i = i + 45) {
double magnify =1.5;
							// Initial Depiction 32 Bit
							DepictionGenerator dptgen = new DepictionGenerator().withSize(1024, 1024).withAtomValues().withZoom(magnify);
							dptgen.depict(molecule).writeTo(output_directory + filename + ".jpg");
									//.writeTo("D:\\Project\\Chembl_Data\\test45/" + filename + "_R" + i +"_M"+magnify+ ".jpg");

							// Image conversion to 8 bit using ImageIO
							BufferedImage image = ImageIO.read(new File(output_directory + filename +".jpg"));
									//.read(new File("D:\\Project\\Chembl_Data\\test45/" + filename + "_R" + i+"_M"+magnify + ".jpg"));
							BufferedImage result = new BufferedImage(image.getWidth(), image.getHeight(),
									BufferedImage.TYPE_BYTE_GRAY);
							result.createGraphics().drawImage(image, 0, 0, Color.WHITE, null);
							ImageIO.write(result, "jpg",new File(output_directory + filename + ".jpg"));
									//new File("D:\\Project\\Chembl_Data\\test45/" + filename + "_R" + i+"_M"+magnify + ".jpg"));

							// Rotating the molecule by 45 Degree and rendering the structure
							GeometryTools.rotate(molecule, point, (45 * Math.PI / 180.0));
									}
						//}
					} catch (Exception e) {
						System.err.println(filename + " Error");
						e.printStackTrace();
					}
					bufferWriter.close();
					continue;

				}

				sdf.close();

			}
		}

		long endTime = System.nanoTime() - startTime;
		double seconds = (double) endTime / 1000000000.0;
		DecimalFormat d = new DecimalFormat(".###");
		System.out.println("All images rendered successfully!! Time: " + d.format(seconds) + " seconds");
	}
}
