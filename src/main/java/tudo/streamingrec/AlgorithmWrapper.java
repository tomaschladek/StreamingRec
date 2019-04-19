package tudo.streamingrec;

import it.unimi.dsi.fastutil.ints.Int2IntOpenHashMap;
import it.unimi.dsi.fastutil.longs.LongArrayList;
import it.unimi.dsi.fastutil.longs.LongOpenHashSet;
import org.apache.commons.lang3.time.StopWatch;
import org.joda.time.Period;
import org.joda.time.format.ISOPeriodFormat;
import tudo.streamingrec.algorithms.Algorithm;
import tudo.streamingrec.data.ClickData;
import tudo.streamingrec.data.Item;
import tudo.streamingrec.evaluation.metrics.HypothesisTestableMetric;
import tudo.streamingrec.evaluation.metrics.Metric;
import tudo.streamingrec.evaluation.metrics.Runtime;
import tudo.streamingrec.evaluation.metrics.Runtime.Type;
import tudo.streamingrec.util.Util;

import java.io.*;
import java.net.URLEncoder;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.*;

/**
 * A wrapper around the algorithm class that executes it in a thread
 * 
 * @author MJ
 *
 */
public class AlgorithmWrapper extends Thread {

	//the wrapped algorithm instance
	private Algorithm testee;
	//the metrics associated with this algorithm
	private List<Metric> metrics;
	//the items for the training phase
	private List<Item> trainItems;
	//the clicks for the training phase
	private List<ClickData> trainTransactions;
	//a queue of work packages (items and clicks) for the testing phase
	private List<WorkPackage> eventQueue;

	/**
	 * creates an algorithm wrapped in a thread
	 * 
	 * @param testee
	 *            the algorithm to be tested
	 * @param metrics
	 *            its associated metrics
	 * @param trainItems -
	 * @param trainTransactions -
	 * @param eventQueue -
	 */
	public AlgorithmWrapper(Algorithm testee, List<Metric> metrics, List<Item> trainItems,
			List<ClickData> trainTransactions, List<WorkPackage> eventQueue) {
		this.testee = testee;
		this.metrics = metrics;
		this.trainItems = trainItems;
		this.trainTransactions = trainTransactions;
		this.eventQueue = eventQueue;
	}

	@SuppressWarnings("unchecked")
	public void run() {
		//keep the runtime
		StopWatch trainTime = new StopWatch();
		StopWatch testTime = new StopWatch();
		StopWatch inBetweenTrainTime = new StopWatch();
		inBetweenTrainTime.start();
		inBetweenTrainTime.suspend();
		executeAlgorithm(trainTime, testTime, inBetweenTrainTime);

		//cleanup to save RAM
		testee = null;
		metrics = null;
		eventQueue = null;
	}

	protected void executeAlgorithm(StopWatch trainTime, StopWatch testTime, StopWatch inBetweenTrainTime) {
		testee.initialize();
		initialTraining(trainTime);
		testing(testTime, inBetweenTrainTime);
		saveRunTimeMetrics(trainTime, testTime, inBetweenTrainTime);

		//all work is done -> write the results to the tmp result file
		//the Runner will retrieve the results via references to the metrics
		writeResult(testee.getName(), metrics);
	}

	protected void testing(StopWatch testTime, StopWatch inBetweenTrainTime) {
		testTime.start();


		//next, we start the test phase
		int nextPercentage = 0;
		for (int i = 0; i < eventQueue.size(); i++) {
			nextPercentage = tryReportProgress(nextPercentage, i);

			// take one work package
			WorkPackage wp = eventQueue.get(i);
			if (wp instanceof WorkPackageArticle) {
				processItem(testTime, inBetweenTrainTime, (WorkPackageArticle) wp);

			} else {
				processTransaction(testTime, inBetweenTrainTime, (WorkPackageClick) wp);
			}
		}
	}

	protected void initialTraining(StopWatch trainTime) {
		trainTime.start();
		// first, we train
		testee.train(trainItems, trainTransactions);
		trainItems = null;
		trainTransactions = null;
		trainTime.stop();
	}

	protected int tryReportProgress(int nextPercentage, int i) {
		// calculate the current progress regularly
		int percentage = (int) ((1d * (i + 1) / eventQueue.size()) * 100);
		if (percentage == nextPercentage) {
			nextPercentage++;
			//report the progress internally if a new percentage is reached
			progress(percentage);
		}
		return nextPercentage;
	}

	protected void processItem(StopWatch testTime, StopWatch inBetweenTrainTime, WorkPackageArticle wp) {
		//in case of article -> send to train method
		Item articleEvent = wp.articleEvent;
		// notify the algorithm about new articles
		testTime.suspend();
		inBetweenTrainTime.resume();
		testee.train(Collections.singletonList(articleEvent), Collections.EMPTY_LIST);
		inBetweenTrainTime.suspend();
		testTime.resume();
	}

	protected void processTransaction(StopWatch testTime, StopWatch inBetweenTrainTime, WorkPackageClick wp) {
		//in case of click, generate recommendation list and then send to train method
		WorkPackageClick wpC = wp;
		//generate recommendations here
		LongArrayList recommendations = testee.recommend(wpC.clickData);
		testTime.suspend();
		inBetweenTrainTime.resume();
		testee.train(Collections.EMPTY_LIST, Collections.singletonList(wpC.clickData));
		inBetweenTrainTime.suspend();
		testTime.resume();
		evaluateMetrics(wpC, recommendations);
	}

	protected void evaluateMetrics(WorkPackageClick wpC, LongArrayList recommendations) {
		//evaluate metrics
		for (Metric m : metrics) {
			try {
				m.evaluate(wpC.clickData.click, recommendations, wpC.groundTruth);
			} catch (Exception ex) {
				throw new RuntimeException(testee.getName() + ": " + ex.getMessage());
			}
		}
	}

	protected void saveRunTimeMetrics(StopWatch trainTime, StopWatch testTime, StopWatch inBetweenTrainTime) {
		//test phase is over -> save the runtime results in the special metric instances
		for (Metric metric : metrics) {
			if (metric instanceof Runtime) {
				Runtime rt = (Runtime) metric;
				if (rt.getType() == Type.Training) {
					rt.setRuntime(trainTime.getTime());
				} else if (rt.getType() == Type.InBetweenTraining) {
					rt.setRuntime(inBetweenTrainTime.getTime());
				} else {
					rt.setRuntime(testTime.getTime() * 1d / eventQueue.size());
				}
			}
		}
	}

	//------------------------------------------------------------
	// Static methods for progress printing and tmp result output
	//------------------------------------------------------------
		
	//synchonization object
	private static Object outputSync = new Object();
	//filename for tmp output
	private static String fileName = null;
	//constants for output file names
	private static final String folder = "output";
	private static final String prefix = "tmp_results_";
	public static final String statPrefix = "stat_results_";
	private static final String postfix = ".txt";
	private static final String statPostfix = ".bin";

	/**
	 * Writes the results of an algorithm to the tmp result file
	 * and to detailed result files in case of t-testable metrics.
	 * @param name -
	 * @param metrics -
	 */
	private static void writeResult(String name, List<Metric> metrics) {
		//intialize the output format
		DecimalFormat df = new DecimalFormat("0.0000000");
		df.setDecimalFormatSymbols(new DecimalFormatSymbols(Locale.US));
		//create the metric result line for this algorithm
		StringBuilder outputString = new StringBuilder();
		outputString.append(name);
		outputString.append(";");
		for (Metric metric : metrics) {
			outputString.append(metric.getName());
			outputString.append(";");
			outputString.append(df.format(metric.getResults()));
			outputString.append(";");
		}
		//write synchronized
		synchronized (outputSync) {
			//if the file does not exist, create folders and filename
			if (fileName == null) {
				new File(folder+"/"+StreamingRec.startTime).mkdirs();
				fileName = folder +"/"+StreamingRec.startTime + "/" + prefix + StreamingRec.startTime + postfix;
				
			}
			try {
				//decide if a header is needed
				boolean printHeader = false;
				if (!new File(fileName).exists()) {
					printHeader = true;
				}
				//create writer
				PrintWriter output = new PrintWriter(new FileWriter(fileName, true));				
				if (printHeader) {
					//print a header
					output.println("#Input files: \"" + StreamingRec.getInputFilenameItems() + "\" & \""
							+ StreamingRec.getInputFilenameClicks() + "\"");
					output.println("#Config files: \"" + StreamingRec.getAlgorithmFileName() + "\" & \""
							+ StreamingRec.getMetricsFileName() + "\"");
					Period period = new Period(StreamingRec.getSessionTimeThreshold());
					String timeThreshold = ISOPeriodFormat.standard().print(period).replace("PT", "");
					output.println("#session Time Thresholds: \"" + timeThreshold + "\"");
					output.println("#Session length filter: " + StreamingRec.getSessionLengthFilter());
					output.println("#Split threshold: " + StreamingRec.getSplitThreshold());
					output.println("#");
				}
				//write the previously created result line
				output.println(outputString.toString());
				//flush and close the writer
				output.flush();
				output.close();

				//print detailed stats for t-test
				try (FileOutputStream f = new FileOutputStream(new File(folder + "/"+StreamingRec.startTime  + "/" + statPrefix + StreamingRec.startTime  + URLEncoder.encode(name, "UTF-8") + statPostfix), true);
					//use java defaut binary serialization to save HDD space
					ObjectOutputStream o =new ObjectOutputStream(f)){
					// Write objects to file
					for (Metric metric : metrics) {
						if (metric instanceof HypothesisTestableMetric)
							o.writeObject((HypothesisTestableMetric) metric);
					}
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	//progress reporting
	public static int nbOfAlgorithms;
	//a map of progress counters
	private static Map<Integer, Integer> progressCounter = new Int2IntOpenHashMap();
	//the output format
	private static DecimalFormat df = new DecimalFormat("0.0");
	{
		df.setDecimalFormatSymbols(new DecimalFormatSymbols(Locale.US));
	}// static setting
	//the last N timestamps for ETA calculation
	private static LinkedList<Long> lastTimestamps = new LinkedList<Long>();

	/**
	 * A method that aggregates the progress reports of the algorithm wrappers
	 * to give a detailed progress report on the console. Algorithms do not report their
	 * progress by name but just their current progress in percent.
	 * 
	 * @param progress the current progress of the algorithm
	 */
	public static void progress(int progress) {
		//check the progress synchronized
		synchronized (progressCounter) {
			//remember the progress count in a map
			Integer count = progressCounter.get(progress);
			if (count == null) {
				count = 0;
			}
			progressCounter.put(progress, count + 1);
			// generate detailed progress output (useful in long runs)
			StringBuilder detailedProgress = new StringBuilder("Detailed progress: ");
			int accProgress = 0;
			int accountedFor = 0;
			for (int i = 100; i >= 0; i--) {
				// go through all percentages and count the number of algorithms
				// in each
				// -> print to detailed statistics and accumulate global stat
				Integer countForThisPercentage = progressCounter.get(i);
				if (countForThisPercentage != null) {
					int cleaned = countForThisPercentage - accountedFor;
					accountedFor += cleaned;
					if (cleaned > 0) {
						accProgress += i * cleaned;
						detailedProgress.append(cleaned + "|" + i + " ");
					}
				}
			}
			// check how many have not reported anything
			int stillInTrain = nbOfAlgorithms - accountedFor;
			if (stillInTrain > 0) {
				detailedProgress.append(stillInTrain + "|T");
			}
			// calculate global and print
			double overall = accProgress * 1d / nbOfAlgorithms;
			System.out.print("Approx. overall progress: " + df.format(overall) + "%. ");
			// calculate and print ETA
			lastTimestamps.add(System.currentTimeMillis());
			//check the last N (20) timestamps of each 1% progress increment to generate an ETA
			double cacheSize = 20;
			if (lastTimestamps.size() > cacheSize) {
				long elapsedMs = System.currentTimeMillis() - lastTimestamps.get(0);
				int remainingMs = (int) ((nbOfAlgorithms * 100d - accProgress) * elapsedMs / cacheSize);
				lastTimestamps.removeFirst();
				System.out.print("ETA: " + Util.printETA(remainingMs));
			}

			// print detailed
			System.out.println(detailedProgress);
		}
	}
	

	/**
	 * A class that represents one work package (i.e. one task for the
	 * algorithm)
	 * 
	 * @author MJ
	 *
	 */
	public abstract static class WorkPackage {
	}

	/**
	 * A work package that contains new article meta data
	 * @author MJ
	 *
	 */
	public static class WorkPackageArticle extends WorkPackage {
		public Item articleEvent;
	}

	/**
	 * A work package that contains click data and the ground truth
	 * (i.e. the next user clicks) related to this click data for later evaluation.
	 * @author MJ
	 *
	 */
	public static class WorkPackageClick extends WorkPackage {
		public ClickData clickData;
		public LongOpenHashSet groundTruth;
	}

}
