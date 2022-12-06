package terminal.model;

import terminal.file_manager.FileManager;
import terminal.process.CMDProcess;
import terminal.users.User;
import terminal.users.UserManager;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.atomic.AtomicInteger;

public class Model {
    private final FileManager fileManager;
    private String userName;
    private String currentPath;
    private final List<LineData> lineData = new ArrayList<>();
    private final List<CMDProcess> cmdProcesses = new ArrayList<>();
    private AtomicInteger pIDs = new AtomicInteger(0);
    private final PropertyChangeSupport propertyChangeSupport = new PropertyChangeSupport(this);
    private User user = null;
    private String un = "";
    private UserManager userManager;

    public Model(){
        fileManager = FileManager.getInstance();
        try {
            userName = InetAddress.getLocalHost().getHostName();
            userManager = UserManager.getInstance();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
        currentPath = "Login$";
    }

    public void executeCommand(String command){
        String result = "";
        String[] parsed = command.split(" ");
        String lowerCommand = command.toLowerCase(Locale.ROOT);
        boolean error = false;
        if (lowerCommand.contains("adduser")){
            if (parsed.length < 5){
                result = "Error: invalid command need super <username> <password> <type super/std>.";
                error = true;
            }
            if (!parsed[0].equalsIgnoreCase("super")){
                result = "Error: invalid command need super <username> <password> <type super/std>.";
                error = true;
            }
            if (error) {
                propertyChangeSupport.firePropertyChange("command", result, command);
                return;
            }
        }
        if (lowerCommand.contains("deluser")){
            if (parsed.length < 2){
                result = "Error: invalid command need super <username>.";
                error = true;
            }
            if (!parsed[0].equalsIgnoreCase("super")){
                result = "Error: invalid command need super <username>.";
                error = true;
            }
            if (error) {
                propertyChangeSupport.firePropertyChange("command", result, command);
                return;
            }
        }
        if (lowerCommand.contains("chpass")){
            if (parsed.length < 3){
                result = "Error: invalid command need super <username> <password>.";
                error = true;
            }
            if (!parsed[0].equalsIgnoreCase("super")){
                result = "Error: invalid command need super <username> <password>.";
                error = true;
            }
            if (error) {
                propertyChangeSupport.firePropertyChange("command", result, command);
                return;
            }
        }
        if (lowerCommand.contains("chusertype")){
            if (parsed.length < 3){
                result = "Error: invalid command need super <username> <type super/std>.";
                error = true;
            }
            if (!parsed[0].equalsIgnoreCase("super")){
                result = "Error: invalid command need super <username> <type super/std>.";
                error = true;
            }
            if (error) {
                propertyChangeSupport.firePropertyChange("command", result, command);
                return;
            }
        }

        switch (parsed.length) {
            case 1:
                if (user == null){
                    if (un.isEmpty()) {
                        un = command;
                        currentPath = "Password$";
                    }else{
                        User login = userManager.login(un, command);
                        if (login == null){
                            result = "Error: Username or password does not match.";
                            currentPath = "Login$";
                            un = "";
                        }else{
                            fileManager.updateUserFile(un);
                            currentPath = fileManager.getCurrentFileName()+"$";
                            un  = "";
                            user = login;
                            result = "Welcome "+user.getUserName();
                        }
                    }
                }else if (command.trim().equalsIgnoreCase("logoff")||command.trim().equalsIgnoreCase("logout")){
                    un = "";
                    user = null;
                    currentPath = "Login$";
                }else {
                    if (command.trim().equalsIgnoreCase("ls")) {
                        result = fileManager.listFiles();
                        CMDProcess process = new CMDProcess(pIDs.addAndGet(1)+"","ls",
                                getClass().getClassLoader().getResource("").getPath()+currentPath.replace("$",""));
                        process.start();
                        cmdProcesses.add(process);
                        try {
                            process.join();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        result = process.getResult();
                    }
                    else if (parsed[0].trim().equalsIgnoreCase("ps")){
                        for (CMDProcess p:cmdProcesses){
                            result += p.getPId()+" "+p.getName()+" "+(!p.status()? "finished":"running")+"\n";
                        }
                    }
                    else if (parsed[0].trim().equalsIgnoreCase("cd")){
                        result = fileManager.cd(" ");
                        currentPath = user.getUserName() + "$";
                    }
                    else if (command.trim().equalsIgnoreCase("quit")||command.trim().equalsIgnoreCase("exit")) {
                        System.exit(0);
                    }else if (command.trim().equalsIgnoreCase("whoAmI")){
                        result = user.getUserName();
                        CMDProcess process = new CMDProcess(pIDs.addAndGet(1)+"","whoAmI",
                                getClass().getClassLoader().getResource("").getPath()+currentPath.replace("$",""));
                        process.start();
                        cmdProcesses.add(process);
                        try {
                            process.join();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        result = result +" "+process.getResult();
                    }else if (command.trim().equalsIgnoreCase("showDir")) {
                        result = "/"+currentPath.substring(0,currentPath.length()-1);
                        CMDProcess process = new CMDProcess(pIDs.addAndGet(1) + "", "showDir",
                                getClass().getClassLoader().getResource("").getPath() + currentPath.replace("$", ""));
                        process.start();
                        cmdProcesses.add(process);
                        try {
                            process.join();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        result = result + " " + process.getResult();
                    }else if (command.trim().equalsIgnoreCase("lsuser")){
                        result = userManager.listAllUser();
                    }else {
                        result = "Error: Command " + command + " not found";
                    }
                }   break;
            case 2:
                if (parsed[0].trim().equalsIgnoreCase("mkdir")){
                    fileManager.makeDirectory(parsed[1]);
                    CMDProcess process = new CMDProcess(pIDs.addAndGet(1)+"","mkdir",parsed[1],
                            getClass().getClassLoader().getResource("").getPath()+currentPath.replace("$",""));
                    process.start();
                    cmdProcesses.add(process);
                }else if (parsed[0].trim().equalsIgnoreCase("touch")){
                    fileManager.createFile(parsed[1]);
                    CMDProcess process = new CMDProcess(pIDs.addAndGet(1)+"","touch",parsed[1],
                            getClass().getClassLoader().getResource("").getPath()+currentPath.replace("$",""));
                    process.start();
                    cmdProcesses.add(process);
                }else if (parsed[0].trim().equalsIgnoreCase("rmdir")){
                    fileManager.createFile(parsed[1]);
                    CMDProcess process = new CMDProcess(pIDs.addAndGet(1)+"","rmdir",parsed[1],
                            getClass().getClassLoader().getResource("").getPath()+currentPath.replace("$",""));
                    process.start();
                    cmdProcesses.add(process);
                }
                
                else if (parsed[0].trim().equalsIgnoreCase("cd")){
                    result = fileManager.cd(parsed[1]);
                    if (parsed[1].equals("..")) {
                        String[] split = currentPath.split("/");
                        currentPath = user.getUserName();
                        if (split.length <= 1){
                            currentPath = fileManager.getCurrentFileName();
                        }
                        for (int i=1;i<split.length-1;i++){
                            currentPath += "/"+split[i];
                        }
                        currentPath += "$";
                    }else {
                        if (!result.isEmpty() & !result.contains("Error")) {
                            currentPath = currentPath.replace("$",
                                    "/"+ result + "$");
                        }
                    }
                }
                else{
                    result = "Error: Command "+command+" not found.";
                }   break;
            case 3:
                if (parsed[1].trim().equalsIgnoreCase("delUser")){
                    if (user.isSuperUser())
                        result = userManager.deleteUser(parsed[2]);
                    else
                        result = "Error: You need super user privilege to execute this command.";
                }else if (parsed[0].trim().equalsIgnoreCase("mv")||parsed[0].trim().equalsIgnoreCase("move")){
                    CMDProcess process = new CMDProcess(pIDs.addAndGet(1)+"","mv",parsed[1],parsed[2],
                            getClass().getClassLoader().getResource("").getPath()+currentPath.replace("$",""));
                    process.start();
                    cmdProcesses.add(process);
                }else if (parsed[0].trim().equalsIgnoreCase("cp")||parsed[0].trim().equalsIgnoreCase("copy")){
                    CMDProcess process = new CMDProcess(pIDs.addAndGet(1)+"","cp",parsed[1],parsed[2],
                            getClass().getClassLoader().getResource("").getPath()+currentPath.replace("$",""));
                    process.start();
                    cmdProcesses.add(process);
                }   break;
            case 4:
                if (parsed[1].trim().equalsIgnoreCase("chPass")){
                    if (user.isSuperUser())
                        result = userManager.changePassword(parsed[2],parsed[3]);
                    else
                        result = "Error: You need super user privilege to execute this command.";
                }else if(parsed[1].trim().equalsIgnoreCase("chUserType")){
                    if (user.isSuperUser()){
                        result = userManager.changeUserType(parsed[2],parsed[3]);
                    }else{
                        result = "Error: You need super user privilege to execute this command.";
                    }
                }   break;
            case 5:
                if (parsed[1].trim().equalsIgnoreCase("addUser")){
                    if (user.isSuperUser())
                        result = userManager.createUser(parsed[2],parsed[3],parsed[4].equalsIgnoreCase("super"));
                    else
                        result = "Error: You need super user privilege to execute this command.";
                }   break;
            default:
                break;
        }

        propertyChangeSupport.firePropertyChange("command",result,command);
    }

    public void addListener(PropertyChangeListener listener){
        propertyChangeSupport.addPropertyChangeListener(listener);
    }

    public void addLineData(LineData lineData){
        this.lineData.add(lineData);
    }

    public String getUserName() {
        return userName;
    }

    public String getCurrentPath() {
        return currentPath;
    }

    public int getLinesSize(){
        return lineData.size();
    }

    public LineData getLineData(int index) {
        return lineData.get(index);
    }
}
