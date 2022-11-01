package bank.buddy.charts;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.atomic.AtomicBoolean;

import bank.buddy.bank.transaction.Category;
import bank.buddy.bank.transaction.TransactionWrapper;
import bank.buddy.ui.ValutaLabel;
import bank.buddy.util.ValutaUtil;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.geometry.Pos;
import javafx.scene.chart.PieChart;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;

public abstract class ChartPane extends BorderPane {

	protected final SimpleObjectProperty<String> titleProperty = new SimpleObjectProperty<>();

	private final PieChart chart;
	private final TableView<TableRow> table;

	public ChartPane(List<TransactionWrapper> transactions) {
		var map = aggregateData(transactions);
		var total = map.values().stream().reduce((a, b) -> a + b).orElse(1.0);

		chart = createChart(map, total);
		table = createTable(map, total);
		setAlignment(table, Pos.CENTER);
		setCenter(chart);

		var header = new HBox();

		var title = new Label();
		title.textProperty().bind(titleProperty);
		title.setFont(new Font(25));

		var spacer = new Region();
		HBox.setHgrow(spacer, Priority.ALWAYS);

		AtomicBoolean pieChartShowing = new AtomicBoolean(true);

		var button = new Button("Toon Tabel");
		button.setOnAction((event) -> {
			if (pieChartShowing.get()) {
				setCenter(table);

				button.setText("Toon Taart Diagram");
				pieChartShowing.set(false);
			} else {
				setCenter(chart);

				button.setText("Toon Tabel");
				pieChartShowing.set(true);
			}
		});

		header.getChildren().setAll(title, spacer, button);
		header.setAlignment(Pos.CENTER);

		setTop(header);

	}

	private final PieChart createChart(Map<Category, Double> map, double total) {
		var pieChartData = map.entrySet().stream().map(e -> {
			var category = e.getKey();

			var data = new PieChart.Data(category.name(), e.getValue());

			var tooltip = new Tooltip();

			var tooltipGrid = new GridPane();
			tooltipGrid.setHgap(5);

			tooltipGrid.add(new Label("Category:"), 0, 0);
			tooltipGrid.add(new Label(category.name()), 1, 0);

			tooltipGrid.add(new Label("Percentage:"), 0, 1);
			tooltipGrid.add(new Label(percentage(e.getValue(), total)), 1, 1);

			tooltipGrid.add(new Label("Waarde:"), 0, 2);
			tooltipGrid.add(new ValutaLabel(e.getValue()), 1, 2);

			tooltip.setGraphic(tooltipGrid);

			data.nodeProperty().addListener((obs, ov, nv) -> {
				nv.setStyle("-fx-pie-color: " + rgbColor(category.background()));

				Tooltip.install(nv, tooltip);
			});

			return data;
		}).toList();

		PieChart chart = new PieChart(FXCollections.observableList(pieChartData));
		chart.setLegendVisible(false);

		return chart;
	}

	private record TableRow(Category category, double value) {
		private TableRow(Entry<Category, Double> e) {
			this(e.getKey(), e.getValue());
		}
	}

	@SuppressWarnings("unchecked")
	private final TableView<TableRow> createTable(Map<Category, Double> map, double total) {
		var data = map.entrySet().stream().map(TableRow::new).sorted((a, b) -> {
			return (int) (b.value() - a.value());
		}).toList();

		var table = new TableView<TableRow>(FXCollections.observableList(data));

		var column1 = new TableColumn<TableRow, String>("Categorie");

		column1.setCellValueFactory((param) -> {
			return new SimpleObjectProperty<>(param.getValue().category().name());
		});

		var column2 = new TableColumn<TableRow, String>("Waarde");
		column2.setCellValueFactory((param) -> {
			return new SimpleObjectProperty<>(ValutaUtil.toValuta(param.getValue().value()));
		});

		var column3 = new TableColumn<TableRow, String>("Percentage");
		column3.setCellValueFactory(param -> new SimpleObjectProperty<>(percentage(param.getValue().value(), total)));

		table.getColumns().setAll(column1, column2, column3);

		return table;
	}

	protected abstract Map<Category, Double> aggregateData(List<TransactionWrapper> transactions);

	private String rgbColor(Color color) {
		return String.format("rgb(%d,%d,%d)", Math.round(color.getRed() * 255), Math.round(color.getGreen() * 255),
				Math.round(color.getBlue() * 255));
	}

	private String percentage(double value, double total) {
		var temp = Math.round(value / total * 10000.0);
		return temp / 100.0 + "%";
	}
}
