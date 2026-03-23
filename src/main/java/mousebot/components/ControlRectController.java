package mousebot.components;

import mousebot.executing.Executor;
import mousebot.main.Controller;

/**
 * Određuje koje su komponente zahvaćene transformacijom (skaliranje, premještanje, rotiranje) kontorlnog pravokutnika.
 *
 * @author josip
 */
public interface ControlRectController {

  // u slučaju da se kontrolni pravokutnik koristi za vrijeme odabira
  public static ControlRectController TSELECT = new ControlRectController() {
    public void transform(Point oldDim, Point dim, Point oldRect, Point rect, boolean finish) {
      SelectionModel sm = Controller.getProject().selectionModel;
      for (int i = 0; i < sm.selected.size(); i++) {
        Component c = sm.selected.get(i);
        c.transform(oldDim, dim, oldRect, rect, finish);
      }
    }

    public void rotated(Point center, double angle, boolean finish) {
      SelectionModel sm = Controller.getProject().selectionModel;
      for (int i = 0; i < sm.selected.size(); i++) {
        Component c = sm.selected.get(i);
        c.rotate(center, angle, finish);
        c.processRect();
      }
    }
  };

  // u slučaju da se kontrolni pravokutnik koristi za postavljanje komponente kojom upravlja trenutni alat
  public static ControlRectController TCOMPONENT = new ControlRectController() {
    public void transform(Point oldDim, Point dim, Point oldRect, Point rect, boolean finish) {
      Component c = Controller.getProject().tool.get().getComponent();
      c.transform(oldDim, dim, oldRect, rect, finish);
    }

    public void rotated(Point center, double angle, boolean finish) {
      Component c = Controller.getProject().tool.get().getComponent();
      c.rotate(center, angle, finish);
      c.processRect();
    }
  };

  // u slučaju da se kontrolni pravokutnik koristi za vrijeme postavljanja komponenti za izvršavanje
  public static ControlRectController EXECUTINGFRAME = new ControlRectController() {
    public void transform(Point oldDim, Point dim, Point oldRect, Point rect, boolean finish) {
      Executor.getExecuteFrame().transform(oldDim, dim, oldRect, rect, finish);
    }

    public void rotated(Point center, double angle, boolean finish) {
      Executor.getExecuteFrame().rotated(center, angle, finish);
    }
  };

  public void transform(Point oldDim, Point dim, Point oldRect, Point rect, boolean finish);

  public void rotated(Point center, double angle, boolean finish);

}