package no.fint.sikri.data.utilities;

import no.fint.arkiv.sikri.oms.ArrayOfstring;
import no.fint.arkiv.sikri.oms.ObjectFactory;
import no.fint.model.felles.kompleksedatatyper.Identifikator;
import no.fint.model.resource.Link;
import org.apache.commons.lang3.StringUtils;

import javax.xml.bind.JAXBElement;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;

public enum SikriUtils {
    ;


//    public static JAXBElement<ArrayOfstring> getKeywords(List<String> keywords) {
//        ObjectFactory objectFactory = new ObjectFactory();
//
//        ArrayOfstring keywordArray = objectFactory.createArrayOfstring();
//        keywords.forEach(keywordArray.getString()::add);
//
//        return objectFactory.createCaseParameterBaseKeywords(keywordArray);
//    }

    public static URL getURL(String location) throws MalformedURLException {
        if (StringUtils.startsWithAny(location, "file:", "http:", "https:")) {
            return new URL(location);
        }
        return new URL("file:" + location);
    }

//    public static JAXBElement<ExternalIdParameter> getExternalIdParameter(Identifikator id) {
//        ObjectFactory objectFactory = new ObjectFactory();
//
//        ExternalIdParameter externalIdParameter = objectFactory.createExternalIdParameter();
//        externalIdParameter.setId(objectFactory.createExternalIdParameterId(id.getIdentifikatorverdi()));
//        externalIdParameter.setType(objectFactory.createExternalIdParameterType(Constants.EXTERNAL_ID_TYPE));
//
//        return objectFactory.createCaseParameterBaseExternalId(externalIdParameter);
//    }

//    public static JAXBElement<ArrayOfClassCodeParameter> getArchiveCodes(String type, String code) {
//        ObjectFactory objectFactory = new ObjectFactory();
//
//        ArrayOfClassCodeParameter arrayOfClassCodeParameter = objectFactory.createArrayOfClassCodeParameter();
//        ClassCodeParameter classCodeParameter = objectFactory.createClassCodeParameter();
//
//        classCodeParameter.setSort(1);
//        classCodeParameter.setIsManualText(Boolean.FALSE);
//        classCodeParameter.setArchiveCode(objectFactory.createClassCodeParameterArchiveCode(code));
//        classCodeParameter.setArchiveType(objectFactory.createClassCodeParameterArchiveType(type));
//        arrayOfClassCodeParameter.getClassCodeParameter().add(classCodeParameter);
//
//        return objectFactory.createCaseParameterBaseArchiveCodes(arrayOfClassCodeParameter);
//    }

    public static <T> void applyParameterFromLink(List<Link> links, Function<String, T> mapper, Consumer<T> consumer) {
        links.stream()
                .map(Link::getHref)
                .filter(StringUtils::isNotBlank)
                .map(s -> StringUtils.substringAfterLast(s, "/"))
                //.map(s -> StringUtils.prependIfMissing(s, "recno:"))
                .map(mapper)
                .findFirst()
                .ifPresent(consumer);
    }
}
