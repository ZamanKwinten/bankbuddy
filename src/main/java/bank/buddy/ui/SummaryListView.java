package bank.buddy.ui;

import java.util.List;
import java.util.function.Function;

import bank.buddy.bank.transaction.TransactionCollection;
import bank.buddy.bank.transaction.TransactionWrapper;
import bank.buddy.ui.SummaryListView.Summary;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.geometry.VPos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Region;
import javafx.scene.text.Font;

public abstract class SummaryListView<T extends Summary> extends ListView<T> {

	public interface Summary {
		String getDateString();

		double income();

		double expenses();

		double balance();
	}

	private static class SummaryCard<T extends Summary> extends GridPane {
		public SummaryCard(T summary) {
			setHgap(25);

			var m = new Label(summary.getDateString());
			m.setFont(new Font(20));
			GridPane.setValignment(m, VPos.TOP);
			this.add(m, 0, 0, 1, 3);

			this.add(new Label("Inkomsten:"), 1, 0);
			this.add(new ValutaLabel(summary.income()), 2, 0);

			this.add(new Label("Uitgaven:"), 1, 1);
			this.add(new ValutaLabel(summary.expenses()), 2, 1);

			var spacer = new Region();
			spacer.setPrefHeight(5);
			this.add(spacer, 1, 2);

			var l = new Label("Balans:");
			l.setStyle("-fx-font-weight:bold");
			this.add(l, 1, 3);
			var v = new ValutaLabel(summary.balance());
			v.setStyle("-fx-font-weight:bold");
			this.add(v, 2, 3);
		}
	}

	public SummaryListView(SimpleObjectProperty<TransactionCollection> collection,
			Function<TransactionCollection, List<T>> parser) {
		super(FXCollections.observableList(parser.apply(collection.get())));
		collection.addListener((obs, ov, nv) -> {
			setItems(FXCollections.observableList(parser.apply(nv)));
		});

		setCellFactory((ListView<T> param) -> {
			var cell = new ListCell<T>() {
				@Override
				protected void updateItem(T item, boolean empty) {
					super.updateItem(item, empty);

					if (empty || item == null) {
						setText(null);
						setGraphic(null);
					} else {
						setText(null);
						setGraphic(new SummaryCard<>(item));
					}
				}
			};

			cell.addEventFilter(MouseEvent.MOUSE_PRESSED, (event) -> {
				var selectionModel = getSelectionModel();
				if (selectionModel.isSelected(cell.getIndex())) {
					selectionModel.clearSelection(cell.getIndex());
					noSelectionCallback();
				} else {
					selectionModel.select(cell.getIndex());
					var summary = selectionModel.getSelectedItem();

					var transactions = getTransaction(collection.get(), summary);

					var view = detailsView(summary, transactions);
					selectionCallback(view);
				}

				event.consume();
			});

			return cell;
		});
	}

	protected abstract void noSelectionCallback();

	protected abstract List<TransactionWrapper> getTransaction(TransactionCollection collection, T key);

	protected abstract Node detailsView(T summary, List<TransactionWrapper> transactions);

	protected abstract void selectionCallback(Node details);
}
