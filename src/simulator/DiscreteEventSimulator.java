package simulator;

import java.util.ArrayList;
import java.util.List;
import common.StdOut;
import config.Constant;
import infrastructure.event.Event;

public class DiscreteEventSimulator extends Simulator {
	public static final int numReceived = 0;
	public long receivedPacketPerUnit[];
	public static final int numSent = 0;
	public static final int numLoss = 0;
	public static final long totalPacketTime = 0;
	public long numEvent = 0;
	private boolean isLimit;
	private double timeLimit;
	private boolean verbose;
	private boolean isAssigned = false;
	public static final long totalHop = 0;

	public List<Integer> sizeOfCurrEvents = new ArrayList<>();

	private static final DiscreteEventSimulator des = new DiscreteEventSimulator(true, Constant.MAX_TIME, false);

	public int halfSizeOfEvents = 0;

	private static boolean IS_LIMIT = false, VERBOSE = false;
	private static double TIME_LIMIT = 0;

	public static void Initialize(boolean isLimit, double timeLimit, boolean verbose) {
		IS_LIMIT = isLimit;
		TIME_LIMIT = timeLimit;
		VERBOSE = verbose;
	}

	public static DiscreteEventSimulator getInstance() {
		if (!des.isAssigned) {
			des.isLimit = IS_LIMIT;
			des.timeLimit = TIME_LIMIT;
			des.verbose = VERBOSE;
			des.receivedPacketPerUnit = new long[(int) (des.timeLimit / Constant.EXPERIMENT_INTERVAL + 1)];
			des.isAssigned = true;
		}
		return des;
	}

	private DiscreteEventSimulator(boolean isLimit, double timeLimit, boolean verbose) {
		super();
		this.isLimit = isLimit;
		this.verbose = verbose;
		this.timeLimit = timeLimit;
		this.receivedPacketPerUnit = new long[(int) (timeLimit / Constant.EXPERIMENT_INTERVAL + 1)];

	}

	public double getTime() {
		return currentTime;
	}

	public double getTimeLimit() {
		return timeLimit;
	}

	@Override
	public void start() {
		if (eventList.isEmpty())
			throw new IllegalStateException("start() called with an empty event list");
		stopped = false;
		simulating = true;
		umontreal.ssj.simevents.Event ev = null;

		try {
			long startTime = System.currentTimeMillis();// remove redundant variable
			int lastPercentage = 0;

			executeAndPrintProgress(lastPercentage, startTime, ev);

			StdOut.print("\r");
		} catch (Exception ex) {
			ex.printStackTrace();
		} finally {
			stopped = true;
			simulating = false;
		}
		System.out.println("# of Events: " + numEvent); // print the number of event
	}

	private void executeAndPrintProgress(int lastPercentage, long startTime, umontreal.ssj.simevents.Event ev) {
		
		while ((ev = removeFirstEvent()) != null && !stopped && (!isLimit || currentTime < timeLimit)) {
			numEvent++;
			ev.actions();
			int percentage = (int) (currentTime) / (int) Constant.EXPERIMENT_INTERVAL;

			if (percentage > lastPercentage) {
				lastPercentage = percentage;
				StdOut.printProgress("Progress", startTime, (long) timeLimit, currentTime);
			}
		}
	}
	
	@Override
	protected Event removeFirstEvent() {
		if (this.stopped) {
			return null;
		} else {
			Event ev = (Event) this.eventList.removeFirst();
			if (ev == null) {
				return null;
			} else {
				this.currentTime = ev.getEndTime();
				ev.setEndTime(-10);

				return ev;
			}
		}
	}

	public boolean isVerbose() {
		return verbose;
	}

	public void log(String message) {
		if (this.verbose) {
			StdOut.printf("At %d: %s\n", (long) this.getTime(), message);
		}
	}

	public void addEvent(Event e) {
		this.getEventList().add(e);
	}

	public long selectNextCurrentTime() {
		return Long.MAX_VALUE;
	}
}
