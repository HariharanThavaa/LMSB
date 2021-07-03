package microservices.book.multiplication.challenge;

import microservices.book.multiplication.user.User;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.AutoConfigureJsonTesters;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.BDDAssertions.then;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;


@ExtendWith(SpringExtension.class)
@AutoConfigureJsonTesters
@WebMvcTest(ChallengeAttemptController.class)
class ChallengeAttemptControllerTest {

    @MockBean
    private ChallengeService challengeService;

    @Autowired
    private MockMvc mvc;

    @Autowired
    private JacksonTester<ChallengeAttemptDTO> jsoRequestAttempt;

    @Autowired
    private JacksonTester<ChallengeAttempt> jsonResultAttempt;

    @Autowired
    private JacksonTester<List<ChallengeAttempt>> jsonStats;

    @Test
    void postValidResult() throws Exception {
        //given
        User user = new User(1L, "Hariharan");
        long attemptId = 5L;
        ChallengeAttemptDTO attemptDTO = new ChallengeAttemptDTO(50, 70, "Hariharan", 3500);
        ChallengeAttempt expectedResponse = new ChallengeAttempt(attemptId, user, 50, 70, 3500, true);
        given(challengeService
                .verifyAttempt(eq(attemptDTO)))
                .willReturn(expectedResponse);

        // when
        MockHttpServletResponse response = mvc.perform(post("/attempts").contentType(MediaType.APPLICATION_JSON)
                .content(jsoRequestAttempt.write(attemptDTO).getJson()))
                .andReturn().getResponse();

        // then
        then(response.getStatus()).isEqualTo(HttpStatus.OK.value());
        then(response.getContentAsString()).isEqualTo(jsonResultAttempt.write(expectedResponse).getJson());
    }

    @Test
    void postInvalidResult() throws Exception {
        //given an attempt with invalid input data
        ChallengeAttemptDTO attemptDTO = new ChallengeAttemptDTO(2000, -70, "Hariharan", 1);

        // when
        MockHttpServletResponse response = mvc.perform(post("/attempts").contentType(MediaType.APPLICATION_JSON)
                .content(jsoRequestAttempt.write(attemptDTO).getJson()))
                .andReturn().getResponse();

        // then
        then(response.getStatus()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    @Test
    void getStatsResult() throws Exception {
        //given
        User user = new User(1L, "Hariharan");
        long attemptId = 5L;
        ChallengeAttempt expectedResponse = new ChallengeAttempt(attemptId, user, 50, 70, 3500, true);
        given(challengeService.getStatsForUser("Hariharan")).willReturn(Collections.singletonList(expectedResponse));

        // when
        MockHttpServletResponse response = mvc.perform(get("/attempts?alias=Hariharan"))
                .andReturn().getResponse();

        // then
        then(response.getStatus()).isEqualTo(HttpStatus.OK.value());
        then(response.getContentAsString())
                .isEqualTo(jsonStats
                        .write(Collections
                                .singletonList(expectedResponse)).getJson());
    }
}
