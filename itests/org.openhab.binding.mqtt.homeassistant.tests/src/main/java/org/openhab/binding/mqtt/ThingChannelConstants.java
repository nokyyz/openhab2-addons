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
package org.openhab.binding.mqtt;

import static org.openhab.binding.mqtt.homeassistant.generic.internal.MqttBindingConstants.HOMEASSISTANT_MQTT_THING;

import org.eclipse.smarthome.core.thing.ThingUID;

/**
 * Static test definitions, like thing, bridge and channel definitions
 *
 * @author David Graeff - Initial contribution
 */
public class ThingChannelConstants {
    // Common ThingUID and ChannelUIDs
    public static final ThingUID testHomeAssistantThing = new ThingUID(HOMEASSISTANT_MQTT_THING, "device234");
}
