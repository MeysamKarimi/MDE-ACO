import java.io.Console;
import java.io.IOException;
import java.text.MessageFormat;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.Map;
import java.util.ArrayList;

import org.eclipse.emf.ecore.*;
import org.eclipse.emf.ecore.resource.Resource;

import org.eclipse.emf.common.util.TreeIterator;
import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EClassifier;
import org.eclipse.emf.ecore.EDataType;
import org.eclipse.emf.ecore.EEnum;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EOperation;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.impl.EDataTypeImpl;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.ocl.ParserException;
import org.eclipse.ocl.ecore.Constraint;
import org.eclipse.ocl.ecore.EcoreEnvironmentFactory;
import org.eclipse.ocl.ecore.OCL;
import org.eclipse.ocl.ecore.OCLExpression;
import org.eclipse.ocl.helper.OCLHelper;

import static com.google.common.collect.Iterables.get;
import static com.google.common.primitives.Primitives.isWrapperType;
import static com.google.common.primitives.Primitives.unwrap;

import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.concurrent.atomic.AtomicLong;
import java.util.logging.Logger;

import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.math3.distribution.IntegerDistribution;
import org.apache.commons.math3.distribution.UniformIntegerDistribution;

import com.google.common.base.Optional;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMultiset;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.ListMultimap;

//import fr.obeo.emf.specimen.internal.EPackagesData;

//import fr.inria.atlanmod.instantiator.Range;

//import fr.obeo.emf.specimen.internal.EPackagesData;

public class Ant {

	Resource model;
	Resource metaModel;
	double fitnessValue;
	int numberOfMetaModelElements;
	int numberOfUserNeedElements;
	int numberOfUserNeedModels;
	boolean isLastIteration = false;
	EPackagesData ePackagesData;
	Set<Resource> models;
	String currentModelPartition;
	SortedSet<String> modelPartitions = new TreeSet<String>();
	int numberofCreatedModelElementss;

	public double getFitnessValue() {
		return fitnessValue;
	}

	public void setFitnessValue(double fitnessValue) {
		this.fitnessValue = fitnessValue;
	}

	public Resource getModel() {
		return model;
	}

	public void setModel(Resource model) {
		if (models == null)
			models = new HashSet<Resource>();
		this.model = model;
		models.add(model);
	}

	public Resource getMetaModel() {
		return metaModel;
	}

	public void setMetaModel(Resource metaModel) {
		this.metaModel = metaModel;
	}

	public int getNumberOfUserNeedElements() {
		return numberOfUserNeedElements;
	}

	public void setNumberOfUserNeedElements(int numberOfUserNeedElements) {
		this.numberOfUserNeedElements = numberOfUserNeedElements;
	}

	public int getNumberOfUserNeedModels() {
		return numberOfUserNeedModels;
	}

	public void setNumberOfUserNeedModels(int numberOfUserNeedModels) {
		this.numberOfUserNeedModels = numberOfUserNeedModels;
	}

	public double getNumberOfMetaModelElements() {
		return numberOfMetaModelElements;
	}

	public void setNumberOfMetaModelElements(int numberOfMetaModelElements) {
		this.numberOfMetaModelElements = numberOfMetaModelElements;
	}

	public SortedSet<String> getModelPartitions() {
		return modelPartitions;
	}

	public void setmodelPartitions(SortedSet<String> modelPartitions) {
		this.modelPartitions = modelPartitions;
	}

	int seed = 100;
	int goalObjects;
	int currentObjects = 0;
	int currentDepth;
	int currentMaxDepth;
	double DOUBLE_UNIT = 0x1.0p-53; // 1.0 / (1L << 53)

	int DEFAULT_AVERAGE_PROPERTIES_SIZE = 8;

	int DEFAULT_AVERAGE_REFERENCES_SIZE = 8;

	int DEFAULT_AVERAGE_VALUES_LENGTH = 64;

	float DEFAULT_PROPERTIES_DEVIATION = 0.1f;

	float DEFAULT_REFERENCES_DEVIATION = 0.1f;

	float DEFAULT_VALUES_DEVIATION = 0.1f;

	Resource metamodelResource = this.metaModel;

	Range elementsRange;

	Range propertiesRange = new Range(Math.round(DEFAULT_AVERAGE_PROPERTIES_SIZE * (1 - DEFAULT_PROPERTIES_DEVIATION)),
			Math.round(DEFAULT_AVERAGE_PROPERTIES_SIZE * (1 + DEFAULT_PROPERTIES_DEVIATION)));

	Range referencesRange = new Range(Math.round(DEFAULT_AVERAGE_REFERENCES_SIZE * (1 - DEFAULT_REFERENCES_DEVIATION)),
			Math.round(DEFAULT_AVERAGE_REFERENCES_SIZE * (1 + DEFAULT_REFERENCES_DEVIATION)));

	Range valuesRange = new Range(Math.round(DEFAULT_AVERAGE_VALUES_LENGTH * (1 - DEFAULT_VALUES_DEVIATION)),
			Math.round(DEFAULT_AVERAGE_VALUES_LENGTH * (1 + DEFAULT_VALUES_DEVIATION)));

	double[] probabilityArray = null;

	public void generate(Resource resource, double[] P, int nModels, int nElements) {

		Problem problemInstance = null;
		try {
			problemInstance = new Problem();
			probabilityArray = P;
		} catch (IOException e) {
			e.printStackTrace();
		}

		numberofCreatedModelElementss = 0;
		resource = problemInstance.metaModel;
		resource.setModified(true);

		this.setNumberOfUserNeedModels(nModels);
		this.setNumberOfUserNeedElements(nElements);
		currentModelPartition = "";
		if (modelPartitions == null)
			modelPartitions = new TreeSet<String>();

		ListMultimap<EClass, EObject> indexByKind = ArrayListMultimap.create();

		ImmutableSet<EClass> possibleRootEClasses = possibleRootEClasses();

		ePackagesData = new EPackagesData(ePackages(), ignoredEClasses());

		// goalObjects = getResourceSizeDistribution(P).sample();
		goalObjects = getNumberOfUserNeedElements();
		currentDepth = 0;
		currentMaxDepth = 0;
		currentObjects = 0;

		// for (int i = 0; i < getNumberOfUserNeedModels(); i++) {
		for (int i = getNumberOfUserNeedModels() - 1; i < getNumberOfUserNeedModels(); i++) {
			// loop for creating root elements
			// while (currentObjects < goalObjects) {
			while (true) {
				EClass eClass = getNextRootEClass(possibleRootEClasses);
				IntegerDistribution dist = getDepthDistributionFor(eClass);
				if (dist == null)
					continue;
				// currentMaxDepth = dist.sample();
				// currentMaxDepth = numberOfUserNeedElements;
				currentMaxDepth = 20;
				if (currentMaxDepth == 0)
					currentMaxDepth++;
				Optional<EObject> generateEObject = generateEObject(eClass, indexByKind);
				System.out.println("main body " + Thread.currentThread().getName() + " "+ Thread.activeCount() + " " + currentModelPartition);
				modelPartitions.add(currentModelPartition);
				if (generateEObject.isPresent()) {					
					// check OCL for test
//				OCL ocl = OCL.newInstance(EcoreEnvironmentFactory.INSTANCE);
//				// create an OCL helper object
//				OCLHelper<EClassifier, EOperation, EStructuralFeature, Constraint> helper =
//						ocl.createOCLHelper();
//
//				// set the OCL context classifier
//				helper.setContext(eClass);
//
//				try {
//					Constraint invariant = helper.createInvariant(
//					    "books->forAll(b1, b2 | b1 <> b2 implies b1.title <> b2.title)");
//					OCLExpression<EClassifier> query = helper.createQuery(
//				    "books->collect(b : Book | b.category)->asSet()");
//				} catch (ParserException e) {
//					// TODO Auto-generated catch block
//					e.printStackTrace();
//				}
//				   
//
//				//ta inja OCL
//					
					resource.getContents().add(generateEObject.get());
				}
				break;
			}

			// LOGGER.info("Generating cross-references");
			// System.out.println("Generating cross-references");

			int totalEObjects = currentObjects;
			int currentEObject = 0;
			TreeIterator<EObject> eAllContents = resource.getAllContents();
			while (eAllContents.hasNext() && currentEObject < numberOfUserNeedModels) {
				currentEObject++;
				// LOGGER.fine(MessageFormat.format("Generating cross references {0} / {1}",
				// currentEObject, totalEObjects));
				// System.out.println(MessageFormat.format("Generating cross references {0} /
				// {1}", currentEObject, totalEObjects));
				EObject eObject = eAllContents.next();
				generateCrossReferences(eObject, indexByKind);
			}

//		LOGGER.info(MessageFormat.format("Requested #EObject={0}", goalObjects));

//		LOGGER.info(MessageFormat.format("Actual #EObject={0}", ImmutableSet.copyOf(indexByKind.values()).size()));

			for (Map.Entry<EClass, Collection<EObject>> entry : indexByKind.asMap().entrySet()) {
				// Log number of elements for resolved EClasses
				EClass eClass = entry.getKey();
				if (!eClass.eIsProxy() || (eClass.eIsProxy() && EcoreUtil.resolve(eClass, resource) != eClass)) {
//				LOGGER.info(MessageFormat.format("#{0}::{1}={2}", 
//						eClass.getEPackage().getNsURI(),
//						eClass.getName(),
//						entry.getValue().size()));
				}
			}
			for (Map.Entry<EClass, Collection<EObject>> entry : indexByKind.asMap().entrySet()) {
				EClass eClass = entry.getKey();
				if (eClass.eIsProxy() && EcoreUtil.resolve(eClass, resource) == eClass) {
					// Warn about unresolved EClasses
//				LOGGER.warning(MessageFormat.format("#{0} (unresolved)={1}", 
//						EcoreUtil.getURI(eClass),
//						entry.getValue().size()));
				}
			}

//		LOGGER.info(MessageFormat.format("Generation finished for resource ''{0}''", resource.getURI()));		
			if (resource.isModified()) {
				// resource.save(Collections.emptyMap());
				this.setModel(resource);
				// this.setFitnessValue(FitnessFunction());
			}
		}

	}

	public int FitnessFunction() // gets the fitness function
	{
		// model exists in this.model
		// metamodel exists in this.metaModel
		int z = 0;
		z += CalculateMetamodelCoverage();
		z += CalulateObjective2();
		return z;
	}

	private int CalculateMetamodelCoverage() {
		int mmCoverage = 0;

		if (IsAnyPartitionConstraintsIsDefined()) // TODO Check if any constraint is defined in attributes/references
		{

		} else {
			mmCoverage = modelPartitions.size();
			for (Resource resource : models) {
				// Check partitions
				// FMDDD
				// FMSSS
				// FMSSD | FMSDD
				// DDDDD
				// SSSSS
				// Add the fitness value of the ant if any new partition is met
			}
		}
		return mmCoverage;
	}

	private boolean IsAnyPartitionConstraintsIsDefined() {
		// TODO Auto-generated method stub
		return false;
	}

	private int CalulateObjective2() {
		// TODO Objective 2 of current ant in this problem
		return 0;
	}

	public ImmutableSet<EClass> possibleRootEClasses() {
		List<EClass> eClasses = new LinkedList<EClass>();
		// creating a subtypes map
		Map<EClass, Set<EClass>> eSubtypesMap = computeSubtypesMap();

		// Eclasses.filter( instance of EClass && not abstract && not interface)
		for (Iterator<EObject> it = this.metaModel.getAllContents(); it.hasNext();) {
			EObject eObject = (EObject) it.next();
			if (eObject instanceof EClass) {
				EClass eClass = (EClass) eObject;
				if (!eClass.isAbstract() && !eClass.isInterface()) {
					eClasses.add(eClass);
				}
			}
		}

		// copying the list of eClasses
		List<EClass> result = new LinkedList<EClass>(eClasses);
//	Collections.copy(result , eClasses);

		// iterating eClasses and removing elements (along with subtypes) being
		// subject to a container reference
		for (EClass cls : eClasses) {
			for (EReference cont : cls.getEAllContainments()) {
				Set<EClass> list = eSubtypesClosure(eSubtypesMap, (EClass) cont.getEType());
				if (list.size() == 0) {
					result.remove((EClass) cont.getEType());
				} else {
					result.removeAll(list);
				}
			}
		}

		return ImmutableSet.copyOf(result);
	}

	private Map<EClass, Set<EClass>> computeSubtypesMap() {
		Map<EClass, Set<EClass>> result = new HashMap<EClass, Set<EClass>>();
		// TreeIterator<EObject> iter = this.metaModel.getAllContents();
		EPackage univEPackage = (EPackage) this.metaModel.getContents().get(0);
		List<EObject> iter = univEPackage.eContents();

		// for (EObject ecls = null ; iter.hasNext(); ) {
		for (EObject ecls : iter) {
			// ecls = iter.next();
			if (ecls instanceof EClass) {
				EClass clazz = (EClass) ecls;
				for (EClass cls : clazz.getEAllSuperTypes()) {
					if (result.containsKey(cls)) {
						result.get(cls).add(clazz);
					} else {
						Set<EClass> list = new HashSet<EClass>();
						list.add(cls);
						list.add(clazz);
						result.put(cls, list);
					}
				}
			}
		}

		return result;
	}

	Random random = new Random(seed);

	protected Map<Object, IntegerDistribution> distributions = new HashMap<Object, IntegerDistribution>();

	public IntegerDistribution getResourceSizeDistribution(double[] P) {
		IntegerDistribution distribution = distributions.get(this.metaModel);
		if (distribution == null) {
			// distribution = new UniformIntegerDistribution((int)
			// Math.floor(numberOfMetaModelElements / 2), numberOfMetaModelElements);
			distribution = new UniformIntegerDistribution(1, 100);
			distribution.reseedRandomGenerator(random.nextLong());
			//distribution.reseedRandomGenerator(RoletteWheelSelection(probabilityArray));
			distributions.put(this.metaModel, distribution);
		}
		return distribution;
	}

	public EClass getNextRootEClass(ImmutableSet<EClass> rootEClasses) {
		IntegerDistribution distribution = distributions.get(rootEClasses);
		if (distribution == null) {
			distribution = new UniformIntegerDistribution(0, rootEClasses.size() - 1);
			// distribution = new UniformIntegerDistribution(1, 100);
			distribution.reseedRandomGenerator(random.nextLong());
			//distribution.reseedRandomGenerator(RoletteWheelSelection(probabilityArray));
			distributions.put(rootEClasses, distribution);
		}

		return rootEClasses.asList().get(distribution.sample());
	}

	public IntegerDistribution getDepthDistributionFor(EClass eClass) {
		IntegerDistribution distribution = distributions.get(eClass);
		if (distribution == null) {
			// distribution = new UniformIntegerDistribution(0, eClass.getFeatureCount() -
			// 1);
			distribution = new UniformIntegerDistribution(1, eClass.getEAllContainments().size());
			distribution.reseedRandomGenerator(random.nextLong());
			//distribution.reseedRandomGenerator(RoletteWheelSelection(probabilityArray));
			distributions.put(eClass, distribution);
		}
		return distribution;
	}

	protected Optional<EObject> generateEObject(EClass eClass, ListMultimap<EClass, EObject> indexByKind) {
		final EObject eObject;
		currentObjects++;
		// LOGGER.fine(MessageFormat.format("Generating EObject {0} / ~{1}
		// (EClass={2})",
		// currentObjects, goalObjects, eClass.getName()));
		eObject = createEObject(eClass, indexByKind);
		generateEAttributes(eObject, eClass);
		generateEContainmentReferences(eObject, eClass, indexByKind);
		return Optional.fromNullable(eObject);
	}

	protected EObject createEObject(EClass eClass, ListMultimap<EClass, EObject> indexByKind) {
		EObject eObject = eClass.getEPackage().getEFactoryInstance().create(eClass);

		indexByKind.put(eClass, eObject);
		for (EClass eSuperType : eClass.getEAllSuperTypes()) {
			indexByKind.put(eSuperType, eObject);
		}

		return eObject;
	}

	protected void generateEAttributes(EObject eObject, EClass eClass) {
		// if(eClass.getEAttributes().size() > 0)
		if (eClass.getEAllAttributes().size() > 0)
			for (EAttribute eAttribute : ePackagesData.eAllAttributes(eClass)) {
				// for (EAttribute eAttribute : eClass.getEAllAttributes()) {
				generateAttributes(eObject, eAttribute);
			}
	}

	protected void generateAttributes(EObject eObject, EAttribute eAttribute) {
		IntegerDistribution distribution = getDistributionFor(eAttribute);
		EDataType eAttributeType = eAttribute.getEAttributeType();
		Class<?> instanceClass = eAttributeType.getInstanceClass();
//		if(instanceClass == null)
//			eAttributeType.setInstanceClass(String.class);
		if (eAttribute.isMany()) {
			generateManyAttribute(eObject, eAttribute, distribution, instanceClass);
		} else {
			generateSingleAttribute(eObject, eAttribute, distribution, instanceClass);
		}
	}

	Map<EAttribute, SortedSet<String>> attributePartitioning;

	protected void generateSingleAttribute(EObject eObject, EAttribute eAttribute, IntegerDistribution distribution,
			Class<?> instanceClass) {
		if (eAttribute.isRequired() || booleanInDistribution(distribution)) {
			final Object value;
			EDataType eAttributeType = eAttribute.getEAttributeType();
			if (attributePartitioning == null)
				attributePartitioning = new HashMap<EAttribute, SortedSet<String>>();
			if (eAttributeType instanceof EEnum) {
				assert instanceClass == null;
				EEnum eEnum = (EEnum) eAttributeType;
				instanceClass = int.class;
				eAttributeType.setInstanceClass(int.class);
				if (attributePartitioning.containsKey(eAttribute)) {
					if (attributePartitioning.get(eAttribute).size() > 1) {
						int randomValue = Math.abs((Integer) nextValue(instanceClass));
						int size = eEnum.getELiterals().size();
						value = eEnum.getELiterals().get(randomValue % size);
					} else if (Integer.parseInt(attributePartitioning.get(eAttribute).first()) == 0) {
						value = Integer.MAX_VALUE;
					} else {
						value = 0;
					}
				} else {
					attributePartitioning.put(eAttribute, new TreeSet<String>());
					value = 0;
				}
				attributePartitioning.get(eAttribute).add(value.toString());
			} else {
				if (instanceClass == null) {
					instanceClass = String.class; // EDataType.class
					eAttributeType.setInstanceClass(String.class);
				}
				value = nextValue(instanceClass);
				if (value == null)
					return;
				if (!attributePartitioning.containsKey(eAttribute)) {
					attributePartitioning.put(eAttribute, new TreeSet<String>());
				}
				attributePartitioning.get(eAttribute).add(value.toString());
			}
			eObject.eSet(eAttribute, value);
		}
	}

	protected void generateManyAttribute(EObject eObject, EAttribute eAttribute, IntegerDistribution distribution,
			Class<?> instanceClass) {
		@SuppressWarnings("unchecked")
		List<Object> values = (List<Object>) eObject.eGet(eAttribute);
		if (attributePartitioning == null)
			attributePartitioning = new HashMap<EAttribute, SortedSet<String>>();
		for (int i = distribution.getSupportLowerBound(); i < distribution.sample(); i++) {
			final Object value;
			EDataType eAttributeType = eAttribute.getEAttributeType();
			if (eAttributeType instanceof EEnum) {
				assert instanceClass == null;
				EEnum eEnum = (EEnum) eAttributeType;
				instanceClass = int.class;
				if (attributePartitioning.containsKey(eAttribute)) {
					if (attributePartitioning.get(eAttribute).size() > 1) {
						int randomValue = Math.abs((Integer) nextValue(instanceClass));
						int size = eEnum.getELiterals().size();
						value = eEnum.getELiterals().get(randomValue % size);
					} else if (Integer.parseInt(attributePartitioning.get(eAttribute).first()) == 0) {
						value = Integer.MAX_VALUE;
					} else {
						value = 0;
					}
				} else {
					attributePartitioning.put(eAttribute, new TreeSet<String>());
					value = 0;
				}
				attributePartitioning.get(eAttribute).add(value.toString());
			} else {
				value = nextValue(instanceClass);
				if (!attributePartitioning.containsKey(eAttribute)) {
					attributePartitioning.put(eAttribute, new TreeSet<String>());
				}
				attributePartitioning.get(eAttribute).add(value.toString());
			}

			values.add(value);
		}
	}

	protected Object nextValue(Class<?> instanceClass) {
		final Object value;
		if (instanceClass == null)
			return null;
		if (instanceClass.isPrimitive() || isWrapperType(instanceClass)) {
			value = nextPrimitive(unwrap(instanceClass));
		} else {
			value = nextObject(instanceClass);
		}
		return value;
	}

	protected void generateEContainmentReferences(EObject eObject, EClass eClass,
			ListMultimap<EClass, EObject> indexByKind) {
		EReference lastEReference = null;
		if (eClass.getEAllContainments().size() > 0)
			for (EReference eReference : ePackagesData.eAllContainment(eClass)) {
				// for (EReference eReference : eClass.getEAllContainments()) {
				// if (eReference.isRequired() || (currentObjects < goalObjects && currentDepth
				// <= currentMaxDepth)) {
				if ((numberofCreatedModelElementss < goalObjects && currentDepth <= currentMaxDepth)) {
					// if (eReference.isRequired()) {
					// if (eReference.isRequired() || currentDepth <= currentMaxDepth) {
					generateEContainmentReference(eObject, eReference, indexByKind, 0);
					lastEReference = eReference;
				}
				// break;
			}
		if (numberofCreatedModelElementss < numberOfUserNeedElements && lastEReference != null) {
			generateEContainmentReference(eObject, lastEReference, indexByKind,
					numberOfUserNeedElements - numberofCreatedModelElementss);
		}

	}

	protected void generateCrossReferences(EObject eObject, ListMultimap<EClass, EObject> indexByKind) {
		Iterable<EReference> eAllNonContainment = ePackagesData.eAllNonContainment(eObject.eClass());
		for (EReference eReference : eAllNonContainment) {
			EClass eReferenceType = eReference.getEReferenceType();
			IntegerDistribution distribution = getDistributionFor(eReference);

			if (eReference.isMany()) {
				@SuppressWarnings("unchecked")
				List<Object> values = (List<Object>) eObject.eGet(eReference);
				int sample = distribution.sample();
				// LOGGER.fine(MessageFormat.format("Generating {0} values for EReference
				// ''{1}'' in EObject {2}", sample, eReference.getName(), eObject.toString()));
				for (int i = 0; i < sample; i++) {
					List<EObject> possibleValues = indexByKind.get(eReferenceType);
					if (!possibleValues.isEmpty()) {
						final EObject nextEObject = possibleValues.get(nextInt(possibleValues.size()));
						values.add(nextEObject);
					}
				}
			} else {
				if (eReference.isRequired() || booleanInDistribution(distribution)) {
					// LOGGER.fine(MessageFormat.format("Generating EReference ''{0}'' in EObject
					// {1}", eReference.getName(), eObject.toString()));
					List<EObject> possibleValues = indexByKind.get(eReferenceType);
					if (!possibleValues.isEmpty()) {
						final EObject nextEObject = possibleValues.get(nextInt(possibleValues.size()));
						eObject.eSet(eReference, nextEObject);
					}
				}
			}
		}
	}

	public IntegerDistribution getDistributionFor(EAttribute eAttribute) {
		IntegerDistribution distribution = distributions.get(eAttribute);
		if (distribution == null) {
			int upperBound = eAttribute.getUpperBound() == EAttribute.UNBOUNDED_MULTIPLICITY ? Integer.MAX_VALUE
					: eAttribute.getUpperBound();
			distribution = new UniformIntegerDistribution(
					Math.max(Math.min(propertiesRange.getMinimum(), upperBound), eAttribute.getLowerBound()),
					Math.min(propertiesRange.getMaximum(), upperBound));
			distribution.reseedRandomGenerator(random.nextLong());
			//distribution.reseedRandomGenerator(RoletteWheelSelection(probabilityArray));
			distributions.put(eAttribute, distribution);
		}
		return distribution;
	}

	private Set<EClass> eSubtypesClosure(Map<EClass, Set<EClass>> eSubtypesMap, EClass eType) {
		Set<EClass> result = new LinkedHashSet<EClass>();
		if (!eSubtypesMap.containsKey(eType)) {
			return Collections.EMPTY_SET;
		} else {
			result.addAll(eSubtypesMap.get(eType));
			for (EClass eSubType : eSubtypesMap.get(eType)) {
				if (!eSubType.equals(eType))
					result.addAll(eSubtypesClosure(eSubtypesMap, eSubType));
			}
		}
		return result;
	}

	protected boolean booleanInDistribution(IntegerDistribution distribution) {
		int sample = distribution.sample();
		return sample <= distribution.getNumericalMean();
	}

	protected Object nextPrimitive(Class<?> instanceClass) {
		if (instanceClass == boolean.class) {
			return nextBoolean();
		} else if (instanceClass == byte.class) {
			byte[] buff = new byte[1];
			nextBytes(buff);
			return buff[0];
		} else if (instanceClass == char.class) {
			char nextChar = (char) nextInt();
			return nextChar;
		} else if (instanceClass == double.class) {
			return nextDouble();
		} else if (instanceClass == float.class) {
			return nextFloat();
		} else if (instanceClass == int.class) {
			return nextInt();
		} else if (instanceClass == long.class) {
			return nextLong();
		} else if (instanceClass == short.class) {
			short nextShort = (short) nextInt();
			return nextShort;
		} else {
			throw new IllegalArgumentException();
		}
	}

	protected Object nextObject(Class<?> instanceClass) {
		if (instanceClass == String.class) {// EDataType.class) {
			return RandomStringUtils.random(getValueDistributionFor(instanceClass).sample(), 0, 0, true, true, null,
					new Random(seed));
		} else {
//			LOGGER.warning(
//					MessageFormat.format("Do not know how to randomly generate ''{0}'' object",
//					instanceClass.getName()));
		}
		return null;
	}

	protected void generateEContainmentReference(EObject eObject, EReference eReference,
			ListMultimap<EClass, EObject> indexByKind, int remainElementSize) {

		if (numberofCreatedModelElementss > getNumberOfUserNeedElements())
			return;
		currentDepth++;

		ImmutableList<EClass> eAllConcreteSubTypeOrSelf = ePackagesData.eAllConcreteSubTypeOrSelf(eReference);
		ImmutableMultiset<EClass> eAllConcreteSubTypesOrSelf = getEReferenceTypesWithWeight(eReference,
				eAllConcreteSubTypeOrSelf);

		if (!eAllConcreteSubTypesOrSelf.isEmpty()) {
			if (eReference.isMany()) {
				generateManyContainmentReference(eObject, eReference, indexByKind, eAllConcreteSubTypesOrSelf,
						remainElementSize);
			} else {
				generateSingleContainmentReference(eObject, eReference, indexByKind, eAllConcreteSubTypesOrSelf);
			}
		}

		currentDepth--;
	}

	public int nextInt(int bound) {
		if (bound <= 0) {
//            throw new IllegalArgumentException(BadBound);
		}

		int r = next(31);
		int m = bound - 1;
		if ((bound & m) == 0) // i.e., bound is a power of 2
			r = (int) ((bound * (long) r) >> 31);
		else {
			for (int u = r; u - (r = u % bound) + m < 0; u = next(31))
				;
		}
		return r;
	}

	public int nextInt() {
		return next(32);
	}

	public double nextDouble() {
		return (((long) (next(26)) << 27) + next(27)) * DOUBLE_UNIT;
	}

	public float nextFloat() {
		return next(24) / ((float) (1 << 24));
	}

	public long nextLong() {
		// it's okay that the bottom word remains signed.
		return ((long) (next(32)) << 32) + next(32);
	}

	final long multiplier = 0x5DEECE66DL;
	final long addend = 0xBL;
	final long mask = (1L << 48) - 1;

	int next(int bits) {
		long oldseed, nextseed;
		// AtomicLong seed2 = seed;
		AtomicLong seed2 = new AtomicLong(initialScramble(seed));
		do {
			oldseed = seed2.get();
			nextseed = (oldseed * multiplier + addend) & mask;
		} while (!seed2.compareAndSet(oldseed, nextseed));
		return (int) (nextseed >>> (48 - bits));
	}

	long initialScramble(long seed) {
		return (seed ^ multiplier) & mask;
	}

	public boolean nextBoolean() {
		return next(1) != 0;
	}

	public void nextBytes(byte[] bytes) {
		for (int i = 0, len = bytes.length; i < len;)
			for (int rnd = nextInt(), n = Math.min(len - i, Integer.SIZE / Byte.SIZE); n-- > 0; rnd >>= Byte.SIZE)
				bytes[i++] = (byte) rnd;
	}

	ImmutableMultiset<EClass> getEReferenceTypesWithWeight(EReference eReference,
			ImmutableList<EClass> eAllSubTypesOrSelf) {
		ImmutableMultiset.Builder<EClass> eAllSubTypesOrSelfWithWeights = ImmutableMultiset.builder();
		for (EClass eClass : eAllSubTypesOrSelf) {
			eAllSubTypesOrSelfWithWeights.addCopies(eClass, getWeightFor(eReference, eClass));
		}
		return eAllSubTypesOrSelfWithWeights.build();
	}

	public int getWeightFor(EReference eReference, EClass eClass) {
		return 1;
	}

	public IntegerDistribution getDistributionFor(EReference eReference) {
		IntegerDistribution distribution = distributions.get(eReference);
		if (distribution == null) {
			int upperBound = eReference.getUpperBound() == EAttribute.UNBOUNDED_MULTIPLICITY ? Integer.MAX_VALUE
					: eReference.getUpperBound();
			distribution = new UniformIntegerDistribution(
					Math.max(Math.min(propertiesRange.getMinimum(), upperBound), eReference.getLowerBound()),
					Math.min(referencesRange.getMaximum(), upperBound));

			distribution.reseedRandomGenerator(random.nextLong());
			//distribution.reseedRandomGenerator(RoletteWheelSelection(probabilityArray));
			distributions.put(eReference, distribution);
		}
		return distribution;
	}

	public int RolleteWheelSelection(EReference eReference) {
		IntegerDistribution distribution = distributions.get(eReference);
		if (distribution == null) {
			int upperBound = eReference.getUpperBound() == EAttribute.UNBOUNDED_MULTIPLICITY ? Integer.MAX_VALUE
					: eReference.getUpperBound();
			distribution = new UniformIntegerDistribution(
					Math.max(Math.min(propertiesRange.getMinimum(), upperBound), eReference.getLowerBound()),
					Math.min(referencesRange.getMaximum(), upperBound));

			distribution.reseedRandomGenerator(random.nextLong());
			//distribution.reseedRandomGenerator(RoletteWheelSelection(probabilityArray));
			distributions.put(eReference, distribution);
		}

		return distribution.sample();
//		int i;
//		int x = distribution.sample();
//		for (i = 0; i < probabilityArray.length; i++) {
//			if(x <= probabilityArray[i+1])
//				break;
//		}
//		return i;
	}

	IntegerDistribution getValueDistributionFor(Class<?> clazz) {
		IntegerDistribution distribution = distributions.get(clazz);
		if (distribution == null) {
			distribution = new UniformIntegerDistribution(valuesRange.getMinimum(), valuesRange.getMaximum());
			distribution.reseedRandomGenerator(random.nextLong());
			//distribution.reseedRandomGenerator(RoletteWheelSelection(probabilityArray));
			distributions.put(clazz, distribution);
		}
		return distribution;
	}

	void generateManyContainmentReference(EObject eObject, EReference eReference,
			ListMultimap<EClass, EObject> indexByKind, ImmutableMultiset<EClass> eAllConcreteSubTypesOrSelf,
			int remainElementSize) {			
		IntegerDistribution distribution = getDistributionFor(eReference);
//			@SuppressWarnings("unchecked")
		List<EObject> values = (List<EObject>) eObject.eGet(eReference);
		int sample = distribution.sample();
		//sample = RolleteWheelSelection(eReference);		
		if (sample > (getNumberOfUserNeedElements() - numberofCreatedModelElementss)) {
			if (remainElementSize > 0) {
				sample = remainElementSize;
			} else {
				int max = getNumberOfUserNeedElements() - numberofCreatedModelElementss;
				int min = 0;
				sample = (int) (Math.random() * ((max - min) + 1)) + min;
			}
			// sample = (getNumberOfUserNeedElements() - numberofCreatedModelElementss);
		}
//			LOGGER.fine(MessageFormat.format("Generating {0} values for EReference ''{1}'' in EObject {2}", sample, eReference.getName(), eObject.toString()));
		numberofCreatedModelElementss += sample;
		if(numberofCreatedModelElementss > getNumberOfUserNeedElements())
			return;
		for (int i = 0; i < sample; i++) {
			int idx = nextInt(eAllConcreteSubTypesOrSelf.size());
			idx = new Random().nextInt(eAllConcreteSubTypesOrSelf.size());
			final Optional<EObject> nextEObject = generateEObject(get(eAllConcreteSubTypesOrSelf, idx), indexByKind);
			if (nextEObject.isPresent()) {
				values.add(nextEObject.get());
			}
		}				

		for (int i = 0; i < sample; i++) {
			if (currentModelPartition == "")
				currentModelPartition += eReference.getName();
			else
				currentModelPartition += "," + eReference.getName();
		}

	}

	void generateSingleContainmentReference(EObject eObject, EReference eReference,
			ListMultimap<EClass, EObject> indexByKind, ImmutableMultiset<EClass> eAllConcreteSubTypesOrSelf) {
		IntegerDistribution distribution = getDistributionFor(eReference);
		if (eReference.isRequired() || booleanInDistribution(distribution)) {
//				LOGGER.fine(MessageFormat.format("Generating EReference ''{0}'' in EObject {1}", eReference.getName(), eObject.toString()));
			int idx = nextInt(eAllConcreteSubTypesOrSelf.size());
			idx = new Random().nextInt(eAllConcreteSubTypesOrSelf.size());
			final Optional<EObject> nextEObject = generateEObject(get(eAllConcreteSubTypesOrSelf, idx), indexByKind);
			if (nextEObject.isPresent()) {
				eObject.eSet(eReference, nextEObject.get());
			}
			numberofCreatedModelElementss++;
			
			if (currentModelPartition == "") {
				currentModelPartition += eReference.getName();
			} else {
				currentModelPartition += "," + eReference.getName();
			}
		}
	}

	ImmutableSet<EPackage> ePackages() {
		Set<EPackage> ePackages = new HashSet<EPackage>();
		for (Iterator<EObject> it = this.metaModel.getAllContents(); it.hasNext();) {
			EObject eObject = (EObject) it.next();
			if (eObject instanceof EPackage) {
				ePackages.add((EPackage) eObject);
			}
		}
		return ImmutableSet.copyOf(ePackages);
	}

	ImmutableSet<EClass> ignoredEClasses() {
		Set<EClass> eClasses = new HashSet<EClass>();
		for (Iterator<EObject> it = this.metaModel.getAllContents(); it.hasNext();) {
			EObject eObject = (EObject) it.next();
			if (eObject instanceof EClass) {
				EClass eClass = (EClass) eObject;
				if (eClass.isAbstract() || eClass.isInterface()) {
					// Abstract EClasses and Interfaces can't be instantiated
					eClasses.add(eClass);
				}
			}
		}
		return ImmutableSet.copyOf(eClasses);
	}

	public int  RoletteWheelSelection(double[] c) {
		double rangeMin = 0.0f;
		double rangeMax = c[c.length - 1];
		Random r = new Random();
		double createdRanNum = rangeMin + (rangeMax - rangeMin) * r.nextDouble();
		int i;
		for (i = 0; i < c.length - 1; i++) {
			if (createdRanNum <= c[i + 1])
				break;
		}
		return i;
	}
}
