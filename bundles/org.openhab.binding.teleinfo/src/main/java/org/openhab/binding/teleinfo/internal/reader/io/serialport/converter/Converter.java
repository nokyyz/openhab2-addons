/**
 * Copyright (c) 2010-2020 Contributors to the openHAB project
 *
 * See the NOTICE file(s) distributed with this work for additional
 * information.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 */
package org.openhab.binding.teleinfo.internal.reader.io.serialport.converter;

import org.openhab.binding.teleinfo.internal.reader.io.serialport.ConvertionException;

/**
 * The {@link Converter} interface defines a converter to translate a Teleinfo String value into Java object.
 *
 * @author Nicolas SIBERIL - Initial contribution
 */
public interface Converter {

    public Object convert(String value) throws ConvertionException;

}
