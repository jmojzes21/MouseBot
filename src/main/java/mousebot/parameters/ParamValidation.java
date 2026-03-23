package mousebot.parameters;

/**
 * Provjera vrijednosti parametra.
 *
 * @author josip
 */
public interface ParamValidation {

  /**
   * Provjera vrijednosti.
   *
   * @param str vrijednost kao string
   * @return valjana vrijednost ili null
   */
  public default Object validate(String str) {
    return str;
  }

}