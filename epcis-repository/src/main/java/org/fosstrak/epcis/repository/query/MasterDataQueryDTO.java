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

package org.fosstrak.epcis.repository.query;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * TODO: javadoc
 * 
 * @author Marco Steybe
 */
public class MasterDataQueryDTO {

    private int maxElementCount = -1;
    private boolean includeAttributes = false;
    private boolean includeChildren = false;
    private List<String> vocabularyTypes = null;
    private List<String> vocabularyEqNames = null;
    private List<String> vocabularyWdNames = null;
    private List<String> attributeNames = null;
    private List<String> includedAttributeNames = null;
    private Map<String, List<String>> attributeNameAndValues = null;

    public void resetQuery() {
        maxElementCount = -1;
        vocabularyTypes = null;
        vocabularyEqNames = null;
        vocabularyWdNames = null;
        attributeNames = null;
        includedAttributeNames = null;
        attributeNameAndValues = null;
    }

    public void addAttributeName(String attrName) {
        if (attributeNames == null) {
            attributeNames = new ArrayList<String>();
        }
        attributeNames.add(attrName);
    }

    public void addAttributeNameAndValues(String attrName, List<String> attrValues) {
        if (attributeNameAndValues == null) {
            attributeNameAndValues = new HashMap<String, List<String>>();
        }
        attributeNameAndValues.put(attrName, attrValues);
    }

    public void addVocabularyEqName(String vocabularyEqName) {
        if (vocabularyEqNames == null) {
            vocabularyEqNames = new ArrayList<String>();
        }
        vocabularyEqNames.add(vocabularyEqName);
    }

    public void addVocabularyType(String vocabularyType) {
        if (vocabularyTypes == null) {
            vocabularyTypes = new ArrayList<String>();
        }
        vocabularyTypes.add(vocabularyType);
    }

    public void addVocabularyWdName(String vocabularyWdName) {
        if (vocabularyWdNames == null) {
            vocabularyWdNames = new ArrayList<String>();
        }
        vocabularyWdNames.add(vocabularyWdName);
    }

    public Map<String, List<String>> getAttributeNameAndValues() {
        return attributeNameAndValues;
    }

    public List<String> getAttributeNames() {
        return attributeNames;
    }

    public boolean getIncludeAttributes() {
        return includeAttributes;
    }

    public boolean getIncludeChildren() {
        return includeChildren;
    }

    public List<String> getIncludedAttributeNames() {
        return includedAttributeNames;
    }

    public int getMaxElementCount() {
        return maxElementCount;
    }

    public List<String> getVocabularyEqNames() {
        return vocabularyEqNames;
    }

    public List<String> getVocabularyTypes() {
        return vocabularyTypes;
    }

    public List<String> getVocabularyWdNames() {
        return vocabularyWdNames;
    }

    public void setAttributeNameAndValues(Map<String, List<String>> attributeNameAndValues) {
        this.attributeNameAndValues = attributeNameAndValues;
    }

    public void setAttributeNames(List<String> attributeNames) {
        this.attributeNames = attributeNames;
    }

    public void setIncludeAttributes(boolean includeAttributes) {
        this.includeAttributes = includeAttributes;
    }

    public void setIncludeChildren(boolean includeChildren) {
        this.includeChildren = includeChildren;
    }

    public void setIncludedAttributeNames(List<String> includedAttributeNames) {
        this.includedAttributeNames = includedAttributeNames;
    }

    public void setMaxElementCount(int maxElementCount) {
        this.maxElementCount = maxElementCount;
    }

    public void setVocabularyEqNames(List<String> vocabularyEqNames) {
        this.vocabularyEqNames = vocabularyEqNames;
    }

    public void setVocabularyTypes(List<String> vocabularyTypes) {
        this.vocabularyTypes = vocabularyTypes;
    }

    public void setVocabularyWdNames(List<String> vocabularyWdNames) {
        this.vocabularyWdNames = vocabularyWdNames;
    }
}
