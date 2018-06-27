package pojo;

import java.util.HashMap;
import java.util.Map;

public class Number {
    private String DEV_ID;
    private String info;

    public Number() {
    }

    public Number(String dev_id, String info) {
        DEV_ID = dev_id;
        this.info = info;

    }


    public String getInfo() {
        return info;
    }

    public void setDEV_ID(String DEV_ID) {
        this.DEV_ID = DEV_ID;
    }

    public void setInfo(String info) {
        this.info = info;
    }

    public String getDEV_ID() {
        return DEV_ID;
    }

    @Override
    public String toString() {
        return "DEV_ID:"+getDEV_ID()+","+"info:"+getInfo();
    }
     public Map<String,String> getMap(){
        Map map = new HashMap();
        map.put("DEV_ID",getDEV_ID());
        map.put("info",getInfo());
        return map;
     }

}
