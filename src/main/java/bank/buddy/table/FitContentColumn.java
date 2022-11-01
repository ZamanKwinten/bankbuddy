package bank.buddy.table;

import bank.buddy.ui.FXUtils;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.scene.control.TableColumn;
import javafx.scene.text.Font;

/**
 * A Column that automatically resizes itself to fit the content
 * 
 * @author Kwinten
 *
 * @param <T>
 */
public class FitContentColumn<T, V> extends TableColumn<T, V> {
	protected final SimpleDoubleProperty maxContentSize = new SimpleDoubleProperty();

	public FitContentColumn(ObjectProperty<Font> font, ColumnDefinition<T, V> columnDefinition) {
		textProperty().addListener((obs, ov, nv) -> {
			// 15 px of padding
			double width = FXUtils.calculateTextControlWidth(font.getValue(), nv, 15);
			if (width > maxContentSize.doubleValue()) {
				maxContentSize.setValue(width);
			}
		});

		setText(columnDefinition.header());

		
		setCellValueFactory((cell) -> {
			var content = columnDefinition.contentProducer().apply(cell.getValue());
			// 15 px of padding
			double width = FXUtils.calculateTextControlWidth(font.getValue(), content.map(columnDefinition::toReadable).orElse(""), 15);
			if (width > maxContentSize.doubleValue()) {
				maxContentSize.setValue(width);
			}

			return new SimpleObjectProperty<>(content.orElse(null));
		});

		font.addListener((obs, o, n) -> {
			maxContentSize.set(maxContentSize.get() * n.getSize() / o.getSize());
		});

		minWidthProperty().bind(maxContentSize);
		maxWidthProperty().bind(maxContentSize);
		prefWidthProperty().bind(maxContentSize);
	}
}
