package bank.buddy.ui;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class AsyncObservable<T> {

	private final CompletableFuture<T> future;

	public AsyncObservable(Supplier<T> supplier) {
		future = new CompletableFuture<>();

		var t = new Thread(() -> {
			future.complete(supplier.get());
		});

		t.setDaemon(true);
		t.start();
	}

	public void whenReady(Consumer<T> consumer) {
		if (future.isDone()) {
			try {
				consumer.accept(future.get());
			} catch (InterruptedException | ExecutionException e) {
				throw new RuntimeException(e);
			}
		}

		future.thenRun(() -> {
			try {
				consumer.accept(future.get());
			} catch (InterruptedException | ExecutionException e) {
				throw new RuntimeException(e);
			}
		});
	}

	public T get() {
		try {
			return future.get();
		} catch (InterruptedException | ExecutionException e) {
			throw new RuntimeException(e);
		}
	}

}
