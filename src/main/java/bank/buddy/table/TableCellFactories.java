package bank.buddy.table;

import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.text.Font;
import javafx.util.Callback;

public class TableCellFactories {

	public static <T> Callback<TableColumn<T, String>, TableCell<T, String>> cell(Font font) {
		return (param) -> {
			return defaultCell(font);
		};
	}

	private static <T> TableCell<T, String> defaultCell(Font font) {
		TableCell<T, String> cell = new TableCell<>() {
			@Override
			public void updateItem(String item, boolean empty) {
				super.updateItem(item, empty);
				if (empty) {
					setText(null);
				} else {
					setText(item);
				}
			}
		};

		cell.fontProperty().bind(new SimpleObjectProperty<>(font));

		return cell;
	}

}
