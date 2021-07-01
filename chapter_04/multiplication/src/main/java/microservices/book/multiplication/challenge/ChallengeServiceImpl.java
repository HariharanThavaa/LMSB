package microservices.book.multiplication.challenge;

import microservices.book.multiplication.user.User;
import org.springframework.stereotype.Service;

@Service
public class ChallengeServiceImpl implements ChallengeService {

    @Override
    public ChallengeAttempt verifyAttempt(ChallengeAttemptDTO resultAttempt) {
        // check if attempt is correct
        boolean isCorrect = resultAttempt.getGuess() == resultAttempt.getFactorA() * resultAttempt.getFactorB();

        // We don't use identifiers for now
        User user = new User(null, resultAttempt.getUserAlias());

        // Builds the domain Object. Null id for now.
        ChallengeAttempt checkedAttempt = new ChallengeAttempt(null, user, resultAttempt.getFactorA(),
                resultAttempt.getFactorB(), resultAttempt.getGuess(), isCorrect);

        return checkedAttempt;
    }
}
