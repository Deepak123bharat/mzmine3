/*
 * Copyright 2006-2020 The MZmine Development Team
 *
 * This file is part of MZmine.
 *
 * MZmine is free software; you can redistribute it and/or modify it under the terms of the GNU
 * General Public License as published by the Free Software Foundation; either version 2 of the
 * License, or (at your option) any later version.
 *
 * MZmine is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even
 * the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General
 * Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with MZmine; if not,
 * write to the Free Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301
 * USA
 */

package io.github.mzmine.util.interpolatinglookuppaintscale;

import java.awt.*;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.TreeMap;
import java.util.logging.Logger;

import io.github.mzmine.main.MZmineCore;

import io.github.mzmine.taskcontrol.TaskStatus;
import io.github.mzmine.util.ExitCode;

import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import javafx.util.Callback;

import static org.openscience.jmol.app.webexport.WebExport.dispose;

public class InterpolatingLookupPaintScaleSetupDialogController extends Stage implements Initializable {


    private static final long serialVersionUID = 1L;

    public static final int VALUEFIELD_COLUMNS = 4;

    private static final TreeMap<Double, Color> lookupTable = new TreeMap<Double, Color>();

    private Logger logger = Logger.getLogger(this.getClass().getName());

    private static Scene mainScene;

    private ExitCode exitCode = ExitCode.CANCEL;

    private javafx.scene.paint.Color bColor;

    private static ObservableList<InterpolatingLookupPaintScaleRow> obTableList = FXCollections.observableArrayList();


    // Load the window FXML
    FXMLLoader loader = new FXMLLoader((getClass().getResource("InterpolatingLookupPaintScaleSetupDialog.fxml")));


    //default constructor
    public InterpolatingLookupPaintScaleSetupDialogController(){
        logger.info("defalut constructor");
    }


    public InterpolatingLookupPaintScaleSetupDialogController(Object parent,
                                                              InterpolatingLookupPaintScale paintScale) {



        Double[] lookupValues = paintScale.getLookupValues();
        for (Double lookupValue : lookupValues) {
            Color color = (Color) paintScale.getPaint(lookupValue);
            InterpolatingLookupPaintScaleRow ir = new InterpolatingLookupPaintScaleRow(lookupValue,color);
            obTableList.add(ir);
            lookupTable.put(lookupValue, color);
        }

        try {
            Parent root = loader.load();
            mainScene  = new Scene(root);
        } catch (IOException e) {
            e.printStackTrace();
        }



        logger.info("cunstroctor of second");



        setScene(mainScene);

    }

    // Load the window FXML
    @FXML
    private AnchorPane main;

    @FXML
    private AnchorPane panelControlsAndList;

    @FXML
    private AnchorPane panelValueAndColor;

    @FXML
    private Label labelValue;

    @FXML
    private TextField fieldValue;

    @FXML
    private Button buttonColor;

    @FXML
    private ColorPicker cp;

    @FXML
    private Button buttonAddModify;

    @FXML
    private Button buttonDelete;

    @FXML
    private AnchorPane panelOKCancelButton;

    @FXML
    private Button buttonOK;

    @FXML
    private Button buttonCancel;

    @FXML
    private ScrollPane scrollpaneLookupValues;

    @FXML
    private TableView<InterpolatingLookupPaintScaleRow> tableLookupValues;

    @FXML
    private TableColumn<InterpolatingLookupPaintScaleRow, Double> Value;

    @FXML
    private TableColumn<InterpolatingLookupPaintScaleRow, Color> Color;


    @Override
    public void initialize(URL location, ResourceBundle resources) {

        logger.info("initialize_of_controller");

        Callback factory = new Callback<TableColumn<InterpolatingLookupPaintScaleRow, Color>, TableCell<InterpolatingLookupPaintScaleRow, Color>>() {

            private int columns = tableLookupValues.getColumns().size();

            @Override
            public TableCell<InterpolatingLookupPaintScaleRow, Color> call(TableColumn<InterpolatingLookupPaintScaleRow, Color> param) {
                return new TableCell<InterpolatingLookupPaintScaleRow, Color>() {
                    @Override
                    protected void updateItem(Color item, boolean empty) {
                        super.updateItem(item, empty);

                        // assign item's toString value as text
                        if (empty || item == null) {
                            setText(null);
                        } else {
                            setText(item.toString());

                                int r = item.getRed();
                                int g = item.getGreen();
                                int b = item.getBlue();
                                String hex = String.format("#%02x%02x%02x", r, g, b);
                                this.setStyle("-fx-background-color: " + hex + ";");

                        }
                    }

                };
            }

        };


        Value.setCellValueFactory(cell-> new ReadOnlyObjectWrapper<>(cell.getValue().getKey()));
        Color.setCellValueFactory(cell-> new ReadOnlyObjectWrapper<>(cell.getValue().getValue()));
        Color.setCellFactory(factory);






        logger.info(obTableList.size()+" size of oblist");
        logger.info(obTableList.toString() + " string of array");

        tableLookupValues.setItems(obTableList);

        //Value.setCellValueFactory(new PropertyValueFactory<>("Value"));

        //
    }

    public void actionPerformed(ActionEvent event) {
        Object src = event.getSource();


        if (src == cp) {
            logger.info("select color is called");
            javafx.scene.paint.Color color = cp.getValue();
            bColor = color;
            logger.info(bColor.toString()+" color of background Hello");
        }

        if (src == buttonAddModify) {
            logger.info("button addmodify is pressed");
            if (fieldValue.getText() == null) {
                MZmineCore.getDesktop().displayMessage("Please enter value first.");
                return;
            }

            Double d = Double.parseDouble(fieldValue.getText());

            java.awt.Color awtColor = new java.awt.Color((float) bColor.getRed(),
                    (float) bColor.getGreen(),
                    (float) bColor.getBlue(),
                    (float) bColor.getOpacity());

            lookupTable.put(d, awtColor);
            InterpolatingLookupPaintScaleRow ir = new InterpolatingLookupPaintScaleRow(d,awtColor);
            obTableList.add(ir);
            scrollpaneLookupValues.requestLayout();
        }

        if (src == buttonDelete) {
            InterpolatingLookupPaintScaleRow selected = tableLookupValues.getSelectionModel().getSelectedItem();
            if (selected != null) {
                obTableList.remove(selected);
            }
            logger.info("button delete is pressed");
            scrollpaneLookupValues.requestLayout();
        }

        if (src == buttonOK) {
            exitCode = ExitCode.OK;
            logger.info("ok button pressed");
            dispose();
        }
        if (src == buttonCancel) {
            exitCode = ExitCode.CANCEL;
            logger.info("cancel button pressed");
            dispose();
        }
    }

    public ExitCode getExitCode() {
        return exitCode;
    }

    public InterpolatingLookupPaintScale getPaintScale() {
        InterpolatingLookupPaintScale paintScale = new InterpolatingLookupPaintScale();
        for (Double value : lookupTable.keySet()) {
            paintScale.add(value, lookupTable.get(value));
        }
        return paintScale;
    }

}
