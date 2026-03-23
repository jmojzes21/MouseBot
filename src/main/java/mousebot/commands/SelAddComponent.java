package mousebot.commands;

import java.util.ArrayList;
import mousebot.components.Component;
import mousebot.components.ControlRect;
import mousebot.components.ControlRectController;
import mousebot.components.SelectionModel;
import mousebot.main.Controller;
import mousebot.main.RectFinder;
import mousebot.parameters.Param;
import mousebot.parameters.ParamsManager;

public class SelAddComponent implements Command {

  private Component c;
  private SelectionModel sm;
  private ParamsManager pm;
  private ControlRect controlRect;

  public SelAddComponent(Component c) {
    this.c = c;
    this.sm = Controller.getProject().selectionModel;
    this.pm = Controller.getParamsManager();
  }

  public void execute() {

    sm.selected.add(c);
    c.selected = true;

    boolean cr = sm.crMode;
    if (cr) {
      if (controlRect == null) {
        RectFinder.getInstance().reset();
      }
    } else {
      Controller.getProject().cpManager.removeControlPoints();
    }
    ArrayList<Param> params = new ArrayList<>();
    for (int i = 0; i < sm.selected.size(); i++) {
      pm.getParamsByComponent(params, sm.selected.get(i));
      if (cr) {
        if (controlRect == null) {
          sm.selected.get(i).processRect();
        }
      } else {
        sm.selected.get(i).createControlPoints();
      }
    }
    if (cr) {
      if (controlRect == null) {
        controlRect = new ControlRect(ControlRectController.TSELECT);
      }
      Controller.getProject().cpManager.cr = controlRect;
    }
    pm.createParams(params);

  }

  public void undo() {

    sm.selected.remove(c);
    c.selected = false;

    boolean cr = sm.crMode;
    if (cr) {
      RectFinder.getInstance().reset();
    } else {
      Controller.getProject().cpManager.removeControlPoints();
    }
    ArrayList<Param> params = new ArrayList<>();
    for (int i = 0; i < sm.selected.size(); i++) {
      pm.getParamsByComponent(params, sm.selected.get(i));
      if (cr) {
        sm.selected.get(i).processRect();
      } else {
        sm.selected.get(i).createControlPoints();
      }
    }
    if (cr) {
      if (RectFinder.getInstance().getStart() == null) {
        Controller.getProject().cpManager.cr = null;
      } else {
        Controller.getProject().cpManager.cr = new ControlRect(ControlRectController.TSELECT);
      }
    }
    pm.createParams(params);

  }

  public void redo() {
    execute();
  }

}