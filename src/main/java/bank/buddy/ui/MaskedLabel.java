package bank.buddy.ui;

import javafx.scene.control.Label;

public class MaskedLabel extends Label {

	public MaskedLabel(String pattern, String text) {

		try {
			StringBuilder sb = new StringBuilder();

			int offset = 0;
			for (int i = 0; i < pattern.length(); i++) {

				if (' ' == pattern.charAt(i)) {
					sb.append(' ');
					offset++;
				} else {
					sb.append(text.charAt(i - offset));
				}
			}

			setText(sb.toString());
		} catch (Exception e) {
			System.err.println("WTF: " + text);
			e.printStackTrace();
		}

	}
}
