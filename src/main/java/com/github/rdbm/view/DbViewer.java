package com.github.rdbm.view;

import javafx.event.Event;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.Window;
import org.apache.ddlutils.model.Database;
import org.apache.ddlutils.model.Table;

/**
 * Created by maxtar.
 */
public class DbViewer extends Stage {

    private Database database;

    public DbViewer(Database database) {
        this.database = database;
    }

    public void createAndShowContent(String fileName, Window parentStage) {
        final Pane pane = new Pane();
        Table[] tables = database.getTables();
        int row = 0;
        int column = 0;
        for (int i = 0; i < tables.length; i++) {
            Table table = tables[i];
            Node tblNode = makeDraggable(new TableElement(table));
            if (i > 0 && i % 3 == 0) {
                row++;
                column = 0;
            }
            // todo think about relocation coordinates
            tblNode.relocate(column * 150 + 50, row * 300 + 50);
            column++;
            pane.getChildren().add(tblNode);
        }
        final BorderPane sceneRoot = new BorderPane();
        BorderPane.setAlignment(pane, Pos.TOP_LEFT);
        sceneRoot.setCenter(pane);
        final Scene scene = new Scene(sceneRoot, 700, (1 + row) * 300);
        setScene(scene);
        setTitle("RDBM: " + fileName);
        initOwner(parentStage);
        initModality(Modality.APPLICATION_MODAL);
        show();
    }

    private Node makeDraggable(final Node node) {
        final DragContext dragContext = new DragContext();
        final Group wrapGroup = new Group(node);

        wrapGroup.addEventFilter(MouseEvent.ANY, Event::consume);

        wrapGroup.addEventFilter(MouseEvent.MOUSE_PRESSED,
                mouseEvent -> {
                    dragContext.mouseAnchorX = mouseEvent.getX();
                    dragContext.mouseAnchorY = mouseEvent.getY();
                    dragContext.initialTranslateX = node.getTranslateX();
                    dragContext.initialTranslateY = node.getTranslateY();
                });

        wrapGroup.addEventFilter(MouseEvent.MOUSE_DRAGGED,
                mouseEvent -> {
                    node.setTranslateX(dragContext.initialTranslateX
                            + mouseEvent.getX() - dragContext.mouseAnchorX);
                    node.setTranslateY(dragContext.initialTranslateY
                            + mouseEvent.getY() - dragContext.mouseAnchorY);
                });

        return wrapGroup;
    }

    private static final class DragContext {
        double mouseAnchorX;
        double mouseAnchorY;
        double initialTranslateX;
        double initialTranslateY;
    }
}
