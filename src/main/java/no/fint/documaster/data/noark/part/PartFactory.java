package no.fint.documaster.data.noark.part;

import no.documaster.model.QueryInput;
import no.documaster.model.QueryResult;
import no.documaster.model.Result__1;
import no.fint.documaster.data.utilities.FintUtils;
import no.fint.documaster.data.utilities.QueryUtils;
import no.fint.documaster.repository.KodeverkRepository;
import no.fint.model.resource.administrasjon.arkiv.PartResource;
import no.fint.model.resource.administrasjon.arkiv.SaksmappeResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class PartFactory {

    @Autowired
    KodeverkRepository kodeverkRepository;

    public PartResource toFintResource(Result__1 result) {

        if (result == null) {
            return null;
        }

        PartResource partResource = new PartResource();
        partResource.setAdresse(FintUtils.createAdresse(result.getFields()));
        partResource.setKontaktinformasjon(FintUtils.createKontaktinformasjon(result.getFields()));
        partResource.setPartNavn(result.getFields().getSakspartNavn());
        partResource.setPartId(FintUtils.createIdentifikator(result.getId()));

        return partResource;
    }

    public QueryInput createQueryInput(String field, String value) {
        return QueryUtils.createQueryInput("Sakspart", field, value);
    }

    public List<PartResource> toFintResourceList(QueryResult result) {
        List<PartResource> output = new ArrayList<>(result.getResults().size());
        for (Result__1 item : result.getResults()) {
            output.add(toFintResource(item));
        }
        return output;
    }

    public QueryInput createQueryInput(SaksmappeResource saksmappe) {
        if (FintUtils.validIdentifikator(saksmappe.getSystemId())) {
            return createQueryInput("refMappe.id", saksmappe.getSystemId().getIdentifikatorverdi());
        } else if (FintUtils.validIdentifikator(saksmappe.getMappeId())) {
            return createQueryInput("refMappe.mappeIdent", saksmappe.getMappeId().getIdentifikatorverdi());
        }
        throw new IllegalArgumentException("Invalid SaksmappeResource: " + saksmappe);
    }
}
