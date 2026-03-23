package mousebot.layers;

import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import mousebot.main.LanguageManager;
import mousebot.main.MouseBot;

/**
 * Prozor za postavke sloja.
 *
 * @author josip
 *
 */
public class LayerOptions extends Stage {

  public LayerOptionsController controller;

  public LayerOptions() {

    initStyle(StageStyle.DECORATED);

    FXMLLoader loader = new FXMLLoader(MouseBot.class.getResource("/layeroptions.fxml"),
        LanguageManager.resourceBundle);
    controller = new LayerOptionsController(this);
    loader.setController(controller);

    try {
      Scene scene = new Scene(loader.load());
      setScene(scene);
    } catch (Exception e) {}

    setTitle(LanguageManager.getString("layer.options"));

    initModality(Modality.APPLICATION_MODAL);
    setResizable(false);

    getIcons().add(MouseBot.APP_ICON);

  }

}