/**
 * 
 */
package org.accada.epcis.queryclient;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author Marco Steybe
 * 
 */
public class QueryCallbackListener extends HttpServlet {

    private static final long serialVersionUID = 6250815925403597265L;
    private static String callbackResults = null;

    public void doPost(final HttpServletRequest req,
                       final HttpServletResponse rsp) throws ServletException,
                                                     IOException {
        // set up output stream
        rsp.setContentType("text/plain");
        final PrintWriter out = rsp.getWriter();

        // get POST data
        try {
            callbackResults =
                    (String) req.getParameterValues("callbackResults")[0];
            System.out.println(callbackResults);
        } catch (NullPointerException e) {
            throw new IOException(
                                  "POST argument \"callbackResults=\" not found");
        }

        out.println("Callback OK.");
        out.flush();
    }

    public void doGet(final HttpServletRequest req,
                      final HttpServletResponse rsp) throws ServletException,
                                                    IOException {
        rsp.setContentType("text/xml");
        final PrintWriter out = rsp.getWriter();
        out.print(callbackResults);

//        out.println("<html>");
//        out.println("<head><title>Query Callback Listener</title></head>");
//        out.println("<body>");
//        out.println("<p>This listener receives query results for queries "
//                + "registered at the EPCIS Query Interface service.</p>");
//        out.println("</body>");
//        out.println("</html>");
    }

    public static String getCallbackResults() {
        return callbackResults;
    }
}
