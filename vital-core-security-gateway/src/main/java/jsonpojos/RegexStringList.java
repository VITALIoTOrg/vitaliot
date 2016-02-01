package jsonpojos;

import java.util.ArrayList;
import java.util.Iterator;

public class RegexStringList extends ArrayList<String>
{
	private static final long serialVersionUID = 4559010279572173308L;

	public boolean contains(Object o)
    {
        if (!(o instanceof String)) {
            return false;
        }
        String s = (String) o;
        Iterator<String> iter = iterator();
        while (iter.hasNext()) {
            String iStr = iter.next();
            if (s.matches(iStr)) {
                return true;
            }
        }
        return false;
    }
}
