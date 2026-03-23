package mousebot.parameters;

import javafx.scene.control.CheckBox;
import mousebot.commands.SetGlobalParam;
import mousebot.main.Controller;
import mousebot.main.FixedChangeListener;
import mousebot.tools.DrawingTool;

/**
 * Za prikaz komponente CheckBox za parametar InverterImage.
 *
 * @author josip
 */
public class ParamCheckBox extends ParamValue<CheckBox> {

  private FixedChangeListener<Boolean> listener;

  public ParamCheckBox() {
    node = new CheckBox();
    node.setText("");
  }

  public void init(Param p) {
    listener = new FixedChangeListener<>() {
      public Boolean validate(Boolean current, Boolean newValue) {
        return newValue;
      }

      public void changed(Boolean oldValue, Boolean newValue) {
        if (Controller.getProject().tool.get() instanceof DrawingTool) {
          Controller.getInvoker().execute(new SetGlobalParam(p, newValue));
        }
        if (Controller.getProject().isToolSelect.get()) {
          Controller.getProject().tool.get().paramChanged(p, new Object[]{oldValue, newValue});
        } else {
          Controller.getProject().tool.get().paramChanged(p, newValue);
        }
        Controller.getInstance().canvas.requestFocus();
        Controller.getProject().render();
      }

      public void setValue(Boolean value) {
        node.setSelected(value);
      }
    };
    node.selectedProperty().addListener(listener);
  }

  public void setValue(Object object) {
    listener.disable();
    node.setSelected((boolean) object);
    listener.enable();
  }

}