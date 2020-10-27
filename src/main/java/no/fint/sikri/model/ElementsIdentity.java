package no.fint.sikri.model;

import lombok.Data;

@Data
public class ElementsIdentity {
    private String
            username,
            password,
            externalSystemName,
            role;
}
