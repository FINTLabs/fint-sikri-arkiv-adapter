package no.fint.sikri.handler.noark;

// TODO: 27/01/2020 Create korrespondansepart
/*
@Service
@Slf4j
public class UpdateKorrespondansepartHandler implements Handler {
    @Autowired
    private ValidationService validationService;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private KorrespondansepartService korrespondansepartService;

    @Override
    public void accept(Event<FintLinks> response) {
        if (response.getOperation() != Operation.CREATE) {
            throw new IllegalArgumentException("Illegal operation: " + response.getOperation());
        }
        if (response.getData() == null || response.getData().size() != 1) {
            throw new IllegalArgumentException("Illegal request data payload.");
        }
        KorrespondansepartResource korrespondansepartResource = objectMapper.convertValue(response.getData().get(0),
                KorrespondansepartResource.class);

        List<Problem> problems = validationService.getProblems(korrespondansepartResource);
        if (!problems.isEmpty()) {
            response.setResponseStatus(ResponseStatus.REJECTED);
            response.setMessage("Payload fails validation!");
            response.setProblems(problems);
            return;
        }

        KorrespondansepartResource result = korrespondansepartService.createKorrespondansepart(korrespondansepartResource);
        response.setData(Collections.singletonList(result));
        response.setResponseStatus(ResponseStatus.ACCEPTED);

    }

    @Override
    public Set<String> actions() {
        return Collections.singleton(ArkivActions.UPDATE_KORRESPONDANSEPART.name());
    }
}
 */
