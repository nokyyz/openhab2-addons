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
package org.openhab.binding.nibeheatpump.internal.models;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;

/**
 * @author Pauli Anttila - Initial contribution
 */
public class PumpModelTest {

    @Before
    public void Before() {
    }

    @Test
    public void TestF1X45() {
        final String pumpModelString = "F1X45";
        final PumpModel pumpModel = PumpModel.getPumpModel(pumpModelString);
        assertEquals(PumpModel.F1X45, pumpModel);
    }

    @Test
    public void TestF1X55() {
        final String pumpModelString = "F1X55";
        final PumpModel pumpModel = PumpModel.getPumpModel(pumpModelString);
        assertEquals(PumpModel.F1X55, pumpModel);
    }

    @Test
    public void TestF750() {
        final String pumpModelString = "F750";
        final PumpModel pumpModel = PumpModel.getPumpModel(pumpModelString);
        assertEquals(PumpModel.F750, pumpModel);
    }

    @Test
    public void TestF470() {
        final String pumpModelString = "F470";
        final PumpModel pumpModel = PumpModel.getPumpModel(pumpModelString);
        assertEquals(PumpModel.F470, pumpModel);
    }

    @Test(expected = IllegalArgumentException.class)
    public void badPumpModelTest() {
        PumpModel.getPumpModel("XXXX");
    }
}
