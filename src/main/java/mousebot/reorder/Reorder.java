package mousebot.reorder;

import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;
import mousebot.main.LanguageManager;
import mousebot.main.MouseBot;

public class Reorder extends Stage {

  public ReorderController rc;

  public Reorder() {

    FXMLLoader loader = new FXMLLoader(MouseBot.class.getResource("/reorder.fxml"), LanguageManager.resourceBundle);
    rc = new ReorderController(this);
    loader.setController(rc);

    try {
      Scene scene = new Scene(loader.load());
      setScene(scene);
    } catch (Exception e) {}

    setTitle(LanguageManager.getString("reorder"));

    initModality(Modality.APPLICATION_MODAL);

    getIcons().add(MouseBot.APP_ICON);

  }

  public void showStage() {
    super.show();
    rc.onShow();
  }

}