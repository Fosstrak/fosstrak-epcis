/*
 * Copyright (C) 2010 ETH Zurich
 *
 * This file is part of Fosstrak (www.fosstrak.org) and
 * was developed as part of the webofthings.com initiative.
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
package org.epcis.fosstrak.restadapter.representation;

import org.epcis.fosstrak.restadapter.config.Config;
import org.epcis.fosstrak.restadapter.http.HTTP;
import org.epcis.fosstrak.restadapter.util.URI;
import org.epcis.fosstrak.restadapter.model.Form;
import org.epcis.fosstrak.restadapter.model.Entry;
import org.epcis.fosstrak.restadapter.model.Content;
import org.epcis.fosstrak.restadapter.model.Resource;
import org.epcis.fosstrak.restadapter.model.TreeMenu;
import org.epcis.fosstrak.restadapter.model.epc.ElectronicProductCode;
import java.util.List;
import java.util.ListIterator;

/**
 * Class to handle the low level details of HTML to build the page from the model
 * @author Mathias Mueller mathias.mueller(at)unifr.ch, <a href="http://www.guinard.org">Dominique Guinard</a>
 *
 */
public class HTMLStringBuilder {

    /**
     * Build a HTML String: horizontal line
     *
     *
     * @return
     */
    public static String getHorizontalLine() {
        return "<br />";
    }

    /**
     * Build a HTML String: break before text
     *
     *
     * @param text
     *
     * @return
     */
    public static String buildLine(String text) {
        return "<br />" + text;
    }

    /**
     * Build a HTML String: bold text
     *
     *
     * @param text
     *
     * @return
     */
    public static String buildLineBold(String text) {
        return "<br /> <b>" + text + "</b>";
    }

    /**
     * Build a HTML String: italic text
     *
     *
     * @param text
     *
     * @return
     */
    public static String buildLineItalic(String text) {
        return "<br /> <i>" + text + "</i>";
    }

    /**
     * Build a HTML String: paragraph before text
     *
     *
     * @param text
     *
     * @return
     */
    public static String buildParagraph(String text) {
        return "<p />" + text;
    }

    /**
     * Build a HTML String: paragraph before bold text
     *
     *
     * @param text
     *
     * @return
     */
    public static String buildParagraphBold(String text) {
        return "<p /> <b>" + text + "</b>";
    }

    /**
     * Build a HTML String: paragraph before italic text
     *
     *
     * @param text
     *
     * @return
     */
    public static String buildParagraphItalic(String text) {
        return "<p /> <i>" + text + "</i>";
    }

    /**
     * Build a HTML String: break before name value text
     *
     *
     * @param name
     * @param value
     *
     * @return
     */
    public static String buildLineNameValue(String name, String value) {
        return "<br />" + name + ": " + value;
    }

    /**
     * Build a HTML String: break before italic name value text
     *
     *
     * @param name
     * @param value
     *
     * @return
     */
    public static String buildLineNameValueItalic(String name, String value) {
        return "<br /> <i>" + name + ": </i>" + value;
    }

    /**
     * Build a HTML String: link
     *
     *
     * @param linkName
     * @param uri
     *
     * @return
     */
    public static String buildLink(String linkName, String uri) {
        return "<a href='" + uri + "'>" + linkName + "</a>";
    }

    /**
     * Build a HTML String: break with link
     *
     *
     * @param linkName
     * @param uri
     *
     * @return
     */
    public static String buildLinkLine(String linkName, String uri) {
        return "<br />" + buildLink(linkName, uri);
    }

    /**
     * Build a HTML String: link
     *
     *
     * @param name
     * @param linkName
     * @param uri
     *
     * @return
     */
    public static String buildLinkLineNameValue(String name, String linkName, String uri) {
        return "<br />" + name + ": " + buildLink(linkName, uri);
    }

    /**
     * Build a HTML String: italic link
     *
     *
     * @param name
     * @param linkName
     * @param uri
     *
     * @return
     */
    public static String buildLinkLineNameValueItalic(String name, String linkName, String uri) {
        return "<br /> <i>" + name + ": </i>" + buildLink(linkName, uri);
    }

    /**
     * Build a HTML String: HTML root element
     *
     *
     * @return
     */
    public static String beginHTML() {
        return "<html xmlns='http://www.w3.org/1999/xhtml'>";
    }

    /**
     * Build a HTML String: close HTML root element
     *
     *
     * @return
     */
    public static String endHTML() {
        return "</html>";
    }

    /**
     * Build a HTML String: HTML start of body element
     *
     *
     * @return
     */
    public static String beginBody() {
        return "<body>";
    }

    /**
     * Build a HTML String: HTML end of body element
     *
     *
     * @return
     */
    public static String endBody() {
        return "</body>";
    }

    /**
     * Build a HTML String: Tree menu in HTML
     *
     *
     * @param title
     * @param treeMenu
     * @param content
     * @param uri
     *
     * @return
     */
    public static String buildContainerBody(String title, String treeMenu, String content, String uri) {
        StringBuilder res = new StringBuilder();

        res.append("<body class='oneColElsCtrHdr'>");
        res.append("<div id='container'>");
        res.append("<div id='header'>");
        res.append("<table width='100%'>");
        res.append("<tr>");
        res.append("<td width='100%'>");
        res.append("<h1 align='left'>EPCIS Browser</h1>");
        res.append("</td>");
        res.append("<td>");
        res.append("<img src='" + URI.addSubPath(Config.GET_RESOURCES_URL(), "/images/restadapter_logo_white.png") + "' height='100' alt='EPCIS REST Adapter Logo' border='0' usemap='#MapLogo' /><map name='MapLogo' id='MapLogo'><area shape='rect' coords='0,0,131,100' href='" + Config.GET_RESTFUL_EPCIS_URL() + "' alt='EPCIS REST Adapter'/></map>");
        res.append("</td>");
        res.append("</tr>");
        res.append("</table>");
        res.append("</div>");
        res.append("<div id='mainContent'>");
        res.append("<p />");
        res.append("<div id='footer'>");
        res.append("<table width='100%'>");
        res.append("<tr>");
        res.append("<td>");
        res.append(buildTitle2(title));
        res.append("<h3>" + treeMenu + "</h3>");
        res.append("</td>");
        res.append("<td width='225' align='right'>");
        res.append(getRepresentationLinks(uri));
        res.append("</td>");
        res.append("</tr>");
        res.append("</table>");
        res.append("</div>");
        res.append("<div id='restfulContent'>");
        res.append("" + content + "");
        res.append("</div>");
        res.append("</div>");
        res.append("</div>");
        res.append("</body>");

        return res.toString();
    }

    /**
     * Build a HTML String: Representation links in HTML
     *
     *
     * @param url
     *
     * @return
     */
    public static String getRepresentationLinks(String url) {
        StringBuilder res = new StringBuilder();

        res.append("<p />");
        res.append("<div class='restContentLinks' title='" + url + "'>");
        res.append(" <a href='' class='resthtml'>HTML</a>");
        res.append(" <a href='' class='restxml'>XML</a>");
        res.append(" <a href='' class='restjson'>JSON</a>");
        res.append(" <a href='' class='restoptions'>OPTIONS</a>");
        res.append(" <a href='' class='resthead'>HEAD</a>");
        res.append("</div>");

        return res.toString();
    }

    /**
     * Build a HTML String: HTML validator icon in HTML
     *
     *
     * @return
     */
    public static String getHTMLValidator() {
        StringBuilder res = new StringBuilder();

        res.append("<p />");
        res.append("<div>");
        res.append("<a href='http://validator.w3.org/check?uri=referer'>");
        res.append("<img src='http://www.w3.org/Icons/valid-xhtml10-blue' alt='Valid XHTML 1.0 Transitional' height='31' width='88' />");
        res.append("</a>");
        res.append("</div>");

        return res.toString();
    }

    /**
     * Build a HTML String: Tree menu from resource in HTML
     *
     *
     * @param resource
     *
     * @return
     */
    public static String buildTreeMenu(Resource resource) {
        StringBuilder res      = new StringBuilder();
        TreeMenu      treeMenu = resource.getTreeMenu();

        if (treeMenu != null) {
            ListIterator treeMenuIterator = treeMenu.getLinksInProperOrder();

            res.append("Navigation:");

            int counter = 0;

            while (treeMenuIterator.hasNext()) {
                Entry link = (Entry) treeMenuIterator.next();

                res.append(" / ");

                if (counter == 0) {
                    res.append(startGreyLinks());
                }

                res.append(buildLink(link));

                if (counter++ == 1) {
                    res.append(endGreyLinks());
                }
            }
        }

        return res.toString();
    }

    /**
     * Build a HTML String: close div in HTML
     *
     *
     * @return
     */
    public static String endContainer() {
        return "</div>";
    }

//  /**
//   * Method description
//   *
//   *
//   * @return
//   */
//  public static String beginUnorderedList(String id, String title, boolean isSelected) {
//      return "<ul id='" + id + "' title='" + title + "' selected='" + isSelected + "'>";
//  }

    /**
     * Build a HTML String: Start unordered list in HTML
     *
     *
     * @return
     */
    public static String beginUnorderedList() {
        return "<ul>";
    }

    /**
     * Build a HTML String: Close unordered list in HTML
     *
     *
     * @return
     */
    public static String endUnorderedList() {
        return "</ul>";
    }

    /**
     * Build a HTML String: Start table with border in HTML
     *
     *
     * @return
     */
    public static String beginTableVisible() {
        return "<table border='1' cellspacing='0' cellpadding='4'>";
    }

    /**
     * Build a HTML String: Start table without border in HTML
     *
     *
     * @return
     */
    public static String beginTable() {
        return "<table>";
    }

    /**
     * Build a HTML String: Close table in HTML
     *
     *
     * @return
     */
    public static String endTable() {
        return "</table>";
    }

    /**
     * Build a HTML String: A list item containing the supplied text in HTML
     *
     *
     * @param text
     *
     * @return
     */
    public static String buildListItem(String text) {
        return "<li>" + text + "</li>";
    }

    /**
     * Build a HTML String: A list item containing the supplied text (as name value pair) in HTML
     *
     *
     * @param name
     * @param value
     *
     * @return
     */
    public static String buildListItem(String name, String value) {
        return buildListItem(buildBold(name) + " " + value);
    }

    /**
     * Build a HTML String: Text in bold in HTML
     *
     *
     * @param text
     *
     * @return
     */
    public static String buildBold(String text) {
        return "<b>" + text + "</b>";
    }

    /**
     * Build a HTML String: Text in italic in HTML
     *
     *
     * @param text
     *
     * @return
     */
    public static String buildItalic(String text) {
        return "<i>" + text + "</i>";
    }

    /**
     * Build a HTML String: Text as title (h1) in HTML
     *
     *
     * @param text
     *
     * @return
     */
    public static String buildTitle1(String text) {
        return "<h1>" + text + "</h1>";
    }

    /**
     * Build a HTML String: Text as title (h2) in HTML
     *
     *
     * @param text
     *
     * @return
     */
    public static String buildTitle2(String text) {
        return "<h2>" + text + "</h2>";
    }

    /**
     * Build a HTML String: Text as title footer div in HTML
     *
     *
     * @param text
     *
     * @return
     */
    public static String buildTitleFooter(String text) {
        return "<div class='footer'>" + buildTitle2(text) + "</div>";
    }

    /**
     * Build a HTML String: Doctype element in HTML
     *
     *
     * @return
     */
    public static String buildDocType() {
        return "<!DOCTYPE html PUBLIC '-//W3C//DTD XHTML 1.0 Transitional//EN' 'http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd'>";
    }

    /**
     * Build a HTML String: HTML header in HTML
     *
     *
     * @return
     */
    public static String buildHeader() {
        StringBuilder res = new StringBuilder();

        res.append("<head>");
        res.append("<meta http-equiv='Content-Type' content='text/html; charset=UTF-8' />");
        res.append("<title>RESTful Electronic Product Code Information Services</title>");
        res.append("<link href='" + URI.addSubPath(Config.GET_RESOURCES_URL(), "/css/restfulepcis.css") + "' rel='stylesheet' type='text/css' />");

//      res.append("<link href='" + URI.addSubPath(Config.GET_RESOURCES_URL(), "/syntaxhighlighter/styles/shCore.css") + "' rel='stylesheet' type='text/css' />");
//      res.append("<link href='" + URI.addSubPath(Config.GET_RESOURCES_URL(), "/syntaxhighlighter/styles/shThemeDefault.css") + "' rel='stylesheet' type='text/css' />");
        res.append(buildJavaScripts());
        res.append("</head>");

        return res.toString();
    }

    /**
     * Build a HTML String: link in HTML
     *
     *
     * @param link
     *
     * @return
     */
    public static String buildDescriptionLink(Entry link) {
        if (link == null) {
            return "";
        }

        StringBuilder res = new StringBuilder();

        if (link.getDescription() != null) {
            String description = buildItalic(link.getDescription());

            if (link.getDescriptionRef() != null) {
                res.append(buildLink(description, link.getDescriptionRef()));
            } else {
                res.append(description);
            }
        }

        return res.toString();
    }

    /**
     * Build a HTML String: named link in HTML
     *
     *
     * @param link
     *
     * @return
     */
    public static String buildNameLink(Entry link) {
        if (link == null) {
            return "";
        }

        StringBuilder res = new StringBuilder();

        if (link.getName() != null) {
            String name = buildBold(link.getName());

            if (link.getNameRef() != null) {
                res.append(buildLink(name, link.getNameRef()));
            } else {
                res.append(name);
            }
        }

        return res.toString();
    }

    /**
     * Build a HTML String: name value link in HTML
     *
     *
     * @param link
     *
     * @return
     */
    public static String buildValueLink(Entry link) {
        if (link == null) {
            return "";
        }

        StringBuilder res = new StringBuilder();

        if (link.getValue() != null) {
            String value = link.getValue();

            if (link.getValueRef() != null) {
                res.append(buildLink(value, link.getValueRef()));
            } else {
                res.append(value);
            }
        }

        return res.toString();
    }

    /**
     * Build a HTML String: Start of grey links in HTML
     *
     *
     * @param link
     *
     * @return
     */
    public static String startGreyLinks() {
        return "<span id='greylink'>";
    }

    /**
     * Build a HTML String: End of grey links in HTML
     *
     *
     * @param link
     *
     * @return
     */
    public static String endGreyLinks() {
        return "</span>";
    }

    /**
     * Build a HTML String: link in HTML
     *
     *
     * @param link
     *
     * @return
     */
    public static String buildLink(Entry link) {
        StringBuilder res         = new StringBuilder();
        String        name        = buildNameLink(link);
        String        value       = buildValueLink(link);
        String        description = buildDescriptionLink(link);

        if (!name.equals("")) {
            res.append(name + ": ");
        }

        if (!value.equals("")) {
            res.append(value);
        }

        if (!description.equals("")) {
            res.append(" - " + description);
        }

        return res.toString();
    }

    /**
     * Build a HTML String: link in HTML
     *
     *
     * @param link
     *
     * @return
     */
    public static String buildLinkAsParagraph(Entry link) {
        return buildParagraph((buildLink(link)));
    }

    /**
     * Build a HTML String: links in HTML
     *
     *
     * @param link
     *
     * @return
     */
    public static String buildLinkAsListItem(Entry link) {
        return buildListItem((buildLink(link)));
    }

    /**
     * Build a HTML String: links in HTML
     *
     *
     * @param links
     *
     * @return
     */
    public static String buildLinks(Content links) {
        StringBuilder res = new StringBuilder();

        if (links.getDescription() != null) {
            res.append(buildLineItalic(links.getDescription()));
        }

        if (links.getContent().isEmpty()) {
            return res.toString();
        }

        res.append(beginUnorderedList());

        for (Entry link : links.getContent()) {
            res.append(buildLinkAsListItem(link));
        }

        res.append(endUnorderedList());

        return res.toString();
    }

    /**
     * Build a HTML String: form in HTML
     *
     *
     * @param form
     *
     * @return
     */
    public static String buildForm(Form form) {
        StringBuilder res = new StringBuilder();

        if (form != null) {

            if (form.getMethod().startsWith(HTTP.AJAX_PREFIX)) {

                // AJAX Javascript is needed to do HTTP PUT, DELETE etc...
                res.append(buildButtonAjax(form));

                return res.toString();
            }

            // use the supported HTTP PUT or GET in the HTML Form

            res.append(beginForm(form));

            String description = form.getDescription();

            if (description != null) {
                res.append(buildLineItalic(description));
            }

            res.append(beginTable());

            List<Entry> inputs = form.getEntries();

            if (inputs != null) {
                for (Entry entry : inputs) {
                    String name = entry.getId();

                    if (name == null) {
                        name = "unknown";
                    }

                    String type  = HTML.INPUT_TYPE_TEXT;
                    String size  = HTML.INPUT_TYPE_TEXT_SIZE;
                    String value = entry.getValue();

                    if (value == null) {
                        value = "";
                    }

                    String entryDescription = entry.getDescription();

                    if (entryDescription == null) {
                        entryDescription = "";
                    }

                    res.append("<tr><td>");
                    res.append(buildNameLink(entry));
                    res.append("</td><td>");
                    res.append(buildInputText(type, name, value, size));
                    res.append("</td><td>");
                    res.append(buildDescriptionLink(entry));
                    res.append("</td></tr>");
                }
            }

            res.append("<tr><td>");

            res.append(buildSubmitButton(form));
            res.append("</td><td>");
            res.append(buildResetButton(form));
            res.append("</td></tr>");
            res.append(endTable());
            res.append(endForm());
        }

        return res.toString();
    }

    /**
     * Build a HTML String: Begin form in HTML
     *
     *
     * @param form
     *
     * @return
     */
    public static String beginForm(Form form) {
        StringBuilder res     = new StringBuilder();

        String        method  = form.getMethod();
        String        enctype = form.getEnctype();
        String        action  = form.getAction();

        res.append("<form action='");
        res.append(action);
        res.append("' ");
        res.append("method='");
        res.append(method);
        res.append("' ");

        if (enctype != null) {
            res.append("enctype='" + enctype + "'");
        }

        res.append(">");

        return res.toString();
    }

    /**
     * Build a HTML String: End form in HTML
     *
     *
     * @return
     */
    public static String endForm() {
        return "</form>";
    }

    /**
     * Build a HTML String: Form Input in HTML
     *
     *
     * @param type
     * @param name
     * @param value
     * @param size
     *
     * @return
     */
    public static String buildInputText(String type, String name, String value, String size) {
        StringBuilder res = new StringBuilder();

        res.append("<input ");
        res.append("type='" + type + "' ");

        if (!name.equals("")) {
            res.append("name='" + name + "' ");
            res.append("id='" + name + "' ");
        }

        if (!size.equals("")) {
            res.append("size='" + size + "' ");
        }

        res.append("value='" + value + "' ");

        res.append("/>");

        return res.toString();
    }

    /**
     * Build a HTML String: JavaScript libraries references in HTML
     *
     *
     * @return
     */
    public static String buildJavaScripts() {
        String jqueryJS         = URI.addSubPath(Config.GET_RESOURCES_URL(), "/javascript/jquery.js");
        String json2JS          = URI.addSubPath(Config.GET_RESOURCES_URL(), "/javascript/json2.js");
        String restfulsupportJS = URI.addSubPath(Config.GET_RESOURCES_URL(), "/javascript/restful.js");
        String jqueryminJS      = URI.addSubPath(Config.GET_RESOURCES_URL(), "/javascript/beautyofcode/jquerymin.js");
        String beautyOfCodeJS   = URI.addSubPath(Config.GET_RESOURCES_URL(), "/javascript/beautyofcode/jquerybeautyOfCode.js");
        String syntaxhighlitherURL  = URI.addSubPath(Config.GET_RESOURCES_URL(), "/javascript/syntaxhighlighter/");
        String RESTfulFormsURL      = URI.addSubPath(Config.GET_RESOURCES_URL(), "/javascript/restfulforms.js");
        String dyndatetimePluginURL = URI.addSubPath(Config.GET_RESOURCES_URL(), "/javascript/dyndatetime/ddt-0.2/jquery.dynDateTime.js");
        String dyndatetimeLangURL = URI.addSubPath(Config.GET_RESOURCES_URL(), "/javascript/dyndatetime/ddt-0.2/lang/calendar-en.js");
        String dyndatetimeCSSURL = URI.addSubPath(Config.GET_RESOURCES_URL(), "/javascript/dyndatetime/ddt-0.2/css/calendar-blue2.css");
        StringBuilder res = new StringBuilder();

        res.append("<script type='text/javascript'>var syntaxhighlitherURL='" + syntaxhighlitherURL + "';</script>");
        res.append("<script type='text/javascript' src='" + jqueryJS + "' > </script>");
        res.append("<script type='text/javascript' src='" + json2JS + "' > </script>");
        res.append("<script type='text/javascript' src='" + restfulsupportJS + "' > </script>");
        res.append("<script type='text/javascript' src='" + jqueryminJS + "'> </script>");
        res.append("<script type='text/javascript' src='" + beautyOfCodeJS + "'> </script>");
        res.append("<script type='text/javascript'> $.beautyOfCode.init({ brushes: ['Xml'] }); </script>");
        res.append("<script type='text/javascript' src='" + RESTfulFormsURL + "'> </script>");
        res.append("<script type='text/javascript' src='" + dyndatetimePluginURL + "'> </script>");
        res.append("<script type='text/javascript' src='" + dyndatetimeLangURL + "'> </script>");
        res.append("<link rel='stylesheet' type='text/css' media='all' href='" + dyndatetimeCSSURL + "'/>");
        return res.toString();
    }

    /**
     * Build a HTML String: AJAX Form Input in HTML
     *
     *
     * @param form
     *
     * @return
     */
    public static String buildButtonAjax(Form form) {
        StringBuilder res     = new StringBuilder();

        Entry         entry   = null;
        String        myUrl   = "";
        String        myID    = "";
        String        myValue = "";

        if (form.getEntries() != null) {
            myUrl = form.getAction();

            if (!form.getEntries().isEmpty()) {
                entry   = form.getEntries().get(0);
                myID    = entry.getId();
                myValue = entry.getValue();
            }
        }

        if (form.getMethod().equals(HTTP.AJAX_PUT)) {
            res.append("<form action='GET'>");

            if (myID.equals("EPCIS")) {
                res.append("<input type='text' class='restinput' value='" + Config.GET_EPCIS_REPOSITORY_URL() + "' size='" + HTML.INPUT_TYPE_TEXT_SIZE + "' />");
                res.append("<input type='button' class='restput' value='update' title='" + myUrl + "' />");
                res.append("<p />");
                res.append("<input type='reset' value='reset' />");
                res.append("<input type= 'button' class='restdefault' value='default repository URL' title='" + Config.GET_EPCIS_REPOSITORY_URL() + "' />");
                res.append("<input type= 'button' class='restfosstrak' value='remote fosstrak demo URL' title='" + Config.FOSSTRAK_REPOSITORY_URL + "' />");
                res.append("</form>");

            }

            if (myID.equals("FEED")) {
                res.append("<input type='text' class='restinput' value='" + Config.GET_FEED_URL() + "' size='" + HTML.INPUT_TYPE_TEXT_SIZE + "' />");
                res.append("<input type='button' class='restput' value='update' title='" + myUrl + "' />");
                res.append("<p />");
                res.append("<input type='reset' value='reset' />");
                res.append("<input type= 'button' class='restdefault' value='default' title='" + Config.RESTFUL_FEED_URL_DEFAULT + "' />");
                res.append("</form>");
            }

            if (myID.equals("SUBSCRIPTION")) {
                res.append("<input type='hidden' class='restinput' value='" + myValue + "' />");

                String feedImageURI = URI.addSubPath(Config.GET_RESOURCES_URL(), "/images/feed-blue.png");

                res.append("<button type='button' class='restput' value='subscribe' title='" + myUrl + "' >");
                res.append("Subscribe!");
                res.append("</button>");
                res.append("</form>");
                res.append("<div id='putFormDiv'>Generate a Feed for this Query</div>");
            }
        }

        if (form.getMethod().equals(HTTP.AJAX_DELETE)) {
            res.append("<form action='GET'>");

            res.append("<button type='button' class='restdelete' value='unsubscribe' title='" + myUrl + "' >");
            res.append("Delete feed!");
            res.append("</button>");

            res.append("</form>");
            res.append("<div id='putFormDiv'>Unsubscribe this Feed</div>");
        }

        return res.toString();
    }

    /**
     * Build a HTML String: Form Submit Button in HTML
     *
     *
     * @param form
     *
     * @return
     */
    public static String buildSubmitButton(Form form) {
        return "<input type='submit' value='" + form.getActionDescription() + "' id='submit'/>";
    }

    /**
     * Build a HTML String: Form Reset Button in HTML
     *
     *
     * @param form
     *
     * @return
     */
    public static String buildResetButton(Form form) {
        if (form.getEntries().size() == 0) {
            return "";
        }

        return "<input type='reset' value='reset form' />";
    }

    /**
     * Build a HTML String: Table in HTML
     *
     *
     * @param entry
     *
     * @return
     */
    public static String buildMemberTableEntry(Entry entry) {
        StringBuilder res = new StringBuilder();

        if (entry == null) {
            return "";
        }

        if ((entry.getName() == null) && (entry.getNameRef() == null) && (entry.getValue() == null) && (entry.getValueRef() == null) && (entry.getDescription() == null) && (entry.getDescriptionRef() == null)) {
            return "";
        }

        res.append("<tr class='memberEntry'>");

        res.append("<td>");
        res.append(buildNameLink(entry));
        res.append("</td>");

        res.append("<td>");
        res.append(buildValueLink(entry));
        res.append("</td>");

        res.append("<td>");
        res.append(buildDescriptionLink(entry));
        res.append("</td>");

        res.append("</tr>");

        return res.toString();
    }

    /**
     * Build a HTML String: Table in HTML
     *
     *
     * @param epc
     *
     * @return
     */
    public static String buildEPCTable_TableEntry(ElectronicProductCode epc) {
        Entry         entry = epc.getEpc();

        StringBuilder res   = new StringBuilder();

        res.append("<tr class='collectionEntry'>");

        res.append("<td>");
        res.append(buildNameLink(entry));
        res.append("</td>");

        res.append("<td>");
        res.append(buildValueLink(entry));
        res.append("</td>");

        res.append("<td>");

        List<Entry> components = epc.getComponents();

        if (!components.isEmpty()) {
            res.append(buildEPCTableEntry(components));
        }

        res.append(buildDescriptionLink(entry));
        res.append("</td>");

        res.append("</tr>");

        return res.toString();
    }

    /**
     * Build a HTML String: Table in HTML
     *
     *
     * @param epc
     *
     * @return
     */
    public static String buildEPCTableEntry(List<Entry> components) {
        StringBuilder res = new StringBuilder();

        res.append(beginTableVisible());
        res.append("<tr class='epcEntry'>");

        for (Entry e : components) {
            res.append("<td>");
            res.append(buildLink(e));
            res.append("</td>");
        }

        res.append("</tr>");
        res.append(endTable());

        return res.toString();
    }
}
