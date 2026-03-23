package mousebot.commands;


import mousebot.components.Line;
import mousebot.main.Controller;

public class LineCancel implements Command {

  public Line line;

  public LineCancel(Line line) {
    this.line = line;
  }

  public void execute() {
    Controller.getProject().currentLayer.components.remove(line);
    Controller.getProject().tool.get().initParamValues();
  }

  public void undo() {
    Controller.getProject().currentLayer.components.add(line);
    Controller.getProject().tool.get().setComponent(line);
    line.setEnd(Controller.getProject().getMouseLoc());
    line.updateParams();
  }

  public void redo() {
    Controller.getProject().currentLayer.components.remove(line);
    Controller.getProject().tool.get().setComponent(null);
    Controller.getProject().tool.get().initParamValues();
  }

}