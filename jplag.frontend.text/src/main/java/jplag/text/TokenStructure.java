/*
 * Created on 27.01.2005
 */
package jplag.text;

import java.util.Hashtable;
import java.util.Map.Entry;
import java.util.Set;

/**
 * @author bikiri
 */
public class TokenStructure {
	protected Hashtable<String, Integer> table = new Hashtable<>();
    protected String[] reverseMapping = null;
    protected int serial = 1; // 0 is FILE_END token

    protected void createReverseMapping() {
        if(this.reverseMapping == null) {
            this.reverseMapping = new String[this.table.size() + 1];
			for (Entry<String, Integer> entry : table.entrySet()) {
				int type = (entry.getValue()).intValue();
				String text = entry.getKey();
                this.reverseMapping[type] = text;
            }
        }
    }

	public Set<Entry<String, Integer>> entrySet() {
        return this.table.entrySet();
    }

    public String tableStatus() {
        return "Size of table:  " + this.table.size();
    }
}
