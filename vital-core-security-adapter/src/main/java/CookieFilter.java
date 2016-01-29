import java.io.IOException;
import java.util.Iterator;
import java.util.List;

import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.container.PreMatching;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.Provider;

import utils.ConfigReader;

@Provider
@PreMatching
public class CookieFilter implements ContainerRequestFilter {

	@Override
	public void filter(ContainerRequestContext request) throws IOException {
		MultivaluedMap<String, String> headers;
		String cookieHeader = "";
		
		ConfigReader configReader = ConfigReader.getInstance();
		
		String ssoToken = configReader.get(ConfigReader.SSO_TOKEN);
		String altToken = configReader.get(ConfigReader.ALT_TOKEN);
		
		headers = request.getHeaders();
		if (headers != null) {
			List<String> cookies = headers.get("Cookie");
			if (cookies != null && !cookies.isEmpty()) {
				Iterator<String> iter = cookies.listIterator();
				while (iter.hasNext()) {
					String cookie = iter.next();
					cookie = cookie.replace(ssoToken, "ssoToken");
					cookie = cookie.replace(altToken, "altToken");
					cookieHeader = cookieHeader + cookie + ";";
				}
				headers.remove("Cookie");
				headers.add("Cookie", cookieHeader);
			}
		}
	}
}
