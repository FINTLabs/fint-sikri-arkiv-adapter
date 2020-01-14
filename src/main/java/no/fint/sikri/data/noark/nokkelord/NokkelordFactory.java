package no.fint.sikri.data.noark.nokkelord;

import no.documaster.model.Fields;
import no.documaster.model.QueryInput;
import no.documaster.model.Result__1;
import no.fint.sikri.data.utilities.QueryUtils;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class NokkelordFactory {
    public QueryInput createQueryInput(String field, String value) {
        return QueryUtils.createQueryInput("Noekkelord", field, value);
    }

    public List<String> toFintResourceList(List<Result__1> input) {
        return input.stream().map(Result__1::getFields).map(Fields::getVerdi).collect(Collectors.toList());
    }
}
