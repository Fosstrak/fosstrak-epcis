/*
 * Copyright (C) 2007 ETH Zurich
 *
 * This file is part of Accada (www.accada.org).
 *
 * Accada is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License version 2.1, as published by the Free Software Foundation.
 *
 * Accada is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with Accada; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin Street, Fifth Floor,
 * Boston, MA  02110-1301  USA
 */

package org.accada.epcis.repository.query;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * TODO: javadoc
 * 
 * @author Marco Steybe
 */
public class MasterDataQuerySql extends MasterDataQuery {

    private static final String SQL_SELECT_FROM = "SELECT uri FROM";
    private static final String SQL_WHERE_BASE = " WHERE 1";

    private StringBuilder sqlSelectFrom = new StringBuilder(SQL_SELECT_FROM);
    private StringBuilder sqlWhereClause = new StringBuilder(SQL_WHERE_BASE);
    private List<Object> sqlQueryParams = new ArrayList<Object>();

    private Map<String, String> vocabularySqlQueries = new HashMap<String, String>();

    public MasterDataQuerySql() {
    }

    public void resetQuery() {
        super.resetQuery();
        resetSql();
    }

    private void resetSql() {
        sqlSelectFrom = new StringBuilder(SQL_SELECT_FROM);
        sqlWhereClause = new StringBuilder(SQL_WHERE_BASE);
        sqlQueryParams = new ArrayList<Object>();
    }

    public List<Object> getSqlParams() {
        return sqlQueryParams;
    }

    public String getSqlForVocabulary(String vocabularyType) {
        if (vocabularySqlQueries.isEmpty()) {
            constructSqlQueries();
        }
        return vocabularySqlQueries.get(vocabularyType);
    }

    private void constructSqlQueries() {
        List<String> vocabularyTypes = super.getVocabularyTypes();
        List<String> attributeNames = super.getAttributeNames();
        Map<String, List<String>> attributeNameAndValues = super.getAttributeNameAndValues();
        List<String> vocabularyEqNames = super.getVocabularyEqNames();
        List<String> vocabularyWdNames = super.getVocabularyWdNames();
        
        for (String vocType : vocabularyTypes) {
            boolean joinedAttribute = false;
            String vocTablename = QueryOperationsBackend.getVocabularyTablename(vocType);
            sqlSelectFrom.append(" ").append(vocTablename).append(",");

            // filter by attribute names
            if (attributeNames != null && !attributeNames.isEmpty()) {
                if (!joinedAttribute) {
                    sqlSelectFrom.append(" ").append(vocTablename).append("_attr,");
                    sqlWhereClause.append(" AND ").append(vocTablename).append(".id=");
                    sqlWhereClause.append(vocTablename).append("_attr.id");
                }
                
                sqlWhereClause.append(" AND ").append(vocTablename).append("_attr.attribute IN (?");
                sqlQueryParams.add(attributeNames.get(0));
                for (int i = 1; i < attributeNames.size(); i++) {
                    sqlWhereClause.append(",?");
                    sqlQueryParams.add(attributeNames.get(i));
                }
                sqlWhereClause.append(")");
            }

            // filter by attribute names and values
            if (attributeNameAndValues != null && !attributeNameAndValues.isEmpty()) {
                if (!joinedAttribute) {
                    sqlSelectFrom.append(" ").append(vocTablename).append("_attr,");
                    sqlWhereClause.append(" AND ").append(vocTablename).append(".id=");
                    sqlWhereClause.append(vocTablename).append("_attr.id");
                }
                for (String attrName : attributeNameAndValues.keySet()) {
                    sqlWhereClause.append(" AND ").append(vocTablename).append("_attr.attribute=?");
                    sqlQueryParams.add(attrName);
                    sqlWhereClause.append(" AND ").append(vocTablename).append("_attr.value IN (?");
                    List<String> attrValues = attributeNameAndValues.get(attrName);
                    sqlQueryParams.add(attrValues.get(0));
                    for (int i = 1; i < attrValues.size(); i++) {
                        sqlWhereClause.append(",?");
                        sqlQueryParams.add(attrValues.get(i));
                    }
                    sqlWhereClause.append(")");
                }
            }

            // filter by vocabulary names
            if (vocabularyEqNames != null && !vocabularyEqNames.isEmpty()) {
                sqlWhereClause.append(" AND ").append(vocTablename).append(".uri IN (?");
                sqlQueryParams.add(vocabularyEqNames.get(0));
                for (int i = 1; i < vocabularyEqNames.size(); i++) {
                    sqlWhereClause.append(",?");
                    sqlQueryParams.add(vocabularyEqNames.get(i));
                }
                sqlWhereClause.append(")");
            }
            if (vocabularyWdNames != null && !vocabularyWdNames.isEmpty()) {
                sqlWhereClause.append(" AND (0");
                for (String vocWdName : vocabularyWdNames) {
                    sqlWhereClause.append(" OR ").append(vocTablename).append(".uri LIKE ?");
                    sqlQueryParams.add(vocWdName + "%");
                }
                sqlWhereClause.append(")");
            }

            // remove last comma
            sqlSelectFrom.delete(sqlSelectFrom.length() - 1, sqlSelectFrom.length());

            // store query to map
            vocabularySqlQueries.put(vocType, sqlSelectFrom.append(sqlWhereClause).toString());
            resetSql();
        }
    }
}
