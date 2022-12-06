package terminal.controller;

import terminal.model.Model;

public abstract class Controller {
    protected Model model;

    public abstract void setModel(Model model);
}
