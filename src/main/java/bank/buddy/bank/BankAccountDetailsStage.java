package bank.buddy.bank;

import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import bank.buddy.bank.transaction.MonthSummary;
import bank.buddy.bank.transaction.TransactionCollection;
import bank.buddy.bank.transaction.TransactionWrapper;
import bank.buddy.bank.transaction.YearSummary;
import bank.buddy.charts.TotalExpensesChart;
import bank.buddy.file.FileMgr;
import bank.buddy.ui.FXUtils;
import bank.buddy.ui.SummaryListView;
import javafx.beans.property.SimpleObjectProperty;
import javafx.geometry.Insets;
import javafx.geometry.VPos;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Stage;

public class BankAccountDetailsStage extends Stage {
	private final ABankAccount account;

	private final StackPane detailsView = new StackPane();

	private final SimpleObjectProperty<TransactionCollection> transactionCollection = new SimpleObjectProperty<>();

	public BankAccountDetailsStage(ABankAccount account) {
		this.account = account;

		FileMgr.get().readAccountTransactions(account.iban()).ifPresentOrElse((coll) -> {
			transactionCollection.set(coll);
		}, () -> {
			account.loadTransactionsFromRemote(coll -> {
				transactionCollection.set(coll);
			});
		});

		GridPane root = new GridPane();
		root.setPadding(new Insets(5));
		root.setHgap(5);
		root.setVgap(5);

		var rekening = new Label("Rekening:");
		rekening.setFont(new Font(25));
		GridPane.setValignment(rekening, VPos.TOP);
		root.add(rekening, 0, 0);

		var card = new BankAccountCard(account);
		card.setCursor(Cursor.HAND);
		card.setOnMouseClicked(event -> {
			account.loadTransactionsFromRemote(coll -> {
				transactionCollection.set(coll);
				this.toFront();
			});
		});
		root.add(card, 1, 0);

		var stackPane = new StackPane();
		GridPane.setVgrow(stackPane, Priority.ALWAYS);

		root.add(stackPane, 0, 1, 2, 1);
		stackPane.getChildren().setAll(initMonthListView());

		AtomicBoolean monthOverviewShowing = new AtomicBoolean(true);
		var button = new Button("Toon Jaar Overzicht");
		button.setOnAction(event -> {
			if (monthOverviewShowing.get()) {
				stackPane.getChildren().setAll(initYearListView());

				monthOverviewShowing.set(false);
				button.setText("Toon Maand Overzicht");
				initDetailsView();
			} else {

				stackPane.getChildren().setAll(initMonthListView());
				monthOverviewShowing.set(true);
				button.setText("Toon Jaar Overzicht");
				initDetailsView();
			}
		});

		root.add(button, 0, 2, 2, 1);

		GridPane.setHgrow(detailsView, Priority.ALWAYS);
		root.add(detailsView, 3, 0, 1, 3);

		detailsView.setBackground(new Background(new BackgroundFill(Color.LIGHTGRAY, CornerRadii.EMPTY, Insets.EMPTY)));

		setScene(new Scene(root));
		setTitle("Overzicht");
		initDetailsView();
	}

	private Node initMonthListView() {
		var listView = new SummaryListView<MonthSummary>(transactionCollection,
				TransactionCollection::getMontlyOverview) {

			@Override
			protected void noSelectionCallback() {
				initDetailsView();
			}

			@Override
			protected List<TransactionWrapper> getTransaction(TransactionCollection collection, MonthSummary key) {
				return collection.getMontlyTransactions(key.toMonth());
			}

			@Override
			protected Node detailsView(MonthSummary summary, List<TransactionWrapper> transactions) {
				return new MonthDetailsView(summary, transactions);
			}

			@Override
			protected void selectionCallback(Node details) {
				initDetailsView(details);
			}

		};
		return listView;
	}

	private Node initYearListView() {
		var listView = new SummaryListView<YearSummary>(transactionCollection,
				TransactionCollection::getYearlyOverview) {

			@Override
			protected void noSelectionCallback() {
				initDetailsView();
			}

			@Override
			protected List<TransactionWrapper> getTransaction(TransactionCollection collection, YearSummary key) {
				return collection.getYearlyTransactions(key.toYear());
			}

			@Override
			protected Node detailsView(YearSummary summary, List<TransactionWrapper> transactions) {
				return new TotalExpensesChart(String.format("Uitgaven voor jaar: %s", summary.getDateString()),
						transactions);
			}

			@Override
			protected void selectionCallback(Node details) {
				initDetailsView(details);
			}
		};
		return listView;
	}

	private void initDetailsView() {
		initDetailsView(new TotalExpensesChart("Totale Uitgaven Overzicht",
				transactionCollection.get().getAllTransactions().map(TransactionWrapper::new).toList()));
	}

	private void initDetailsView(Node view) {
		detailsView.getChildren().setAll(view);
		FXUtils.expandToScene(this.getScene());
	}
}
