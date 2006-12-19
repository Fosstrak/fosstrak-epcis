package org.accada.epcis.repository.wrapper;


/**
 * A BusinessTransaction conists of two elements, the BizTransTypeID and the
 * BizTransID which are Vocabularies.
 * 
 * @author Alain Remund
 */
public class BusinessTransaction {
    /**
     * The private Variable for the BizTransTypeID.
     */
    private Vocabulary type;

    /**
     * The private Variable for the BizTransID.
     */
    private Vocabulary id;

    /**
     * Constructor for a new BusinessTransaction.
     * 
     * @param aType
     *            with the value of the BizTransTypeID.
     * @param aId
     *            with the value of the BizTransID.
     */
    public BusinessTransaction(final Vocabulary aType, final Vocabulary aId) {
        type = aType;
        id = aId;
    }

    /**
     * @return value of the BizTransTypeID.
     */
    public Vocabulary getBizTransType() {
        return type;
    }

    /**
     * @return value of the BizTransID.
     */
    public Vocabulary getBizTransID() {
        return id;
    }
}
