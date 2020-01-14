//Name: Anushree Badarinath Kasal
//Student ID: 1001624311
//Net ID: abk4311
// Lab2
//https://www.coderanch.com/t/685553/java/Lamport-Clock-Difficulties-Logic-implement
//https://medium.com/@balrajasubbiah/lamport-clocks-and-vector-clocks-b713db1890d7
//https://codippa.com/how-to-generate-a-random-number-with-in-a-range-in-java/
//https://dzone.com/articles/random-number-generation-in-java


import java.util.Random;

public class LogicalClock {

	private int clock;
	private Object syncObj;

	public LogicalClock() {
		syncObj = new Object();
		clock = new Random().nextInt(50);
		runThread();
	}

	// Starts the timer to update the clock every second at rate of 10.
	private void runThread() {
		new Thread(new Runnable() {

			private void incrementThread() {
				synchronized (syncObj) {
					clock += 10;
				}
			}

			@Override
			public void run() {
				while (true) {
					try {
						incrementThread();
						Thread.sleep(999);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}
		}).start();
	}

	// Logic to check if adjustment is needed.
	public int checkAdjustment(int otherClock) {

		synchronized (syncObj) {
			if (otherClock > clock) {
				// adjusting clock
				clock = otherClock + 1;
				return clock;
			} else {
				// No adjustment needed.
				return -99;
			}
		}
	}
    // returning clock value
	public int getClock() {
		return clock;
	}
}
