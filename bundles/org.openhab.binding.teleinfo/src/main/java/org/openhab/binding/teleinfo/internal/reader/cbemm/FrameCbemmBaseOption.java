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
package org.openhab.binding.teleinfo.internal.reader.cbemm;

import org.openhab.binding.teleinfo.internal.reader.common.FrameBaseOption;

/**
 * The {@link FrameCbemmBaseOption} class defines a CBEMM Teleinfo frame with Base option.
 *
 * @author Nicolas SIBERIL - Initial contribution
 */
public class FrameCbemmBaseOption extends FrameCbemm implements FrameBaseOption {

    private static final long serialVersionUID = 5560141193379363335L;

    private int base;

    public FrameCbemmBaseOption() {
        // default constructor
    }

    @Override
    public int getBase() {
        return base;
    }

    @Override
    public void setBase(int base) {
        this.base = base;
    }
}
