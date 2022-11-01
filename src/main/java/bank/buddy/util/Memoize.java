package bank.buddy.util;

import java.util.function.Supplier;

public class Memoize<T> implements Supplier<T> {

	private T cache;
	private final Supplier<T> supplier;

	public Memoize(Supplier<T> supplier) {
		this.supplier = supplier;
	}

	@Override
	public T get() {
		if (cache == null) {
			synchronized (this) {
				if (cache == null) {
					cache = supplier.get();
				}
			}
		}

		return cache;
	}

}
