package bank.buddy.table;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.BiConsumer;
import java.util.function.Function;

import bank.buddy.bank.transaction.TransactionWrapper;
import bank.buddy.util.ValutaUtil;
import javafx.collections.FXCollections;
import javafx.scene.control.TableCell;

public class TransactionTable
		extends AutoFillingTable<bank.buddy.table.TransactionTable.TransactionRow, TransactionWrapper> {

	static class TransactionRow {
		private TransactionWrapper income;
		private TransactionWrapper expense;

		void setIncome(TransactionWrapper trans) {
			this.income = trans;
		}

		void setExpense(TransactionWrapper trans) {
			this.expense = trans;
		}

		Optional<TransactionWrapper> income() {
			return Optional.ofNullable(income);
		}

		Optional<TransactionWrapper> expense() {
			return Optional.ofNullable(expense);
		}
	}

	private static class TransactionTableColumn implements ColumnDefinition<TransactionRow, TransactionWrapper> {

		private final String header;
		private final Function<TransactionRow, Optional<TransactionWrapper>> func;

		TransactionTableColumn(String header, Function<TransactionRow, Optional<TransactionWrapper>> func) {
			this.header = header;
			this.func = func;
		}

		@Override
		public String header() {
			return header;
		}

		@Override
		public Function<TransactionRow, Optional<TransactionWrapper>> contentProducer() {
			return func;
		}

		@Override
		public String toReadable(TransactionWrapper val) {
			return ValutaUtil.toValuta(val.transaction().amount());
		}

		@Override
		public void layoutCellImpl(TransactionWrapper val, TableCell<TransactionRow, TransactionWrapper> cell) {
			cell.setText(ValutaUtil.toValuta(val.transaction().amount()));
			cell.textFillProperty().bind(val.textFill());
			cell.backgroundProperty().bind(val.background());
		}

		@Override
		public TransactionWrapper cellSelection(TransactionRow val) {
			return func.apply(val).get();
		}

	}

	public TransactionTable(List<TransactionWrapper> transactions) {
		addColumn(new TransactionTableColumn("Uitgaven", TransactionRow::expense));
		addColumn(new TransactionTableColumn("Inkomsten", TransactionRow::income));

		getSelectionModel().setCellSelectionEnabled(true);

		// Handle values
		var values = new ArrayList<TransactionRow>();

		AtomicInteger incomeIndex = new AtomicInteger(0);
		AtomicInteger expenseIndex = new AtomicInteger(0);
		transactions.forEach(trans -> {
			if (trans.transaction().amount() < 0) {
				merge(expenseIndex, trans, values, TransactionRow::setExpense);
			} else {
				merge(incomeIndex, trans, values, TransactionRow::setIncome);
			}
		});

		setItems(FXCollections.observableList(values));
	}

	private void merge(AtomicInteger atomicIndex, TransactionWrapper trans, List<TransactionRow> values,
			BiConsumer<TransactionRow, TransactionWrapper> callback) {
		int index = atomicIndex.getAndIncrement();
		TransactionRow row;
		if (index == values.size()) {
			row = new TransactionRow();
			values.add(row);
		} else {
			row = values.get(index);
		}

		callback.accept(row, trans);
	}

}
