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

import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.eclipse.jdt.annotation.Nullable;
import org.eclipse.smarthome.config.discovery.DiscoveryService;
import org.eclipse.smarthome.core.thing.Bridge;
import org.eclipse.smarthome.core.thing.Thing;
import org.eclipse.smarthome.core.thing.ThingTypeUID;
import org.eclipse.smarthome.core.thing.ThingUID;
import org.eclipse.smarthome.core.thing.binding.BaseThingHandlerFactory;
import org.eclipse.smarthome.core.thing.binding.ThingHandler;
import org.eclipse.smarthome.core.thing.binding.ThingHandlerFactory;
import org.eclipse.smarthome.io.transport.serial.SerialPortManager;
import org.openhab.binding.teleinfo.internal.TeleinfoDiscoveryService;
import org.openhab.binding.teleinfo.internal.serial.TeleinfoSerialControllerHandler;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The {@link TeleinfoThingHandlerFactory} is responsible for creating things and thing
 * handlers.
 *
 * @author Nicolas SIBERIL - Initial contribution
 */
@NonNullByDefault
@Component(configurationPid = "binding.teleinfo", service = ThingHandlerFactory.class)
public class TeleinfoThingHandlerFactory extends BaseThingHandlerFactory {

    private static final Set<ThingTypeUID> SUPPORTED_THING_TYPES_UIDS = Stream
            .of(THING_TYPE_SERIAL_CONTROLLER, THING_HCHP_ELECTRICITY_METER_TYPE_UID).collect(Collectors.toSet());

    private Logger logger = LoggerFactory.getLogger(TeleinfoThingHandlerFactory.class);

    private @NonNullByDefault({}) SerialPortManager serialPortManager;
    private Map<ThingUID, ServiceRegistration<?>> discoveryServiceRegs = new HashMap<>();

    @Override
    public boolean supportsThingType(ThingTypeUID thingTypeUID) {
        return SUPPORTED_THING_TYPES_UIDS.contains(thingTypeUID);
    }

    @Reference
    protected void setSerialPortManager(final SerialPortManager serialPortManager) {
        this.serialPortManager = serialPortManager;
    }

    protected void unsetSerialPortManager(final SerialPortManager serialPortManager) {
        this.serialPortManager = null;
    }

    @Override
    protected @Nullable ThingHandler createHandler(Thing thing) {
        TeleinfoAbstractControllerHandler controller = null;
        ThingTypeUID thingTypeUID = thing.getThingTypeUID();

        if (THING_TYPE_SERIAL_CONTROLLER.equals(thingTypeUID)) {
            controller = new TeleinfoSerialControllerHandler((Bridge) thing, serialPortManager);
        }

        if (controller != null) {
            TeleinfoDiscoveryService discoveryService = new TeleinfoDiscoveryService(controller, 60);
            discoveryService.activate();

            discoveryServiceRegs.put(controller.getThing().getUID(), bundleContext.registerService(
                    DiscoveryService.class.getName(), discoveryService, new Hashtable<String, Object>()));

            return controller;
        }

        if (THING_BASE_ELECTRICITY_METER_TYPE_UID.equals(thing.getThingTypeUID())) {
            throw new IllegalStateException("NOT YET IMPLEMENTED");
        } else if (THING_HCHP_ELECTRICITY_METER_TYPE_UID.equals(thing.getThingTypeUID())) {
            return new TeleinfoHchpElectricityMeterHandler(thing);
        } else if (THING_TEMPO_ELECTRICITY_METER_TYPE_UID.equals(thing.getThingTypeUID())) {
            throw new IllegalStateException("NOT YET IMPLEMENTED");
        } else if (THING_EJP_ELECTRICITY_METER_TYPE_UID.equals(thing.getThingTypeUID())) {
            throw new IllegalStateException("NOT YET IMPLEMENTED");
        } else {
            throw new IllegalStateException("Teleinfo frame type not supported: " + thing.getThingTypeUID());
        }
    }

    @Override
    protected synchronized void removeHandler(ThingHandler thingHandler) {
        if (thingHandler instanceof TeleinfoAbstractControllerHandler) {
            ServiceRegistration<?> serviceReg = this.discoveryServiceRegs.get(thingHandler.getThing().getUID());
            if (serviceReg != null) {
                // remove discovery service, if bridge handler is removed
                TeleinfoDiscoveryService service = (TeleinfoDiscoveryService) bundleContext
                        .getService(serviceReg.getReference());
                if (service != null) {
                    service.deactivate();
                }
                serviceReg.unregister();
                discoveryServiceRegs.remove(thingHandler.getThing().getUID());
            }
        }
    }
}
