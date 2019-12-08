package tellko.smarthub.Entities;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ExpandableListDataPump {
    public static HashMap<String, List<String>> getData() {
        HashMap<String, List<String>> expandableListDetail = new HashMap<String, List<String>>();

        List<String> settings = new ArrayList<String>();
        settings.add("Change Password");


        expandableListDetail.put("SETTINGS", settings);
        return expandableListDetail;
    }
}
