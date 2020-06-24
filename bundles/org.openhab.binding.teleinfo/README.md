# Teleinfo Binding

_The Teleinfo binding supports an interface to ENEDIS/ERDF [Teleinfo protocol](http://www.linuxembarque.free.fr/electro/compt_energie/specifications_techniques_edf_teleinfo.pdf) for (French) Electricity Meter._
_Teleinfo is a protocol to read many electrical statistics of your electricity meter: TODO_



_If possible, provide some resources like pictures, a YouTube video, etc. to give an impression of what can be done with this binding. You can place such resources into a `doc` folder next to this README.md._

## Supported Things

The Teleinfo binding provides support for the following mode:

- HCHP mode
- Base mode
- Tempo mode
- EJP mode

### Teleinfo controller

Before the binding can be used, a serial controller must be added. This needs to be done manually. Select __Teleinfo Serial Controller__ and enter the serial port.


## Discovery

_Describe the available auto-discovery features here. Mention for what it works and what needs to be kept in mind when using it._

## Binding Configuration

_If your binding requires or supports general configuration settings, please create a folder ```cfg``` and place the configuration file ```<bindingId>.cfg``` inside it. In this section, you should link to this file and provide some information about the options. The file could e.g. look like:_

```
# Configuration for the Philips Hue Binding
#
# Default secret key for the pairing of the Philips Hue Bridge.
# It has to be between 10-40 (alphanumeric) characters
# This may be changed by the user for security reasons.
secret=openHABSecret
```

_Note that it is planned to generate some part of this based on the information that is available within ```src/main/resources/ESH-INF/binding``` of your binding._

_If your binding does not offer any generic configurations, you can remove this section completely._

## Thing Configuration

_Describe what is needed to manually configure a thing, either through the (Paper) UI or via a thing-file. This should be mainly about its mandatory and optional configuration parameters. A short example entry for a thing file can help!_

_Note that it is planned to generate some part of this based on the XML files within ```src/main/resources/ESH-INF/thing``` of your binding._

## Channels

_Here you should provide information about available channel types, what their meaning is and how they can be used._

_Note that it is planned to generate some part of this based on the XML files within ```src/main/resources/ESH-INF/thing``` of your binding._

| channel  | type                        | description                                                    |
|----------|-----------------------------|----------------------------------------------------------------|
| ISOUSC   | `Number:ElectricCurrent` | Subscribed electric current                                    |
| PTEC     | `String`                   | Current pricing period                                         |
| IMAX     | `Number:ElectricCurrent` | Maximum electric current                                       |
| ADPS     | `Number:ElectricCurrent` | Excess electric current warning                                |
| IINST    | `Number:ElectricCurrent` | Instantaneous electric current                                 |
| PAPP     | `Number`                   | Instantaneous apparent power (Unit: `VA`)            |
| HHPHC    | `String`                   | Pricing schedule group (HCHP pricing option only)              | 
| HCHC     | `Number:Energy`           | Meter reading for low rate pricing (HCHP pricing option only)  | 
| HCHP     | `Number:Energy`           | Meter reading for high rate pricing (HCHP pricing option only) |
| BASE     | `Number:Energy`           | Meter reading (BASE pricing option only)                       |


## Full Example

_Provide a full usage example based on textual configuration files (*.things, *.items, *.sitemap)._

## Supported hardwares

_The Teleinfo binding has been successfully validated with below hardware configuration:_

| Serial interface | Power Energy Meter model    | Mode                   |
|----------|--------|------------------------------|
| GCE Electronics USB Teleinfo module [(more details)](http://gce-electronics.com/fr/usb/655-module-teleinfo-usb.html) | Actaris A14C5 | Single-phase HCHC  |
| Cartelectronic USB Teleinfo modem [(more details)](https://www.cartelectronic.fr/teleinfo-compteur-enedis/17-teleinfo-1-compteur-usb-rail-din-3760313520028.html) | Actaris A14C5 [ Single-Phase HCHP |


