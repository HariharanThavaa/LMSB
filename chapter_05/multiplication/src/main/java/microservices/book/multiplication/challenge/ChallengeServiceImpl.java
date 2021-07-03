package microservices.book.multiplication.challenge;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import microservices.book.multiplication.user.User;
import microservices.book.multiplication.user.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Service
public class ChallengeServiceImpl implements ChallengeService {

    private final UserRepository userRepository;
    private final ChallengeAttemptRepository attemptRepository;

    @Override
    public ChallengeAttempt verifyAttempt(ChallengeAttemptDTO resultAttempt) {
        // check if the user already exists for that alias, otherwise create it
        User user = userRepository.findByAlias(resultAttempt.getUserAlias())
                .orElseGet(() -> {
                    log.info("Creating new user with alias {}", resultAttempt.getUserAlias());
                    return userRepository.save(new User(resultAttempt.getUserAlias()));
                });

        // check if attempt is correct
        boolean isCorrect = resultAttempt.getGuess() == resultAttempt.getFactorA() * resultAttempt.getFactorB();

        // Builds the domain Object. Null id for now.
        ChallengeAttempt checkedAttempt = new ChallengeAttempt(null,
                user,
                resultAttempt.getFactorA(),
                resultAttempt.getFactorB(),
                resultAttempt.getGuess(),
                isCorrect);

        // Stores the attempt
        ChallengeAttempt storeAttempt = attemptRepository.save(checkedAttempt);

        return storeAttempt;
    }

    @Override
    public List<ChallengeAttempt> getStatsForUser(String userAlias) {
        return attemptRepository.findTop10ByUserAliasOrderByIdDesc(userAlias);
    }
}
