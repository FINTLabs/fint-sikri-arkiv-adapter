package no.fint.sikri.model;

import lombok.Data;

@Data
public class SikriIdentity {
    private String
            username,
            password,
            externalSystemName,
            role;
}
