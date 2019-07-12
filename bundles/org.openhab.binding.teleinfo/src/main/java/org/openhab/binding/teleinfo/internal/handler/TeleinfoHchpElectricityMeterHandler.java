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
package org.openhab.binding.teleinfo.internal.handler;

import static org.openhab.binding.teleinfo.internal.TeleinfoBindingConstants.*;

import org.eclipse.jdt.annotation.NonNull;
import org.eclipse.smarthome.core.library.types.DecimalType;
import org.eclipse.smarthome.core.library.types.StringType;
import org.eclipse.smarthome.core.thing.Thing;
import org.openhab.binding.teleinfo.internal.reader.Frame;
import org.openhab.binding.teleinfo.internal.reader.FrameOptionHeuresCreuses;

/**
 * The {@link TeleinfoHchpElectricityMeterHandler} class defines a handler for a HCHP Electricity Meters thing.
 *
 * @author Nicolas SIBERIL - Initial contribution
 */
public class TeleinfoHchpElectricityMeterHandler extends TeleinfoAbstractElectricityMeterHandler {

    public TeleinfoHchpElectricityMeterHandler(Thing thing) {
        super(thing);
    }

    @Override
    public void onFrameReceived(@NonNull TeleinfoAbstractControllerHandler controllerHandler, @NonNull Frame frame) {
        super.onFrameReceived(controllerHandler, frame);

        String adco = getThing().getProperties().get(THING_HCHP_ELECTRICITY_METER_PROPERTY_ADCO);
        if (adco.equalsIgnoreCase(frame.getADCO())) {
            FrameOptionHeuresCreuses hcFrame = (FrameOptionHeuresCreuses) frame;

            updateState(THING_HCHP_ELECTRICITY_METER_CHANNEL_HCHC, new DecimalType(hcFrame.getIndexHeuresCreuses()));
            updateState(THING_HCHP_ELECTRICITY_METER_CHANNEL_HCHP, new DecimalType(hcFrame.getIndexHeuresPleines()));
            updateState(THING_HCHP_ELECTRICITY_METER_CHANNEL_HHPHC, new StringType(hcFrame.getGroupeHoraire().name()));
        }

    }

}
