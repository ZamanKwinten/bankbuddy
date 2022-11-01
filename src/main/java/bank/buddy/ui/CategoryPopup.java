package bank.buddy.ui;

import bank.buddy.bank.transaction.Category;
import bank.buddy.ui.CategoryPopup.CategoryPopupParam;
import javafx.scene.Node;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;

public class CategoryPopup extends PopupWithReturn<Category, CategoryPopupParam> {

	public record CategoryPopupParam(String name, Color textFill, Color backgroundFill) {

		public CategoryPopupParam() {
			this("", Color.BLACK, Color.WHITE);
		}
	}

	public CategoryPopup(CategoryPopupParam param) {
		super(param);
	}

	private TextField nameField;
	private ColorPicker textCP;
	private ColorPicker backgroundCP;

	@Override
	protected String getPopupTitle() {
		return "Nieuwe Categorie";
	}

	@Override
	protected Node getContent() {
		var root = new GridPane();

		root.add(new Label("Name:"), 0, 0);
		nameField = new TextField(param.name());
		root.add(nameField, 1, 0);

		root.add(new Label("Tekst kleur:"), 0, 1);
		textCP = new ColorPicker(param.textFill());
		root.add(textCP, 1, 1);

		root.add(new Label("Achtergrond kleur:"), 0, 2);
		backgroundCP = new ColorPicker(param.backgroundFill());
		root.add(backgroundCP, 1, 2);

		return root;
	}

	@Override
	protected Category getReturnValue() {
		return new Category(nameField.getText(), textCP.getValue(), backgroundCP.getValue());
	}

}
