package mousebot.commands;

import java.util.ArrayList;
import mousebot.components.Component;
import mousebot.components.SelectionModel;
import mousebot.main.Controller;
import mousebot.main.RectFinder;
import mousebot.parameters.Param;

public class SelSetGP implements Command {

  public ArrayList<Component> components = new ArrayList<>();

  private ArrayList<Object> prevValues = new ArrayList<>();
  private Param p;
  private Object newVal;
  private Object oldVal;
  private SelectionModel sm = Controller.getProject().selectionModel;

  public SelSetGP(Param p, Object oldVal, Object newVal) {
    this.p = p;
    this.oldVal = oldVal;
    this.newVal = newVal;
  }

  public void execute() {
    for (int i = 0; i < components.size(); i++) {
      prevValues.add(components.get(i).getParamValue(p));
      components.get(i).setParamValue(p, newVal);
    }
    if (!sm.crMode) {
      Controller.getProject().cpManager.removeControlPoints();
      for (int i = 0; i < sm.selected.size(); i++) {
        sm.selected.get(i).createControlPoints(false);
      }
    } else {
      RectFinder.getInstance().reset();
      for (int i = 0; i < sm.selected.size(); i++) {
        sm.selected.get(i).processRect();
      }
      Controller.getProject().cpManager.cr.resetPoints();
    }
  }

  public void undo() {
    p.val.setValue(oldVal);
    for (int i = 0; i < components.size(); i++) {
      components.get(i).setParamValue(p, prevValues.get(i));
    }
    if (!sm.crMode) {
      Controller.getProject().cpManager.removeControlPoints();
      for (int i = 0; i < sm.selected.size(); i++) {
        sm.selected.get(i).createControlPoints(false);
      }
    } else {
      RectFinder.getInstance().reset();
      for (int i = 0; i < sm.selected.size(); i++) {
        sm.selected.get(i).processRect();
      }
      Controller.getProject().cpManager.cr.resetPoints();
    }
  }

  public void redo() {
    p.val.setValue(newVal);
    execute();
  }

}