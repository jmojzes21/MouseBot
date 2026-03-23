package mousebot.options;

import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import mousebot.main.LanguageManager;
import mousebot.main.MouseBot;

/**
 * Prikaz prozora postavki programa.
 *
 * @author josip
 */
public class Options extends Stage {

  public OptionsController opc;

  public Options() {

    initStyle(StageStyle.DECORATED);

    FXMLLoader loader = new FXMLLoader(MouseBot.class.getResource("/options.fxml"), LanguageManager.resourceBundle);
    opc = new OptionsController(this);
    loader.setController(opc);

    try {
      Scene scene = new Scene(loader.load());
      setScene(scene);
    } catch (Exception e) {}

    setTitle(LanguageManager.getString("options"));

    initModality(Modality.APPLICATION_MODAL);
    setResizable(false);

    getIcons().add(MouseBot.APP_ICON);

  }

}