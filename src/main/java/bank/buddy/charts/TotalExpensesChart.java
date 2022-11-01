package bank.buddy.charts;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import bank.buddy.bank.transaction.Category;
import bank.buddy.bank.transaction.TransactionWrapper;

public class TotalExpensesChart extends ChartPane {

	public TotalExpensesChart(String title, List<TransactionWrapper> transactions) {
		super(transactions);

		titleProperty.set(title);
	}

	@Override
	protected Map<Category, Double> aggregateData(List<TransactionWrapper> data) {
		var result = new HashMap<Category, Double>();
		data.forEach(trans -> {
			var amount = trans.transaction().amount();
			if (amount < 0) {
				var category = trans.category();
				result.put(category, Math.abs(amount) + result.computeIfAbsent(category, k -> 0.0));
			}

		});

		return result;
	}

}
