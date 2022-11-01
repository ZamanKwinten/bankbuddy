package bank.buddy.table;

import java.util.Objects;

import javafx.beans.InvalidationListener;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.text.Font;

/**
 * A column that either fits the content or fills the remainder of the page
 * 
 * @author Kwinten
 *
 * @param <T>
 */
public class FillRemainingColumn<T, V> extends FitContentColumn<T, V> implements ChangeListener<Number> {
	private final TableView<T> table;

	private final SimpleDoubleProperty actualWidth = new SimpleDoubleProperty();

	public FillRemainingColumn(TableView<T> table, ObjectProperty<Font> font, ColumnDefinition<T, V> columnDefinition) {
		super(font, columnDefinition);

		this.table = table;
		table.widthProperty().addListener(this);

		table.getColumns().addListener((InvalidationListener) c -> {
			for (TableColumn<T, ?> column : table.getColumns()) {
				if (!Objects.equals(column, this)) {
					column.widthProperty().addListener(this);
				}
			}
		});

		maxContentSize.addListener(this);

		minWidthProperty().bind(actualWidth);
		maxWidthProperty().bind(actualWidth);
		prefWidthProperty().bind(actualWidth);
	}

	@Override
	public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
		double tableWidth = table.widthProperty().get();
		double otherColumnWidth = 0;
		for (TableColumn<T, ?> column : table.getColumns()) {
			if (!Objects.equals(column, this)) {
				otherColumnWidth += column.widthProperty().doubleValue();
			}
		}

		double remaining = tableWidth - otherColumnWidth - 15;
		actualWidth.set(Math.max(remaining, maxContentSize.doubleValue()));
	}

}
