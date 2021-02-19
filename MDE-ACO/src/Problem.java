import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Hashtable;
import java.util.Random;

//import org.eclipse.emf.common.util.EList;
//import org.eclipse.emf.common.util.URI;
//import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EClassifier;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.emf.ecore.xmi.impl.EcoreResourceFactoryImpl;
import org.eclipse.emf.ecore.xmi.impl.XMIResourceFactoryImpl;
import org.eclipse.emf.ecore.xmi.impl.XMLResourceImpl;

import org.eclipse.emf.common.notify.AdapterFactory;
import org.eclipse.emf.common.notify.Notifier;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.TreeIterator;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;



public class Problem {

	Resource metaModel;	
	public Resource getMetaModel() {
		return metaModel;
	}
	public void setMetaModel(Resource metaModel) {
		this.metaModel = metaModel;
	}
	
	int numberOfMetaModelElements;	
	public int getNumberOfMetaModelElements() {
		return numberOfMetaModelElements;
	}
	public void setNumberOfMetaModelElements(int numberOfMetaModelElements) {
		this.numberOfMetaModelElements = numberOfMetaModelElements;
	}
	
	EPackage ePackage; 
	public EPackage getePackage() {
		return ePackage;
	}
	public void setePackage(EPackage ePackage) {
		this.ePackage = ePackage;
	}
	
	
	public Problem() throws IOException 
	{
		LoadMetamodel();
		//LoadMetamodel2();		
		LoadWellFormedRules();
		CalculateCoverageNeeds();
	}
	
	private void CalculateCoverageNeeds() {
		ExtractAttribuePartitionInformation();
		ExtractAssociationPartitionInformation();
		
	}
	private void ExtractAssociationPartitionInformation() {
		
		
	}
	private void ExtractAttribuePartitionInformation() {
		
		
	}
	private void LoadMetamodel2() throws IOException {
		Resource metamodelResource = new XMLResourceImpl(URI.createFileURI("inputs/Grafcet.ecore"));
		metamodelResource.load(Collections.emptyMap());
		EcoreUtil.resolveAll(metamodelResource);		
		
		registerPackages(metamodelResource);	    			
	}
	private void LoadWellFormedRules() {
		// TODO Load OCLs		
	}
	private void LoadMetamodel() throws IOException { // loads the input metamodel		

		ResourceSet resourceSet = new ResourceSetImpl();

		resourceSet.getResourceFactoryRegistry().getExtensionToFactoryMap().put("ecore",
				new EcoreResourceFactoryImpl());
		resourceSet.getResourceFactoryRegistry().getExtensionToFactoryMap().put("xmi", new XMIResourceFactoryImpl());
		
//		Resource myMetaModel = resourceSet.getResource(URI.createFileURI("inputs/Grafcet.ecore"), true);
		Resource myMetaModel = resourceSet.getResource(URI.createFileURI("inputs/Families.ecore"), true);
//		Resource myMetaModel = resourceSet.getResource(URI.createFileURI("inputs/hsm.ecore"), true);
//		Resource myMetaModel = resourceSet.getResource(URI.createFileURI("inputs/Tutorial.ecore"), true);		
//		Resource myMetaModel = resourceSet.getResource(URI.createFileURI("inputs/CPL.ecore"), true);
//		Resource myMetaModel = resourceSet.getResource(URI.createFileURI("inputs/petrinet.ecore"), true);
//		Resource myMetaModel = resourceSet.getResource(URI.createFileURI("inputs/BibTeX.ecore"), true);
//		Resource myMetaModel = resourceSet.getResource(URI.createFileURI("inputs/GraphML.ecore"), true);		
//		Resource myMetaModel = resourceSet.getResource(URI.createFileURI("inputs/pnml.ecore"), true);
		
//		
//		Resource myMetaModel = resourceSet.getResource(URI.createFileURI("inputs/Class.ecore"), true);
		
		
		EPackage univEPackage = (EPackage) myMetaModel.getContents().get(0);		
		//resourceSet.getPackageRegistry().put("http://grafcet/1.0", univEPackage);				
		//resourceSet.getPackageRegistry().put("http://grafcet/1.0", univEPackage);

//		Resource myModel = resourceSet.getResource(URI.createURI("inputs/Grafcet.xmi"), true);
		
		this.metaModel = myMetaModel;		
		this.ePackage = univEPackage;
		this.numberOfMetaModelElements = univEPackage.getEClassifiers().size();
		
		System.out.println("Problem.metaModel is loaded!");
	}		
	
	private static boolean isFailed(org.eclipse.emf.common.util.BasicDiagnostic diagnosticChain) {
		return (diagnosticChain.getSeverity() & org.eclipse.emf.common.util.Diagnostic.ERROR) == org.eclipse.emf.common.util.Diagnostic.ERROR;
	}
	
	private static void registerPackages(Resource resource) {
		EObject eObject = resource.getContents().get(0);
		if (eObject instanceof EPackage) {
			EPackage p = (EPackage) eObject;
			EPackage.Registry.INSTANCE.put(p.getNsURI(), p);
		}
	}
}
