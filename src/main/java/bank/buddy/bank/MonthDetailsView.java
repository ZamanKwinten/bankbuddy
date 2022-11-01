package bank.buddy.bank;

import java.util.List;

import bank.buddy.bank.transaction.MonthSummary;
import bank.buddy.bank.transaction.TransactionWrapper;
import bank.buddy.charts.TotalExpensesChart;
import bank.buddy.table.TransactionTable;
import bank.buddy.ui.CategorySelectionPopup;
import bank.buddy.ui.CategorySelectionPopup.CategorySelectionPopupParam;
import bank.buddy.ui.FXUtils;
import bank.buddy.ui.MaskedLabel;
import bank.buddy.ui.ValutaLabel;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;

public class MonthDetailsView extends GridPane {

	private final StackPane details = new StackPane();

	public MonthDetailsView(MonthSummary summary, List<TransactionWrapper> transactions) {
		setVgap(5);
		setHgap(10);

		var monthCard = new MonthSummaryCard(summary);
		add(monthCard, 0, 0);

		var table = new TransactionTable(transactions);

		table.onSelectionChange(this::initTransactionDetails, () -> initEmptyDetails(summary, transactions));

		GridPane.setVgrow(table, Priority.ALWAYS);
		add(table, 0, 1);

		GridPane.setVgrow(details, Priority.ALWAYS);
		GridPane.setHgrow(details, Priority.ALWAYS);
		add(details, 1, 1);

		initEmptyDetails(summary, transactions);
	}

	private void initEmptyDetails(MonthSummary summary, List<TransactionWrapper> transactions) {
		setDetails(new TotalExpensesChart(String.format("Uitgaven voor maand: %s", summary.getDateString()),
				transactions));
	}

	private void initTransactionDetails(TransactionWrapper trans) {
		var grid = new GridPane();
		grid.setHgap(5);
		grid.setVgap(5);

		var c0r0 = new Label("Begunstigde:");
		grid.add(c0r0, 0, 0);
		var c1r0 = new Label(trans.transaction().counterpartyName());
		grid.add(c1r0, 1, 0);

		var c0r1 = new Label("IBAN:");
		grid.add(c0r1, 0, 1);

		var c1r1 = trans.transaction().counterPartyIban().isBlank() ? new Label("Betaling Bancontact")
				: new MaskedLabel(".... .... .... ....", trans.transaction().counterPartyIban());
		grid.add(c1r1, 1, 1);

		var c0r2 = new Label("Bedrag:");
		grid.add(c0r2, 0, 2);
		var c1r2 = new ValutaLabel(trans.transaction().amount());
		grid.add(c1r2, 1, 2);

		var c0r3 = new Label("Datum:");
		grid.add(c0r3, 0, 3);
		var c1r3 = new Label(String.format("%02d/%02d/%s", trans.transaction().day(), trans.transaction().month(),
				trans.transaction().year()));
		grid.add(c1r3, 1, 3);

		var c0r4 = new Label("Mededeling:");
		grid.add(c0r4, 0, 4);
		var c1r4 = new Label(trans.transaction().extraInfo());
		grid.add(c1r4, 1, 4);

		var c0r5 = new Label("Categorie:");
		grid.add(c0r5, 0, 5);

		var c1r5 = new Label("");
		c1r5.setCursor(Cursor.HAND);

		c1r5.textProperty().bind(trans.categoryName());
		c1r5.textFillProperty().bind(trans.textFill());
		c1r5.backgroundProperty().bind(trans.background());

		c1r5.setOnMouseClicked(event -> {
			var list = trans.transaction().amount() < 0 ? GlobalState.expenseCategories : GlobalState.incomeCategories;
			var counterPartyNameMap = trans.transaction().amount() < 0 ? GlobalState.expenseCounterPartyNameMap
					: GlobalState.incomeCounterPartyNameMap;
			var transactionIDMap = trans.transaction().amount() < 0 ? GlobalState.expenseTransactionIDMap
					: GlobalState.incomeTransactionIDMap;

			var popup = new CategorySelectionPopup(
					new CategorySelectionPopupParam(trans.transaction().amount() < 0, trans.categoryName(), list));
			popup.open().ifPresent(x -> {
				switch (x.action()) {
				case ALL_TRANSACTIONS -> {
					counterPartyNameMap.put(trans.transaction().counterpartyName(), x.category());
				}
				case ONLY_THIS_TRANSACTION -> {
					transactionIDMap.put(trans.transaction().id(), x.category());
				}
				}
			});
		});
		grid.add(c1r5, 1, 5);

		setDetails(grid);
	}

	private void setDetails(Node n) {
		details.getChildren().setAll(n);

		if (this.getScene() != null) {
			FXUtils.expandToScene(this.getScene());
		}
	}
}
