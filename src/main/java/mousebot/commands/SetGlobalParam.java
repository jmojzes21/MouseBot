package mousebot.commands;

import mousebot.main.Controller;
import mousebot.parameters.GlobalParams;
import mousebot.parameters.Param;

public class SetGlobalParam implements Command {

  private Param param;
  private Object oldValue, newValue;

  public SetGlobalParam(Param param, Object newValue) {
    this.param = param;
    oldValue = GlobalParams.getInstance().get(param);
    this.newValue = newValue;
  }

  public void execute() {
    GlobalParams.getInstance().set(param, newValue);
  }

  public void undo() {
    GlobalParams.getInstance().set(param, oldValue);
    Controller.getParamsManager().setValue(param, oldValue);
    Controller.getProject().tool.get().paramChanged(param, oldValue);
  }

  public void redo() {
    GlobalParams.getInstance().set(param, newValue);
    Controller.getParamsManager().setValue(param, newValue);
    Controller.getProject().tool.get().paramChanged(param, newValue);
  }

}