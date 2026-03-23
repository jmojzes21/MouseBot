package mousebot.parameters;

import javafx.scene.control.ChoiceBox;
import mousebot.commands.SetGlobalParam;
import mousebot.main.Controller;
import mousebot.main.FixedChangeListener;
import mousebot.tools.DrawingTool;

/**
 * Za prikaz komponente za parametar tipa bezierove krivulje.
 *
 * @author josip
 */
public class ParamBezierType<E> extends ParamValue<ChoiceBox<E>> {

  private FixedChangeListener<E> listener;

  @SafeVarargs
  public ParamBezierType(E... e) {
    node = new ChoiceBox<>();
    node.setPrefWidth(116);
    node.getItems().addAll(e);
  }

  public void init(Param p) {
    listener = new FixedChangeListener<>() {
      public E validate(E current, E newValue) {
        return newValue;
      }

      public void changed(E oldValue, E newValue) {
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

      public void setValue(E value) {
        node.getSelectionModel().select(value);
      }
    };
    node.getSelectionModel().selectedItemProperty().addListener(listener);
  }

  @SuppressWarnings("unchecked")
  public void setValue(Object object) {
    listener.disable();
    node.getSelectionModel().select((E) object);
    listener.enable();
  }

}