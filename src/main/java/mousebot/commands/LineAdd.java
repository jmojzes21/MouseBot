package mousebot.commands;

import mousebot.components.Line;
import mousebot.main.Controller;

public class LineAdd implements Command {

  private Line line;

  public LineAdd(Line line) {
    this.line = line;
  }

  public void execute() {
    Controller.getProject().currentLayer.components.add(line);
    line.updateParams();
  }

  public void undo() {
    Controller.getProject().currentLayer.components.remove(line);
    Controller.getProject().tool.get().setComponent(null);
    line.unfreeze();
    Controller.getProject().tool.get().initParamValues();

    if (Controller.getInvoker().canUndo.get() && Controller.getInvoker().getNextUndo() instanceof LineFinish) {
      Controller.getInvoker().undo();
    }

  }

  public void redo() {
    Controller.getProject().currentLayer.components.add(line);
    Controller.getProject().tool.get().setComponent(line);
    line.setEnd(Controller.getProject().getMouseLoc());
  }

}