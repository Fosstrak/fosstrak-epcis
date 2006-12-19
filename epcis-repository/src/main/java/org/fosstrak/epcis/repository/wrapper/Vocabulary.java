package org.accada.epcis.repository.wrapper;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;


/**
 * The URI class is final and cant be extended. Therefore, following the
 * Adapter-Pattern, we created this new Class Voci which adapts the URI
 * Class.
 * 
 * @author Alain Remund
 */
public class Vocabulary {
    /**
     * The original URI-instance.
     */
    private URI holder = null;

    /**
     * Used for methods which return a URI.
     * 
     * @param uri
     *            the original URI-instance
     */
    private Vocabulary(final URI uri) {
        holder = uri;
    }

    public Vocabulary(final String str) throws VociSyntaxException {
        try {
            holder = new URI(str);
        } catch (final URISyntaxException e) {
            throw new VociSyntaxException(e.getInput(), e.getReason(),
                    e.getIndex());
        }
    }

    public Vocabulary(final String scheme, final String ssp, final String fragment)
            throws VociSyntaxException {
        try {
            holder = new URI(scheme, ssp, fragment);
        } catch (final URISyntaxException e) {
            throw new VociSyntaxException(e.getInput(), e.getReason(),
                    e.getIndex());
        }
    }

    public Vocabulary(final String scheme, final String userInfo,
            final String host, final int port, final String path,
            final String query, final String fragment)
            throws VociSyntaxException {
        try {
            holder = new URI(scheme, userInfo, host, port, path, query,
                    fragment);
        } catch (final URISyntaxException e) {
            throw new VociSyntaxException(e.getInput(), e.getReason(),
                    e.getIndex());
        }
    }

    public Vocabulary(final String scheme, final String host, final String path,
            final String fragment) throws VociSyntaxException {
        try {
            holder = new URI(scheme, host, path, fragment);
        } catch (final URISyntaxException e) {
            throw new VociSyntaxException(e.getInput(), e.getReason(),
                    e.getIndex());
        }
    }

    public Vocabulary(final String scheme, final String authority,
            final String path, final String query, final String fragment)
            throws VociSyntaxException {
        try {
            holder = new URI(scheme, authority, path, query, fragment);
        } catch (final URISyntaxException e) {
            throw new VociSyntaxException(e.getInput(), e.getReason(),
                    e.getIndex());
        }
    }

    public int compareTo(final URI that) {
        return holder.compareTo(that);
    }

    public boolean equals(final Object ob) {
        return holder.equals(ob);
    }

    public String getAuthority() {
        return holder.getAuthority();
    }

    public String getFragment() {
        return holder.getFragment();
    }

    public String getHost() {
        return holder.getHost();
    }

    public String getPath() {
        return holder.getPath();
    }

    public int getPort() {
        return holder.getPort();
    }

    public String getQurey() {
        return holder.getQuery();
    }

    public String getRawAuthority() {
        return holder.getRawAuthority();
    }

    public String getRawFragment() {
        return holder.getRawFragment();
    }

    public String getRawPath() {
        return holder.getRawPath();
    }

    public String getRawQuery() {
        return holder.getRawQuery();
    }

    public String getRawSchemaSpecificPart() {
        return holder.getRawSchemeSpecificPart();
    }

    public String getRawUserInfo() {
        return holder.getUserInfo();
    }

    public String getScheme() {
        return holder.getScheme();
    }

    public String getSchemeSpecificPart() {
        return holder.getSchemeSpecificPart();
    }

    public String getUserInfo() {
        return holder.getUserInfo();
    }

    public int hashCode() {
        return holder.hashCode();
    }

    public boolean isAbsolute() {
        return holder.isAbsolute();
    }

    public boolean isOpaque() {
        return holder.isOpaque();
    }

    public Vocabulary normalize() {
        holder = holder.normalize();
        return this;
    }

    public Vocabulary parseServerAuthority() throws VociSyntaxException {
        Vocabulary temp;
        try {
            temp = new Vocabulary(holder.parseServerAuthority());
        } catch (final URISyntaxException e) {
            throw new VociSyntaxException(e.getInput(), e.getReason(),
                    e.getIndex());
        }
        return temp;
    }

    public Vocabulary relativize(final Vocabulary voci) {
        final Vocabulary temp = new Vocabulary(holder.relativize(voci.holder));
        return temp;
    }

    public Vocabulary resolve(final String str) {
        final Vocabulary temp = new Vocabulary(holder.resolve(str));
        return temp;
    }

    public Vocabulary resolve(final Vocabulary voci) {
        final Vocabulary temp = new Vocabulary(holder.resolve(voci.holder));
        return temp;
    }

    public String toASCIIString() {
        return holder.toASCIIString();
    }

    public String toString() {
        return holder.toString();
    }

    public URL toURL() throws MalformedURLException {
        return holder.toURL();
    }

    /**
     * The URI class is final and cant be extended. Therefore, following the
     * Adapter-Pattern, we created a new Class Voci and its Exception Class.
     * 
     * @author Alain Remund
     */
    public class VociSyntaxException extends Exception {
        /**
         * 
         */
        private static final long serialVersionUID = 6985575937852945318L;

        URISyntaxException holder = null;

        public VociSyntaxException(final String input, final String reason,
                final int index) {
            holder = new URISyntaxException(input, reason, index);
        }

        public VociSyntaxException(final String input, final String reason) {
            holder = new URISyntaxException(input, reason);
        }

        public int getIndex() {
            return holder.getIndex();
        }

        public String getInput() {
            return holder.getInput();
        }

        public String getMessage() {
            return holder.getMessage();
        }

        public String getReason() {
            return holder.getReason();
        }
    }
}
