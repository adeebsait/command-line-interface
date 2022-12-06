package terminal.controller;


import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import terminal.model.LineData;
import terminal.model.Model;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;


public class LineController extends Controller implements PropertyChangeListener {
    public Label username;
    public Label directory;
    public Label command;
    public TextField textInput;
    private int index = 0;

    @Override
    public void setModel(Model model) {
        this.model = model;
        model.addListener(this);
        username.setText(model.getUserName());
        directory.setText(model.getCurrentPath());
        index = model.getLinesSize();
    }

    public void keyPressed(KeyEvent keyEvent) {
        KeyCode code = keyEvent.getCode();
        if (code == KeyCode.ENTER){
            if (textInput.getText().isEmpty()) return;
            model.executeCommand(textInput.getText());
        }
        if (code == KeyCode.UP){
            index--;
            if (index < 0){
                index = 0;
                return;
            }
            LineData lineData = model.getLineData(index);
            textInput.setText(lineData.getController().getCommand());

        }
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        if (evt.getPropertyName().equals("command")){
            command.setText(textInput.getText());
            textInput.setVisible(false);
            command.setVisible(true);
        }
    }

    public String getCommand() {
        return command.getText();
    }
}
