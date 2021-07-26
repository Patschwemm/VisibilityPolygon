package geometry;

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
    private static Slider beta = new Slider(0, 720, 0);

    // ================================================================================================
    // gridpane
    // ================================================================================================
    GridPane gp;
    int rowIndex = 0;

    //properties
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

        // ----------------------------------------------------------------------------------------------------------
        // Points Options
        // ----------------------------------------------------------------------------------------------------------
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
                if(GUI.vis_q.betavis == true){
                    GUI.betavis_q_rec.deleteBetaVisPolygon();
                    GUI.vis_q.betavis = false;
                    beta.valueProperty().setValue(0);
                }
            }
        });
        addButton("Point Nodes", delnode);


        // ----------------------------------------------------------------------------------------------------------
        // Polygon Options
        // ----------------------------------------------------------------------------------------------------------

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
                    if(GUI.betavis_q_rec != null){
                        GUI.betavis_q_rec.deleteBetaVisPolygon();
                    }
                    beta.valueProperty().setValue(0);
                }
            }
        });


        beta.valueProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observableValue, Number oldvalue, Number newvalue) {
                if(GUI.vis_q != null){
                    if (newvalue.intValue() == 0){
                        VisPolygon.betavis = false;
                        GUI.betavis_q_rec.deleteBetaVisPolygon();
                    } else if (oldvalue.intValue() == 0 && newvalue.intValue()!= 0){
                        VisPolygon.betavis = true;
                        GUI.betavis_q_rec = new Beta_Visibility_Brute(beta.getValue());
                    } else{
                        VisPolygon.betavis = true;
                        GUI.betavis_q_rec.deleteBetaVisPolygon();
                        GUI.betavis_q_rec = new Beta_Visibility_Brute(beta.getValue());
                    }
                }else {
                    System.out.println("Input Visibility Polygon and necessities");
                }
            }
        });


        beta.setShowTickLabels(true);
        beta.setShowTickMarks(true);
        beta.setMajorTickUnit(90);
        beta.setMinorTickCount(45);
        addNumberSlider("Beta-Erweiterung", 0, beta);



        // ----------------------------------------------------------------------------------------------------------
        // Polygon Drawer
        // ----------------------------------------------------------------------------------------------------------

        addSeparator("Polygon Drawer");

        PolygonDrawer drawer = new PolygonDrawer();

        //create Buttons for drawing an already set custom polygon in Class PolygonDrawer

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
        addButton("S-Polygon", draw_s);

        Button draw_c = new Button("Draw");
        draw_c.setFont(font);
        draw_c.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                drawer.draw_C_Polygon();
            }
        });
        addButton("C-polygon", draw_c);

        Button draw_right_cave = new Button("Draw");
        draw_right_cave.setFont(font);
        draw_right_cave.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                drawer.draw_generic_right_cave();
            }
        });
        addButton("Generic Right Cave", draw_right_cave);

        Button draw_left_cave = new Button("Draw");
        draw_left_cave.setFont(font);
        draw_left_cave.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                drawer.draw_generic_left_cave();
            }
        });
        addButton("Generic Left Cave", draw_left_cave);

        Button draw_reverse_s_polygon = new Button("Draw");
        draw_reverse_s_polygon.setFont(font);
        draw_reverse_s_polygon.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                drawer.draw_reverse_S_Polygon();
            }
        });
        addButton("Reverse S Polygon", draw_reverse_s_polygon);

        Button draw_z_poly = new Button("Draw");
        draw_z_poly.setFont(font);
        draw_z_poly.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                drawer.draw_z_polygon();
            }
        });
        addButton("Z Polygon", draw_z_poly);

        Button draw_right_corridor_cave = new Button("Draw");
        draw_right_corridor_cave.setFont(font);
        draw_right_corridor_cave.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                drawer.draw_right_corridor_caves();
            }
        });
        addButton("Right Corridor Caves", draw_right_corridor_cave);

        Button draw_checkrecursion_test = new Button("Draw");
        draw_checkrecursion_test.setFont(font);
        draw_checkrecursion_test.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                drawer.draw_check_rec_area_tester();
            }
        });
        addButton("Check Recursion Area tester", draw_checkrecursion_test);

        Button draw_custom_polygon = new Button("Draw");
        draw_custom_polygon.setFont(font);
        draw_custom_polygon.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                drawer.draw_custom_polygon();
            }
        });
        addButton("Custom Polygon", draw_custom_polygon);

        Button draw_custom_polygon2 = new Button("Draw");
        draw_custom_polygon2.setFont(font);
        draw_custom_polygon2.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                drawer.draw_custom_polygon2();
            }
        });
        addButton("Custom Polygon Nr. 2", draw_custom_polygon2);

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
        Separator separator = new Separator();

        label.setFont(Font.font(null, FontWeight.BOLD, 14));
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

    //gets the vis_q_status
    public CheckBox get_vis_q_Status() {
        return vis_q_status;
    }

    //sets beta slider to zero
    public static void setBetaZero(){
        beta.valueProperty().setValue(0);
    }

    //returns settings width for Layout purpose
    public final int getSettingsWidth() {
        return this.toolbarWidth;
    }



}

