package mousebot.executing;

import java.awt.Robot;
import java.awt.event.InputEvent;
import mousebot.components.Point;
import mousebot.main.RegManager;

/**
 * Upravljač miša.
 *
 * @author josip
 */
public class MouseBot {

  private static Robot robot; // za upravljanje mišom
  public static int delay = RegManager.getDelay();

  static {
    try {
      robot = new Robot();
    } catch (Exception e) {}
  }

  public static void move(Point p) {
    move(p.x, p.y);
  }

  /**
   * Postavi lokaciju pokazivača miša.
   *
   * @param x
   * @param y
   */
  public static void move(double x, double y) {
    robot.mouseMove((int) x, (int) y);
    try {
      Thread.sleep(delay);
    } catch (Exception e) {}
  }

  /**
   * Pritisni tipku miša.
   */
  public static void press() {
    robot.mousePress(InputEvent.BUTTON1_DOWN_MASK);
    System.out.println(delay);
    try {
      Thread.sleep(delay);
    } catch (Exception e) {}
  }

  /**
   * Otpusti tipku miša.
   */
  public static void release() {
    robot.mouseRelease(InputEvent.BUTTON1_DOWN_MASK);
    try {
      Thread.sleep(delay);
    } catch (Exception e) {}
  }

}