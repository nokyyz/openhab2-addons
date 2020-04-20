# Teleinfo Binding

The Teleinfo binding supports an interface to ENEDIS/ERDF [Teleinfo protocol](http://www.linuxembarque.free.fr/electro/compt_energie/specifications_techniques_edf_teleinfo.pdf) for (French) Electricity Meter. This binding works with a Teleinfo Modem correctly configured and plugged to the I1 and I2 terminals of your electricity meter. Teleinfo Modems can be ordered (see the [list of tested hardware](#tested-hardware) below) or can be home-made (see [this example](http://bernard.lefrancois.free.fr)).

Teleinfo is a protocol to read many electrical statistics of your electricity meter: instantaneous power consumption, current price period, meter reading... 
These values can be used to

- send your meter reading to your electricity provider with a simple copy/paste,
- improve your rules and minimize electricity costs,
- check if your subscription is relevant for your needs,
- monitor your electricity consumption,

## Supported Things

The Teleinfo binding provides support for single phase and three phase connection, ICC evolution and the following pricing modes:

- HCHP mode
- Base mode
- Tempo mode
- EJP mode

| Thing type                                 | Connection   | Pricing mode | ICC evolution |
|--------------------------------------------|--------------|--------------|---------------|
| cbemm_base_electricitymeter                | single-phase | Base         |               |
| cbemm_ejp_electricitymeter                 | single-phase | EJP          |               |
| cbemm_hc_electricitymeter                  | single-phase | HCHP         |               |
| cbemm_tempo_electricitymeter               | single-phase | Tempo        |               |
| cbemm_evolution_icc_base_electricitymeter  | single-phase | Base         | [x]           |
| cbemm_evolution_icc_ejp_electricitymeter   | single-phase | EJP          | [x]           |
| cbemm_evolution_icc_hc_electricitymeter    | single-phase | HCHP         | [x]           |
| cbemm_evolution_icc_tempo_electricitymeter | single-phase | Tempo        | [x]           |
| cbetm_base_electricitymeter                | three-phase  | Base         |               |
| cbetm_ejp_electricitymeter                 | three-phase  | EJP          |               |
| cbetm_hc_electricitymeter                  | three-phase  | HCHP         |               |
| cbetm_tempo_electricitymeter               | three-phase  | Tempo        |               |

## Thing Configuration

Before the binding can be used, a serial controller must be added. This needs to be done manually. Select __Teleinfo Serial Controller__ and enter the serial port. Once the serial controller added, electricity meters will be automatically discovered and a new thing labelled __Teleinfo ADCO #id__ will be created (where __#id__ is  your delivery point identifier).

| Thing type                                | Parameter  | Meaning                               | Possible values |
|-------------------------------------------|------------|---------------------------------------|----------------|
| SerialController                          | serialPort | Path to the serial controller         | /dev/ttyXXXX   |
| cbe`<phase>`m_`<icc>`_`<mode>`_electricitymeter | adco       | Electricity delivery point identifier | 031728832562   |

## Channels

Channel availabity depends on the electricity connection (single or three phase) and on the pricing mode (Base, HCHP, EJP or Tempo).

| Channel  | Type                      | Description                                              | Phase  | Mode  |
|----------|---------------------------|----------------------------------------------------------|--------|-------|
| isousc   | `Number:ElectricCurrent`  | Subscribed electric current                              | All    | All   |
| ptec     | `String`                  | Current pricing period                                   | All    | All   |
| imax     | `Number:ElectricCurrent`  | Maximum consumed electric current                        | Single | All   |
| imax1    | `Number:ElectricCurrent`  | Maximum consumed electric current on phase 1             | Three  | All   |
| imax2    | `Number:ElectricCurrent`  | Maximum consumed electric current on phase 2             | Three  | All   |
| imax3    | `Number:ElectricCurrent`  | Maximum consumed electric current on phase 3             | Three  | All   |
| adps     | `Number:ElectricCurrent`  | Excess electric current warning                          | Single | All   |
| adir1    | `Number:ElectricCurrent`  | Excess electric current on phase 1 warning               | Three  | All   |
| adir2    | `Number:ElectricCurrent`  | Excess electric current on phase 2 warning               | Three  | All   |
| adir3    | `Number:ElectricCurrent`  | Excess electric current on phase 3 warning               | Three  | All   |
| iinst    | `Number:ElectricCurrent`  | Instantaneous electric current                           | Single | All   |
| iinst1   | `Number:ElectricCurrent`  | Instantaneous electric current on phase 1                | Three  | All   |
| iinst2   | `Number:ElectricCurrent`  | Instantaneous electric current on phase 2                | Three  | All   |
| iinst3   | `Number:ElectricCurrent`  | Instantaneous electric current on phase 3                | Three  | All   |
| papp     | `Number`                  | Instantaneous apparent power (Unit: `VA`)                | Three, single (ICC evolution only) | All   |
| hhphc    | `String`                  | Pricing schedule group                                   | All    | HCHP  |
| hchc     | `Number:Energy`           | Total consumed energy at low rate pricing                | All    | HCHP  |
| hchp     | `Number:Energy`           | Total consumed energy at high rate pricing               | All    | HCHP  |
| base     | `Number:Energy`           | Total consumed energy                                    | All    | Base  |
| ejphn    | `Number:Energy`           | Total consumed energy at low rate pricing                | All    | EJP   |
| ejphpm   | `Number:Energy`           | Total consumed energy at high rate pricing               | All    | EJP   |
| bbrhcjb  | `Number:Energy`           | Total consumed energy at low rate pricing on blue days   | All    | Tempo |
| bbrhpjb  | `Number:Energy`           | Total consumed energy at high rate pricing on blue days  | All    | Tempo |
| bbrhcjw  | `Number:Energy`           | Total consumed energy at low rate pricing on white days  | All    | Tempo |
| bbrhpjw  | `Number:Energy`           | Total consumed energy at high rate pricing on white days | All    | Tempo |
| bbrhcjr  | `Number:Energy`           | Total consumed energy at low rate pricing on red days    | All    | Tempo |
| bbrhpjr  | `Number:Energy`           | Total consumed energy at high rate pricing on red days   | All    | Tempo |
| pejp     | `Number:Duration`         | Prior notice to EJP start                                | All    | EJP   |
| demain   | `String`                  | Following day color                                      | All    | Tempo |

## Full Example

`teleinfo.things` for a serial USB controller on `/dev/ttyUSB0` for a Single-phase Electricity meter with HC/HP option - CBEMM Evolution ICC:

```
Bridge teleinfo:bridge:serialcontroller [ serialport="/dev/ttyUSB0" ]{
  Thing cbemm_evolution_icc_hc_electricitymeter teleinfo1 [ adco="031728832562"]
}
```

`teleinfo.items`: 


```
Number:ElectricCurrent iSousc "iSousc" {channel="teleinfo:teleinfo1:isousc"}
```

## Tested hardwares

The Teleinfo binding has been successfully validated with below hardware configuration:

| Serial interface | Power Energy Meter model    | Mode(s)                   |
|----------|--------|------------------------------|
| GCE Electronics USB Teleinfo module [(more details)](http://gce-electronics.com/fr/usb/655-module-teleinfo-usb.html) | Actaris A14C5 | - Single-phase HCHP & Base |
| Cartelectronic USB Teleinfo modem [(more details)](https://www.cartelectronic.fr/teleinfo-compteur-enedis/17-teleinfo-1-compteur-usb-rail-din-3760313520028.html) | Actaris A14C5 | Single-phase HCHP |

