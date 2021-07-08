package microservices.book.gamification.game.badgeprocessors;

import microservices.book.gamification.challenge.ChallengeSolvedDTO;
import microservices.book.gamification.game.domain.BadgeType;
import microservices.book.gamification.game.domain.ScoreCard;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.BDDAssertions.then;

class FirstWonBadgeProcessorTest {

    private BadgeProcessor badgeProcessor;

    @BeforeEach
    public void setUp(){
        badgeProcessor = new FirstWonBadgeProcessor();
    }

    @Test
    public void testFirstWonBadge(){
        // given
        int currentScore = 35;
        List<ScoreCard> scoreCardList = Collections.singletonList(new ScoreCard());
        ChallengeSolvedDTO solved = null;

        // when
        Optional<BadgeType> type = badgeProcessor.processForOptionalBadge(currentScore, scoreCardList, solved);

        then(type.get()).isEqualTo(BadgeType.FIRST_WON);
    }

}