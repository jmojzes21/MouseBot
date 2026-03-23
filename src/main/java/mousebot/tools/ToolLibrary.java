package mousebot.tools;

import javafx.scene.control.Control;
import javafx.scene.control.Label;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.VBox;
import mousebot.main.LanguageManager;

/*
 * Za grupiranje alata u grupe.
 */
public class ToolLibrary extends VBox {

  private FlowPane fp;

  public ToolLibrary(String name) {

    Label label = new Label(LanguageManager.getString(name));
    fp = new FlowPane();
    fp.setHgap(4);
    fp.setVgap(5);
    fp.setPrefWidth(Control.USE_PREF_SIZE);

    getChildren().addAll(label, fp);

  }

  public ToolLibrary addTool(Tool tool) {
    fp.getChildren().add(tool.toolHolder);
    return this;
  }

}