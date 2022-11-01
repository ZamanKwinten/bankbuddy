package bank.buddy.bank;

import bank.buddy.bank.transaction.MonthSummary;
import bank.buddy.ui.ValutaLabel;
import javafx.geometry.VPos;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Region;
import javafx.scene.text.Font;

public class MonthSummaryCard extends GridPane {

	public MonthSummaryCard(MonthSummary summary) {
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
