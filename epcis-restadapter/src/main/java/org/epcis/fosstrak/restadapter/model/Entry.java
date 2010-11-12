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
package org.epcis.fosstrak.restadapter.model;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * The JAXB entry of a resource model
 * @author Mathias Mueller mathias.mueller(at)unifr.ch
 *
 */
@XmlRootElement(namespace = "fosstrak.org/epcis/restadapter")
@XmlAccessorType(XmlAccessType.PUBLIC_MEMBER)
public class Entry {

    private String id;
    private String name;
    private String nameRef;
    private String description;
    private String descriptionRef;
    private String value;
    private String valueRef;

    /**
     * Method description
     *
     *
     * @return
     */
    public String getId() {
        return id;
    }

    /**
     * Method description
     *
     *
     * @param id
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * Method description
     *
     *
     * @return
     */
    public String getName() {
        return name;
    }

    /**
     * Method description
     *
     *
     * @param name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Method description
     *
     *
     * @return
     */
    public String getNameRef() {
        return nameRef;
    }

    /**
     * Method description
     *
     *
     * @param nameRef
     */
    public void setNameRef(String nameRef) {
        this.nameRef = nameRef;
    }

    /**
     * Method description
     *
     *
     * @return
     */
    public String getDescription() {
        return description;
    }

    /**
     * Method description
     *
     *
     * @param description
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * Method description
     *
     *
     * @return
     */
    public String getDescriptionRef() {
        return descriptionRef;
    }

    /**
     * Method description
     *
     *
     * @param descriptionRef
     */
    public void setDescriptionRef(String descriptionRef) {
        this.descriptionRef = descriptionRef;
    }

    /**
     * Method description
     *
     *
     * @return
     */
    public String getValue() {
        return value;
    }

    /**
     * Method description
     *
     *
     * @param value
     */
    public void setValue(String value) {
        this.value = value;
    }

    /**
     * Method description
     *
     *
     * @return
     */
    public String getValueRef() {
        return valueRef;
    }

    /**
     * Method description
     *
     *
     * @param valueRef
     */
    public void setValueRef(String valueRef) {
        this.valueRef = valueRef;
    }
}
