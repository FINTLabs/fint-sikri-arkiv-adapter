package no.novari.fint.sikri.data.noark.nokkelord;

import lombok.extern.slf4j.Slf4j;
import no.novari.fint.sikri.service.SikriObjectModelService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class NokkelordService {

    @Autowired
    private NokkelordFactory nokkelordFactory;

    @Autowired
    private SikriObjectModelService sikriObjectModelService;

}
