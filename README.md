# DECIMER
The project aims to develop methods to employ deep learning to recognize and interpret chemical structure from images in the printed and online literature with the aim of re-discovering scientific facts about natural products and their meta-data. The method, of course, will be generically applicable to most organic chemistry publications.  
The project is co-supervised by Prof. Christoph Steinbeck, Jena, and Prof. Achim Zielesny, Westphalian University of Applied Sciences, Recklinghausen.

- This repository contains scripts written in Java using [CDK](https://cdk.github.io) Libraries at the back end.
    - Here we have scripts that we used to explore various ways in depicting images. 
    - We have scripts used to generate SMILES, Inchis from SDF files, and vice versa. 
    - We also have scripts written to curate big chemical databases, to get relevant data for our own use case
    - You can also find tools to check the validation of our machine learning algorithm written in Python. Which is hosted in [DECIMER-I2S](https://github.com/Kohulan/DECIMER-Image-to-SMILES) and [DECIMER-Python](https://github.com/Kohulan/Decimer-Python) Repositories.

#### DECIMER source directory layout
```bash
├── DECIMER/src/org/openscience/
    └─decimer/
        ├ ─ ConformerGeneration.java
        ├ ─ DescriptorCalculator.java
        ├ ─ HIsotopeFinder.java
        ├ ─ Image2Array.java
        ├ ─ Image2ArrayRandom.java
        ├ ─ ImageResizer.java
        ├ ─ IndividualAtomCount.java
        ├ ─ LabelCreator.java
        ├ ─ MolWeightCalculator.java
        ├ ─ MoleculeFilters.java
        ├ ─ MoleculeSelector.java
        ├ ─ RejectChargedMols.java
        ├ ─ SmilesDepictor.java
        ├ ─ StrcutureDepitor8bit.java
        ├ ─ StructureDepictor.java
        ├ ─ StructureDepictorGrayscale.java
        └ ─ TanimotoCalculator.java

```

## License:
- This project is licensed under the MIT License - see the [LICENSE](https://github.com/Kohulan/Decimer-Python/blob/master/LICENSE) file for details

## Author:
- [Kohulan](github.com/Kohulan)

![GitHub Logo](https://github.com/Kohulan/DECIMER-Image-to-SMILES/blob/master/assets/DECIMER_logo.png?raw=true)
## Project Website
- [DECIMER](https://kohulan.github.io/Decimer-Official-Site/)
## Research Group
- [Website](https://cheminf.uni-jena.de)

![GitHub Logo](https://github.com/Kohulan/DECIMER-Image-to-SMILES/blob/master/assets/CheminfGit.png?raw=true)
