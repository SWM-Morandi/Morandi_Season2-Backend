package kr.co.morandi.backend.defense_information.domain.service.dailydefense;

import kr.co.morandi.backend.defense_information.application.port.out.dailydefense.DailyDefensePort;
import kr.co.morandi.backend.defense_information.application.port.out.dailydefense.DailyDefenseProblemPort;
import kr.co.morandi.backend.defense_information.domain.model.dailydefense.DailyDefense;
import kr.co.morandi.backend.defense_information.domain.model.defense.RandomCriteria;
import kr.co.morandi.backend.defense_information.domain.model.defense.ProblemTier;
import kr.co.morandi.backend.problem_information.domain.model.problem.Problem;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Map;

import static kr.co.morandi.backend.defense_information.domain.model.defense.ProblemTier.*;

@Service
@RequiredArgsConstructor
public class DailyDefenseGenerationService {

    private final DailyDefenseProblemPort dailyDefenseProblemPort;
    private final DailyDefensePort dailyDefensePort;

    private static final Map.Entry<Long, RandomCriteria> PROBLEM_1 = getRandomCriteria(1L, B5, B1, 1000L, 30000L);
    private static final Map.Entry<Long, RandomCriteria> PROBLEM_2 = getRandomCriteria(2L, S5, S4, 1000L, 30000L);
    private static final Map.Entry<Long, RandomCriteria> PROBLEM_3 = getRandomCriteria(3L, S3, S1, 1000L, 30000L);
    private static final Map.Entry<Long, RandomCriteria> PROBLEM_4 = getRandomCriteria(4L, G5, G4, 1000L, 30000L);
    private static final Map.Entry<Long, RandomCriteria> PROBLEM_5 = getRandomCriteria(5L, G3, G1, 1000L, 30000L);
    private static final String POSTFIX = "%d월 %d일 오늘의 문제";

    @Transactional
    public boolean generateDailyDefense(LocalDateTime requestTime) {
        final Map<Long, RandomCriteria> request = Map.ofEntries(PROBLEM_1, PROBLEM_2, PROBLEM_3, PROBLEM_4, PROBLEM_5);

        final Map<Long, Problem> dailyDefenseProblem = dailyDefenseProblemPort.getDailyDefenseProblem(request);

        final LocalDate targetDate = requestTime.plusDays(1L).toLocalDate();

        final DailyDefense dailyDefense = DailyDefense.create(targetDate,
                String.format(POSTFIX, targetDate.getMonthValue(), targetDate.getDayOfMonth()), dailyDefenseProblem);

        dailyDefensePort.saveDailyDefense(dailyDefense);

        return true;
    }

    private static Map.Entry<Long, RandomCriteria> getRandomCriteria(Long problemNumber,
                                                                     ProblemTier startTier,
                                                                     ProblemTier endTier,
                                                                     Long minSolvedCount,
                                                                     Long maxSolvedCount) {

        return Map.entry(problemNumber, RandomCriteria.builder()
                .minSolvedCount(minSolvedCount)
                .maxSolvedCount(maxSolvedCount)
                .difficultyRange(RandomCriteria.DifficultyRange.of(startTier, endTier))
                .build());
    }

}
