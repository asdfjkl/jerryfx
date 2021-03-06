package org.asdfjkl.jerryfx.gui;

import java.util.ArrayList;

public class Engine {

    private String name = "";
    private String path = "";

    private boolean isInternal = false;

    public ArrayList<EngineOption> options = new ArrayList<>();

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getPath() { return path; }
    public void setPath(String path) { this.path = path; }

    public boolean isInternal() { return isInternal; }
    public void setInternal(boolean internal) { this.isInternal = internal; }

    public String writeToString() {
        StringBuilder sb = new StringBuilder();
        sb.append(name);
        sb.append('|');
        sb.append(path);
        for(EngineOption enOpt : options) {
            sb.append('|');
            sb.append(enOpt.toUciOptionString());
            if(enOpt.type == EngineOption.EN_OPT_TYPE_CHECK) {
                if(enOpt.checkStatusValue) {
                    sb.append("|true");
                } else {
                    sb.append("|false");
                }
            }
            if(enOpt.type == EngineOption.EN_OPT_TYPE_SPIN) {
                sb.append("|");
                sb.append(enOpt.spinValue);
            }
            if(enOpt.type == EngineOption.EN_OPT_TYPE_STRING) {
                sb.append("|");
                sb.append(enOpt.stringValue);
            }
            if(enOpt.type == EngineOption.EN_OPT_TYPE_COMBO) {
                sb.append("|");
                sb.append(enOpt.comboValue);
            }
        }
        return sb.toString();
    }

    public void restoreFromString(String s) {

        options.clear();
        String[] values = s.split("\\|");
        if(values.length > 0) {
            name = values[0];
        }
        if(values.length > 1) {
            path = values[1];
        }
        for(int i=2;i< values.length;i+=2) {
            if(i+1 < values.length) {
                EngineOption option = new EngineOption();
                String uciOptionString = values[i];
                System.out.println("recovered: "+uciOptionString);
                option.parseUciOptionString(uciOptionString);
                if(option.type == EngineOption.EN_OPT_TYPE_CHECK) {
                    if(values[i+1].equals("true")) {
                        option.checkStatusValue = true;
                    } else {
                        option.checkStatusValue = false;
                    }
                }
                if(option.type == EngineOption.EN_OPT_TYPE_SPIN) {
                    option.spinValue = Integer.parseInt(values[i+1]);
                }
                if(option.type == EngineOption.EN_OPT_TYPE_STRING) {
                    option.stringValue = values[i+1];
                }
                if(option.type == EngineOption.EN_OPT_TYPE_COMBO) {
                    option.comboValue = values[i+1];
                }
                options.add(option);
            }
        }
    }

    public Engine makeCopy() {

        Engine copy = new Engine();
        copy.name = this.name;
        copy.path = this.path;
        copy.isInternal = this.isInternal;

        for(EngineOption option : options) {
            copy.options.add(option);
        }

        return copy;
    }


}
