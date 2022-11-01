package bank.buddy.table;

import java.util.Optional;
import java.util.function.Function;

import javafx.geometry.Insets;
import javafx.scene.control.TableCell;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.paint.Color;

public interface ColumnDefinition<T, V> {

	String header();

	Function<T, Optional<V>> contentProducer();

	public default Optional<V> getValue(T val) {
		return contentProducer().apply(val);
	}

	public String toReadable(V val);

	public default void layoutCell(V val, TableCell<T, V> cell) {
		cell.backgroundProperty().unbind();
		cell.textFillProperty().unbind();
		cell.setTextFill(Color.BLACK);
		cell.setBackground(new Background(new BackgroundFill(Color.TRANSPARENT, CornerRadii.EMPTY, Insets.EMPTY)));
		if (val == null) {
			cell.setText(null);
		} else {
			layoutCellImpl(val, cell);
		}
	}

	void layoutCellImpl(V val, TableCell<T, V> cell);

	public V cellSelection(T val);
}
