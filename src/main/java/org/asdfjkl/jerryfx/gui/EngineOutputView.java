package org.asdfjkl.jerryfx.gui;

import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;

public class EngineOutputView {

    private TextFlow txtEngineOut;

    private Text engineId;
    private Text depth;
    private Text nps;
    private Text pv1;
    private Text pv2;
    private Text pv3;
    private Text pv4;

    private boolean isEnabled = true;

    public EngineOutputView(TextFlow txtEngineOut) {
        this.txtEngineOut = txtEngineOut;

        this.engineId = new Text("Stockfish (internal)");
        this.depth = new Text("");
        this.nps = new Text("");
        this.pv1 = new Text("");
        this.pv2 = new Text("");
        this.pv3 = new Text("");
        this.pv4 = new Text("");

        Text spacer1 = new Text("     ");
        Text spacer2 = new Text("     ");

        this.txtEngineOut.getChildren().addAll(
                this.engineId,
                spacer1,
                this.depth,
                spacer2,
                this.nps,
                new Text(System.lineSeparator()),
                new Text(System.lineSeparator()),
                this.pv1,
                new Text(System.lineSeparator()),
                this.pv2,
                new Text(System.lineSeparator()),
                this.pv3,
                new Text(System.lineSeparator()),
                this.pv4);

        //this.txtEngineOut.getChildren().addAll(currentEval);
    }

    public void enableOutput() { isEnabled = true; }

    public void disableOutput() {
        isEnabled = false;
        clearOutput();
    }

    public boolean isEnabled() { return isEnabled; }

    private void clearOutput() {

        engineId.setText("");
        depth.setText("");
        nps.setText("");
        pv1.setText("");
        pv2.setText("");
        pv3.setText("");
        pv4.setText("");
    }

    public void setText(String info) {

        if(isEnabled) {

            //pv1.setText(info);
            // | id (Level MAX) | current Move + depth  |  nps | eval+line pv1 | .. pv2 | ...pv3 | ...pv4
            String[] infos = info.split("\\|");

            if (infos.length > 1 && !infos[1].isEmpty()) {
                engineId.setText(infos[1]);
            }
            if (infos.length > 2 && !infos[2].isEmpty()) {
                depth.setText(infos[2]);
            }
            if (infos.length > 3 && !infos[3].isEmpty()) {
                nps.setText(infos[3]);
            }
            if (infos.length > 4 && !infos[4].isEmpty()) {
                pv1.setText(infos[4]);
            }
            if (infos.length > 5 && !infos[5].isEmpty()) {
                pv2.setText(infos[5]);
            } else {
                pv2.setText("");
            }
            if (infos.length > 6 && !infos[6].isEmpty()) {
                pv3.setText(infos[6]);
            } else {
                pv3.setText("");
            }
            if (infos.length > 7 && !infos[7].isEmpty()) {
                pv4.setText(infos[7]);
            } else {
                pv4.setText("");
            }
        }
    }
}
