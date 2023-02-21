package no.fint.sikri.data.noark.common

import spock.lang.Specification

import java.util.regex.Matcher
import java.util.regex.Pattern


class NoarkTitleSpec extends Specification {

    def "Shield title with marked words"(){
        when:
        def caseTitle = "Sak 1 - @Ola Norman@ og @Lisa Norman@ oppføring av garasje"
        Pattern pattern = Pattern.compile("@([^@]+)@");
        Matcher matcher = pattern.matcher(caseTitle);
        String shieldedTitle = caseTitle;
        while (matcher.find()) {
            String match = matcher.group();
            String shieldedMatch = match.substring(1, match.length() - 1);
            String[] words = shieldedMatch.split(" ");
            for (String word : words) {
                shieldedMatch = shieldedMatch.replace(word, "*****");
            }
            shieldedTitle = shieldedTitle.replace(match, shieldedMatch);
        }

        then:
        shieldedTitle == "Sak 1 - ***** ***** og ***** ***** oppføring av garasje"
    }
}
