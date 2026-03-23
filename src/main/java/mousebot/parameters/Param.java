package mousebot.parameters;

import mousebot.components.Bezier.BezierType;
import mousebot.main.LanguageManager;

/**
 * Skup svih parametara.
 *
 * @author josip
 */
public enum Param {

  LENGTH("params.length", new ParamTextField(), new ParamValidation() {
    public Object validate(String str) {
      try {
        double v = Double.parseDouble(str);
        if (v > 0d) {
          return v;
        }
      } catch (Exception e) {}
      return null;
    }
  }),
  ANGLE("params.angle", new ParamTextField(), new ParamValidation() {
    public Object validate(String str) {
      try {
        return Double.parseDouble(str);
      } catch (Exception e) {}
      return null;
    }
  }),


  DENSITY("params.density", new ParamTextField(), new ParamValidation() {
    public Object validate(String str) {
      try {
        Integer v = Integer.parseInt(str);
        if (v >= 0) {
          return v;
        }
      } catch (Exception e) {}
      return null;
    }
  }),

  BEZIER_TYPE("params.beziertype", new ParamBezierType<BezierType>(BezierType.values()), new ParamValidation() {}),

  ACCURACY_BEZIER("params.accuracy", new ParamTextField(), new ParamValidation() {
    public Object validate(String str) {
      try {
        Double v = Double.parseDouble(str);
        v = Math.round(v * 1E3d) / 1E3d;
        if (v > 0d && v <= 1d) {
          return v;
        }
      } catch (Exception e) {}
      return null;
    }
  }),

  ACCURACY_TEXT("params.accuracy", new ParamTextField(), new ParamValidation() {
    public Object validate(String str) {
      try {
        Double v = Double.parseDouble(str);
        v = Math.round(v * 1E2d) / 1E2d;
        if (v > 0d && v <= 1d) {
          return v;
        }
      } catch (Exception e) {}
      return null;
    }
  }),

  INVERTED_IMAGE("params.invertedcolors", new ParamCheckBox(), new ParamValidation() {}),

  TRANSPARENCY("params.transparency", new ParamTextField(), new ParamValidation() {
    public Object validate(String str) {
      try {
        Double v = Double.parseDouble(str);
        v = Math.round(v * 1E2d) / 1E2d;
        if (v >= 0.1d && v <= 1d) {
          return v;
        }
      } catch (Exception e) {}
      return null;
    }
  });

  public String name; // naziv
  public ParamValue<?> val; // komponenta kojom je prikazan
  public ParamValidation pv; // provjera vrijednosti

  Param(String name, ParamValue<?> val, ParamValidation pv) {
    if (name != null) {
      this.name = LanguageManager.getString(name);
    }
    val.init(this);
    this.val = val;
    this.pv = pv;
  }

}