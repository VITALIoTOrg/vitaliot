package eu.vital.management.rest;

import eu.vital.management.security.SecurityService;
import org.apache.commons.httpclient.Header;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;

import javax.inject.Inject;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;

@WebServlet(urlPatterns = "/security/*", asyncSupported = true)
public class SecurityRestService extends HttpServlet {

	@Inject
	SecurityService securityService;

	public void doGet(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) throws IOException, ServletException {
		// Create a GET request
		GetMethod getMethodProxyRequest = new GetMethod(getProxyURL(httpServletRequest));
		// Forward the request headers
		setProxyRequestHeaders(httpServletRequest, getMethodProxyRequest);
		// Execute the proxy request
		executeProxyRequest(getMethodProxyRequest, httpServletRequest, httpServletResponse);
	}

	public void doPost(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) throws IOException, ServletException {
		// Create a standard POST request
		PostMethod postMethodProxyRequest = new PostMethod(getProxyURL(httpServletRequest));
		// Forward the request headers
		setProxyRequestHeaders(httpServletRequest, postMethodProxyRequest);
		handleStandardPost(postMethodProxyRequest, httpServletRequest);
		// Execute the proxy request
		executeProxyRequest(postMethodProxyRequest, httpServletRequest, httpServletResponse);
	}

	private void handleStandardPost(PostMethod postMethodProxyRequest, HttpServletRequest httpServletRequest) {
		// Get the client POST data as a Map
		Map<String, String[]> mapPostParameters = (Map<String, String[]>) httpServletRequest.getParameterMap();
		// Create a List to hold the NameValuePairs to be passed to the PostMethod
		List<NameValuePair> listNameValuePairs = new ArrayList<>();
		// Iterate the parameter names
		for (String stringParameterName : mapPostParameters.keySet()) {
			// Iterate the values for each parameter name
			String[] stringArrayParameterValues = mapPostParameters.get(stringParameterName);
			for (String stringParamterValue : stringArrayParameterValues) {
				// Create a NameValuePair and store in list
				NameValuePair nameValuePair = new NameValuePair(stringParameterName, stringParamterValue);
				listNameValuePairs.add(nameValuePair);
			}
		}
		// Set the proxy request POST data
		postMethodProxyRequest.setRequestBody(listNameValuePairs.toArray(new NameValuePair[] {}));
	}

	private void executeProxyRequest(HttpMethod httpMethodProxyRequest, HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) throws IOException, ServletException {
		// Create a default HttpClient
		HttpClient httpClient = new HttpClient();

		httpMethodProxyRequest.setFollowRedirects(false);
		// Execute the request
		int intProxyResponseCode = httpClient.executeMethod(httpMethodProxyRequest);
		// Pass the response code back to the client
		httpServletResponse.setStatus(intProxyResponseCode);
		// Pass response headers back to the client
		Header[] headerArrayResponse = httpMethodProxyRequest.getResponseHeaders();
		for (Header header : headerArrayResponse) {
			httpServletResponse.setHeader(header.getName(), header.getValue());
		}
		// Send the content to the client
		InputStream inputStreamProxyResponse = httpMethodProxyRequest.getResponseBodyAsStream();
		BufferedInputStream bufferedInputStream = new BufferedInputStream(inputStreamProxyResponse);
		OutputStream outputStreamClientResponse = httpServletResponse.getOutputStream();
		int intNextByte;
		while ((intNextByte = bufferedInputStream.read()) != -1) {
			outputStreamClientResponse.write(intNextByte);
		}
	}

	private void setProxyRequestHeaders(HttpServletRequest httpServletRequest, HttpMethod httpMethodProxyRequest) {
		// Get an Enumeration of all of the header names sent by the client
		Enumeration enumerationOfHeaderNames = httpServletRequest.getHeaderNames();
		while (enumerationOfHeaderNames.hasMoreElements()) {
			String stringHeaderName = (String) enumerationOfHeaderNames.nextElement();
			if (stringHeaderName.equalsIgnoreCase("Content-Length"))
				continue;
			// As per the Java Servlet API 2.5 documentation:
			//		Some headers, such as Accept-Language can be sent by clients
			//		as several headers each with a different value rather than
			//		sending the header as a comma separated list.
			// Thus, we get an Enumeration of the header values sent by the client
			Enumeration enumerationOfHeaderValues = httpServletRequest.getHeaders(stringHeaderName);
			while (enumerationOfHeaderValues.hasMoreElements()) {
				String stringHeaderValue = (String) enumerationOfHeaderValues.nextElement();
				// In case the proxy host is running multiple virtual servers,
				// rewrite the Host header to ensure that we get content from
				// the correct virtual server
				if (stringHeaderName.equalsIgnoreCase("Host")) {
					URL securityProxyUrl = securityService.getSecurityProxyUrl();
					stringHeaderValue = securityProxyUrl.getHost() + ":" + securityProxyUrl.getPort();
				}
				Header header = new Header(stringHeaderName, stringHeaderValue);
				// Set the same header on the proxy request
				httpMethodProxyRequest.setRequestHeader(header);
			}
		}
	}

	private String getProxyURL(HttpServletRequest httpServletRequest) {
		// Set the protocol to HTTP
		String securityProxyUrl = securityService.getSecurityProxyUrl().toString();

		// Handle the path given to the servlet
		securityProxyUrl += httpServletRequest.getPathInfo();
		// Handle the query string
		if (httpServletRequest.getQueryString() != null) {
			securityProxyUrl += "?" + httpServletRequest.getQueryString();
		}
		return securityProxyUrl;
	}
}
