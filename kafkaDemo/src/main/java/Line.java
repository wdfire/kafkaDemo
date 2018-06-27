import pojo.Number;

import java.util.HashMap;
import java.util.Map;

public class Line {
    private String line;

    public Line(String line) {
        this.line = line;
    }

    public String getLine() {
        return line;
    }

    public void setLine(String line) {
        this.line = line;
    }
    public String getWords(){
        String[] split = this.line.split(" ");
        Number word = new Number();
        for (int i = 0;i< split.length;i++){
            switch(i){
                case 0:word.setInfo(split[1]);
                case 1:word.setDEV_ID(split[0]);
            }
        }
        return word.toString();
    }


}
