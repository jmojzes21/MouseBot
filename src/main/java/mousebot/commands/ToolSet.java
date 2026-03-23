package mousebot.commands;

import mousebot.main.Controller;
import mousebot.tools.Tool;

public class ToolSet implements Command {

  private Tool prevTool, newTool;

  public ToolSet(Tool prevTool, Tool newTool) {
    this.prevTool = prevTool;
    this.newTool = newTool;
  }

  public void execute() {
    prevTool.cancel();
    Controller.getProject().tool.set(newTool);
    Controller.getParamsManager().createParams(newTool.createParams());
    newTool.initParamValues();
  }

  public void undo() {
    Controller.getProject().tool.set(prevTool);
    Controller.getToolsManager().listener.disable();
    Controller.getToolsManager().toggleGroup.selectToggle(prevTool.toolHolder);
    Controller.getToolsManager().listener.enable();
    Controller.getParamsManager().createParams(prevTool.createParams());
    prevTool.initParamValues();
  }

  public void redo() {
    Controller.getProject().tool.set(newTool);
    Controller.getToolsManager().listener.disable();
    Controller.getToolsManager().toggleGroup.selectToggle(newTool.toolHolder);
    Controller.getToolsManager().listener.enable();
    Controller.getParamsManager().createParams(newTool.createParams());
    newTool.initParamValues();
  }

  public boolean shouldUnsave() {
    return false;
  }

}