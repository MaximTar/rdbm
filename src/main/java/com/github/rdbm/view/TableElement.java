package com.github.rdbm.view;

import javafx.scene.control.Separator;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import org.apache.ddlutils.model.Column;
import org.apache.ddlutils.model.Table;

/**
 * Created by maxtar.
 */
public class TableElement extends StackPane {

    public TableElement(Table table) {
        Text name = new Text(table.getName());
        Text columns = new Text();
        String columnsString = "";
        for (Column column : table.getColumns()) {
            if (table.getColumnIndex(column) == table.getColumnCount() - 1) {
                columnsString += column.getName();
            } else {
                columnsString += column.getName() + System.getProperty("line.separator");
            }
        }
        columns.setText(columnsString);
        VBox vBox = new VBox(5, name, new Separator(), columns);
        String vBoxStyle = "-fx-border-color: black;" +
                "-fx-padding: 10;" +
                "-fx-border-style: solid inside;" +
                "-fx-border-radius: 5;" +
//                "-fx-border-insets: 100;" +
                "-fx-border-width: 1;";
        vBox.setStyle(vBoxStyle);
        getChildren().addAll(vBox);
    }
}
