package mousebot.tools;

import java.awt.GraphicsEnvironment;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Callback;
import mousebot.commands.TextAdd;
import mousebot.commands.TextCancel;
import mousebot.components.Text;
import mousebot.main.Controller;
import mousebot.main.LanguageManager;
import mousebot.main.MouseBot;
import mousebot.parameters.Param;

/**
 * Za dodavanje teksta.
 *
 * @author josip
 */
public class TText extends DrawingTool<Text> {

  private static final Param[] gparams = {Param.ACCURACY_TEXT};

  // za prozor
  private Stage addTextStage;
  private TextArea text;
  private ComboBox<String> fonts;

  public TText() {
    super("tl.misc.text", "text");

    // inicijaliziranje prozora za dodavanje teksta
    addTextStage = new Stage();
    addTextStage.setTitle(LanguageManager.getString("text.add"));
    addTextStage.getIcons().add(MouseBot.APP_ICON);
    addTextStage.initModality(Modality.APPLICATION_MODAL);

    VBox vbox = new VBox();
    vbox.setSpacing(12);
    vbox.getStylesheets().add("/Goliath-Envy.css");
    vbox.setStyle("-fx-font-size: 16;");

    HBox hbox1 = new HBox();
    hbox1.setSpacing(12);
    hbox1.setAlignment(Pos.CENTER_LEFT);
    Label label = new Label(LanguageManager.getString("text.fontfamily"));
    fonts = new ComboBox<>();
    fonts.setCellFactory(new Callback<ListView<String>, ListCell<String>>() {

      public ListCell<String> call(ListView<String> p) {

        final ListCell<String> cell = new ListCell<String>() {

          boolean inited = false;

          protected void updateItem(String val, boolean bln) {
            super.updateItem(val, bln);

            if (!inited && val != null) {
              setStyle("-fx-font-family:\"" + val + "\";");
              inited = true;
            }

            if (val != null) {
              setText(val);
            } else {
              setText(null);
            }
          }

        };

        return cell;
      }
    });

    GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
    fonts.getItems().addAll(ge.getAvailableFontFamilyNames());
    fonts.getSelectionModel().selectedItemProperty().addListener((o, oldVal, newVal) -> {
      text.setStyle("-fx-font-family:\"" + newVal + "\";");
    });

    hbox1.getChildren().addAll(label, fonts);

    text = new TextArea();
    VBox.setVgrow(text, Priority.ALWAYS);

    fonts.getSelectionModel().select("Consolas");

    HBox hbox2 = new HBox();
    hbox2.setSpacing(12);
    hbox2.setAlignment(Pos.CENTER);
    Button bAdd = new Button(LanguageManager.getString("text.btn.add"));
    bAdd.setOnAction(e -> {
      addTextStage.close();
    });
    Button bClose = new Button(LanguageManager.getString("text.btn.close"));
    bClose.setOnAction(e -> {
      text.setText("");
      addTextStage.close();
    });
    hbox2.getChildren().addAll(bAdd, bClose);

    vbox.getChildren().addAll(hbox1, text, hbox2);

    vbox.setPadding(new Insets(12, 12, 24, 12));

    Scene scene = new Scene(vbox);
    addTextStage.setScene(scene);

  }

  public void mousePressed(double x, double y, MouseButton button) {

    if (button == MouseButton.PRIMARY) {

      if (c == null) {

        this.text.setText("");
        Controller.getInstance().mbPressed--;
        addTextStage.showAndWait();
        String text = this.text.getText().trim();
        if (text.length() > 0) {
          c = new Text(x, y, text, fonts.getSelectionModel().getSelectedItem());
          Controller.getInvoker().execute(new TextAdd(c));
        }

      }

    } else if (button == MouseButton.SECONDARY) {
      cancel();
    }

  }

  public void mouseReleased(double x, double y, MouseButton button) {}

  public void mouseMoved(double x, double y) {}

  public void mouseDragged(double x, double y, MouseButton button) {}

  public void cancel() {
    if (c != null) {
      Controller.getInvoker().execute(new TextCancel(c));
    }
  }

  public Param[] createParams() {
    return gparams;
  }

  public void paramChanged(Param p, Object v) {
    if (c == null) {return;}
    c.setParamValue(p, v);
  }

}