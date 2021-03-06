package org.asdfjkl.jerryfx.gui;

import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import jfxtras.styles.jmetro.JMetro;
import org.asdfjkl.jerryfx.lib.CONSTANTS;

import java.util.HashMap;
import java.util.Map;

public class DialogEditGameData {

    Stage stage;
    HashMap<String, String> pgnHeaders = new HashMap<>();
    TextField site = new TextField();
    TextField date = new TextField();
    TextField round = new TextField();
    TextField event = new TextField();
    TextField white = new TextField();
    TextField black = new TextField();
    TextField eco = new TextField();
    boolean accepted = false;
    int gameResult = -1;
    RadioButton rbWhiteWin = new RadioButton("1-0");
    RadioButton rbBlackWin = new RadioButton("0-1");
    RadioButton rbDraw = new RadioButton("1/2-1/2");
    RadioButton rbUndecided = new RadioButton("*");

    public boolean show(HashMap<String, String> pgnHeaders, int gameResult) {

        for (Map.Entry<String, String> entry : pgnHeaders.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();
            pgnHeaders.put(key,value);
        }
        this.gameResult = gameResult;

        stage = new Stage();
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.initStyle(StageStyle.UTILITY);

        Button btnOk = new Button();
        btnOk.setText("OK");

        Button btnCancel = new Button();
        btnCancel.setText("Cancel");
        Region spacer = new Region();

        HBox hbButtons = new HBox();
        hbButtons.getChildren().addAll(spacer, btnOk, btnCancel);
        hbButtons.setHgrow(spacer, Priority.ALWAYS);
        hbButtons.setSpacing(10);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 20, 0, 20));

        site.setPromptText("Site");
        if(pgnHeaders.get("Site") != null) {
            site.setText(pgnHeaders.get("Site"));
        }

        date.setPromptText("Date");
        if(pgnHeaders.get("Date") != null) {
            date.setText(pgnHeaders.get("Date"));
        }

        round.setPromptText("Round");
        if(pgnHeaders.get("Round") != null) {
            round.setText(pgnHeaders.get("Round"));
        }

        event.setPromptText("Event");
        if(pgnHeaders.get("Event") != null) {
            event.setText(pgnHeaders.get("Event"));
        }

        white.setPromptText("White");
        if(pgnHeaders.get("White") != null) {
            white.setText(pgnHeaders.get("White"));
        }

        black.setPromptText("Black");
        if(pgnHeaders.get("Black") != null) {
            black.setText(pgnHeaders.get("Black"));
        }

        eco.setPromptText("Eco");
        if(pgnHeaders.get("Eco") != null) {
            eco.setText(pgnHeaders.get("Eco"));
        }

        grid.add(new Label("Site:"), 0, 0);
        grid.add(site, 1, 0);
        grid.add(new Label("Date:"), 0, 1);
        grid.add(date, 1, 1);
        grid.add(new Label("Round:"), 0, 2);
        grid.add(round, 1, 2);
        grid.add(new Label("Event:"), 0, 3);
        grid.add(event, 1, 3);
        grid.add(new Label("White:"), 0, 4);
        grid.add(white, 1, 4);
        grid.add(new Label("Black:"), 0, 5);
        grid.add(black, 1, 5);
        grid.add(new Label("Eco:"), 0, 6);
        grid.add(eco, 1, 6);

        grid.add(new Label("Result:"), 0,8);

        ToggleGroup radioGroup = new ToggleGroup();

        rbWhiteWin.setToggleGroup(radioGroup);
        rbBlackWin.setToggleGroup(radioGroup);
        rbDraw.setToggleGroup(radioGroup);
        rbUndecided.setToggleGroup(radioGroup);

        if(gameResult == CONSTANTS.RES_WHITE_WINS) {
            rbWhiteWin.setSelected(true);
        }
        if(gameResult == CONSTANTS.RES_BLACK_WINS) {
            rbBlackWin.setSelected(true);
        }
        if(gameResult == CONSTANTS.RES_DRAW) {
            rbDraw.setSelected(true);
        }
        if(gameResult == CONSTANTS.RES_UNDEF || gameResult == CONSTANTS.RES_ANY) {
            rbUndecided.setSelected(true);
        }

        HBox hbox = new HBox(rbWhiteWin, rbBlackWin, rbDraw, rbUndecided);
        hbox.setSpacing(10);
        hbox.setPadding(new Insets(0, 20, 20, 20));

        VBox vbox = new VBox();
        vbox.getChildren().addAll(grid, hbox, hbButtons);
        vbox.setSpacing(10);
        vbox.setPadding( new Insets(10));

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
        stage.showAndWait();

        return accepted;
    }

    private void btnOkClicked() {
        accepted = true;
        pgnHeaders.put("Site", site.getText());
        pgnHeaders.put("Date", date.getText());
        pgnHeaders.put("Round", round.getText());
        pgnHeaders.put("Event", event.getText());
        pgnHeaders.put("White", white.getText());
        pgnHeaders.put("Black", black.getText());
        pgnHeaders.put("Eco", eco.getText());

        if(rbWhiteWin.isSelected()) {
            gameResult = CONSTANTS.RES_WHITE_WINS;
        }
        if(rbBlackWin.isSelected()) {
            gameResult = CONSTANTS.RES_BLACK_WINS;
        }
        if(rbDraw.isSelected()) {
            gameResult = CONSTANTS.RES_DRAW;
        }
        if(rbUndecided.isSelected()) {
            gameResult = CONSTANTS.RES_UNDEF;
        }

        stage.close();
    }

    private void btnCancelClicked() {
        accepted = false;
        stage.close();
    }

}
