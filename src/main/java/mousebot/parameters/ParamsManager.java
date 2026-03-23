package mousebot.parameters;

import java.util.ArrayList;
import javafx.scene.control.Control;
import javafx.scene.control.Label;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import mousebot.components.Component;
import mousebot.main.Controller;

/**
 * Upravitelj parametara.
 *
 * @author josip
 */
public class ParamsManager {

  /**
   * Postavlja vrijednost za određeni parametar.
   *
   * @param param
   * @param value
   */
  public void setValue(Param param, Object value) {
    if (value instanceof Double) {
      value = Math.round((double) value * 100d) / 100d;
    }
    param.val.setValue(value);
  }

  /**
   * Prikazuje parametre u predviđeno mjesto.
   *
   * @param params
   */
  public void createParams(Param[] params) {

    GridPane gp = new GridPane();
    for (int i = 0; params != null && i < params.length; i++) {

      Label text = new Label(params[i].name + ":");
      gp.add(text, 0, i);

      gp.add(params[i].val.node, 1, i);

      if (!Controller.getProject().isToolSelect.get()) {
        GlobalParams.getInstance().init(params[i]);
      }
    }

    gp.setHgap(6);
    gp.setVgap(6);

    ColumnConstraints cc = new ColumnConstraints();
    cc.setPrefWidth(Control.USE_COMPUTED_SIZE);
    cc.setMinWidth(Control.USE_PREF_SIZE);
    cc.setMaxWidth(Control.USE_PREF_SIZE);
    gp.getColumnConstraints().add(cc);

    Controller.getInstance().paramsPane.setContent(gp);

    VBox.setVgrow(gp, Priority.ALWAYS);

  }

  public void createParams(ArrayList<Param> params) {

    Param p[] = new Param[params.size()];
    for (int i = 0; i < params.size(); i++) {
      p[i] = params.get(i);
    }
    createParams(p);

  }

  /**
   * Uklanja prikaz parametara.
   */
  public void clearParams() {
    Controller.getInstance().paramsPane.setContent(null);
  }

  /**
   * Vraća sve parametre i vrijednosti određene komponente.
   *
   * @param params
   * @param c
   */
  public void getParamsByComponent(ArrayList<Param> params, Component c) {

    c.paramValues.forEach((p, cVal) -> {

      if (containsParam(params, p)) {

        Object paramVal = p.val.value;

        if (!cVal.equals(paramVal)) {

          if (p.val instanceof ParamTextField) {
            p.val.setValue("...");
          } else {
            params.remove(p);
          }

        }
      } else {
        setValue(p, c.getParamValue(p));
        params.add(p);

      }

    });

  }

  /**
   * Nalazi li se parametar p u listi parametara params.
   *
   * @param params
   * @param p
   * @return
   */
  private boolean containsParam(ArrayList<Param> params, Param p) {
    for (int i = 0; i < params.size(); i++) {
      if (params.get(i) == p) {
        return true;
      }
    }
    return false;
  }

}