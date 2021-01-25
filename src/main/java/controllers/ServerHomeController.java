/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import dbconnection.*;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URL;
import java.util.Date;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;

/**
 * FXML Controller class
 *
 * @author Mohab
 */
public class ServerHomeController implements Initializable {
    @FXML
    private Button start;
    @FXML
    private TextArea taLog;
    @FXML
    private Button stop;
    @FXML
    private Button listUsers;
    @FXML
    private TableView<ListUsers> table = new TableView();
    @FXML
    private TableColumn<ListUsers, String> nameCol;
    @FXML
    private TableColumn<ListUsers, String> emailCol ;
    @FXML
    private TableColumn<ListUsers, Integer> scoreCol ;
    @FXML
    private TableColumn<ListUsers, ImageView> statusCol ;
    @FXML
    private TableColumn<ListUsers, ImageView> avatarCol ;
    
    private ServerSocket serverSocket;
    
    private boolean ServerOn = false;
    Thread main;
    boolean tableOn = false;
    DBMS db = new DBMS();
    private DataInputStream dis;
    private PrintStream ps;
    private Player p;
    private ManagePlayerConnection playeConn;
    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
        //set up columns
        nameCol.setCellValueFactory(new PropertyValueFactory<ListUsers,String>("name"));
        emailCol.setCellValueFactory(new PropertyValueFactory<ListUsers,String>("email"));
        scoreCol.setCellValueFactory(new PropertyValueFactory<ListUsers,Integer>("main_score"));
        statusCol.setCellValueFactory(new PropertyValueFactory<ListUsers,ImageView>("stat"));
        avatarCol.setCellValueFactory(new PropertyValueFactory<ListUsers,ImageView>("avat"));
    }    
    
    public void ViewUsersAction(ActionEvent event) throws IOException
    {
        if(tableOn == true)
        {
            tableOn = false;
        }else{
            tableOn = true;
        }
            table.setVisible(tableOn);
        try {
            table.setItems(db.ViewForServer());
        } catch (InstantiationException ex) {
            Logger.getLogger(ServerHomeController.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            Logger.getLogger(ServerHomeController.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(ServerHomeController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public void StartServer(ActionEvent event)
    {
        ServerOn = true;
        start.setDisable(ServerOn);
        if(main == null)
        {
        main = new Thread( new Runnable() {
            @Override
            public void run() {
                try 
                {
                    // Create a server socket
                    serverSocket = new ServerSocket(5080);

                    Platform.runLater(() -> taLog.appendText(new Date() + ": Server started at socket 5080\n"));
                    
                    // Ready to create a session for every two players
                    while (ServerOn)
                    {
                             //Socket s = serverSocket.accept();
                            playeConn = new ManagePlayerConnection();
                            playeConn.startConnection(serverSocket);
                            new PlayerHandler(playeConn);
                    }
                    serverSocket.close();
                }
                catch(IOException | ClassNotFoundException | InstantiationException | IllegalAccessException ex)
                {
                    ex.printStackTrace();
                }
            }            
        });
        
        main.start();
        }else{
        main.resume();
    }
    }
    public void StopServer(ActionEvent event) 
    {
        ServerOn = false;
        start.setDisable(ServerOn);
        main.stop();
        
    }

}