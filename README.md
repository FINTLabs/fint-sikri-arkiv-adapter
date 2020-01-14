# FINT Elements/ePhorte adapter
This adapter connects Sikris Elements/ePhorte to FINT.

It uses the the following webserives:
- `Object Model Service V3 En`
- `Document Service V3`  

# Properties

| Properties                                            | Default                                  | Description   |
| :---------------------------------------------------- | :----------------------------------------| :------------ |
| fint.sikri.user                                       |                                          |               |
| fint.sikri.password                                   |                                          |               |
| fint.sikri.endpoint-base-url                          |                                          |               |
| fint.file-repository.cache-directory                  | file-cache                               |               |
| fint.file-repository.cache-spec                       | expireAfterAccess=5m,expireAfterWrite=7m |               |
| fint.kulturminne.tilskudd-fartoy.arkivdel             |                                          |               |
| fint.kulturminne.tilskudd-fartoy.sub-archive          |                                          |               |
| fint.kulturminne.tilskudd-fartoy.keywords             |                                          |               |
| fint.kulturminne.tilskudd-fartoy.achive-code-type     |                                          |               |
| fint.kulturminne.tilskudd-fartoy.intitial-case-status |                                          |               |


# SOAP debug parameters

```bash
-Dcom.sun.xml.ws.transport.http.client.HttpTransportPipe.dump=true 
-Dcom.sun.xml.internal.ws.transport.http.client.HttpTransportPipe.dump=true 
-Dcom.sun.xml.ws.transport.http.HttpAdapter.dump=true 
-Dcom.sun.xml.internal.ws.transport.http.HttpAdapter.dump=true 
-Dcom.sun.xml.internal.ws.transport.http.HttpAdapter.dumpTreshold=100000
```