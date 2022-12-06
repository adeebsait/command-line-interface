package terminal.model;

import javafx.scene.layout.HBox;
import terminal.controller.LineController;

public class LineData{
    private final HBox view;
    private final LineController controller;

    public LineData(HBox view, LineController controller) {
        this.view = view;
        this.controller = controller;
    }

    public HBox getView() {
        return view;
    }

    public LineController getController() {
        return controller;
    }
}
