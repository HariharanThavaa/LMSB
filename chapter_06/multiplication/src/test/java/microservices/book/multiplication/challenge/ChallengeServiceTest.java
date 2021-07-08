package microservices.book.multiplication.challenge;

import microservices.book.multiplication.serviceclients.GamificationServiceClient;
import microservices.book.multiplication.user.User;
import microservices.book.multiplication.user.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.BDDAssertions.then;
import static org.mockito.AdditionalAnswers.returnsFirstArg;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class ChallengeServiceTest {

    private ChallengeService challengeService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private ChallengeAttemptRepository attemptRepository;

    @Mock
    private GamificationServiceClient gameClient;

    @BeforeEach
    public void setUp(){
        challengeService = new ChallengeServiceImpl(userRepository, attemptRepository, gameClient);

    }

    @Test
    public void checkCorrectAttemptTest() {
        // given
        given(attemptRepository.save(any())).will(returnsFirstArg());
        ChallengeAttemptDTO attemptDTO = new ChallengeAttemptDTO(50, 60, "Hariharan", 3000);

        // when
        ChallengeAttempt resultAttempt = challengeService.verifyAttempt(attemptDTO);

        // then
        then(resultAttempt.isCorrect()).isTrue();
        verify(userRepository).save(new User("Hariharan"));
        verify(attemptRepository).save(resultAttempt);
        verify(gameClient).sendAttempt(any());
    }

    @Test
    public void checkWrongAttemptTest() {
        // given
        given(attemptRepository.save(any())).will(returnsFirstArg());
        ChallengeAttemptDTO attemptDTO = new ChallengeAttemptDTO(50, 60, "Hariharan", 5000);

        // when
        ChallengeAttempt resultAttempt = challengeService.verifyAttempt(attemptDTO);

        // then
        then(resultAttempt.isCorrect()).isFalse();
        verify(userRepository).save(new User("Hariharan"));
        verify(attemptRepository).save(resultAttempt);
        verify(gameClient).sendAttempt(any());
    }

    @Test
    public void checkExistingUserTest() {
        // given
        given(attemptRepository.save(any())).will(returnsFirstArg());
        User existingUser = new User(1L, "Hariharan");
        given(userRepository.findByAlias("Hariharan")).willReturn(Optional.of(existingUser));

        ChallengeAttemptDTO attemptDTO = new ChallengeAttemptDTO(50, 60, "Hariharan", 5000);

        // when
        ChallengeAttempt resultAttempt = challengeService.verifyAttempt(attemptDTO);

        // then
        then(resultAttempt.isCorrect()).isFalse();
        then(resultAttempt.getUser()).isEqualTo(existingUser);
        verify(userRepository, never()).save(any());
        verify(attemptRepository).save(resultAttempt);
        verify(gameClient).sendAttempt(any());
    }

    @Test
    public void getStatsForUserTest(){
        // when
        List<ChallengeAttempt> resultAttempt = challengeService.getStatsForUser("Hariharan");

        // then
        verify(attemptRepository).findTop10ByUserAliasOrderByIdDesc("Hariharan");
    }

}