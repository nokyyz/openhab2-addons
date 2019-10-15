/**
 * Copyright (c) 2010-2019 Contributors to the openHAB project
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

import org.openhab.binding.teleinfo.internal.reader.common.FrameTempoOption;

/**
 * The {@link FrameCbemmTempoOption} class defines a CBEMM Teleinfo frame with Tempo option.
 *
 * @author Nicolas SIBERIL - Initial contribution
 */
public class FrameCbemmTempoOption extends FrameCbemm implements FrameTempoOption {

    private static final long serialVersionUID = 6423861816467362730L;

    private int bbrhpjr;
    private int bbrhcjr;
    private int bbrhpjw;
    private int bbrhcjw;
    private int bbrhpjb;
    private int bbrhcjb;
    private CouleurDemain demain;

    public FrameCbemmTempoOption() {
        // default constructor
    }

    @Override
    public int getBbrhpjr() {
        return bbrhpjr;
    }

    @Override
    public int getBbrhcjr() {
        return bbrhcjr;
    }

    @Override
    public int getBbrhpjw() {
        return bbrhpjw;
    }

    @Override
    public int getBbrhcjw() {
        return bbrhcjw;
    }

    @Override
    public int getBbrhpjb() {
        return bbrhpjb;
    }

    @Override
    public int getBbrhcjb() {
        return bbrhcjb;
    }

    @Override
    public CouleurDemain getDemain() {
        return demain;
    }

    @Override
    public void setBbrhpjr(int bbrhpjr) {
        this.bbrhpjr = bbrhpjr;
    }

    @Override
    public void setBbrhcjr(int bbrhcjr) {
        this.bbrhcjr = bbrhcjr;
    }

    @Override
    public void setBbrhpjw(int bbrhpjw) {
        this.bbrhpjw = bbrhpjw;
    }

    @Override
    public void setBbrhcjw(int bbrhcjw) {
        this.bbrhcjw = bbrhcjw;
    }

    @Override
    public void setBbrhpjb(int bbrhpjb) {
        this.bbrhpjb = bbrhpjb;
    }

    @Override
    public void setBbrhcjb(int bbrhcjb) {
        this.bbrhcjb = bbrhcjb;
    }

    @Override
    public void setDemain(CouleurDemain demain) {
        this.demain = demain;
    }

}
