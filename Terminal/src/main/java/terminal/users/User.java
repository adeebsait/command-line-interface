package terminal.users;

import java.io.Serializable;

public class User implements Serializable {
    private String userName;
    private String password;
    private boolean superUser;

    public User(String userName, String password, boolean superUser) {
        this.userName = userName;
        this.password = password;
        this.superUser = superUser;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public boolean isSuperUser() {
        return superUser;
    }

    public void setSuperUser(boolean superUser) {
        this.superUser = superUser;
    }
}
