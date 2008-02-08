package org.accada.epcis.soap;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;

/**
 * This object contains factory methods for each Java content interface and Java
 * element interface generated in the org.accada.epcis.soap package.
 * <p>
 * An ObjectFactory allows you to programatically construct new instances of the
 * Java representation for XML content. The Java representation of XML content
 * can consist of schema derived interfaces and classes representing the binding
 * of schema type definitions, element declarations and model groups. Factory
 * methods for each of these are provided in this class.
 */
@XmlRegistry
public class ObjectFactory {

    private final static QName _BusinessService_QNAME = new QName(
            "http://www.unece.org/cefact/namespaces/StandardBusinessDocumentHeader", "BusinessService");
    private final static QName _StandardBusinessDocument_QNAME = new QName(
            "http://www.unece.org/cefact/namespaces/StandardBusinessDocumentHeader", "StandardBusinessDocument");
    private final static QName _CorrelationInformation_QNAME = new QName(
            "http://www.unece.org/cefact/namespaces/StandardBusinessDocumentHeader", "CorrelationInformation");
    private final static QName _ScopeInformation_QNAME = new QName(
            "http://www.unece.org/cefact/namespaces/StandardBusinessDocumentHeader", "ScopeInformation");
    private final static QName _StandardBusinessDocumentHeader_QNAME = new QName(
            "http://www.unece.org/cefact/namespaces/StandardBusinessDocumentHeader", "StandardBusinessDocumentHeader");

    /**
     * Create a new ObjectFactory that can be used to create new instances of
     * schema derived classes for package: org.accada.epcis.soap
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link ManifestItem }
     */
    public ManifestItem createManifestItem() {
        return new ManifestItem();
    }

    /**
     * Create an instance of {@link ContactInformation }
     */
    public ContactInformation createContactInformation() {
        return new ContactInformation();
    }

    /**
     * Create an instance of {@link Scope }
     */
    public Scope createScope() {
        return new Scope();
    }

    /**
     * Create an instance of {@link BusinessScope }
     */
    public BusinessScope createBusinessScope() {
        return new BusinessScope();
    }

    /**
     * Create an instance of {@link CorrelationInformation }
     */
    public CorrelationInformation createCorrelationInformation() {
        return new CorrelationInformation();
    }

    /**
     * Create an instance of {@link PartnerIdentification }
     */
    public PartnerIdentification createPartnerIdentification() {
        return new PartnerIdentification();
    }

    /**
     * Create an instance of {@link Partner }
     */
    public Partner createPartner() {
        return new Partner();
    }

    /**
     * Create an instance of {@link StandardBusinessDocumentHeader }
     */
    public StandardBusinessDocumentHeader createStandardBusinessDocumentHeader() {
        return new StandardBusinessDocumentHeader();
    }

    /**
     * Create an instance of {@link Manifest }
     */
    public Manifest createManifest() {
        return new Manifest();
    }

    /**
     * Create an instance of {@link BusinessService }
     */
    public BusinessService createBusinessService() {
        return new BusinessService();
    }

    /**
     * Create an instance of {@link ServiceTransaction }
     */
    public ServiceTransaction createServiceTransaction() {
        return new ServiceTransaction();
    }

    /**
     * Create an instance of {@link StandardBusinessDocument }
     */
    public StandardBusinessDocument createStandardBusinessDocument() {
        return new StandardBusinessDocument();
    }

    /**
     * Create an instance of {@link DocumentIdentification }
     */
    public DocumentIdentification createDocumentIdentification() {
        return new DocumentIdentification();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link BusinessService }{@code >}}
     */
    @XmlElementDecl(namespace = "http://www.unece.org/cefact/namespaces/StandardBusinessDocumentHeader", name = "BusinessService", substitutionHeadNamespace = "http://www.unece.org/cefact/namespaces/StandardBusinessDocumentHeader", substitutionHeadName = "ScopeInformation")
    public JAXBElement<BusinessService> createBusinessService(BusinessService value) {
        return new JAXBElement<BusinessService>(_BusinessService_QNAME, BusinessService.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link StandardBusinessDocument }{@code >}}
     */
    @XmlElementDecl(namespace = "http://www.unece.org/cefact/namespaces/StandardBusinessDocumentHeader", name = "StandardBusinessDocument")
    public JAXBElement<StandardBusinessDocument> createStandardBusinessDocument(StandardBusinessDocument value) {
        return new JAXBElement<StandardBusinessDocument>(_StandardBusinessDocument_QNAME,
                StandardBusinessDocument.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link CorrelationInformation }{@code >}}
     */
    @XmlElementDecl(namespace = "http://www.unece.org/cefact/namespaces/StandardBusinessDocumentHeader", name = "CorrelationInformation", substitutionHeadNamespace = "http://www.unece.org/cefact/namespaces/StandardBusinessDocumentHeader", substitutionHeadName = "ScopeInformation")
    public JAXBElement<CorrelationInformation> createCorrelationInformation(CorrelationInformation value) {
        return new JAXBElement<CorrelationInformation>(_CorrelationInformation_QNAME, CorrelationInformation.class,
                null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Object }{@code >}}
     */
    @XmlElementDecl(namespace = "http://www.unece.org/cefact/namespaces/StandardBusinessDocumentHeader", name = "ScopeInformation")
    public JAXBElement<Object> createScopeInformation(Object value) {
        return new JAXBElement<Object>(_ScopeInformation_QNAME, Object.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link StandardBusinessDocumentHeader }{@code >}}
     */
    @XmlElementDecl(namespace = "http://www.unece.org/cefact/namespaces/StandardBusinessDocumentHeader", name = "StandardBusinessDocumentHeader")
    public JAXBElement<StandardBusinessDocumentHeader> createStandardBusinessDocumentHeader(
            StandardBusinessDocumentHeader value) {
        return new JAXBElement<StandardBusinessDocumentHeader>(_StandardBusinessDocumentHeader_QNAME,
                StandardBusinessDocumentHeader.class, null, value);
    }

}
