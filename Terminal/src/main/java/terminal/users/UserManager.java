package terminal.users;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class UserManager {
    private static final String USERS_FILE = "users.txt";
    private static UserManager userManager = null;
    private final List<User> users;
    private boolean fileNotFound = false;

    private UserManager() throws IOException, ClassNotFoundException {
            users = loadUsers();
            if (fileNotFound){
                writeData();
            }
    }

    public static UserManager getInstance() {
        if (userManager == null){
            try {
                userManager = new UserManager();
            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
        return userManager;
    }

    public User login(String username, String password){
        for (User user:users){
            if (user.getUserName().equals(username)){
                if (user.getPassword().equals(password)){
                    return user;
                }
            }
        }
        return null;
    }

    public String createUser(String username, String password, boolean superUser){
        User user = null;
        for (User u:users){
            if (u.getUserName().equals(username)) {
                user = u;
                break;
            }
        }
        if (user != null){
            return "Error: User "+username+" already exists.";
        }
        user = new User(username,password,superUser);
        users.add(user);
        writeData();
        return username+" created.";
    }

    public String deleteUser(String username){
        User user = null;
        for (User u:users){
            if (u.getUserName().equals(username)) {
                user = u;
                break;
            }
        }
        if (user == null){
            return "Error: User "+username+" does not exists.";
        }
        users.remove(user);
        writeData();
        return "User"+ username + " deleted.";
    }

    public String changeUserType(String username, String std){
        User user = null;
        for (User u:users){
            if (u.getUserName().equals(username)) {
                user = u;
                break;
            }
        }
        if (user == null){
            return "Error: User "+username+" does not exists.";
        }
        if (std.contains("super"))
            user.setSuperUser(true);
        else
            user.setSuperUser(false);
        writeData();
        return "User"+ username + "'s type was changed.";
    }

    public String changePassword(String username, String password){
        User user = null;
        for (User u:users){
            if (u.getUserName().equals(username)) {
                user = u;
                break;
            }
        }
        if (user == null){
            return "Error: User "+username+" does not exists.";
        }
        user.setPassword(password);
        writeData();
        return "User"+ username + "'s password was changed.";
    }

    public String listAllUser(){
        StringBuilder res = new StringBuilder();
        for (User user:users){
            res.append(user.getUserName()).append(" ").append(user.isSuperUser()?"super":"standard").append("\n");
        }
        return res.toString();
    }

    private List<User> loadUsers() throws IOException, ClassNotFoundException {
        List<User> users = new ArrayList<>();
        File file = new File(USERS_FILE);
        if (!file.exists()){
            User admin = new User("root","root",true);
            users.add(admin);
            fileNotFound = true;
            return users;
        }
        ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file));
        Object read = null;
        while ((read = ois.readObject()) != null){
            User user = (User) read;
            users.add(user);
        }
        ois.close();
        return users;
    }

    private void writeData(){
        File file = new File(USERS_FILE);
        try {
            if (!file.exists()) {
                boolean newFile = file.createNewFile();
            }
            ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(file));
            for (User user : users) {
                oos.writeObject(user);
            }
            oos.writeObject(null);
            oos.flush();
            oos.close();
        }catch (IOException e){
            e.printStackTrace();
        }
    }
}
