package geometry;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.Property;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

public class Settings {


    private CheckBox vis_q_status = new CheckBox();

    // ================================================================================================
    // gridpane
    // ================================================================================================
    GridPane gp;
    int rowIndex = 0;

    //propertiers
    private int toolbarWidth = 315;


    // instance handling
    // ----------------------------------------
    private static Settings settings = new Settings();

    private Settings() {
    }

    public static Settings get() {
        return settings;
    }


    public Node createSettings() {

        gp = new GridPane();

        // gridpane layout
        gp.setPrefWidth(Settings.get().getSettingsWidth());

        gp.setHgap(1);
        gp.setVgap(10);
        gp.setPadding(new Insets(8));

        // set column size in percent
        ColumnConstraints column = new ColumnConstraints();
        column.setPercentWidth(50);
        gp.getColumnConstraints().add(column);

        column = new ColumnConstraints();
        column.setPercentWidth(70);
        gp.getColumnConstraints().add(column);

        // add components for settings to gridpane

        // Points Options
        // -------------------------------------
        addSeparator("Point Options");

        Button drawpoly = new Button("Connect");
        Font font = Font.font("Arial", FontWeight.BOLD, 11);
        drawpoly.setFont(font);
        drawpoly.setMaxSize(60, 10);


        Button delnode = new Button("Delete");
        delnode.setFont(font);
        delnode.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                GUI.polygon.deletePolygon();
                if(GUI.vis_q!= null) { GUI.vis_q.deleteVisPolygon();}
                vis_q_status.setSelected(false);
            }
        });
        addButton("Point Nodes", delnode);


        CheckBox caption = new CheckBox();
        addCheckBox("Node Caption", caption);


        CheckBox hidenode = new CheckBox();
        addCheckBox("Hide Node", hidenode);

        // Polygon Options
        // -------------------------------------
        addSeparator("Polygon Options");




        addCheckBox("VisibilityPolygon", vis_q_status);
        vis_q_status.selectedProperty().addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
                if (vis_q_status.isSelected()) {
                    GUI.vis_q = new VisPolygon();
                } else {
                    GUI.vis_q = null;
                    geometry.EventHandler.clicks = 0;
                    geometry.EventHandler.p_moving = false;
                    GUI.polygonscene.getChildren().clear();
                }
            }
        });


        Slider beta = new Slider(0, 720, 0);
        beta.valueProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observableValue, Number number, Number t1) {
                if (beta.getValue() == 0){
                    GUI.betavis_q.deletePolygon();
                    System.out.println("deleted");
                } else if ( GUI.betavis_q!=null){
                    System.out.println("betavisq != null");
                    GUI.betavis_q.deletePolygon();
                    GUI.betavis_q = new BetaVis(beta.getValue());
                } else if (GUI.betavis_q == null) {
                    System.out.println("betavisq == null");
                    GUI.betavis_q = new BetaVis(beta.getValue());
                }

            }
        });

        //GUI.betavis_q.setBeta_global(beta.getValue());

        beta.setShowTickLabels(true);
        beta.setShowTickMarks(true);
        beta.setMajorTickUnit(90);
        beta.setMinorTickCount(45);
        addNumberSlider("Beta-Erweiterung", 0, beta);

//        CheckBox realtime = new CheckBox();
//        addCheckBox("Real Time Update", realtime);


        // Polygon Drawer
        // -------------------------------------
        addSeparator("Polygon Drawer");

        PolygonDrawer drawer = new PolygonDrawer();


        Button drawlaby_right = new Button("Draw");
        drawlaby_right.setFont(font);
        drawlaby_right.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                drawer.draw_Labyrinth_polygon();
            }
        });
        addButton("Right-Sided Labyrinth", drawlaby_right);

        Button draw_s = new Button("Draw");
        draw_s.setFont(font);
        draw_s.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                drawer.draw_S_polygon();
            }
        });
        addButton("S polygon", draw_s);

        return gp;
    }

    // ------------------------------------------------------------------------------------------------
    // gui helper methods
    // ------------------------------------------------------------------------------------------------


    private void addSeparator(String text) {
        gp.addRow(rowIndex++, createSeparator(text));
    }

    private void addNumberSlider(String text, int digits, Slider slider) {

        // number format, eg "%.3f"
        String format = "%." + digits + "f";

        addNumberSlider(text, slider, format);

    }

    private void addNumberSlider(String text, Slider slider, String labelFormat) {


        Label valueLabel = new Label();
        valueLabel.setPrefWidth(70);
        valueLabel.textProperty().bind(slider.valueProperty().asString(labelFormat));

        HBox box = new HBox();
        box.setSpacing(8);
        box.getChildren().addAll(slider, valueLabel);

        gp.addRow(rowIndex++, new Label(text), box);
    }

    private void addButton(String text, Button button) {

        HBox box = new HBox();
        box.setSpacing(8);
        box.getChildren().addAll(button);

        gp.addRow(rowIndex++, new Label(text), box);
    }

    private void addButtons(String text, Button button1, Button button2) {


        HBox box = new HBox();
        box.setSpacing(8);
        box.getChildren().addAll(button1, button2);

        gp.addRow(rowIndex++, new Label(text), box);
    }

    private void addToggleButton(String text, ToggleButton tbutton) {
        Label valueLabel = new Label();
        valueLabel.setPrefWidth(70);


        HBox box = new HBox();
        box.setSpacing(8);
        box.getChildren().addAll(tbutton, valueLabel);

        gp.addRow(rowIndex++, new Label(text), box);
    }

    private void addCheckBox(String text, CheckBox cb) {
        gp.addRow(rowIndex++, new Label(text), cb);
    }


    private Node createSeparator(String text) {

        VBox box = new VBox();

        Label label = new Label(text);
        label.setFont(Font.font(null, FontWeight.BOLD, 14));

        Separator separator = new Separator();

        box.getChildren().addAll(separator, label);

        box.setFillWidth(true);

        GridPane.setColumnSpan(box, 2);
        GridPane.setFillWidth(box, true);
        GridPane.setHgrow(box, Priority.ALWAYS);

        return box;
    }


    // ------------------------------------------------------------------------------------------------
    // Getters and Setters
    // ------------------------------------------------------------------------------------------------

    public CheckBox get_vis_q_Status() {
        return vis_q_status;
    }




    public final int getSettingsWidth() {
        return this.toolbarWidth;
    }

}

