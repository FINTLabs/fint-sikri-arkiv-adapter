package no.fint.sikri.data.noark.sak;


import lombok.extern.slf4j.Slf4j;
import no.fint.arkiv.CaseProperties;
import no.fint.arkiv.sikri.oms.CaseType;
import no.fint.model.resource.arkiv.noark.SakResource;
import no.fint.sikri.data.noark.common.NoarkFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class SakFactory {

    @Autowired
    private NoarkFactory noarkFactory;

    public SakResource toFintResource(CaseType result) {
        return noarkFactory.applyValuesForSaksmappe(new CaseProperties(), result, new SakResource());
    }

    public List<SakResource> toFintResourceList(List<CaseType> cases) {
        return cases.stream().map(this::toFintResource).collect(Collectors.toList());
    }

}
