import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.SortedSet;
import java.util.concurrent.TimeUnit;

import org.eclipse.emf.common.util.BasicMonitor;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.emf.ecore.xmi.XMIResource;
import org.eclipse.emf.ecore.xmi.impl.EcoreResourceFactoryImpl;
import org.eclipse.emf.ecore.xmi.impl.XMIResourceFactoryImpl;
import org.eclipse.emf.ecore.xmi.impl.XMIResourceImpl;
import org.eclipse.emf.compare.*;
import org.eclipse.emf.compare.EMFCompare;
import org.eclipse.emf.compare.merge.BatchMerger;
import org.eclipse.emf.compare.merge.IBatchMerger;
import org.eclipse.emf.compare.merge.IMerger;
import org.eclipse.emf.compare.scope.DefaultComparisonScope;
import org.eclipse.emf.compare.scope.IComparisonScope;

import com.google.common.base.Stopwatch;

public class ACO {

	public static void main(String[] args) throws IOException {

		Stopwatch stopwatch = Stopwatch.createStarted();
		// Problem Definition
		//Problem problemInstance = null;
//		try {
			// inja
//			URI uri1 = URI.createFileURI("inputs/model_0_iteration_0.xmi");
//			URI uri2 = URI.createFileURI("inputs/model_0_iteration_1.xmi");
//						
//
//			Resource.Factory.Registry.INSTANCE.getExtensionToFactoryMap().put("ecore", new XMIResourceFactoryImpl());
//			Resource.Factory.Registry.INSTANCE.getExtensionToFactoryMap().put("xmi", new XMIResourceFactoryImpl());			
//
//			ResourceSet resourceSet1 = new ResourceSetImpl();
//			ResourceSet resourceSet2 = new ResourceSetImpl();
//
//			resourceSet1.getResource(uri1, true);
//			resourceSet2.getResource(uri2, true);
//
//			IComparisonScope scope = new DefaultComparisonScope(resourceSet1, resourceSet2, null);
//			Comparison comparison = EMFCompare.builder().build().compare(scope);
//
//			List<Diff> differences = comparison.getDifferences();
//			// Let's merge every single diff
//			IMerger.Registry mergerRegistry = new IMerger.RegistryImpl();
//			IBatchMerger merger = new BatchMerger(mergerRegistry);
//			merger.copyAllLeftToRight(differences, new BasicMonitor());
//			// ta inja
			//problemInstance = new Problem();
//		} catch (IOException e) {
//			e.printStackTrace();
//		}

		Problem problemInstance = new Problem();
		int nModels = 100; // Get the number of models that the user needs
		int nElements = 30; // Get the number of elements that the user want in each model (Jahanbin)
		// ACO Parameters
		int maxIt = 10; // Maximum number of Iterations
		int nAnt = 30; // 30 Number of Ants (Population Size)
		int Q = 2;
		double tau0 = 1; // Q / (problemInstance.numberOfMetaModelElements * 0.5);// 1; // Initial
							// Phromone
		int alpha = 1; // Phromone Exponential Weight
		int beta = 1; // Heurist Exponential Weight
		double rho = 0.05; // Evaporation Rate

		// Initialization
		double eta0 = 1;// problemInstance.numberOfMetaModelElements; // Heuristic information (harchi
						// bishtar coverage
						// bishtar)

//		double[] tau = new double[problemInstance.numberOfMetaModelElements]; // Phromone Array
//		double[] eta = new double[problemInstance.numberOfMetaModelElements]; // Heuristic Array
//		for (int i = 0; i < tau.length; i++) {
//			tau[i] = tau0;
//			eta[i] = eta0;
//		}

//		List<Double> tau = new ArrayList<>(); // be ezaye har yaal
		HashMap<String, HashMap<String, Double>> tau = new HashMap<String, HashMap<String, Double>>(); // tau[b]
																										// -><L,0.3>,
																										// <U, 0.2>, <M,
																										// 05>
		// List<Double> eta = new ArrayList<>();
		HashMap<String, HashMap<String, Double>> eta = new HashMap<String, HashMap<String, Double>>();
//		double[] eta = new double[problemInstance.numberOfMetaModelElements]; // Heuristic Array
//		for (int i = 0; i < tau.length; i++) {
//			tau[i] = tau0;
//			eta[i] = eta0;
//		}

		double[] bestCost = new double[maxIt]; // Array to Hold Best Cost Values
		for (int i = 0; i < bestCost.length; i++) {
			bestCost[i] = 0;
		}

		// Ant Colony Matrix
		Ant[] ants = new Ant[nAnt];
		for (Ant ant : ants) {
			ant = new Ant();
		}

		// Best Ant in an iteration
		Ant bestAnt = new Ant();
		bestAnt.model = null;
		bestAnt.fitnessValue = 0;

		// Best Ant in an iteration goes here for Pareto-Front calculation
		ArrayList<Ant> finalResult = new ArrayList<Ant>();

		// ACO Main Loop
		for (int it = 1; it <= maxIt; it++) {
//			problemInstance = null;
//			try {
//				problemInstance = new Problem();
//			} catch (IOException e) {
//				e.printStackTrace();
//			}
			for (int k = 0; k < nAnt; k++) {
				ants[k] = null;
			}	
			
			List<Ant> annts = new ArrayList<Ant>();
			Arrays.asList(ants).parallelStream() 
            .forEach( 
                s -> { 
                	Ant currentAnt = InitializeAnt(s, problemInstance, tau, eta, alpha, beta, nModels, nElements);
                	annts.add(currentAnt);
                }); 
		    
			for (int k = 0; k < annts.size(); k++) {

				// Move Ants
 				//ants[k] = InitializeAnt(ants[k], problemInstance, tau, eta, alpha, beta, nModels, nElements);

//				int oskol = 0;
//				// save bikhod!
//				for (Resource resource : annts.get(k).getModels()) {
//					XMIResource xmiResource = new XMIResourceImpl(
//							URI.createFileURI("outputs/result" + "__alaki" + oskol++ + ".xmi"));
//					xmiResource.getContents().addAll(EcoreUtil.copyAll(resource.getContents()));
//
//					final Map<Object, Object> saveOptions = xmiResource.getDefaultSaveOptions();
//					saveOptions.put(XMIResource.OPTION_DECLARE_XML, Boolean.TRUE);
//					saveOptions.put(XMIResource.OPTION_PROCESS_DANGLING_HREF,
//							XMIResource.OPTION_PROCESS_DANGLING_HREF_DISCARD);
//					saveOptions.put(XMIResource.OPTION_SCHEMA_LOCATION, Boolean.TRUE);
//					saveOptions.put(XMIResource.OPTION_DEFER_IDREF_RESOLUTION, Boolean.TRUE);
//					saveOptions.put(XMIResource.OPTION_SKIP_ESCAPE_URI, Boolean.FALSE);
//					saveOptions.put(XMIResource.OPTION_ENCODING, "UTF-8");
//
//					// save the resource
//					try {
//						xmiResource.save(saveOptions);
//					} catch (IOException e) {
//						// TODO Auto-generated catch block
//						e.printStackTrace();
//					}
//				}

				// save bikhoda! ta inja

				try
				{
				if (annts.get(k).getFitnessValue() >= bestAnt.getFitnessValue())
					bestAnt = annts.get(k);
				}
				catch(Exception ex)
				{
					int x = 0;
				}
			}

			finalResult.add(bestAnt); // Best ant in each iteration saves for Pareto-Front calculation

			// Update Phromones
//			for (int k = 0; k < ants.length; k++) {
//				for (int l = 0; l < tau.size(); l++) {
//					//tau[l] += Q / ants[k].getFitnessValue(); // if minimization problem in defined
//					tau.set(l, tau.get(l) + (Q / ants[k].getFitnessValue()));
//				}
//			}
			for (int k = 0; k < annts.size(); k++) {
				for (String associationName : tau.keySet()) {
					for (String bound : tau.get(associationName).keySet()) { // Lower, Upper and Middle
						double newValue = (1 - rho) * tau.get(associationName).get(bound) 
								+ (double) Q / (1/ (double)annts.get(k).getFitnessValue());
						tau.get(associationName).replace(bound, newValue); //
					}
				}
			}


//			for (String associationName : tau.keySet()) {
//				for (String bound : tau.get(associationName).keySet()) { // Lower, Upper and Middle
//					double newValue = //tau.get(associationName).get(bound) +
//							(1 - rho) * tau.get(associationName).get(bound);
//					tau.get(associationName).replace(bound, newValue); //
//				}
//			}

			// Show Iteration Information
			System.out.println("Iteration: " + it + ": Best Fitness Values: " + bestAnt.getFitnessValue());
		}

		stopwatch.stop(); // optional
		long duration = stopwatch.elapsed(TimeUnit.SECONDS);

		System.out.println("Execution Time(S): " + duration);
		
		System.out.println("Heap: " + ManagementFactory.getMemoryMXBean().getHeapMemoryUsage());
		System.out.println("NonHeap: " + ManagementFactory.getMemoryMXBean().getNonHeapMemoryUsage());
		
		double BestofTheBestFitnessValue = 0;
		Set<Resource> BestSetOfModels = null;
		for (int i = 0; i < finalResult.size(); i++) {
			if (finalResult.get(i).getFitnessValue() > BestofTheBestFitnessValue) {
				BestofTheBestFitnessValue = finalResult.get(i).getFitnessValue();
				BestSetOfModels = finalResult.get(i).models;
			}
		}

		System.out.println("---------Generating models within best of the best Ant------");
		System.out.println("Best of the best Ant fitness value: " + BestofTheBestFitnessValue);
		int c = 0;
		for (Resource resource : BestSetOfModels) {
			// XMIResource xmiResource = new
			// XMIResourceImpl(URI.createFileURI("outputs/BestModel_" + c++ + ".xmi"));
			XMIResource xmiResource = new XMIResourceImpl(URI.createFileURI("outputs/result" + c++ + "_100.xmi"));
			xmiResource.getContents().addAll(EcoreUtil.copyAll(resource.getContents()));

			final Map<Object, Object> saveOptions = xmiResource.getDefaultSaveOptions();
			saveOptions.put(XMIResource.OPTION_DECLARE_XML, Boolean.TRUE);
			saveOptions.put(XMIResource.OPTION_PROCESS_DANGLING_HREF, XMIResource.OPTION_PROCESS_DANGLING_HREF_DISCARD);
			saveOptions.put(XMIResource.OPTION_SCHEMA_LOCATION, Boolean.TRUE);
			saveOptions.put(XMIResource.OPTION_DEFER_IDREF_RESOLUTION, Boolean.TRUE);
			saveOptions.put(XMIResource.OPTION_SKIP_ESCAPE_URI, Boolean.FALSE);
			saveOptions.put(XMIResource.OPTION_ENCODING, "UTF-8");

			// save the resource
			try {
				xmiResource.save(saveOptions);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}

		System.out.println("---------Generating models within best of the Ants in each iteration------");
		for (int it = 0; it < maxIt; it++) {

			System.out.println("Iteration: " + (it + 1) + ": Best Fitness Values: " + finalResult.get(it).fitnessValue);
			int cc = 0;
			for (Resource resource : finalResult.get(it).models) {
				XMIResource xmiResource = new XMIResourceImpl(
						URI.createFileURI("outputs/model_" + cc + "_iteration_" + it + ".xmi"));
				xmiResource.getContents().addAll(EcoreUtil.copyAll(resource.getContents()));

				final Map<Object, Object> saveOptions = xmiResource.getDefaultSaveOptions();
				saveOptions.put(XMIResource.OPTION_DECLARE_XML, Boolean.TRUE);
				saveOptions.put(XMIResource.OPTION_PROCESS_DANGLING_HREF,
						XMIResource.OPTION_PROCESS_DANGLING_HREF_DISCARD);
				saveOptions.put(XMIResource.OPTION_SCHEMA_LOCATION, Boolean.TRUE);
				saveOptions.put(XMIResource.OPTION_DEFER_IDREF_RESOLUTION, Boolean.TRUE);
				saveOptions.put(XMIResource.OPTION_SKIP_ESCAPE_URI, Boolean.FALSE);
				saveOptions.put(XMIResource.OPTION_ENCODING, "UTF-8");

				// save the resource
				try {
					xmiResource.save(saveOptions);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}

		System.out.println("----------------Best Ants-----------------");
		for (int it = 0; it < maxIt; it++) {
			// TODO: Calculate Pareto Front based on "finalResult"
			System.out.println("Iteration: " + (it + 1) + ": Best Fitness Values: " + finalResult.get(it).fitnessValue);

			XMIResource xmiResource = new XMIResourceImpl(URI.createFileURI("outputs/model_" + it + ".xmi"));
			xmiResource.getContents().addAll(EcoreUtil.copyAll(finalResult.get(it).model.getContents()));

			final Map<Object, Object> saveOptions = xmiResource.getDefaultSaveOptions();
			saveOptions.put(XMIResource.OPTION_DECLARE_XML, Boolean.TRUE);
			saveOptions.put(XMIResource.OPTION_PROCESS_DANGLING_HREF, XMIResource.OPTION_PROCESS_DANGLING_HREF_DISCARD);
			saveOptions.put(XMIResource.OPTION_SCHEMA_LOCATION, Boolean.TRUE);
			saveOptions.put(XMIResource.OPTION_DEFER_IDREF_RESOLUTION, Boolean.TRUE);
			saveOptions.put(XMIResource.OPTION_SKIP_ESCAPE_URI, Boolean.FALSE);
			saveOptions.put(XMIResource.OPTION_ENCODING, "UTF-8");

			// save the resource
			try {
				xmiResource.save(saveOptions);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	private static Ant InitializeAnt(Ant ant, Problem problemInstance, HashMap<String, HashMap<String, Double>> tau,
			HashMap<String, HashMap<String, Double>> eta, int alpha, int beta, int nModels, int nElements) {

		problemInstance = null;
		try {
			problemInstance = new Problem();
		} catch (IOException e) {
			e.printStackTrace();
		}

		// Create EmptyAnt properties
		ant = new Ant();

		ant.metaModel = problemInstance.metaModel;
		ant.numberOfMetaModelElements = problemInstance.numberOfMetaModelElements;
		ant.numberOfUserNeedElements = nElements;

		// Calculate the Probability for ACO
		// double[] P = new double[tau.size()];
		HashMap<String, HashMap<String, Double>> P = new HashMap<String, HashMap<String, Double>>();

		// for (int i = 0; i < tau.size(); i++) {
		if (tau.size() > 0) {
			for (String associationName : tau.keySet()) { // tau[b]
				// Creating P for each tau entry (Associations(//Lower, Upper and Middle) -> 2,
				// 5, 3-4)
				HashMap<String, Double> bounds = new HashMap<String, Double>();
				// P.put(associationName, new HashMap<String, Double>()); // P[b][L], P[b][U],
				// P[b][M]
				double sumOfP = 0;

				for (String bound : tau.get(associationName).keySet()) { // Lower, Upper and Middle
					double tau_i = tau.get(associationName).get(bound); // tau_i P[b][L].value
					double eta_i = eta.get(associationName).get(bound); // eta_i P[b][L].value
					sumOfP += Math.pow(tau_i, alpha) * Math.pow(eta_i, beta); // Makhraj dorost shod
				}

				for (String bound : tau.get(associationName).keySet()) { // Lower, Upper and Middle
					double tau_i = tau.get(associationName).get(bound); // tau_i P[b][L].value
					double eta_i = eta.get(associationName).get(bound); // eta_i P[b][L].value

					double newValue = Math.pow(tau_i, alpha) * Math.pow(eta_i, beta); // Soorat dorost shod
					// HashMap<String, Double> ccc = P.get(associationName);
					// Double ccc2 = (P.get(associationName).get(bound));
					if (P.get(associationName) != null) {
						if (P.get(associationName).get(bound) != null)
							P.get(associationName).replace(bound, newValue / sumOfP); // Kole ehtemal dorost shod
						else {
							bounds.put(bound, newValue);
							P.put(associationName, bounds);
						}

					} else {
						bounds.put(bound, newValue);
						P.put(associationName, bounds);
					}
				}
			}
		}

		ant.setmodelPartitions(null);
		for (int i = 0; i < nModels; i++) {
			ant.generate(ant.metaModel, P, nModels, nElements);
		}
		ant.setFitnessValue(ant.FitnessFunction());
		try {
			Thread.sleep(10);
			if (tau.size() == 0 && P.size() > 0) { // Happen Just in very first iteration
				// HashMap<String, HashMap<String, Double>>
				for (String associationName : P.keySet()) {
					tau.put(associationName, new HashMap<String, Double>());
					eta.put(associationName, new HashMap<String, Double>());

					for (String bound : P.get(associationName).keySet()) { // Lower, Upper and Middle
						tau.get(associationName).put(bound, 1.0);
						eta.get(associationName).put(bound, (double) ant.getFitnessValue());
						//eta.get(associationName).put(bound, (double) ant.FitnessFunction());
						// tau.get(associationName).get(bound) + (double) Q / ants[k].getFitnessValue();
						// tau.get(associationName).replace(bound, newValue); //
					}
				}
			} // only at first iteration
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return ant;

	}

	static double[] CumSum(double[] in) {
		double[] out = new double[in.length];
		double total = 0;
		for (int i = 0; i < in.length; i++) {
			total += in[i];
			out[i] = total;
		}
		return out;
	}

	static double RouletteWheelSelection(double[] P) {
		Random rnd = new Random();
		double r = rnd.nextDouble();
		double[] C = CumSum(P);

		for (int i = 0; i < C.length; i++) {
			if (r <= C[i])
				return C[i];
		}
		return 1;
	}
}
