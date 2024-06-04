package it.unisa.control;

import java.io.IOException;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class PathTraversalFilter implements Filter {

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        // Inizializzazione del filtro se necessario
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;

        String path = httpRequest.getRequestURI();

        // Verifica del percorso per rilevare tentativi di path traversal
        if (path.contains("..") || path.contains("/WEB-INF/") || path.contains("/META-INF/")) {
            // Debug: log del blocco del percorso
            System.out.println("Blocked path traversal attempt: " + path);
            httpResponse.sendError(HttpServletResponse.SC_FORBIDDEN, "Access denied.");
            return;
        }

        // Continuare con il filtro successivo nella catena
        chain.doFilter(request, response);
    }

    @Override
    public void destroy() {
        // Pulizia del filtro se necessario
    }
}
