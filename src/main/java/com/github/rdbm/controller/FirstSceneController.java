package com.github.rdbm.controller;

import com.github.rdbm.model.ConnectionManager;
import com.github.rdbm.utils.OdbReader;
import com.github.rdbm.view.DbViewer;
import com.mchange.v2.c3p0.ComboPooledDataSource;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.FileChooser;
import org.apache.ddlutils.Platform;
import org.apache.ddlutils.PlatformFactory;
import org.apache.ddlutils.model.Database;

import javax.sql.DataSource;
import java.beans.PropertyVetoException;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.sql.*;
import java.util.*;

/**
 * Created by maxtar.
 */
public class FirstSceneController implements Initializable {

    @FXML
    private Label selectedFile;
    @FXML
    private TextField passwordTextField;
    @FXML
    private TextField usernameTextField;
    @FXML
    private ComboBox<String> DbTypeComboBox;
    private String filePath;
    private String fileName;

    public void handleSelectButtonAction() {
        File file = new FileChooser().showOpenDialog(selectedFile.getScene().getWindow());
        if (file != null) {
            filePath = file.getPath();
            fileName = file.getName();
            selectedFile.setText(filePath);
        }
    }

    public void handleReadButtonAction() throws SQLException, ClassNotFoundException {

        ConnectionManager connectionManager = new ConnectionManager(DbTypeComboBox.getSelectionModel().getSelectedItem(),
                filePath, "org.hsqldb.jdbcDriver", usernameTextField.getText(), passwordTextField.getText());

        ComboPooledDataSource cpds;
        try {
            Database database = null;
            if (Objects.equals(DbTypeComboBox.getSelectionModel().getSelectedItem(), "odb")) {
                OdbReader odbReader = new OdbReader(filePath, usernameTextField.getText(), passwordTextField.getText());
                try {
                    database = odbReader.getDatabase();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else {
                cpds = connectionManager.getComboPooledDataSource();
                database = readDatabase(cpds);
            }

            if (database == null) {
                // todo handle
            } else {
                DbViewer dbStage = new DbViewer(database);
                // todo cpds.close();
                dbStage.createAndShowContent(fileName, selectedFile.getScene().getWindow());
            }

        } catch (PropertyVetoException e) {
            // todo handle
            e.printStackTrace();
        }
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        DbTypeComboBox.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (Objects.equals(passwordTextField.getText(), "") && Objects.equals(usernameTextField.getText(), "")) {
                switch (newValue) {
                    case "hsqldb":
                        usernameTextField.setText("sa");
                        passwordTextField.setText("");
                        break;
                    case "odb":
                        usernameTextField.setText("sa");
                        passwordTextField.setText("");
                        break;
                }
            }
        });
    }

    private Database readDatabase(DataSource dataSource) {
        Platform platform = PlatformFactory.createNewPlatformInstance(dataSource);
        return platform.readModelFromDatabase(null);
    }
}
