package common;

import umontreal.ssj.rng.MRG31k3p;
import umontreal.ssj.rng.RandomStream;

import java.util.Random;

public class RandomGenerator {
	private static final int SEED_NOT_SET = -1;
	private static int seed = SEED_NOT_SET;

	public static void setSeed(int seed) {
		RandomGenerator.seed = seed;
		MRG31k3p.setPackageSeed(new int[] { seed, seed + 1, seed + 2, seed + 3, seed + 4, seed + 5 });
		Random r = new Random();
		r.setSeed(123);
	}

	public static void checkSeed() {
		if (seed == SEED_NOT_SET) {
			setSeed((int) (System.currentTimeMillis() % 1e9));
		}
	}

	public static int nextInt(int u, int v) {
		checkSeed();
		RandomStream r = new MRG31k3p();
		return r.nextInt(u, v);
	}

	public static double nextDouble() {
		checkSeed();
		RandomStream r = new MRG31k3p();
		return r.nextDouble();
	}

	public static void main(String[] args) {
		StdOut.println(RandomGenerator.nextDouble());
		StdOut.println(RandomGenerator.nextDouble());
		StdOut.println(RandomGenerator.nextDouble());
		StdOut.println(RandomGenerator.nextDouble());
	}
}
