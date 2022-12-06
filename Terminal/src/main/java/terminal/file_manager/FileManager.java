package terminal.file_manager;

import terminal.process.CMDProcess;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class FileManager {
    private static final String DATA_FILE = "data.txt";
    private static FileManager fileManager = null;
    private List<File> files;
    private File currentFile = null;

    private FileManager(){
        try {
            files = loadData();
            currentFile = files.get(0);
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public static FileManager getInstance() {
        if (fileManager == null) fileManager = new FileManager();
        return fileManager;
    }

    public void makeDirectory(String name){
        File file = new File(name, true, currentFile);
        currentFile.getFiles().add(file);
        try {
            writeData();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String cd(String filename){
        File file = null;
        if (filename.equals("..")){
            if (currentFile.getParent() != null){
                currentFile = currentFile.getParent();
            }
            return "";
        }
        if (filename.equals(" ")){
            while (currentFile.getParent() != null){
                currentFile = currentFile.getParent();
            }
            return "";
        }
        for (File f:currentFile.getFiles()){
            if (f.getName().equals(filename)){
                file = f;
                break;
            }
        }
        if (file != null){
            if (file.isDirectory()) {
                currentFile = file;
                return currentFile.getName();
            }else{
                return "Error: Cannot cd into file.";
            }
        }else{
            return "Error: Directory "+filename+" does not exists.";
        }

    }

    public void createFile(String name){
        File file = new File(name, currentFile);
        currentFile.getFiles().add(file);
        try {
            writeData();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String listFiles(){
        StringBuilder stringBuilder = new StringBuilder();
        for (File file:currentFile.getFiles()){
            String fileType = file.isDirectory() ? "Folder": "File";
            stringBuilder.append(fileType);
            for (int i=0;i< (file.isDirectory()? 15:20);i++){
                stringBuilder.append(" ");
            }
            stringBuilder.append(file.getName()).append("\n");
        }
        return stringBuilder.toString();
    }

    public void createUserDirectory(String username){
        CMDProcess process = new CMDProcess("0","mkdir",username,
                getClass().getClassLoader().getResource("").getPath());
        process.start();
        try {
            process.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void updateUserFile(String username){
        File file = null;
        for (File f: files){
            if (f.getName().equals(username)){
                file = f;
                break;
            }
        }
        if (file == null){
            createUserDirectory(username);
            file = new File(username, true, null);
            files.add(file);
            try {
                writeData();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        currentFile = file;
    }

    private List<File> loadData() throws IOException, ClassNotFoundException {
        List<File> files = new ArrayList<>();
        java.io.File file = new java.io.File(DATA_FILE);
        if (!file.exists()){
            File newFile = new File("root", true, null);
            files.add(newFile);
            CMDProcess process = new CMDProcess("0","mkdir","root",
                    getClass().getClassLoader().getResource("").getPath());
            process.start();
            try {
                process.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            currentFile = newFile;
            return files;
        }
        ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file));
        Object read = null;
        while ((read = ois.readObject()) != null){
            File readFile = (File) read;
            files.add(readFile);
        }
        ois.close();
        return files;
    }

    private void writeData() throws IOException {
        java.io.File file = new java.io.File(DATA_FILE);
        if (!file.exists()){
            boolean newFile = file.createNewFile();
        }
        ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(file));
        for (File f:files){
            oos.writeObject(f);
        }
        oos.writeObject(null);
        oos.flush();
        oos.close();
    }

    public String getCurrentFileName() {
        return currentFile.getName();
    }
}
