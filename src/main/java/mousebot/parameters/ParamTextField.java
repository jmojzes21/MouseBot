package mousebot.parameters;

import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import mousebot.commands.SetGlobalParam;
import mousebot.main.Controller;
import mousebot.tools.DrawingTool;

/**
 * Za prikaz komponete TextField za postavljanje vrijednosti određenih parametara.
 *
 * @author josip
 */
public class ParamTextField extends ParamValue<TextField> {

  public ParamTextField() {
    node = new TextField();
    node.setPrefWidth(96);
    node.focusedProperty().addListener((o, oldVal, newVal) -> {
      if (!newVal) {
        setValue(value);
      }
    });
  }

  public void init(Param p) {
    node.setOnKeyPressed(e -> {
      if (e.getCode() == KeyCode.ENTER) {
        Object val = p.pv.validate(node.getText());
        if (val == null) {
          node.setText(value.toString());
        } else {
          Object oldValue = value;
          setValue(val);
          if (Controller.getProject().tool.get() instanceof DrawingTool && GlobalParams.getInstance().isGlobal(p)) {
            Controller.getInvoker().execute(new SetGlobalParam(p, val));
          }
          if (Controller.getProject().isToolSelect.get()) {
            Controller.getProject().tool.get().paramChanged(p, new Object[]{oldValue, val});
          } else {
            Controller.getProject().tool.get().paramChanged(p, val);
          }
          Controller.getInstance().canvas.requestFocus();
          Controller.getProject().render();
        }
      } else if (e.getCode() == KeyCode.ESCAPE) {
        setValue(value);
        Controller.getInstance().canvas.requestFocus();
      }
    });
  }

  public void setValue(Object value) {
    node.setText(value.toString());
    this.value = value;
  }

}