module mousebot {

  requires transitive javafx.graphics;
  requires javafx.fxml;
  requires javafx.swing;
  requires transitive javafx.controls;
  requires java.prefs;
  requires java.logging;
  requires transitive java.desktop;
  requires com.sun.jna.platform;

  exports mousebot.commands;
  exports mousebot.components;
  exports mousebot.executing;
  exports mousebot.layers;
  exports mousebot.main;
  exports mousebot.options;
  exports mousebot.parameters;
  exports mousebot.tools;
  exports mousebot.reorder;

}