package terminal.file_manager;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class File implements Serializable {
    private String name;
    private boolean directory = false;
    private final List<File> files = new ArrayList<>();
    private File parent;

    public File(String name, File parent) {
        this.name = name;
        this.parent = parent;
    }

    public File(String name, boolean directory, File parent) {
        this.name = name;
        this.directory = directory;
        this.parent = parent;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isDirectory() {
        return directory;
    }

    public void setDirectory(boolean directory) {
        this.directory = directory;
    }

    public List<File> getFiles() {
        return files;
    }

    public File getParent() {
        return parent;
    }

    @Override
    public String toString() {
        return String.format( "%5s OF %-8s", getName(), directory ? "Folder":"File" );
    }
}
