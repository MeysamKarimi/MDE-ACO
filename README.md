# MDE-ACO
Source code and the Mutation Analysis for the ACO Approach for Test Case Generation 

## Source code and the Mutation Analysis for the ACO Approach for Test Case Generation 

This project contains the implementation of ACO approach to generate metamodel instances in the context of model transfromation testing. Also, this project supports and verifies the mutation analysis performed in [1]. It has been developed with Eclipse Modeling Tools, version IDE 2020-06 (4.16.0). MDE Testing Framework [2], anATLyzer [3] and ATL plugin are needed.

### ACO
There is a folder named MDE-ACO contating the ACO approach to generate test models. Folder "src" contains the source code written in Java 8. Folder "Inputs", contais the input metamodels in which models should be generated from. For instance "Families.ecore" that is the case study described in [1], is located in this folder. Folder "Outputs" will hold generated models after running the ACO. For instance, there are four models that conform to "Families.ecore" described in [1] resulting from ACO execution.

### Mutation Analysis

This project contains the mutation analysis performed on five case studies [1]. Folders “analysis_caseStudyName” contain all the artifacts regarding each of the case studies. For instance, there are two zip files namely ACO and Random which refer to mutation analysis of the approaches. There are three folders in each approach, namely, zoo, mottu and wimmer where refer to typing, semantic and syntax mutants, respectively [2]. Mutants, mutant-killing models and sample of output models resulting from the running of the mutants can be found in each folder.

In order to re-execute it with the different case studies and the different mutants, some lines of code in [2] must be commented and uncommented. After doing so, the class can be executed by right-clicking and selecting Run As -> JUnit Plug-in Test. The code in this Java class contains explanations for executing each case study, for instance:

```java
String testcase = "Grafcet2PetriNet";
String[] metamodelFiles = new String[] { metamodel(testcase, "Grafcet.ecore"), metamodel(testcase, "PetriNet.ecore") };
String[] metamodelNames = new String[] { "Grafcet", "PetriNet" };	
AtlTransformation trafo = AtlTransformation.fromFile(trafo("Grafcet2PetriNet", "Grafcet2PetriNet.atl"), metamodelFiles, metamodelNames);
```

After executing the program, some information will be displayed in the console. At the end, it indicates the file with the result of the mutation analysis. This is a TXT file named "result" that is located in each case study folder of the containing the average result of model transformation mutants of the corresponding analysis_caseStudyName folder.


[1] Meysam Karimi, Shekoufeh Kolahdouz-Rahimi, Javier Troya. "Test model generation for model transformation testing applying ant colony optimization". Submitted, 2020

[2] Guerra, E., Cuadrado, J. S., & de Lara, J. (2019). Towards Effective Mutation Testing for ATL. In 22nd ACM/IEEE International Conference on Model Driven Engineering Languages and Systems, MODELS 2019, Munich, Germany, September 15-20, 2019 (pp. 78–88). IEEE. doi: 10.1109/MODELS.2019.00-13.

[3] J. S´anchez Cuadrado, E. Guerra, and J. de Lara, “Static analysis of model transformations,” IEEE Trans. Software Eng., vol. 43, no. 9, pp. 868–897, 2017.
