package tudo.streamingrec;

import it.unimi.dsi.fastutil.objects.Object2ObjectLinkedOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import org.apache.commons.lang3.StringUtils;
import org.joda.time.Period;
import org.joda.time.format.ISOPeriodFormat;
import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import tudo.streamingrec.AlgorithmWrapper.WorkPackage;
import tudo.streamingrec.AlgorithmWrapper.WorkPackageClick;
import tudo.streamingrec.algorithms.Algorithm;
import tudo.streamingrec.data.*;
import tudo.streamingrec.data.session.SessionExtractor;
import tudo.streamingrec.evaluation.metrics.HypothesisTestableMetric;
import tudo.streamingrec.evaluation.metrics.Metric;
import tudo.streamingrec.util.*;

import java.io.IOException;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.Map.Entry;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * Main entry point for the framework
 *
 * @author MK, MJ
 *
 */
@Command(name = "StreamingRec",
		footer = "Copyright(c) 2017 Mozhgan Karimi, Michael Jugovac, Dietmar Jannach",
		description = "A java framework for news recommendation algorithm evaluation. Usage:",
		showDefaultValues = true,
		sortOptions = false)
public class StreamingRec {
	//the item input file
	@Option(names = {"-i", "--items"}, paramLabel="<FILE>", description = "Path to the item input file in CSV format")
	private static String INPUT_FILENAME_ITEMS = "C:/Users/tomas.chladek/Documents/Personal/Uni/Master/3rd/DIP/GDataset/all/data/finished_Items.csv";
	//the click input file
	@Option(names = {"-c", "--clicks"}, paramLabel="<FILE>", description = "Path to the clicks input file in CSV format")
	private static String INPUT_FILENAME_CLICKS =  "C:/Users/tomas.chladek/Documents/Personal/Uni/Master/3rd/DIP/GDataset/all/data/finished_Clicks.csv";
	//are we using the "old" format, i.e., the inefficient format optimized only for plista?
	@Option(names = {"-f", "--old-format"}, description = "Uses the old click file format")
	private static boolean OLD_FILE_FORMAT = false;
	//should we deduplicate the input files? Same item & user withing 1 minute is considered to be duplicate and is not added to the stream
	@Option(names = {"-d", "--deduplicate"}, description = "Deduplicates the data")
	private static boolean DEDUPLICATE = true;
	//the path to the metric json config file
	@Option(names = {"-m", "--metrics-config"}, paramLabel="<FILE>", description = "Path to the metrics json config file")
	private static String METRICS_FILE_NAME = "C:/Users/tomas.chladek/Documents/Personal/Uni/Master/3rd/DIP/streamingRecMine/config/metrics-config.json";
	//the path to the algorithm json config file
	@Option(names = {"-a", "--algorithm-config"}, paramLabel="<FILE>", description = "Path to the algorithm json config file")
	private static String ALGORITHM_FILE_NAME = "C:/Users/tomas.chladek/Documents/Personal/Uni/Master/3rd/DIP/streamingRecMine/config/algorithm-config-simple.json";
	//the time for the sessions inactivity threshold
	@Option(names = {"-t", "--session-time-threshold"}, paramLabel="<VALUE>", description = "The idle time threshold for separating two user sessions in milliseconds.")
	private static long SESSION_TIME_THRESHOLD = 1000 * 60 * 20;
	// if this parameter is set to greater than 0, sessions with this number or
	// less clicks will be removed from the data set
	@Option(names = {"-l", "--session-length-filter"}, paramLabel="<VALUE>", description = "If set to N, sessions with N or less clicks will be removed from the dataset. If set to 0, nothing will be filtered.")
	private static int SESSION_LENGTH_FILTER = 1;
	// if you don't want verbose stats, turn the next variable to false
	@Option(names = {"-o", "--output-stats"}, description = "Outputs more detailed stats. Might take more time.")
	private static boolean OUTPUT_STATS = false;
	//where to split the data into training and test
	@Option(names = {"-p", "--split-threshold"}, paramLabel="<VALUE>", description = "Split threshold for splitting the dataset into training and test set")
	private static double SPLIT_THRESHOLD = 0.166;//0.0456;//11;
	//the number of threads to use
	@Option(names = {"-n", "--thread-count"}, paramLabel="<VALUE>", description = "Number of threads to use. Less threads result in less CPU usage but also less RAM usage.")
	private static int THREAD_COUNT = Runtime.getRuntime().availableProcessors()-1;

	//the global start time used for output writing to the same folder
	public static String startTime;
	//for command line help
	@Option(names = {"-h", "--help"}, hidden=true, usageHelp = true)
	private static boolean helpRequested;

	private static IDataManager dataManager = new DataManager();
	private static IWorkPackageFactory workPackageFactory = new WorkPackageFactory();

	public static void main(String[] args) throws IOException, ParseException, InterruptedException {
		if(args.length==0){
			System.out.println("Help for commandline arguments with -h");
		}
		//command line parsing
		CommandLine.populateCommand(new StreamingRec(), args);
		if (helpRequested) {
			CommandLine.usage(new StreamingRec(), System.out);
			return;
		}
		//if deduplication is deactivated -> write a warning in red
		if (!DEDUPLICATE) {
			System.err.println("Warning! Deduplication disabled.");
		}
		//save the global start time
		Calendar instance = Calendar.getInstance();
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd-hh-mm-ss");
		dateFormat.setTimeZone(instance.getTimeZone());
		startTime = dateFormat.format(instance.getTime());
		//redirect the console output to a file
		Util.redirectConsole();
		//set the sessions extractor's session split thresholds
		SessionExtractor.setThresholdInMS(SESSION_TIME_THRESHOLD);

		//create a human readable session threshold for the output
		String timeThreshold = ISOPeriodFormat.standard().print(new Period(SESSION_TIME_THRESHOLD)).replace("PT", "");
		// output the parameters
		printFileInfo(timeThreshold);

		Map<String, Algorithm> algorithmsWithName = getAlgorithms();

		// read the data
		SplitData splitData = dataManager.getSplitData(INPUT_FILENAME_ITEMS, INPUT_FILENAME_CLICKS, OUTPUT_STATS,
				DEDUPLICATE, OLD_FILE_FORMAT,SESSION_LENGTH_FILTER,SPLIT_THRESHOLD);


		// create list of metrics
		Map<String, List<Metric>> metrics = new Object2ObjectLinkedOpenHashMap<>();
		Map<Metric, String> metricsByAlgorithm = new Object2ObjectLinkedOpenHashMap<>();
		Map<String, List<Metric>> metricsByName = new Object2ObjectLinkedOpenHashMap<>();
		// for each algorithm, create a list of metrics
		for (String algorithmName : algorithmsWithName.keySet()) {
			List<Metric> metricsList = Config.loadMetrics(METRICS_FILE_NAME);
			for (Metric m : metricsList) {
				m.setAlgorithm(algorithmName);
				//add the metrics to maps for better retrieval
				addMetricToMaps(m, algorithmName, m.getName(), metrics, metricsByAlgorithm, metricsByName);
			}
		}
		executeAlgorithms(algorithmsWithName, splitData, metrics);
		printResult(timeThreshold, metricsByAlgorithm, metricsByName);
	}

	private static void executeAlgorithms(Map<String, Algorithm> algorithmsWithName, SplitData splitData, Map<String, List<Metric>> metrics) throws InterruptedException {
		// re-extract the events based on type (item or transaction) for later convenience
		List<Item> trainingItems = new ObjectArrayList<>();
		List<Transaction> trainingTransactions = new ObjectArrayList<>();
		Util.extractEventTypes(splitData.trainingData, trainingItems, trainingTransactions);

		long realTrainTime = trainingTransactions.get(trainingTransactions.size()-1).timestamp.getTime() - trainingTransactions.get(0).timestamp.getTime();


		// create main session extractor and user history helper
		List<ClickData> trainingWorkPackages = new ObjectArrayList<>();
		for (Transaction t : trainingTransactions) {
			trainingWorkPackages
					.add(((WorkPackageClick) workPackageFactory.getWorkPackage(t,null)).clickData);
		}
		//save some RAM
		trainingTransactions = null;

		// create a thread pool executor to limit the number of concurrent
		// threads to avoid thrashing
		ExecutorService executor = Executors.newFixedThreadPool(THREAD_COUNT);

		// extract all sessions of users for evaluation phase
		// create map of next transactions
		List<Transaction> testTransactions = new ObjectArrayList<Transaction>();
		Util.extractEventTypes(splitData.testData, new ObjectArrayList<Item>(), testTransactions);
		SessionExtractor sessionExtractorforEvaluation = new SessionExtractor();
		for (Transaction t : testTransactions) {
			sessionExtractorforEvaluation.addClick(t);
		}
        long realTestTime = testTransactions.get(testTransactions.size()-1).timestamp.getTime() - testTransactions.get(0).timestamp.getTime();
		//save some RAM
		testTransactions = null;

		//Log the time window of the eval
		System.out.println("The training time window is: " + Util.printETA(realTrainTime));
		System.out.println("The test time window is: " + Util.printETA(realTestTime));


		// test phase
		int nextPercentage = 0;
		List<WorkPackage> testWorkPackages = new ObjectArrayList<>();
		for (int i = 0; i < splitData.testData.size(); i++) {
			// log the progress
			int percentage = (int) ((1d * (i + 1) / splitData.testData.size()) * 10);
			if (percentage == nextPercentage) {
				System.out.println("Creating work packages. Progress: " + percentage * 10 + "%");
				nextPercentage++;
			}
			// extract the current event
			Event currentEvent = splitData.testData.get(i);
			//create a work package (with click, session, ground truth, etc.)
			//and add it to the list of test packages
			testWorkPackages
					.add(workPackageFactory.getWorkPackage(currentEvent, sessionExtractorforEvaluation));
		}

		// create threaded wrappers
		AlgorithmWrapper.nbOfAlgorithms = algorithmsWithName.size();
		for (Entry<String, Algorithm> alg : algorithmsWithName.entrySet()) {
			AlgorithmWrapper wrapper = new AlgorithmWrapper(alg.getValue(), metrics.get(alg.getKey()), trainingItems,
					trainingWorkPackages, testWorkPackages);
			//execute right away
			executor.execute(wrapper);
		}
		//save some RAM
		algorithmsWithName = null;
		trainingItems = null;
		trainingWorkPackages = null;
		testWorkPackages = null;

		//wait for all algorithms to finish (note: AlgorithmWrapper class does some tmp output)
		executor.shutdown();
		while (!executor.awaitTermination(10, TimeUnit.SECONDS)) {
			// wait for threads to finish
		}
	}

	private static void printResult(String timeThreshold, Map<Metric, String> metricsByAlgorithm, Map<String, List<Metric>> metricsByName) {
		// output parameters again for convenience
		System.out.println();
		printFileInfo(timeThreshold);

		// print evaluation results extracted from metric classes
		DecimalFormat df = new DecimalFormat("0.0000000");
		df.setDecimalFormatSymbols(new DecimalFormatSymbols(Locale.US));

		//print the actual results
		for (Entry<String, List<Metric>> ml : metricsByName.entrySet()) {
			System.out.println(ml.getKey());
			for (Metric m : metricsByName.get(ml.getKey())) {
				System.out.println(
						StringUtils.rightPad(metricsByAlgorithm.get(m), 70, ' ') + "\t" + df.format(m.getResults()));
			}
		}

		//create and print statistical significance tests
		List<HypothesisTestableMetric> statMetrics = new ArrayList<>();
		for (Entry<String, List<Metric>> ml : metricsByName.entrySet()) {
			if(ml.getValue().iterator().next() instanceof HypothesisTestableMetric){
				for (Metric metric : ml.getValue()) {
					statMetrics.add((HypothesisTestableMetric) metric);
				}
			}
		}
		if(OUTPUT_STATS){
			System.out.println();
			System.out.println("---- STATISTICAL RESULTS ----");
			System.out.println();
			System.out.println(Util.executeStatisticalTests(statMetrics, true));
		}
	}

	private static Map<String, Algorithm> getAlgorithms() throws IOException {
		// load algorithms so that configuration errors appear before input file loading
		List<Algorithm> tmpAlgorithms = Config.loadAlgorithms(ALGORITHM_FILE_NAME);
		printAlgorithms(tmpAlgorithms);
		// create algorithms
		Map<String, Algorithm> algorithmsWithName = new Object2ObjectLinkedOpenHashMap<String, Algorithm>();
		for (Algorithm alg : tmpAlgorithms) {
			algorithmsWithName.put(alg.getName(), alg);
		}
		return algorithmsWithName;
	}

	private static void printAlgorithms(List<Algorithm> tmpAlgorithms) {
		// output names of algorithms and check redundant names
		Set<String> names = new ObjectOpenHashSet<>();
		System.out.println("Tested algorithms:");
		for (Algorithm algorithm : tmpAlgorithms) {
			if (!names.add(algorithm.getName())) {
				System.err.println(
						"Duplicate name for algorithm: \"" + algorithm.getName() + "\". Please check config file.");
				System.err.println("Terminating");
				throw new IllegalArgumentException("Duplicate name for algorithm");
			}
			System.out.println(algorithm.getName());
		}
		System.out.println();
	}

	private static void printFileInfo(String timeThreshold) {
		System.out.println(
				"Input files: \"" + INPUT_FILENAME_ITEMS + "\" & \"" + INPUT_FILENAME_CLICKS + "\"");
		System.out.println("Config files: \"" + ALGORITHM_FILE_NAME + "\" & \"" + METRICS_FILE_NAME + "\"");
		System.out.println("session Time Thresholds: \"" + timeThreshold + "\"");
		System.out.println("Session length filter: " + SESSION_LENGTH_FILTER);
		System.out.println("Split threshold: " + SPLIT_THRESHOLD);
		System.out.println();
	}


	/**
	 * Add each metrics to some maps so that they can be found later and printed nicely.
	 *
	 * @param metric -
	 * @param algorithmName -
	 * @param metricName -
	 * @param metricsByAlgorithm -
	 * @param metricsToAlgorithm -
	 * @param metricsByName -
	 */
	private static void addMetricToMaps(Metric metric, String algorithmName, String metricName,
										Map<String, List<Metric>> metricsByAlgorithm, Map<Metric, String> metricsToAlgorithm,
										Map<String, List<Metric>> metricsByName) {
		metricsToAlgorithm.put(metric, algorithmName);
		List<Metric> list = metricsByName.get(metricName);
		if (list == null) {
			list = new ObjectArrayList<Metric>();
			metricsByName.put(metricName, list);
		}
		list.add(metric);
		list = metricsByAlgorithm.get(algorithmName);
		if (list == null) {
			list = new ObjectArrayList<Metric>();
			metricsByAlgorithm.put(algorithmName, list);
		}
		list.add(metric);
	}

	/**
	 * the item input file
	 * @return the item input file
	 */
	static String getInputFilenameItems() {
		return INPUT_FILENAME_ITEMS;
	}

	/**
	 * the click input file
	 * @return the click input file
	 */
	static String getInputFilenameClicks() {
		return INPUT_FILENAME_CLICKS;
	}

	/**
	 * the path to the metric json config file
	 * @return the path to the metric json config file
	 */
	static String getMetricsFileName() {
		return METRICS_FILE_NAME;
	}

	/**
	 * the path the the algorithm json config file
	 * @return the path the the algorithm json config file
	 */
	static String getAlgorithmFileName() {
		return ALGORITHM_FILE_NAME;
	}

	/**
	 * the time for the sessions inactivity threshold
	 * @return the time for the sessions inactivity threshold
	 */
	static long getSessionTimeThreshold() {
		return SESSION_TIME_THRESHOLD;
	}

	/**
	 * if this parameter is set to greater than 0, sessions with this number or
	 * less clicks will be removed from the data set
	 * @return the session Length Filter
	 */
	static int getSessionLengthFilter() {
		return SESSION_LENGTH_FILTER;
	}

	/**
	 * where to split the data into training and test
	 * @return the split Threshold
	 */
	static double getSplitThreshold() {
		return SPLIT_THRESHOLD;
	}

	/**
	 * the global start time used for output writing to the same folder
	 * @return the global start Time
	 */
	static String getStartTime() {
		return startTime;
	}
}
