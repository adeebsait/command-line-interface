package terminal.process;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;

public class CMDProcess extends Thread{
    private final String pID;
    private final ProcessBuilder builder;
    private Process process;
    private final StringBuilder sb;

    public CMDProcess(String pID, String... command) {
        this.pID = pID;
        builder = new ProcessBuilder();
        if (command.length < 3) {
                builder.command("bash", "-c",command[0]);
        }else if (command.length < 4){
                builder.command(command[0],command[1]);
        }else if (command.length < 5){
                builder.command(command[0], command[1],command[2]);
        }
        builder.directory(new File(command[command.length-1]));
        sb = new StringBuilder();
        setName(command[0]);
        System.out.println(builder.command());
    }

    @Override
    public void run() {
        try {
            process = builder.start();
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line;
            while ((line = reader.readLine()) != null){
                System.out.println(line);
                sb.append(line).append("\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("Stopped");
    }

    public boolean status(){
        if (process != null){
            return process.isAlive();
        }
        return false;
    }

    public String getPId() {
        return pID;
    }

    public String getResult() {
        return sb.toString();
    }
}
