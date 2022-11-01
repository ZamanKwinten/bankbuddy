package bank.buddy.ui;

import java.util.Objects;

import bank.buddy.bank.transaction.Category;
import bank.buddy.ui.CategoryConfirmationPopup.CategoryConfirmationParam;
import bank.buddy.ui.CategoryConfirmationPopup.CategoryConfirmationReturn;
import bank.buddy.ui.CategoryPopup.CategoryPopupParam;
import bank.buddy.ui.CategorySelectionPopup.CategorySelectionPopupParam;
import bank.buddy.ui.CategorySelectionPopup.CategorySelectionReturn;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.paint.Color;

public class CategorySelectionPopup extends PopupWithReturn<CategorySelectionReturn, CategorySelectionPopupParam> {

	public record CategorySelectionReturn(SimpleObjectProperty<Category> category, CategoryConfirmationReturn action) {

	}

	public record CategorySelectionPopupParam(boolean isExpense, ObservableValue<String> currentCategoryName,
			ObservableList<SimpleObjectProperty<Category>> list) {

	}

	public CategorySelectionPopup(CategorySelectionPopupParam param) {
		super(param);
	}

	private CategorySelectionReturn result;

	@Override
	protected String getPopupTitle() {
		return "Categorieën";
	}

	@Override
	protected Node getContent() {

		var list = new ListView<>(param.list());
		list.setMaxWidth(Double.MAX_VALUE);
		list.setOrientation(Orientation.VERTICAL);

		var instance = this;
		list.setCellFactory((param) -> {
			var cell = new ListCell<SimpleObjectProperty<Category>>() {
				@Override
				protected void updateItem(SimpleObjectProperty<Category> input, boolean empty) {
					super.updateItem(input, empty);

					if (empty || input == null) {
						setText(null);
						setGraphic(null);
						setTextFill(Color.BLACK);
						setBackground(
								new Background(new BackgroundFill(Color.TRANSPARENT, CornerRadii.EMPTY, Insets.EMPTY)));
					} else {
						var item = input.get();

						var root = new HBox();
						root.setAlignment(Pos.CENTER_LEFT);

						var label = new Label(item.name());
						label.setTextFill(item.text());

						var spacer = new Region();
						HBox.setHgrow(spacer, Priority.ALWAYS);
						var editButton = new Button("Wijzigen");

						editButton.setOnAction(event -> {
							new CategoryPopup(new CategoryPopupParam(item.name(), item.text(), item.background()))
									.open().ifPresent(c -> {
										input.set(c);

										label.setText(c.name());
										label.setTextFill(c.text());
										setBackground(new Background(
												new BackgroundFill(c.background(), CornerRadii.EMPTY, Insets.EMPTY)));
									});
						});

						setBackground(
								new Background(new BackgroundFill(item.background(), CornerRadii.EMPTY, Insets.EMPTY)));

						root.getChildren().addAll(label, spacer, editButton);
						setGraphic(root);

						setOnMouseClicked(event -> {
							if (!Objects.equals(input.get().name(), instance.param.currentCategoryName().getValue())) {
								new CategoryConfirmationPopup(new CategoryConfirmationParam(instance.param.isExpense()))
										.open().ifPresent(e -> {
											result = new CategorySelectionReturn(input, e);
											instance.close();
										});
							} else {
								instance.isCanceled = true;
								instance.close();
							}
						});
					}
				}
			};

			return cell;
		});

		return list;
	}

	@Override
	protected Footer getFooter() {
		var addButton = new Button("Categorie toevoegen");

		addButton.setOnAction(event -> {
			new CategoryPopup(new CategoryPopupParam()).open().ifPresent(e -> {
				param.list().add(new SimpleObjectProperty<>(e));
			});
		});

		return new Footer("", addButton);
	}

	@Override
	protected CategorySelectionReturn getReturnValue() {
		return result;
	}

}
