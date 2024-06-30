package kr.co.morandi.backend.defense_information.domain.model.stagedefense;

import kr.co.morandi.backend.defense_information.domain.model.stagedefense.model.StageDefense;
import kr.co.morandi.backend.defense_record.domain.model.stagedefense_record.StageDetail;
import kr.co.morandi.backend.member_management.domain.model.member.Member;
import kr.co.morandi.backend.problem_information.domain.model.problem.Problem;
import kr.co.morandi.backend.defense_record.domain.model.stagedefense_record.StageRecord;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.test.context.ActiveProfiles;

import static kr.co.morandi.backend.defense_information.domain.model.defense.ProblemTier.B5;
import static kr.co.morandi.backend.member_management.domain.model.member.SocialType.GOOGLE;
import static kr.co.morandi.backend.problem_information.domain.model.problem.ProblemStatus.ACTIVE;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

@ActiveProfiles("test")
class StageDetailTest {
    @DisplayName("스테이지 문제 기록이 생성되면 초기 정답 시간은 0이다.")
    @Test
    void initialSolvedTimeIsZero() {
        // given
        StageDefense randomStageDefense = mock(StageDefense.class);
        StageRecord stageDefenseRecord = mock(StageRecord.class);
        Problem problem = createProblem();
        Member member = createMember();

        // when
        StageDetail stageDefenseProblemRecord = StageDetail.create(member,1L, problem, stageDefenseRecord, randomStageDefense);

        // then
        assertThat(stageDefenseProblemRecord.getSolvedTime()).isZero();

    }
    @DisplayName("원하는 스테이지 번호에 따른 스테이지 문제 기록을 만들 수 있다.")
    @Test
    void createStageDefenseProblemRecordWithStageNumber() {
        // given
        StageDefense randomStageDefense = mock(StageDefense.class);
        StageRecord stageDefenseRecord = mock(StageRecord.class);
        Problem problem = createProblem();
        Member member = createMember();

        // when
        StageDetail stageDefenseProblemRecord = StageDetail.create(member, 1L, problem, stageDefenseRecord, randomStageDefense);
        // then
        assertThat(stageDefenseProblemRecord.getStageNumber()).isEqualTo(1L);

    }
    @DisplayName("스테이지 문제 기록이 생성되면 초기 정답 여부는 false이다.")
    @Test
    void initialIsSolvedIsFalse() {
        // given
        StageDefense randomStageDefense = mock(StageDefense.class);
        StageRecord stageDefenseRecord = mock(StageRecord.class);
        Problem problem = createProblem();
        Member member = createMember();

        // when
        StageDetail stageDefenseProblemRecord = StageDetail.create(member, 1L, problem, stageDefenseRecord, randomStageDefense);

        // then
        assertThat(stageDefenseProblemRecord)
                .extracting("isSolved")
                .isEqualTo(false);

    }
    @DisplayName("스테이지 문제 기록이 생성되면 submitCount는 0이다.")
    @Test
    void initialSubmitCountIsZero() {
        // given
        StageDefense randomStageDefense = mock(StageDefense.class);
        StageRecord stageDefenseRecord = mock(StageRecord.class);
        Problem problem = createProblem();
        Member member = createMember();

        // when
        StageDetail stageDefenseProblemRecord = StageDetail.create(member, 1L, problem, stageDefenseRecord, randomStageDefense);
        // then
        assertThat(stageDefenseProblemRecord)
                .extracting("submitCount")
                .isEqualTo(0L);

    }
    @DisplayName("스테이지 문제 기록이 생성되면 초기 정답 코드는 null이다.")
    @Test
    void initialSolvedCodeIsNull() {
        // given
        StageDefense randomStageDefense = mock(StageDefense.class);
        StageRecord stageDefenseRecord = mock(StageRecord.class);
        Problem problem = createProblem();
        Member member = createMember();

        // when
        StageDetail stageDefenseProblemRecord = StageDetail.create(member, 1L, problem, stageDefenseRecord, randomStageDefense);

        // then
        assertThat(stageDefenseProblemRecord)
                .extracting("correctSubmitId")
                .isNull();

    }
    @DisplayName("스테이지 문제 기록을 생성할 수 있다.")
    @Test
    void createStageDefenseProblemRecord() {
        // given
        StageDefense randomStageDefense = mock(StageDefense.class);
        StageRecord stageDefenseRecord = mock(StageRecord.class);
        Problem problem = createProblem();
        Member member = createMember();

        // when
        StageDetail stageDefenseProblemRecord = StageDetail.create(member, 1L, problem, stageDefenseRecord, randomStageDefense);
        // then
        assertThat(stageDefenseProblemRecord)
                .extracting("member", "problem", "record", "defense")
                .contains(member, problem, stageDefenseRecord, randomStageDefense);
    }
    private Member createMember() {
        return Member.builder()
                .email("user" + "@gmail.com")
                .socialType(GOOGLE)
                .nickname("nickname")
                .description("description")
                .build();
    }
    private Problem createProblem() {
        return Problem.builder()
                .baekjoonProblemId(1L)
                .problemTier(B5)
                .problemStatus(ACTIVE)
                .solvedCount(0L)
                .build();
    }
}