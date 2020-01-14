package no.fint.documaster.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.cache.*;
import lombok.extern.slf4j.Slf4j;
import no.fint.documaster.AdapterProps;
import no.fint.documaster.data.exception.FileNotFound;
import no.fint.documaster.data.noark.dokument.DokumentfilService;
import no.fint.model.resource.administrasjon.arkiv.DokumentfilResource;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Repository;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.ExecutionException;

@Slf4j
@Repository
public class CachedFileService extends CacheLoader<String, Path> implements RemovalListener<String, Path> {

    @Autowired
    private AdapterProps props;

    @Autowired
    private DokumentfilService dokumentfilService;

    @Autowired
    private ObjectMapper objectMapper;

    private LoadingCache<String, Path> files;

    @PostConstruct
    public void init() throws IOException {
        files = CacheBuilder.from(props.getCacheSpec()).removalListener(this).build(this);
        if (!Files.exists(props.getCacheDirectory())) {
            Files.createDirectories(props.getCacheDirectory());
        }
        if (!Files.isDirectory(props.getCacheDirectory())) {
            throw new IllegalArgumentException("Not a directory: " + props.getCacheDirectory());
        }
    }

    @Scheduled(initialDelay = 10000, fixedDelayString = "${fint.file-cache.scan-interval:1500000}")
    public void scan() {
        try {
            log.info("Start scanning cache directory for files.");
            Files.walk(props.getCacheDirectory()).filter(Files::isRegularFile).map(Path::toAbsolutePath).forEach(this::addFile);
            log.info("Finished scanning cache directory. {} file(s) in repository", files.size());
        } catch (IOException e) {
            log.error("During scan", e);
        }
    }

    @Scheduled(initialDelay = 20000, fixedDelayString = "${fint.file-cache.clean-interval:150000}")
    public void cleanUp() {
        files.cleanUp();
    }

    private void addFile(Path path) {
        String id = getId(path);
        files.put(id, path);
    }

    private String getId(Path path) {
        return StringUtils.removeEndIgnoreCase(path.getFileName().toString(), ".json");
    }

    public DokumentfilResource getFile(String recNo) throws FileNotFound {
        try {
            Path path = files.get(recNo);
            return readFile(path);
        } catch (ExecutionException | IOException e) {
            throw new FileNotFound(e.getMessage());
        }

    }

    private DokumentfilResource readFile(Path path) throws IOException {
        return objectMapper.readValue(Files.newInputStream(path), DokumentfilResource.class);
    }

    private Path saveFile(DokumentfilResource dokumentfilResource) throws IOException {
        Path path = props.getCacheDirectory().resolve(dokumentfilResource.getSystemId().getIdentifikatorverdi() + ".json");
        objectMapper.writeValue(Files.newOutputStream(path), dokumentfilResource);
        return path;
    }

    @Override
    public void onRemoval(RemovalNotification<String, Path> removal) {
        if (removal.wasEvicted()) {
            Path path = removal.getValue();
            try {
                Files.delete(path);
            } catch (IOException e) {
                log.warn("Unable to delete {}: {}", path, e.getMessage());
            }
        }
    }

    @Override
    public Path load(String docId) throws Exception {
        log.info("Loading {} ...", docId);
        DokumentfilResource resource = dokumentfilService.getDokumentfil(docId);
        return saveFile(resource);
    }

}
