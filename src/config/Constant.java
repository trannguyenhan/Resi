package config;

public class Constant {
	// Switch delay ~ 100ns
	public static final int SWITCH_DELAY = 100;
	// Link bandwidth, set default to 1Gps
	public static final long LINK_BANDWIDTH = (long) 96 * 1000 * 1000;
	// Default length of link ~ 5m
	public static final double DEFAULT_LINK_LENGTH = 5;
	public static final double HOST_TO_SWITCH_LENGTH = 0.1;

	// Velocity of link m/ns
	public static final double PROPAGATION_VELOCITY = 1.0 / 5;// 0.2 m/ns

	// Host/Switch delay, default is 100ns
	public static final int HOST_DELAY = 100000;// ns => 10^5 * 10^(-9) = 10^(-4)(s)

	// Packet size ~ 1Mb
	public static final int PACKET_SIZE = // (int) 1e5; // 100Kb
			9600; // 9.6Kb
	// Maximum time system
	public static final long MAX_TIME = 60 * ((long) 1e9);
	public static final int TIME_REARRANGE = (int) 1e9;

	public static final int PACKET_INTERVAL = PACKET_SIZE;

	public static final double RETRY_TIME = 10;
	public static final double PORT_BUFFER_SIZE = 1e5;
	public static final double EXPERIMENT_INTERVAL = 1e7;
	public static final int QUEUE_SIZE = 5;
	public static final int SWITCH_CYCLE = 10;
	public static final int CREDIT_DELAY = 1;

}
