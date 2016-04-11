package utils;

import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;

/**
 * Created by l.bracco on 18/11/2015.
 */

public class HttpCommonClient
{
    private static HttpCommonClient instance;
    public PoolingHttpClientConnectionManager cm;
    public CloseableHttpClient httpc;

    private HttpCommonClient()
    {
        cm = new PoolingHttpClientConnectionManager();
        cm.setMaxTotal(200);
        cm.setDefaultMaxPerRoute(100);
        httpc = HttpClientBuilder.create().setConnectionManager(cm).build();
    }

    public static HttpCommonClient getInstance()
    {
        if(instance == null)
            instance = new HttpCommonClient();
        return instance;
    }
}
