package no.fint.sikri.service

import no.fint.adapter.event.EventResponseService
import no.fint.adapter.event.EventStatusService
import no.fint.event.model.DefaultActions
import no.fint.event.model.Event
import spock.lang.Specification

import java.util.concurrent.Executor

class EventHandlerServiceSpec extends Specification {
    private EventHandlerService eventHandlerService
    private EventStatusService eventStatusService
    private EventResponseService eventResponseService
    private Executor executor

    void setup() {
        executor = new Executor() {
            @Override
            void execute(Runnable command) {
                command.run()
            }
        }
        eventStatusService = Mock(EventStatusService)
        eventResponseService = Mock(EventResponseService)
        eventHandlerService = new EventHandlerService(
                executor: executor,
                eventStatusService: eventStatusService,
                eventResponseService: eventResponseService
        )
    }

    def "Post response on health check"() {
        given:
        def component = 'test'
        def event = new Event('rogfk.no', 'test', DefaultActions.HEALTH, 'test')

        when:
        eventHandlerService.handleEvent(component, event)

        then:
        1 * eventResponseService.postResponse(component, _ as Event)
    }
}
