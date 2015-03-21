/*
 * Created on 27.01.2005
 */
package jplag.text;

import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

/**
 * @author bikiri
 */
public class TokenStructure {
    protected Hashtable table = new Hashtable();
    protected String[] reverseMapping = null;
    protected int serial = 1; // 0 is FILE_END token

    protected void createReverseMapping() {
        if(this.reverseMapping == null) {
            this.reverseMapping = new String[this.table.size() + 1];
            for(Iterator iter = table.entrySet().iterator(); iter.hasNext(); ) {
                Map.Entry entry = (Map.Entry) iter.next();
                int type = ((Integer) entry.getValue()).intValue();
                String text = (String) entry.getKey();
                this.reverseMapping[type] = text;
            }
        }
    }

    public Set entrySet() {
        return this.table.entrySet();
    }

    public String tableStatus() {
        return "Size of table:  " + this.table.size();
    }
}
