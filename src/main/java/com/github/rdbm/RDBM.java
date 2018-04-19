package com.github.rdbm;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.util.Properties;

/**
 * Created by maxtar.
 */
public class RDBM extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("/rdbmfs.fxml"));
        primaryStage.setTitle("RDBM");
        primaryStage.setScene(new Scene(root));
        primaryStage.show();

        // Disable logging in c3p0 connection pooling lib.
        Properties p = new Properties(System.getProperties());
        p.put("com.mchange.v2.log.MLog", "com.mchange.v2.log.FallbackMLog");
        p.put("com.mchange.v2.log.FallbackMLog.DEFAULT_CUTOFF_LEVEL", "OFF");
        System.setProperties(p);
    }

    public static void main(String[] args) {
        launch(args);
    }
}
