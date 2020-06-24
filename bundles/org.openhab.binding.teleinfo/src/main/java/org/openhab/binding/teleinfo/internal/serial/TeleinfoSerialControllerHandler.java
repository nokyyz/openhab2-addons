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
package org.openhab.binding.teleinfo.internal.serial;

import static org.openhab.binding.teleinfo.internal.TeleinfoBindingConstants.*;

import java.io.IOException;
import java.util.TooManyListenersException;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.apache.commons.lang.StringUtils;
import org.eclipse.jdt.annotation.NonNull;
import org.eclipse.jdt.annotation.Nullable;
import org.eclipse.smarthome.core.library.types.DecimalType;
import org.eclipse.smarthome.core.thing.Bridge;
import org.eclipse.smarthome.core.thing.ChannelUID;
import org.eclipse.smarthome.core.thing.ThingStatus;
import org.eclipse.smarthome.core.thing.ThingStatusDetail;
import org.eclipse.smarthome.core.types.Command;
import org.eclipse.smarthome.io.transport.serial.PortInUseException;
import org.eclipse.smarthome.io.transport.serial.SerialPort;
import org.eclipse.smarthome.io.transport.serial.SerialPortIdentifier;
import org.eclipse.smarthome.io.transport.serial.SerialPortManager;
import org.eclipse.smarthome.io.transport.serial.UnsupportedCommOperationException;
import org.openhab.binding.teleinfo.internal.handler.TeleinfoAbstractControllerHandler;
import org.openhab.binding.teleinfo.internal.reader.Frame;
import org.openhab.binding.teleinfo.internal.reader.io.serialport.InvalidFrameException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The {@link TeleinfoSerialControllerHandler} class defines a handler for serial controller.
 *
 * @author Nicolas SIBERIL - Initial contribution
 */
public class TeleinfoSerialControllerHandler extends TeleinfoAbstractControllerHandler
        implements TeleinfoReceiveThreadListener {

    private final Logger logger = LoggerFactory.getLogger(TeleinfoSerialControllerHandler.class);

    private static final int SERIAL_RECEIVE_TIMEOUT = 250;
    private static final int SERIAL_PORT_DELAY_RETRY_IN_SECONDS = 60;

    private SerialPortManager serialPortManager;
    private org.eclipse.smarthome.io.transport.serial.SerialPort serialPort;
    private TeleinfoReceiveThread receiveThread;
    private ScheduledFuture<?> keepAliveThread;
    private @Nullable TeleinfoSerialControllerConfiguration config;
    private long invalidFrameCounter = 0;

    public TeleinfoSerialControllerHandler(@NonNull Bridge thing, SerialPortManager serialPortManager) {
        super(thing);
        this.serialPortManager = serialPortManager;
    }

    @Override
    public void initialize() {
        logger.info("Initializing Teleinfo Serial controller");
        invalidFrameCounter = 0;
        updateStatus(ThingStatus.UNKNOWN);

        openSerialPortAndStartReceiving();

        keepAliveThread = scheduler.scheduleWithFixedDelay(() -> {
                logger.debug("Check Teleinfo receiveThread status...");
                logger.debug("isInitialized() = {}", isInitialized());
                if (receiveThread != null) {
                    logger.debug("receiveThread.isAlive() = {}", receiveThread.isAlive());
                }
                if (isInitialized() && (receiveThread == null || (receiveThread != null && !receiveThread.isAlive()))) {
                    updateStatus(ThingStatus.UNKNOWN, ThingStatusDetail.NONE, ERROR_UNKNOWN_RETRY_IN_PROGRESS);
                    logger.info("Try to restart Teleinfo receiving...");
                    stopReceivingAndCloseSerialPort();
                    openSerialPortAndStartReceiving();
                }
            }, 60, 60, TimeUnit.SECONDS);

        if (ThingStatus.OFFLINE.equals(getThing().getStatus())) {
            logger.info("Teleinfo Serial is initialized, but the bridge is currently OFFLINE due to errors");
        } else {
            logger.info("Teleinfo Serial is initialized");
        }
    }

    @Override
    public void dispose() {
        logger.info("Teleinfo Serial is stopping...");
        if(keepAliveThread != null) {
            if(!keepAliveThread.isCancelled())
                keepAliveThread.cancel(true);
            keepAliveThread = null;
        }
        stopReceivingAndCloseSerialPort();
        logger.info("Teleinfo Serial is stopped");

        super.dispose();
    }

    @Override
    public void handleCommand(ChannelUID channelUID, Command command) {
    }

    @Override
    public void onFrameReceived(@NonNull TeleinfoReceiveThread receiveThread, @NonNull Frame frame) {
        updateStatus(ThingStatus.ONLINE);
        fireOnFrameReceivedEvent(frame);
    }

    @Override
    public void onInvalidFrameReceived(@NonNull TeleinfoReceiveThread receiveThread,
            @NonNull InvalidFrameException error) {
        invalidFrameCounter++;
        updateState(THING_SERIAL_CONTROLLER_CHANNEL_INVALID_FRAME_COUNTER, new DecimalType(invalidFrameCounter));
    }

    @Override
    public void onSerialPortInputStreamIOException(@NonNull TeleinfoReceiveThread receiveThread,
            @NonNull IOException e) {
        updateStatus(ThingStatus.OFFLINE, ThingStatusDetail.NONE, ERROR_UNKNOWN_RETRY_IN_PROGRESS);
    }

    @Override
    public boolean continueOnReadNextFrameTimeoutException(@NonNull TeleinfoReceiveThread receiveThread,
            @NonNull TimeoutException e) {
        logger.warn("Retry in progress. Next retry in {} seconds...", SERIAL_PORT_DELAY_RETRY_IN_SECONDS);
        updateStatus(ThingStatus.UNKNOWN, ThingStatusDetail.NONE, ERROR_UNKNOWN_RETRY_IN_PROGRESS);
        try {
            Thread.sleep(SERIAL_PORT_DELAY_RETRY_IN_SECONDS * 1000);
            return true;
        } catch (InterruptedException e1) {
            return false;
        }
    }

    private void openSerialPortAndStartReceiving() {
        logger.debug("startReceiving [start]");

        config = getConfigAs(TeleinfoSerialControllerConfiguration.class);

        if (config.serialport == null || StringUtils.isBlank(config.serialport)) {
            logger.error("Teleinfo port is not set.");
            return;
        }

        logger.info("Connecting to serial port '{}'...", config.serialport);
        String currentOwner = null;
        try {
            final SerialPortIdentifier portIdentifier = serialPortManager.getIdentifier(config.serialport);
            logger.debug("portIdentifier = {}", portIdentifier);
            if (portIdentifier == null) {
                logger.error("No port identifier for '{}'", config.serialport);
                updateStatus(ThingStatus.OFFLINE, ThingStatusDetail.OFFLINE.COMMUNICATION_ERROR,
                        ERROR_OFFLINE_SERIAL_NOT_FOUND);
                return;
            }
            logger.debug("Opening portIdentifier");
            currentOwner = portIdentifier.getCurrentOwner();
            logger.debug("portIdentifier.getCurrentOwner() = {}", currentOwner);
            SerialPort commPort = portIdentifier.open("org.openhab.binding.teleinfo", 5000);
            serialPort = commPort;

            serialPort.setSerialPortParams(1200, SerialPort.DATABITS_7, SerialPort.STOPBITS_1, SerialPort.PARITY_EVEN);
            serialPort.enableReceiveThreshold(1);
            serialPort.enableReceiveTimeout(SERIAL_RECEIVE_TIMEOUT);
            logger.debug("Starting receive thread");
            receiveThread = new TeleinfoReceiveThread(serialPort, this, config.autoRepairInvalidADPSgroupLine);
            receiveThread.start();

            // RXTX serial port library causes high CPU load
            // Start event listener, which will just sleep and slow down event loop
            serialPort.addEventListener(receiveThread);
            serialPort.notifyOnDataAvailable(true);
            logger.info("Connected to serial port '{}'", config.serialport);
        } catch (PortInUseException e) {
            updateStatus(ThingStatus.OFFLINE, ThingStatusDetail.OFFLINE.COMMUNICATION_ERROR,
                    ERROR_OFFLINE_SERIAL_INUSE);
            logger.error(
                    "An error occurred during serial port connection. Detail: \"port is currently already in use (by '{}')\"",
                    currentOwner);
        } catch (UnsupportedCommOperationException e) {
            updateStatus(ThingStatus.OFFLINE, ThingStatusDetail.OFFLINE.COMMUNICATION_ERROR,
                    ERROR_OFFLINE_SERIAL_UNSUPPORTED);
            logger.error("An error occurred during serial port connection. Detail: \"{}\"", e.getLocalizedMessage(), e);
        } catch (TooManyListenersException e) {
            updateStatus(ThingStatus.OFFLINE, ThingStatusDetail.OFFLINE.COMMUNICATION_ERROR,
                    ERROR_OFFLINE_SERIAL_LISTENERS);
            logger.error("An error occurred during serial port connection. Detail: \"{}\"", e.getLocalizedMessage(), e);
        }
        logger.debug("startReceiving [end]");
    }

    private void stopReceivingAndCloseSerialPort() {
        logger.debug("stopReceiving [start]");

        if (receiveThread != null) {
            receiveThread.interrupt();
            try {
                receiveThread.join();
            } catch (InterruptedException e) {
            }
            receiveThread.setListener(null);
            receiveThread = null;
        }
        if (serialPort != null) {
            serialPort.close();
            serialPort = null;
        }

        logger.debug("stopReceiving [end]");
    }
}
