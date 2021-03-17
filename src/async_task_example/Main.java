package async_task_example;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class Main {
	private static final ExecutorService executor = Executors
			.newFixedThreadPool(5);

	public static void main(String[] args) throws Exception {
		List<Future<String>> rets = new ArrayList<>();
		for (int i = 0; i < 5; i++) {
			Future<String> ret = executor.submit(new MyTask());
			rets.add(ret);
		}

		rets.stream().forEach(s -> {
			try {
				System.out.println(System.currentTimeMillis() + ":" + s.get());
			} catch (Exception e) {
			}
		});
		executor.shutdown();
	}

}

class MyTask implements Callable<String> {
	@Override
	public String call() throws Exception {
		System.out.println(System.currentTimeMillis() + ": call "
				+ Thread.currentThread().getName());

		Thread.sleep(2 * 1000);

		return Thread.currentThread().getName();
	}
}
