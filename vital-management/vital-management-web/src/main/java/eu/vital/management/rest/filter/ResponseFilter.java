package eu.vital.management.rest.filter;

import java.io.IOException;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerResponseContext;
import javax.ws.rs.container.ContainerResponseFilter;
import javax.ws.rs.ext.Provider;

@Provider
public class ResponseFilter implements ContainerResponseFilter {

    @Override
    public void filter(final ContainerRequestContext requestContext, final ContainerResponseContext cres) throws IOException {
        String setCookie;

        if((setCookie = requestContext.getHeaderString("Set-Cookie")) != null) {
            String domain = cres.getLocation().getHost();
            Pattern pattern = Pattern.compile("^[^.]*(..*)$");
            Matcher matcher = pattern.matcher(domain);
            if (matcher.find()) {
                domain = matcher.group(1);
            }
            cres.getHeaders().remove("Set-Cookie");
            cres.getHeaders().add("Set-Cookie", setCookie.replaceAll("Domain=[^;]*", "Domain=" + domain));
        }
    }
}

