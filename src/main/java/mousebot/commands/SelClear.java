package mousebot.commands;

import java.util.ArrayList;
import mousebot.components.Component;
import mousebot.components.ControlRect;
import mousebot.components.SelectionModel;
import mousebot.main.Controller;
import mousebot.parameters.Param;
import mousebot.parameters.ParamsManager;

public class SelClear implements Command {

  private ArrayList<Component> backup;
  private SelectionModel sm;
  private ParamsManager pm;
  private ControlRect controlRect;

  public SelClear() {
    this.sm = Controller.getProject().selectionModel;
    this.pm = Controller.getParamsManager();
  }

  public void execute() {

    if (backup == null) {
      backup = new ArrayList<>();
      for (int i = 0; i < sm.selected.size(); i++) {
        backup.add(sm.selected.get(i));
      }
      controlRect = Controller.getProject().cpManager.cr;
    }

    for (int i = 0; i < sm.selected.size(); i++) {
      sm.selected.get(i).selected = false;
    }
    sm.selected.clear();
    Controller.getProject().cpManager.removeControlPoints();
    Controller.getProject().cpManager.cr = null;

    pm.clearParams();

  }

  public void undo() {
    boolean cr = sm.crMode;
    for (int i = 0; i < backup.size(); i++) {
      Component c = backup.get(i);
      Controller.getProject().selectionModel.selected.add(c);
      c.selected = true;
      if (!cr) {
        c.createControlPoints(false);
      }
    }
    if (cr) {
      Controller.getProject().cpManager.cr = controlRect;
    }

    ArrayList<Param> params = new ArrayList<>();
    for (int i = 0; i < sm.selected.size(); i++) {
      pm.getParamsByComponent(params, sm.selected.get(i));
    }
    pm.createParams(params);

    Command c = Controller.getInvoker().getNextUndo();
    if (c instanceof SelRemove) {
      Controller.getInvoker().undo();
    }

  }

  public void redo() {
    execute();
  }

}