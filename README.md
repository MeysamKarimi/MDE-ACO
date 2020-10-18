# MDE-ACO
Mutation Analysis for the ACO Approach for Test Case Generation 

## Implementation of the Mutation Analysis for the ACO Approach for Test Case Generation

This project supports and verifies the mutation analysis performed in [1]. It has been developed with Eclipse Modeling Tools, version IDE 2020-06 (4.16.0). MDE Testing Framework [2], anATLyzer [3] and ATL plugin are needed. There is a project named Mutation_Analysis.

## Mutation_Analysis

This project contains the mutation analysis performed on six case studies [1]. Folders “analysis_caseStudyName” contain all the artifacts regarding each of the case studies. For instance, there is a folder named "results" containing the model transformation mutants and the models generated automatically by our multi-objective approach are located in folder "model" ("AutomaticModel_CaseStudyName[modelNumber].xmi").

The executable Java file is available at src->mutation.evaluation.zoo, src->mutation.evaluation.mottu and src->mutation.evaluation.wimmer.

In order to execute it with the different case studies and the different mutants, some lines of code must be commented and uncommented. After doing so, the class can be executed by right-clicking and selecting Run As -> JUnit Plug-in Test. The code in this Java class contains explanations for executing each case study, for instance:

String testcase = "Grafcet2PetriNet";
String[] metamodelFiles = new String[] { metamodel(testcase, "Grafcet.ecore"), metamodel(testcase, "PetriNet.ecore") };
String[] metamodelNames = new String[] { "Grafcet", "PetriNet" };	
AtlTransformation trafo = AtlTransformation.fromFile(trafo("Grafcet2PetriNet", "Grafcet2PetriNet.atl"), metamodelFiles, metamodelNames);

After executing the program, some information will be displayed in the console. At the end, it indicates the file with the result of the mutation analysis. This is a CSV file that is placed in the results folder of the corresponding analysis_caseStudyName folder.

[1] Meysam Karimi, Shekoufeh Kolahdouz-Rahimi, Javier Troya. "Test model generation for model transformation testing applying ant colony optimization". Submitted, 2020
[2] Guerra, E., Cuadrado, J. S., & de Lara, J. (2019). Towards Effective Mutation Testing for ATL. In 22nd ACM/IEEE International Conference on Model Driven Engineering Languages and Systems, MODELS 2019, Munich, Germany, September 15-20, 2019 (pp. 78–88). IEEE. doi: 10.1109/MODELS.2019.00-13.
[3] J. S´anchez Cuadrado, E. Guerra, and J. de Lara, “Static analysis of model transformations,” IEEE Trans. Software Eng., vol. 43, no. 9, pp. 868–897, 2017.
