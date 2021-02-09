# FINT Elements/ePhorte adapter
This adapter connects Sikris Elements/ePhorte to FINT.

It uses the the following webserives:
- `Object Model Service V3 En`
- `Document Service V3`  

# Properties

| Properties                                            | Default                                  | Description   |
| :---------------------------------------------------- | :----------------------------------------| :------------ |
| fint.sikri.endpoint-base-url                          |                                          |               |
| fint.file-repository.cache-directory                  | file-cache                               |               |
| fint.file-repository.cache-spec                       | expireAfterAccess=5m,expireAfterWrite=7m |               |

# Case defaults

See https://github.com/FINTLabs/fint-arkiv-case-defaults/blob/master/README.md for information on how to configure
case defaults.

# Configuring Ephorte Identities

This adapter supports configuration of the `EphorteIdentity` to be used based on the case type.
This is done in two steps:

1. Configure accounts to use
1. Configure account per case type

## Accounts

Accounts are configured using the following properties:

- `fint.sikri.identity.account.<id>.external-system-name`
- `fint.sikri.identity.account.<id>.username`
- `fint.sikri.identity.account.<id>.password`
- `fint.sikri.identity.account.<id>.role` (optional)

The `<id>` part is a name you can choose freely.

## Case types

Case types are linked to accounts using the properties of the following form:

- `fint.sikri.identity.casetype.default=<id>`
- `fint.sikri.identity.casetype.<case>=<id>`

# SOAP debug parameters

```bash
-Dcom.sun.xml.ws.transport.http.client.HttpTransportPipe.dump=true 
-Dcom.sun.xml.internal.ws.transport.http.client.HttpTransportPipe.dump=true 
-Dcom.sun.xml.ws.transport.http.HttpAdapter.dump=true 
-Dcom.sun.xml.internal.ws.transport.http.HttpAdapter.dump=true 
-Dcom.sun.xml.internal.ws.transport.http.HttpAdapter.dumpTreshold=100000
```