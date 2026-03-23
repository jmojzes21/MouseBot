package mousebot.executing;

import java.util.ArrayList;
import javafx.application.Platform;
import mousebot.components.Component;
import mousebot.layers.Layer;
import mousebot.layers.LayersManager;
import mousebot.main.Controller;

/**
 * Upravitelj za izvršavanje.
 *
 * @author josip
 */
public class Executor {

  private static ExecuteFrame ef = null; // objekt pregleda
  private static boolean executing = false; // izvršava li se program
  private static Thread thread; // thread za izvršavanje

  /**
   * Poziva se pri pritisku tipke F2. Ako prozor pregleda nije prikazan, prikazat će se. Ako je prozor pregleda
   * prikazan, počet će izvršavanje.
   */
  public static void call() {
    if (ef == null) {
      showExecuter();
    } else {
      ArrayList<Component> components = ef.getComponents();
      stop();
      try {
        Thread.sleep(50);
      } catch (Exception e) {}
      execute(components);
    }
  }

  // prikaže prozor
  private static void showExecuter() {

    boolean components = false;

    LayersManager lm = Controller.getLayersManager();
    for (int l = 0; l < lm.layers.size(); l++) {
      Layer layer = lm.layers.get(l);
      if (!layer.isEnabled()) {continue;}
      if (!layer.components.isEmpty()) {
        components = true;
        break;
      }
    }

    if (!components) {return;}

    Platform.runLater(() -> {
      ef = new ExecuteFrame();
      ef.initComponents();
      ef.show();
    });
  }

  // započinje izvršavanje
  private static void execute(ArrayList<Component> components) {
    executing = true;
    thread = new Thread(() -> {
      try {
        for (int i = 0; i < components.size(); i++) {
          check();
          components.get(i).execute();
        }
      } catch (Exception e) {}
      executing = false;
      MouseBot.release();
    });
    thread.start();

  }

  /**
   * Poziva se pri pritisku tipke Escape. Ako je prozor pregleda prikazan, zatvorit će se. Ako se program izvršava,
   * prekinut će se izvršavanje.
   */
  public static void stop() {

    if (isExecuting()) {
      stopExecuting();
      try {
        thread.join();
      } catch (Exception e) {}
      return;
    }

    if (ef != null) {
      Platform.runLater(new Runnable() {
        public void run() {
          ef.close();
          ef = null;
          Controller.getInstance().stage.toBack();
        }
      });
    }
  }

  /**
   * @return traje li izvršavanje
   */
  public static synchronized boolean isExecuting() {
    return executing;
  }

  /**
   * Zaustavlja izvršavanje.
   */
  public static synchronized void stopExecuting() {
    executing = false;
  }

  /**
   * Za učinkovitije zaustavljanje izvršavanja komponente.
   *
   * @throws Exception ako je potrebno zaustaviti izvršavanje
   */
  public static void check() throws Exception {
    if (!isExecuting()) {
      throw new Exception();
    }
  }

  /**
   * @return true ako je prozor pregleda prikazan, inače false
   */
  public static boolean isVisible() {
    return ef != null;
  }

  public static ExecuteFrame getExecuteFrame() {
    return ef;
  }

}