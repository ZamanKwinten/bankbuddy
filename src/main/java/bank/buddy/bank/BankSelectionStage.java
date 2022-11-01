package bank.buddy.bank;

import java.util.Arrays;
import java.util.stream.Collectors;

import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.layout.HBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class BankSelectionStage extends Stage {

	public BankSelectionStage() {
		super();

		setTitle("Selecteer uw bank");

		var content = new HBox();

		content.getChildren().addAll(Arrays.stream(BankEnum.values()).sorted().map(e -> {
			var label = e.getLabel();

			label.setOnMouseClicked(even -> {
				e.getLoginStage().show();
				this.close();
			});

			return label;
		}).collect(Collectors.toList()));

		content.setPadding(new Insets(20));

		Scene scene = new Scene(content);

		setScene(scene);
		initModality(Modality.APPLICATION_MODAL);

		setResizable(false);
	}
}
