package no.fint.sikri.service

import com.fasterxml.jackson.databind.ObjectMapper
import com.google.common.cache.LoadingCache
import no.fint.documaster.AdapterProps
import spock.lang.Specification

import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths

class CachedFileServiceSpec extends Specification {

    private CachedFileService cachedFileService
    private LoadingCache<String, Path> cache
    private Path path

    void setup() {
        path = Files.createTempDirectory(Paths.get('build', 'tmp'), 'filerepo')
        cache = Mock()
        cachedFileService = new CachedFileService(files: cache, objectMapper: new ObjectMapper(), props: new AdapterProps(cacheDirectory: path))
    }

    void cleanup() {
        Files.walk(path).sorted(Comparator.reverseOrder()).forEach(Files.&deleteIfExists)
    }
    
    def "Scan for files in cache"() {
        given:
        Files.createFile(path.resolve("1.json"))

        when:
        cachedFileService.scan()

        then:
        1 * cache.put('1', _ as Path)
    }
}
