package terminal.controller;

import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import terminal.model.LineData;
import terminal.model.Model;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Objects;
import java.util.ResourceBundle;

public class TerminalController extends Controller implements Initializable,PropertyChangeListener {


    public ScrollPane terminal;
    public VBox vbox;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        vbox.minHeightProperty().bind(terminal.heightProperty());
        vbox.heightProperty().addListener(observable -> terminal.setVvalue(1D));
    }

    @Override
    public void setModel(Model model) {
        this.model = model;
        model.addListener(this);
        FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("fxml/line.fxml"));
        try {
            HBox load = loader.load();
            LineController controller = loader.getController();
            controller.setModel(model);
            vbox.getChildren().add(load);
            LineData lineData = new LineData(load, controller);
            model.addLineData(lineData);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        String newValue = (String) evt.getNewValue();
        if (newValue.equalsIgnoreCase("clear")){
            vbox.getChildren().clear();
        }else {
            String listedFiles = (String) evt.getOldValue();
            if (!listedFiles.isEmpty()) {
                Label label = new Label(listedFiles);
                try {
                    String cssPath = Objects.requireNonNull(getClass().getClassLoader()
                            .getResource("css/style.css")).toURI().toString();
                    String[] split = cssPath.split(":");
                    label.setStyle(split[split.length-1]);
                    label.getStyleClass().add("results");
                } catch (URISyntaxException e) {
                    e.printStackTrace();
                }
                label.setFont(new Font(14));
                label.setPadding(new Insets(0,0,0,5));
                vbox.getChildren().add(label);
            }
        }
        FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("fxml/line.fxml"));
        try {
            HBox load = loader.load();
            LineController controller = loader.getController();
            controller.setModel(model);
            vbox.getChildren().add(load);
            LineData lineData = new LineData(load, controller);
            model.addLineData(lineData);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
