import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.SortedSet;
import java.util.concurrent.TimeUnit;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.emf.ecore.xmi.XMIResource;
import org.eclipse.emf.ecore.xmi.impl.XMIResourceImpl;

import com.google.common.base.Stopwatch;

public class ACO {

	public static void main(String[] args) {

		Stopwatch stopwatch = Stopwatch.createStarted();
		// Problem Definition
		Problem problemInstance = null;
		try {
			problemInstance = new Problem();
		} catch (IOException e) {
			e.printStackTrace();
		}

		int nModels = 15; // Get the number of models that the user needs
		int nElements = 50; // Get the number of elements that the user want in each model (Jahanbin)		
		// ACO Parameters
		int maxIt = 100; // Maximum number of Iterations
		int nAnt = 50; // Number of Ants (Population Size)
		int Q = 2;
		double tau0 = Q / (problemInstance.numberOfMetaModelElements * 0.5);// 1; // Initial Phromone
		int alpha = 1; // Phromone Exponential Weight
		int beta = 1; // Heurist Exponential Weight
		double rho = 0.05; // Evaporation Rate

		// Initialization
		double eta = problemInstance.numberOfMetaModelElements; // Heuristic information (harchi bishtar coverage
																// bishtar)

		double[] tau = new double[problemInstance.numberOfMetaModelElements]; // Phromone Array
		for (int i = 0; i < tau.length; i++) {
			tau[i] = tau0;
		}

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

			for (int k = 0; k < nAnt; k++) {

				// Move Ants
				ants[k] = InitializeAnt(ants[k], problemInstance, tau, eta, alpha, beta, nModels, nElements);

				if (ants[k].getFitnessValue() >= bestAnt.getFitnessValue())
					bestAnt = ants[k];
			}

			finalResult.add(bestAnt); // Best ant in each iteration saves for Pareto-Front calculation

			// Update Phromones
			for (int k = 0; k < ants.length; k++) {
				for (int l = 0; l < tau.length; l++) {
					tau[l] += Q / ants[k].getFitnessValue(); // if minimization problem in defined
				}
			}

			// Evaporation
			for (int l = 0; l < tau.length; l++) {
				tau[l] += (1 - rho) * tau[l];
			}

			// Show Iteration Information
			System.out.println("Iteration: " + it + ": Best Fitness Values: " + bestAnt.getFitnessValue());
		}

		stopwatch.stop(); // optional
		long duration = stopwatch.elapsed(TimeUnit.SECONDS);

		System.out.println("Execution Time(S): " + duration);

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
			//XMIResource xmiResource = new XMIResourceImpl(URI.createFileURI("outputs/BestModel_" + c++ + ".xmi"));
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
	}

	private static Ant InitializeAnt(Ant ant, Problem problemInstance, double[] tau, double eta, int alpha, int beta,
			int nModels, int nElements) {

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
		double[] P = new double[tau.length];
		double sumOfP = 0;

		for (int i = 0; i < tau.length; i++) {
			P[i] = 0;
			for (int j = 0; j < P.length; j++) {
				if (i == j) {
					P[i] = 0;
					continue;
				}
				P[i] += Math.pow(tau[j], alpha) * Math.pow(eta, beta);
			}
			sumOfP += P[i];
		}
		// Normalize the probabilities
		for (int i = 0; i < P.length; i++) {
			P[i] /= sumOfP;
		}

		// double elementIndex = RouletteWheelSelection(P);
		// till here
		ant.setmodelPartitions(null);
		for (int i = 0; i < nModels; i++)
		{
			ant.generate(ant.metaModel, P, nModels, nElements);
			try {
				Thread.sleep(10);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		ant.setFitnessValue(ant.FitnessFunction());
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
