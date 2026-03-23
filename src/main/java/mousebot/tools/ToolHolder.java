package mousebot.tools;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.ToggleButton;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.text.TextAlignment;
import mousebot.main.Controller;
import mousebot.main.LanguageManager;
import mousebot.main.MouseBot;

/*
 * Grafička komponenta za prikaz alata.
 */
public class ToolHolder extends ToggleButton {

  public ToolHolder(String name, String icon) {
    setText(LanguageManager.getString(name)); // prikaz imena alata
    setCursor(Cursor.HAND);

    // dodavanje ikone
    ImageView img = new ImageView(new Image(MouseBot.getResource("/icons/" + icon + ".png")));
    setGraphic(img);

    // ostalo
    setContentDisplay(ContentDisplay.TOP);
    setWrapText(true);
    setTextAlignment(TextAlignment.CENTER);
    setGraphicTextGap(0);
    setAlignment(Pos.CENTER);
    setFocusTraversable(false);
    setToggleGroup(Controller.getInstance().toolsManager.toggleGroup);
    setLineSpacing(-6);

    // podešavanje širine
    widthProperty().addListener(new ChangeListener<Number>() {
      public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
        ToolHolder.this.widthProperty().removeListener(this);
        ToolHolder.this.setPrefWidth(newValue.intValue() > 72 ? 148 : 72);
      }
    });

  }

}