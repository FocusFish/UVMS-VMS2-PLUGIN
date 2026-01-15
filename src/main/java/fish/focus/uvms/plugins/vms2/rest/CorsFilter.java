package fish.focus.uvms.plugins.vms2.rest;

import javax.annotation.Resource;
import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@WebFilter(asyncSupported = true, urlPatterns = {"/*"})
public class CorsFilter implements Filter {
    /**
     * {@code corsOriginRegex} is valid for given host/origin names/IPs and any range of sub domains.
     * <p>
     * localhost:[2]8080/9001
     * 127.0.0.1:[2]8080/9001
     * 192.168.***.***:[2]8080
     * liaswf16[t,u,d]:[2]8080
     * havochvatten.se:[2]8080
     */
    @Resource(lookup = "java:global/cors_allowed_host_regex")
    private String corsOriginRegex;

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse res, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest httpServletRequest = (HttpServletRequest) request;
        String origin = httpServletRequest.getHeader("ORIGIN");

        String remoteAddress = getClientIpInfo(request);
        
        if(!validateHost(remoteAddress)) {
        	HttpServletResponse response = (HttpServletResponse) res;
        	response.sendError(401, "IP not allowed");
        	return;
        }
        
        if(origin != null && validateHost(origin)) {
            HttpServletResponse response = (HttpServletResponse) res;
            response.setHeader(Constants.ACCESS_CONTROL_ALLOW_ORIGIN, origin);
            response.setHeader(Constants.ACCESS_CONTROL_ALLOW_METHODS, Constants.ACCESS_CONTROL_ALLOWED_METHODS);
            response.setHeader(Constants.ACCESS_CONTROL_ALLOW_HEADERS, Constants.ACCESS_CONTROL_ALLOW_HEADERS_ALL);
            
            if (httpServletRequest.getMethod().equals("OPTIONS")) {
                response.setStatus(200);
                return;
            }
            
        }
        chain.doFilter(request, res);
    }

    private boolean validateHost(String host) {
        Pattern pattern = Pattern.compile(corsOriginRegex);
        Matcher matcher = pattern.matcher(host);
        return matcher.matches();
    }
    
    private static String getClientIpInfo(ServletRequest request) {
        String remoteAddr = "";

        if (request != null) {
            remoteAddr = request.getRemoteAddr();
        } 
        return remoteAddr;
    }

    @Override
    public void destroy() {
    }
}
