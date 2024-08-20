package kr.co.morandi.backend.problem_information.domain.model.problem;

import jakarta.persistence.*;
import kr.co.morandi.backend.common.model.BaseEntity;
import kr.co.morandi.backend.defense_information.domain.model.defense.ProblemTier;
import lombok.*;

import static kr.co.morandi.backend.problem_information.domain.model.problem.ProblemStatus.INIT;

@Entity
@Table(name = "problem", indexes = {
        @Index(name = "idx_problem_solvedCount_tier_status", columnList = "solvedCount, problemTier, problemStatus")
})
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Problem extends BaseEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long problemId;

    private Long baekjoonProblemId;

    @Enumerated(EnumType.STRING)
    private ProblemTier problemTier;

    @Enumerated(EnumType.STRING)
    private ProblemStatus problemStatus;

    private Long solvedCount;

    @Builder
    private Problem(Long baekjoonProblemId, ProblemTier problemTier, ProblemStatus problemStatus, Long solvedCount) {
        this.baekjoonProblemId = baekjoonProblemId;
        this.problemTier = problemTier;
        this.problemStatus = problemStatus;
        this.solvedCount = solvedCount;
    }

    public void activate() {
        this.problemStatus = ProblemStatus.ACTIVE;
    }

    public static Problem create(Long baekjoonProblemId, ProblemTier problemTier, Long solvedCount) {
        return Problem.builder()
                .baekjoonProblemId(baekjoonProblemId)
                .problemTier(problemTier)
                .problemStatus(INIT)
                .solvedCount(solvedCount)
                .build();
    }
}
