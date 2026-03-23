package mousebot.commands;

import javafx.scene.Cursor;
import mousebot.components.ControlPointsManager;
import mousebot.components.ControlRect;
import mousebot.components.ControlRectController;
import mousebot.components.SelectionModel;
import mousebot.main.Controller;
import mousebot.main.RectFinder;

public class SelChangeMode implements Command {

  private SelectionModel sm = Controller.getProject().selectionModel;
  private ControlPointsManager cpManager = Controller.getProject().cpManager;

  private ControlRect cr;

  public void execute() {
    sm.crMode = !sm.crMode;
    if (sm.crMode) {
      cpManager.removeControlPoints();
      if (!sm.selected.isEmpty()) {
        if (cr == null) {
          RectFinder.getInstance().reset();
          for (int i = 0; i < sm.selected.size(); i++) {
            sm.selected.get(i).processRect();
          }
          cr = new ControlRect(ControlRectController.TSELECT);
        }
        cpManager.cr = cr;
      }
    } else {
      cr = cpManager.cr;
      cpManager.cr = null;
      for (int i = 0; i < sm.selected.size(); i++) {
        sm.selected.get(i).createControlPoints(false);
      }
    }
    Controller.getInstance().canvas.setCursor(Cursor.DEFAULT);

  }

  public void undo() {
    execute();
  }

  public void redo() {
    execute();
  }

}