package bank.buddy.table;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

import bank.buddy.bank.transaction.Category;
import javafx.beans.InvalidationListener;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.input.MouseEvent;
import javafx.scene.text.Font;

public class AutoFillingTable<T, V> extends TableView<T> {
	private static final ObjectProperty<Font> font = new SimpleObjectProperty<>(new Font(20));

	private final Map<ColumnDefinition<T, V>, TableColumn<T, V>> columnMap = new HashMap<>();
	private final ObservableList<ColumnDefinition<T, V>> definitions = FXCollections.observableArrayList();

	private final SimpleObjectProperty<V> selectedItem = new SimpleObjectProperty<>(null);

	public AutoFillingTable() {
		definitions.addListener((InvalidationListener) e -> {
			getColumns().clear();
			columnMap.clear();
			if (definitions.size() > 0) {
				for (int i = 0; i < definitions.size() - 1; i++) {
					var definition = definitions.get(i);

					handleColumnDefinition(definition, new FitContentColumn<>(font, definition));
				}

				var definition = definitions.get(definitions.size() - 1);
				handleColumnDefinition(definition, new FillRemainingColumn<>(this, font, definition));
			}
		});

	}

	private void handleColumnDefinition(ColumnDefinition<T, V> definition, TableColumn<T, V> column) {
		column.setCellFactory((param) -> {
			var cell = new TableCell<T, V>() {
				@Override
				public void updateItem(V item, boolean empty) {
					super.updateItem(item, empty);
					definition.layoutCell(item, this);
				}
			};

			cell.addEventFilter(MouseEvent.MOUSE_PRESSED, event -> {
				var row = cell.getIndex();

				var selectionModel = getSelectionModel();

				if (selectionModel.isSelected(row, column)) {
					selectionModel.clearSelection(row, column);
					selectedItem.set(null);
				} else {
					selectionModel.select(row, column);
					selectedItem.set(definition.cellSelection(selectionModel.getSelectedItem()));
				}

				event.consume();
			});

			cell.fontProperty().bind(font);
			return cell;
		});

		column.setSortable(false);
		getColumns().add(column);
		columnMap.put(definition, column);
	}

	public void addColumn(ColumnDefinition<T, V> definition) {
		definitions.add(definition);
	}

	public void onSelectionChange(Consumer<V> onSelect, Runnable onClear) {
		selectedItem.addListener((obs, ov, nv) -> {
			if (nv != null) {
				onSelect.accept(nv);
			} else {
				onClear.run();
			}
		});
	}
}