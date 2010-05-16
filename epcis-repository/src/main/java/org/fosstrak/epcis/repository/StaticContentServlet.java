package org.fosstrak.epcis.repository;

import java.io.IOException;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import javax.servlet.http.HttpServletResponse;

/**
 * This is a wrapper for the default Servlet which handles requests to
 * static content.
 * 
 * @author Marco Steybe
 */
public class StaticContentServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    public void doGet(final HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        RequestDispatcher rd = getServletContext().getNamedDispatcher("default");
        HttpServletRequest wrapped = new HttpServletRequestWrapper(req) {
            @Override
            public String getPathInfo() {
                return "/static" + req.getPathInfo();
            }
        };
        rd.forward(wrapped, resp);
    }
}
