package async_task_example;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Main {
	private static final ExecutorService executor = Executors
			.newFixedThreadPool(3);

	private static final ExecutorService retPoller = Executors
			.newFixedThreadPool(3);

	private static Map<String, String> retMap = Collections
			.synchronizedMap(new HashMap<>());

	public static void main(String[] args) throws Exception {
		for (int i = 0; i < 5; i++) {
			String key = "key" + Integer.toString(i);
			executor.submit(new MyTask(key, retMap));
		}

		for (int i = 0; i < 5; i++) {
			String key = "key" + Integer.toString(i);
			retPoller.submit(new PollerTask(key, retMap));
		}

		executor.shutdown();
		retPoller.shutdown();
	}

}

class MyTask implements Runnable {

	private String key;
	private Map<String, String> map;

	public MyTask(String key, Map<String, String> map) {
		this.key = key;
		this.map = map;
	}

	@Override
	public void run() {
		System.out.println(System.currentTimeMillis() + ": call "
				+ Thread.currentThread().getName());
		try {
			Thread.sleep(3 * 1000);
			//Thread.sleep(10 * 1000); // timeout occured.
		} catch (InterruptedException e) {
		}

		String threadName = Thread.currentThread().getName();
		map.put(key, "task finished. [" + threadName + "]");
	}
}

class PollerTask implements Runnable {

	private String key;
	private Map<String, String> map;

	public PollerTask(String key, Map<String, String> map) {
		this.key = key;
		this.map = map;
	}

	@Override
	public void run() {
		int timeout = 0;
		while (true) {
			if (timeout == 5) {
				System.err.println(key + " is timeout.");
				break;
			}

			if (map.get(key) != null) {
				System.out.println(map.get(key));
				break;
			}

			try {
				Thread.sleep(1 * 1000);
			} catch (InterruptedException e) {
			}
			timeout++;
		}
	}
}