package eu.vital.management.rest.proxy;

import org.apache.commons.io.IOUtils;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public abstract class ProxyRestService extends HttpServlet {

	public void doGet(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) throws IOException, ServletException {
		// Execute the proxy request
		executeProxyRequest(httpServletRequest, httpServletResponse);
	}

	public void doPost(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) throws IOException, ServletException {
		executeProxyRequest(httpServletRequest, httpServletResponse);
	}

	public void doPut(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) throws IOException, ServletException {
		executeProxyRequest(httpServletRequest, httpServletResponse);
	}

	public void doDelete(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) throws IOException, ServletException {
		executeProxyRequest(httpServletRequest, httpServletResponse);
	}

	private void executeProxyRequest(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) throws IOException, ServletException {
		// Create a default HttpClient
		URL url = new URL(getProxyURL(httpServletRequest));
		HttpURLConnection httpProxyRequest = (HttpURLConnection) url.openConnection();
		try {
			httpProxyRequest.setRequestMethod(httpServletRequest.getMethod());
			copyProxyRequestHeaders(httpServletRequest, httpProxyRequest);
			httpProxyRequest.setFollowRedirects(false);

			// Check if POST or PUT and copy data from input
			if (httpProxyRequest.getRequestMethod().equalsIgnoreCase("POST") || httpProxyRequest.getRequestMethod().equalsIgnoreCase("PUT")) {
				httpProxyRequest.setDoOutput(true);
				OutputStream httpProxyRequestOutputStream = httpProxyRequest.getOutputStream();
				// Send Data from httpServletRequest => httpProxyRequest
				InputStream httpServletRequestInputStream = httpServletRequest.getInputStream();
				IOUtils.copy(httpServletRequestInputStream, httpProxyRequestOutputStream);
			}

			// Pass the response code back to the client
			int responseStatus = httpProxyRequest.getResponseCode();
			httpServletResponse.setStatus(responseStatus);

			// Pass response headers back to the client
			Map<String, List<String>> headerArrayResponse = httpProxyRequest.getHeaderFields();
			for (String headerName : headerArrayResponse.keySet()) {
				if (headerName != null) {
                    List<String> headerValues = headerArrayResponse.get(headerName);
                    if (headerName.toLowerCase().equals("set-cookie")) {
                        String domain = httpServletRequest.getRequestURL().toString();
                        Pattern pattern = Pattern.compile("^[^:]*:[^.]*(.[^:/]*).*$");
                        Matcher matcher = pattern.matcher(domain);
                        if (matcher.find()) {
                            domain = matcher.group(1);
                        }
                        httpServletResponse.addHeader(headerName, headerValues.toString().replaceAll("Domain=[^;]*", "Domain=" + domain));
                    } else {
                        httpServletResponse.addHeader(headerName, headerValues.toString());
                    }
				}
			}
			// Send the content to the client httpProxyRequest => httpServletResponse
            InputStream httpProxyRequestInputStream;
            if (responseStatus >= 200 && responseStatus <= 299)
    			httpProxyRequestInputStream = httpProxyRequest.getInputStream();
            else
                httpProxyRequestInputStream = httpProxyRequest.getErrorStream();
			OutputStream httpServletResponseOutputStream = httpServletResponse.getOutputStream();
			IOUtils.copy(httpProxyRequestInputStream, httpServletResponseOutputStream);

		} finally {
			httpProxyRequest.disconnect();
		}
	}

	private void copyProxyRequestHeaders(HttpServletRequest httpServletRequest, HttpURLConnection httpMethodProxyRequest) {
		// Get an Enumeration of all of the header names sent by the client
		Enumeration enumerationOfHeaderNames = httpServletRequest.getHeaderNames();
		while (enumerationOfHeaderNames.hasMoreElements()) {
			String stringHeaderName = (String) enumerationOfHeaderNames.nextElement();
			if (stringHeaderName.equalsIgnoreCase("Content-Length")) {
				continue;
			}
			Enumeration enumerationOfHeaderValues = httpServletRequest.getHeaders(stringHeaderName);
			while (enumerationOfHeaderValues.hasMoreElements()) {
				String stringHeaderValue = (String) enumerationOfHeaderValues.nextElement();
				if (stringHeaderName.equalsIgnoreCase("Host")) {
					URL securityProxyUrl = getRemoteUrl();
					stringHeaderValue = securityProxyUrl.getHost() + ":" + securityProxyUrl.getPort();
				}
				// Set the same header on the proxy request
				httpMethodProxyRequest.addRequestProperty(stringHeaderName, stringHeaderValue);
			}
		}
	}

	private String getProxyURL(HttpServletRequest httpServletRequest) {
		// Set the protocol to HTTP
		String securityProxyUrl = getRemoteUrl().toString();

		// Handle the path given to the servlet
		securityProxyUrl += httpServletRequest.getPathInfo().replaceAll(" ", "%20");
		// Handle the query string
		if (httpServletRequest.getQueryString() != null) {
			securityProxyUrl += "?" + httpServletRequest.getQueryString();
		}
		return securityProxyUrl;
	}

	protected abstract URL getRemoteUrl();
}
