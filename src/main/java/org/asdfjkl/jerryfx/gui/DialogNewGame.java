package org.asdfjkl.jerryfx.gui;

import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.layout.*;
import javafx.stage.Modality;
import javafx.stage.Stage;
import jfxtras.styles.jmetro.JMetro;
import java.util.function.DoubleFunction;

public class DialogNewGame {

    DoubleFunction<Double> createStrengthValues = x -> {
        if(x <= 3.0) {
            return x;
        }
        if(x == 4.0) {
            return 5.0;
        }
        if(x == 5.0) {
            return 10.0;
        }
        if(x == 6.0) {
            return 15.0;
        }
        if(x == 7) {
            return 30.0;
        }
        return x;
    };

    Stage stage;
    boolean accepted = false;

    RadioButton rbEnterMoves = new RadioButton("Just Enter Moves");
    RadioButton rbComputer = new RadioButton("Computer");
    RadioButton rbWhite = new RadioButton("White");
    RadioButton rbBlack = new RadioButton("Black");

    Slider sliderStrength = new Slider();
    Slider sliderThinkTime = new Slider();

    int thinkTime = 3;
    int strength = 20;

    public boolean show(boolean isInternalEngine, int currEngineStrength, int currThinkTime) {

        strength = currEngineStrength;
        //thinkTime = currThinkTime;
        System.out.println("dlg: setting thinktime: "+thinkTime);

        stage = new Stage();
        stage.initModality(Modality.APPLICATION_MODAL);
        //stage.initStyle(StageStyle.UTILITY);

        Button btnOk = new Button();
        btnOk.setText("OK");

        Button btnCancel = new Button();
        btnCancel.setText("Cancel");
        Region spacer = new Region();

        HBox hbButtons = new HBox();
        hbButtons.getChildren().addAll(spacer, btnOk, btnCancel);
        hbButtons.setHgrow(spacer, Priority.ALWAYS);
        hbButtons.setSpacing(10);

        ToggleGroup radioGroupGame = new ToggleGroup();
        ToggleGroup radioGroupSide = new ToggleGroup();

        rbEnterMoves.setToggleGroup(radioGroupGame);
        rbComputer.setToggleGroup(radioGroupGame);

        rbWhite.setToggleGroup(radioGroupSide);
        rbBlack.setToggleGroup(radioGroupSide);

        rbEnterMoves.setSelected(true);
        rbWhite.setSelected(true);

        HBox hbGame = new HBox(rbEnterMoves, rbComputer);
        hbGame.setSpacing(10);
        hbGame.setPadding(new Insets(5, 20, 20, 0));

        HBox hbSide = new HBox(rbWhite, rbBlack);
        hbSide.setSpacing(10);
        hbSide.setPadding(new Insets(5, 20, 20, 0));

        sliderStrength.setMin(0);
        sliderStrength.setMax(20);

        sliderStrength.setBlockIncrement(1);
        sliderStrength.setMajorTickUnit(1);
        sliderStrength.setMinorTickCount(0);
        sliderStrength.setSnapToTicks(true);

        Label txtStrength = new Label();
        HBox hboxStrength = new HBox();
        hboxStrength.getChildren().addAll(sliderStrength, txtStrength);
        hboxStrength.setSpacing(20);

        sliderStrength.valueProperty().addListener(
                ((observableValue, number, t1) -> {
                    int i = t1.intValue();
                    strength = i;
                    txtStrength.setText("Level "+Integer.toString(strength));
                })
        );
        sliderStrength.setValue(20);
        sliderStrength.setValue(currEngineStrength);
        sliderStrength.setStyle("-show-value-on-interaction: false;");
        if(!isInternalEngine) {
            sliderStrength.setDisable(true);
        }

        sliderThinkTime.setMin(1);
        sliderThinkTime.setMax(7);

        sliderThinkTime.setBlockIncrement(1);
        sliderThinkTime.setMajorTickUnit(1);
        sliderThinkTime.setMinorTickCount(0);
        sliderThinkTime.setSnapToTicks(true);

        Label txtThinkTime = new Label();
        HBox hboxthinkTime = new HBox();
        hboxthinkTime.getChildren().addAll(sliderThinkTime, txtThinkTime);
        hboxthinkTime.setSpacing(20);

        sliderThinkTime.valueProperty().addListener(
                ((observableValue, number, t1) -> {
                    int i = t1.intValue();
                    switch(i) {
                        case 1:
                            thinkTime = 1;
                            break;
                        case 2:
                            thinkTime = 2;
                            break;
                        case 3:
                            thinkTime = 3;
                            break;
                        case 4:
                            thinkTime = 5;
                            break;
                        case 5:
                            thinkTime = 10;
                            break;
                        case 6:
                            thinkTime = 15;
                            break;
                        case 7:
                            thinkTime = 30;
                            break;
                    }
                    txtThinkTime.setText(Integer.toString(thinkTime)+ " sec(s)");
                })
        );

        sliderThinkTime.setStyle("-show-value-on-interaction: false;");
        System.out.println("dlg: setting thinktime: "+currThinkTime);
        if(currThinkTime <= 3) {
            sliderThinkTime.setValue(3);
            sliderThinkTime.setValue(currThinkTime);
        }
        if(currThinkTime == 5) {
            sliderThinkTime.setValue(4);
        }
        if(currThinkTime == 10) {
            sliderThinkTime.setValue(5);
        }
        if(currThinkTime == 15) {
            sliderThinkTime.setValue(6);
        }
        if(currThinkTime == 30) {
            sliderThinkTime.setValue(7);
        }

        VBox vbox = new VBox();
        vbox.getChildren().addAll(
                new Label("Select Enemy:"),
                hbGame,
                new Label("Chose your Side:"),
                hbSide,
                new Label("Computer Strength (internal engine only)"),
                hboxStrength,
                new Label("Computer's Time per Move"),
                hboxthinkTime,
                hbButtons);
        vbox.setSpacing(10);
        vbox.setPadding( new Insets(15, 25, 10, 25));

        btnOk.setOnAction(e -> {
            btnOkClicked();
        });

        btnCancel.setOnAction(e -> {
            btnCancelClicked();
        });

        Scene scene = new Scene(vbox);

        JMetro jMetro = new JMetro();
        jMetro.setScene(scene);
        stage.setScene(scene);
        stage.getIcons().add(new Image("icons/app_icon.png"));

        stage.showAndWait();

        return accepted;
    }

    private void btnOkClicked() {
        accepted = true;
        stage.close();
    }

    private void btnCancelClicked() {
        accepted = false;
        stage.close();
    }

}


