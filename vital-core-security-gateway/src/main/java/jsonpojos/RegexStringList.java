package jsonpojos;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class RegexStringList extends ArrayList<String>
{
	private static final long serialVersionUID = 4559010279572173308L;

	public boolean contains(List<Object> o)
    {
		for (int i = 0; i < o.size(); i++) {
	        if ((o.get(i) instanceof String)) {
		        String s = (String) o.get(i);
		        Iterator<String> iter = iterator();
		        while (iter.hasNext()) {
		            String iStr = iter.next();
		            if (s.matches(iStr)) {
		                return true;
		            }
		        }
	        }
		}
		return false;
    }
}
