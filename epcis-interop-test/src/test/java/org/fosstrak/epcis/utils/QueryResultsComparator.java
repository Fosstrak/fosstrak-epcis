/*
 * Copyright (C) 2007 ETH Zurich
 *
 * This file is part of Fosstrak (www.fosstrak.org).
 *
 * Fosstrak is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License version 2.1, as published by the Free Software Foundation.
 *
 * Fosstrak is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with Fosstrak; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin Street, Fifth Floor,
 * Boston, MA  02110-1301  USA
 */

package org.fosstrak.epcis.utils;

import java.io.IOException;
import java.text.ParseException;
import java.util.Calendar;
import java.util.LinkedList;
import java.util.List;

import org.fosstrak.epcis.model.QueryResults;
import org.custommonkey.xmlunit.Diff;
import org.custommonkey.xmlunit.Difference;
import org.custommonkey.xmlunit.DifferenceConstants;
import org.custommonkey.xmlunit.DifferenceListener;
import org.custommonkey.xmlunit.XMLUnit;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

/**
 * Utility class providing operations to compare QueryResults objects.
 * 
 * @author Marco Steybe
 */
public final class QueryResultsComparator {

    static {
        XMLUnit.setNormalizeWhitespace(true);
    }

    /**
     * Empty default constructor. Utility classes should not have public
     * constructors.
     */
    private QueryResultsComparator() {
    }

    /**
     * Compares the two given QueryResults with each other and returns
     * <code>true</code> if they are identical, <code>false</code>
     * otherwise. If the two instances are not similar, it prints information to
     * System.out.
     * 
     * @param expResults
     *            The expected QueryResults object.
     * @param actResults
     *            The actual QueryResults object.
     * @return <code>true</code> if the two given QueryResults objects are
     *         identical, <code>false</code> otherwise.
     */
    public static boolean identical(final QueryResults expResults, final QueryResults actResults) throws IOException {
        Document expDoc = QueryResultsParser.queryResultsToDocument(expResults);
        Document actDoc = QueryResultsParser.queryResultsToDocument(actResults);

        Diff diff = new Diff(expDoc, actDoc);
        diff.overrideDifferenceListener(new MyDifferenceLister());
        // if (!diff.identical()) {
        // DetailedDiff ddiff = new DetailedDiff(diff);
        // List<Difference> diffs = ddiff.getAllDifferences();
        // for (Difference d : diffs) {
        // System.out.println(d.getId() + ": " + d);
        // }
        // }
        return diff.identical();
    }

    private static class MyDifferenceLister implements DifferenceListener {
        private List<Difference> ignore;

        MyDifferenceLister() {
            ignore = new LinkedList<Difference>();
            ignore.add(DifferenceConstants.HAS_DOCTYPE_DECLARATION);
            ignore.add(DifferenceConstants.NAMESPACE_PREFIX);
            ignore.add(DifferenceConstants.CHILD_NODELIST_SEQUENCE);
            ignore.add(DifferenceConstants.CHILD_NODELIST_LENGTH);
            ignore.add(DifferenceConstants.ATTR_SEQUENCE);
            ignore.add(DifferenceConstants.COMMENT_VALUE);
        }

        public int differenceFound(Difference difference) {
            // ignore <recordTime> presence
            if (difference.equals(DifferenceConstants.CHILD_NODE_NOT_FOUND)
                    && "recordTime".equals(difference.getTestNodeDetail().getValue())) {
                return RETURN_IGNORE_DIFFERENCE_NODES_IDENTICAL;
            }
            // ignore <recordTime> value
            if (difference.equals(DifferenceConstants.TEXT_VALUE)
                    && "recordTime".equals(difference.getTestNodeDetail().getNode().getParentNode().getNodeName())) {
                return RETURN_IGNORE_DIFFERENCE_NODES_IDENTICAL;
            }
            // check if <eventTime> equal
            if (difference.equals(DifferenceConstants.TEXT_VALUE)
                    && "eventTime".equals(difference.getTestNodeDetail().getNode().getParentNode().getNodeName())) {
                try {
                    Calendar testCal = TimeParser.parseAsCalendar(difference.getTestNodeDetail().getValue());
                    Calendar controlCal = TimeParser.parseAsCalendar(difference.getControlNodeDetail().getValue());
                    return controlCal.getTimeInMillis() == testCal.getTimeInMillis() ? RETURN_IGNORE_DIFFERENCE_NODES_IDENTICAL
                            : RETURN_ACCEPT_DIFFERENCE;
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
            return ignore.contains(difference) ? RETURN_IGNORE_DIFFERENCE_NODES_IDENTICAL : RETURN_ACCEPT_DIFFERENCE;
        }

        /**
         * @see org.custommonkey.xmlunit.DifferenceListener#skippedComparison(org.w3c.dom.Node,
         *      org.w3c.dom.Node)
         */
        public void skippedComparison(Node n1, Node n2) {
        }
    }
}
