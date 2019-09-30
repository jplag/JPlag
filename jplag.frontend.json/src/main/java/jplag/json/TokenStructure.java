package jplag.json;

import java.util.Hashtable;
import java.util.Map;
import java.util.Set;

public class TokenStructure {
    protected Hashtable<String, Integer> table = new Hashtable<>();
    protected String[] reverseMapping = null;
    protected int serial = JsonTokenConstants.DYNAMIC_START; // 0 is FILE_END token

    protected void createReverseMapping() {
        if(this.reverseMapping == null) {
            this.reverseMapping = new String[this.table.size() + 1];
            for (Map.Entry<String, Integer> entry : table.entrySet()) {
                int type = (entry.getValue()).intValue();
                String text = entry.getKey();
                this.reverseMapping[type] = text;
            }
        }
    }

    public Set<Map.Entry<String, Integer>> entrySet() {
        return this.table.entrySet();
    }

    public String tableStatus() {
        return "Size of table:  " + this.table.size();
    }
}

