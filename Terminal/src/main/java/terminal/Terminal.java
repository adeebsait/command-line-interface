package terminal;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.ScrollPane;
import javafx.stage.Stage;
import terminal.controller.TerminalController;
import terminal.model.Model;
//import terminal.server.httpServer;

public class Terminal extends Application {
    private final int PORT = 9091;    
    Model serv = new Model();
    @Override
    public void start(Stage stage) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("fxml/terminal.fxml"));
        ScrollPane load = loader.load();
        TerminalController controller = loader.getController();
        controller.setModel(new Model());
        stage.setTitle("Terminal");
        stage.setScene(new Scene(load));
        stage.show();
    }
    public void run() {
        try {
            ServerSocket server;
            server = new ServerSocket(PORT);
            System.out.println("MiniServer active "+PORT);
            boolean shudown = true;
            while (shudown) {               
                Socket socket;                
                socket = server.accept();
                InputStream is = socket.getInputStream();
                PrintWriter out = new PrintWriter(socket.getOutputStream());            
                BufferedReader in = new BufferedReader(new InputStreamReader(is));              
                String line;
                line = in.readLine();
                //System.out.println(line);
                String auxLine = line;
                line = "";
                // looks for post data
                int postDataI = -1;
                while ((line = in.readLine()) != null && (line.length() != 0)) {
                    System.out.println(line);
                    if (line.contains("Content-Length:")) {
                        postDataI = Integer.parseInt(line
                                .substring(
                                        line.indexOf("Content-Length:") + 16,
                                        line.length()));
                    }
                }
                String postData = "";
                for (int i = 0; i < postDataI; i++) {
                    int intParser = in.read();
                    postData += (char) intParser;
                }                               
                out.println("HTTP/1.0 200 OK");
                out.println("Content-Type: text/html; charset=utf-8");
                out.println("Server: MINISERVER");
                // this blank line signals the end of the headers
                out.println("");
                // Send the HTML page               
                out.println("<H1>Welcome to the Terminal server</H1>");
                out.println("<H2>GET->"+auxLine+ "</H2>");        
                out.println("<H2>Post->"+postData+ "</H2>");
                out.println("<form name=\"input\" action=\"imback\" method=\"post\">");
                out.println("Command: <input type=\"text\" name=\"cmd\"><input type=\"submit\" value=\"Submit\"></form>");
                out.println(postData);
                String cmd = postData.substring(4,postData.length());
                //if(!"".equals(cmd))
                    //serv.executeCommand(cmd);
                //Model.executeCommand(postData);
                //if your get parameter contains shutdown it will shutdown
                if(auxLine.contains("shutdown")){
                    shudown = false;
                }
                out.close();
                socket.close();
            }
            server.close();            
        } catch (IOException | NumberFormatException e) {
            e.printStackTrace();
        }
    }   

    public static void main(String[] args) throws IOException {
        launch(args);
        Terminal gtp = new Terminal();
        gtp.run();
        
        //httpServer.main(args);
        
    }
}
