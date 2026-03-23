package mousebot.parameters;

import java.util.HashMap;
import mousebot.components.Bezier.BezierType;

/**
 * Definira zadane vrijednost određenih parametara.
 *
 * @author josip
 */
public class GlobalParams {

  private static GlobalParams gp = new GlobalParams();

  public static GlobalParams getInstance() {
    return gp;
  }

  private HashMap<Param, Object> params = new HashMap<>();

  private GlobalParams() {
    params.put(Param.DENSITY, 0);
    params.put(Param.BEZIER_TYPE, BezierType.QUADRATIC);
    params.put(Param.ACCURACY_BEZIER, 0.05d);
    params.put(Param.ACCURACY_TEXT, 0.2d);
    params.put(Param.TRANSPARENCY, 1d);
  }

  public void set(Param param, Object val) {
    params.put(param, val);
  }

  public Object get(Param param) {
    return params.get(param);
  }

  public boolean isGlobal(Param p) {
    return params.containsKey(p);
  }

  public void init(Param p) {
    if (isGlobal(p)) {
      p.val.setValue(get(p));
    }
  }

}