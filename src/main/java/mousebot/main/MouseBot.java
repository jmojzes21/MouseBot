package mousebot.main;


import com.sun.jna.platform.win32.User32;
import java.io.File;
import java.io.InputStream;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import mousebot.executing.Executor;

/**
 * Glavna klasa.
 *
 * @author josip
 */
public class MouseBot extends Application {

  public static final Image APP_ICON = new Image(getResource("/icons/app.png"));
  public static final String APP_TITLE = "MouseBot";

  private static final AtomicBoolean executorThreadActive = new AtomicBoolean(true);
  private static Thread executorThread;

  public static void main(String[] args) throws Exception {
    initExecutorThread();
    launch(args);
  }

  public void start(Stage stage) throws Exception {

    // učitavanje datoteke app.fxml za definiranje korisničkog sučelja
    FXMLLoader loader = new FXMLLoader(MouseBot.class.getResource("/app.fxml"),
        LanguageManager.resourceBundle);
    loader.setController(Controller.getInstance()); // postavljanje kontrolera za gui

    Controller.getInstance().stage = stage;

    Scene scene = new Scene(loader.load());

    stage.setScene(scene);
    stage.setTitle(APP_TITLE);

    stage.setOnCloseRequest(e -> {
      // prije zatvaranje programa, potrebno je uputiti upit korisniku
      if (!Controller.getInstance().onAppExit()) {
        e.consume();
      }
    });

    stage.setOnHiding(e -> {
      executorThreadActive.set(false);

      try {
        executorThread.interrupt();
        executorThread.join();
      } catch (InterruptedException _) {}
    });

    stage.getIcons().add(APP_ICON);
    stage.show();

    // provjera je li program pokrenut s početnim parametrom
    // koji definira automatsko otvaranje određenog projekta

    List<String> params = getParameters().getRaw();
    if (params.size() == 1) {
      File file = new File(params.get(0));
      if (file.exists() && file.isFile()) {
        Controller.getInstance().onOpenProject(file);
        Controller.getProject().render();
      }
    }

  }

  /**
   * Potrebno je konstantno provjeravati jesu li pritisnute neke glavne tipke koje služe za pokretanje izvršavanja i
   * zaustavljanja crtanja bez obzira na prozor koji je trenutno u fokusu.
   */
  private static void initExecutorThread() {

    executorThread = new Thread(() -> {

      var controller = Controller.getInstance();
      var user32 = User32.INSTANCE;
      final int VK_F2 = 0x71;
      final int VK_ESCAPE = 0x1B;

      while (executorThreadActive.get()) {

        if (isKeyPressed(user32, VK_F2) && controller.notMouseButtonPressed()) {
          Executor.call();
          try {
            Thread.sleep(2000);
          } catch (InterruptedException _) {}
        } else if (isKeyPressed(user32, VK_ESCAPE) && controller.notMouseButtonPressed()) {
          Executor.stop();
        }

        try {
          Thread.sleep(100);
        } catch (InterruptedException _) {}
      }

    });

    executorThread.start();
  }

  private static boolean isKeyPressed(User32 user32, int code) {
    return user32.GetAsyncKeyState(code) != 0;
  }

  public static InputStream getResource(String url) {
    return MouseBot.class.getResourceAsStream(url);
  }

}