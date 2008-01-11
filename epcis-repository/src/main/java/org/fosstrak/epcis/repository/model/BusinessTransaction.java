/*
 * Copyright (C) 2008 ETH Zurich
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

package org.accada.epcis.repository.model;

import static org.accada.epcis.repository.Functions.eq;
import static org.accada.epcis.repository.Functions.hc;

/**
 * A business transaction as defined in section 7.2.6 of the spec.
 * @author Sean Wellington
 */
public class BusinessTransaction {
	
	private Long id;

	private BusinessTransactionId bizTransaction;
	
	private BusinessTransactionTypeId type;
	
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public BusinessTransactionId getBizTransaction() {
		return bizTransaction;
	}

	public void setBizTransaction(BusinessTransactionId bizTrans) {
		this.bizTransaction = bizTrans;
	}

	public BusinessTransactionTypeId getType() {
		return type;
	}

	public void setType(BusinessTransactionTypeId type) {
		this.type = type;
	}
	
	@Override
	public int hashCode() {
		return hc(bizTransaction) ^ hc(type);
	}

	@Override
	public boolean equals(Object o) {
		if (o instanceof BusinessTransaction) {
			BusinessTransaction that = (BusinessTransaction)o;
			return eq(this.bizTransaction, that.bizTransaction) && eq(this.type, that.type);
		}
		else {
			return false;
		}
	}

}
