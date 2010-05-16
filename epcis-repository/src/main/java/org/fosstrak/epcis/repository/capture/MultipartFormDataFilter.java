package org.fosstrak.epcis.repository.capture;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletInputStream;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;

import org.apache.commons.fileupload.FileItemIterator;
import org.apache.commons.fileupload.FileItemStream;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.fileupload.util.Streams;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * This Filter implementation modifies the input stream of the servlet request
 * if the request's contentType is <code>multipart/form-data</code> and removes
 * the leading and trailing multipart HTML form overhead. Note that this is a
 * Fosstrak extension and not part of the EPCIS specification.
 * <p>
 * The filter can be placed 'in front' of the EPCIS capture interface servlet,
 * such that the capture interface can also be invoked from HTML pages using
 * forms. That is, an HTML capture client can send an HTTP POST request using
 * contentType <code>multipart/form-data</code> instead of the specified
 * <code>text/xml</code> and this filter takes care of appropriately handling
 * the request before it reaches the capture interface.
 * 
 * @author Marco Steybe
 */
public class MultipartFormDataFilter implements Filter {

    private static final Log LOG = LogFactory.getLog(CaptureOperationsServlet.class);

    public void init(FilterConfig filterConfig) throws ServletException {
        // nothing to do
    }
    
    public void destroy() {
        // nothing to do
    }

    public void doFilter(ServletRequest req, ServletResponse resp, FilterChain chain) throws IOException, ServletException {
        // check that we have a file upload request
        boolean isMultipart = ServletFileUpload.isMultipartContent((HttpServletRequest) req);
        if (isMultipart) {
            LOG.info("receiving capture request from an HTML client, replacing 'multipart/form-data' content with xml payload");
            ServletFileUpload upload = new ServletFileUpload();
            String event = null;
            try {
                // parse the request
                FileItemIterator it = upload.getItemIterator((HttpServletRequest) req);
                while (it.hasNext()) {
                    FileItemStream item = it.next();
                    InputStream stream = item.openStream();
                    if ("event".equals(item.getFieldName())) {
                        LOG.debug("found 'event' multipart/form-data");
                        event = Streams.asString(stream);
                        req.setAttribute("xml", event);
                    } else if (item.isFormField()) {
                        // preserve the remaining attributes and pass them along as-is
                        req.setAttribute(item.getFieldName(), Streams.asString(stream));
                    } else {
                        LOG.debug("found unexpected uploaded file");
                    }
                }
            } catch (Throwable e) {
                String msg = "unable to parse multipart/form-data, ignoring contents!";
                LOG.warn(msg, e);
            }
            if (event != null) {
                final InputStream xml = new ByteArrayInputStream(event.getBytes());
                req = new HttpServletRequestWrapper((HttpServletRequest) req) {
                    @Override
                    public ServletInputStream getInputStream() throws IOException {
                       return new WrappedServletInputStream(xml);
                    } 
                 };
                chain.doFilter(req, resp);
            }
        } else {
            chain.doFilter(req, resp);
        }
    }

    private class WrappedServletInputStream extends ServletInputStream {
        private InputStream is;
        public WrappedServletInputStream(InputStream is) {
            this.is = is;
        }
        @Override
        public int read() throws IOException {
            return is.read();
        }
        @Override
        public boolean markSupported() {
            return false;
        }
        @Override
        public synchronized void mark(int i) {
            throw new RuntimeException(new IOException("mark not supported"));
        }
        @Override
        public synchronized void reset() throws IOException {
            throw new IOException("reset not supported");
        }
    }
}
