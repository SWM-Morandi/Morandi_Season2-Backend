package kr.co.morandi.backend.defense_management.application.usecase.session;

import kr.co.morandi.backend.defense_information.application.port.out.dailydefense.DailyDefensePort;
import kr.co.morandi.backend.defense_information.domain.model.dailydefense.DailyDefense;
import kr.co.morandi.backend.defense_information.domain.service.defense.ProblemGenerationService;
import kr.co.morandi.backend.defense_management.application.mapper.session.StartDailyDefenseMapper;
import kr.co.morandi.backend.defense_management.application.port.out.session.DefenseSessionPort;
import kr.co.morandi.backend.defense_management.application.request.session.StartDailyDefenseServiceRequest;
import kr.co.morandi.backend.defense_management.application.response.session.StartDailyDefenseResponse;
import kr.co.morandi.backend.defense_management.domain.event.CreateDefenseMessageEvent;
import kr.co.morandi.backend.defense_management.domain.event.DefenseStartTimerEvent;
import kr.co.morandi.backend.defense_management.domain.model.session.DefenseSession;
import kr.co.morandi.backend.defense_record.application.port.out.dailyrecord.DailyRecordPort;
import kr.co.morandi.backend.defense_record.domain.model.dailydefense_record.DailyRecord;
import kr.co.morandi.backend.member_management.application.port.out.member.MemberPort;
import kr.co.morandi.backend.member_management.domain.model.member.Member;
import kr.co.morandi.backend.problem_information.application.port.out.problemcontent.ProblemContentPort;
import kr.co.morandi.backend.problem_information.application.response.problemcontent.ProblemContent;
import kr.co.morandi.backend.problem_information.domain.model.problem.Problem;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Optional;

import static kr.co.morandi.backend.defense_information.domain.model.defense.DefenseType.DAILY;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class DailyDefenseManagementUsecase {

    private final DailyDefensePort dailyDefensePort;
    private final DailyRecordPort dailyRecordPort;
    private final ProblemGenerationService problemGenerationService;
    private final DefenseSessionPort defenseSessionPort;
    private final MemberPort memberPort;
    private final ProblemContentPort problemContentPort;
    private final ApplicationEventPublisher publisher;

    @Transactional
    public StartDailyDefenseResponse startDailyDefense(StartDailyDefenseServiceRequest request, Long memberId, LocalDateTime requestTime) {
        Long problemNumber = request.getProblemNumber();
        Member member = memberPort.findMemberById(memberId);

        // 세션이랑 세션 Detail을 찾아서 응시 기록이 있는지 살펴보기
        final Optional<DefenseSession> maybeDefenseSession = defenseSessionPort.findTodaysDailyDefenseSession(member, requestTime);

        // 오늘의 Defense를 찾아오기
        final DailyDefense dailyDefense = dailyDefensePort.findDailyDefense(DAILY, requestTime.toLocalDate());

        // 오늘의 문제 목록 중에서 원하는 문제를 찾아서 시도하려는 문제 목록에 추가 (오늘의 문제 목록에 해당 문제가 없으면 예외 발생)
        final Map<Long, Problem> tryProblem = dailyDefense.getTryingProblem(problemNumber, problemGenerationService);

        // DefenseSession이 있으면 get, 없으면 새로운 DefenseSession을 생성
        final DefenseSession defenseSession = maybeDefenseSession.orElseGet(() -> createNewSession(member, requestTime, dailyDefense, tryProblem));

        // DefenseSession의 recordId로 DailyRecord를 찾고 문제를 시도했는지 확인하고 시도하지 않았으면 시도하도록 함
        Long recordId = defenseSession.getRecordId();
        DailyRecord dailyRecord = dailyRecordPort.findDailyRecord(member, recordId, requestTime.toLocalDate())
                .orElseThrow(() -> new IllegalArgumentException("세션에 해당하는 응시 기록이 없습니다."));

        if (!defenseSession.hasTriedProblem(problemNumber)) {
            dailyRecord.tryMoreProblem(tryProblem);
            defenseSession.tryMoreProblem(problemNumber, requestTime);
        }

        final DefenseSession savedDefenseSession = defenseSessionPort.saveDefenseSession(defenseSession);


        // 문제 내용 가져오기
        final Map<Long, ProblemContent> problemContent = getProblemContents(tryProblem);

        // 문제 목록을 DefenseProblemResponse DTO로 변환
        return StartDailyDefenseMapper.of(tryProblem, dailyDefense, savedDefenseSession, dailyRecord, problemContent);
    }

    /*
    *  백준 문제 ID 목록을 받아서 문제 내용을 가져오는 메소드
    * */
    private Map<Long, ProblemContent> getProblemContents(Map<Long, Problem> tryProblem) {
        return problemContentPort.getProblemContents(tryProblem.values()
                .stream()
                .map(Problem::getBaekjoonProblemId)
                .toList());
    }

    /*
    *  세션이 존재하지 않을 경우 새롭게 시험을 시작하는 메소드
    * */
    private DefenseSession createNewSession(Member member, LocalDateTime now, DailyDefense dailyDefense, Map<Long, Problem> tryProblem) {
        DailyRecord dailyRecord = DailyRecord.tryDefense(now, dailyDefense, member, tryProblem);
        DailyRecord savedDailyRecord = dailyRecordPort.saveDailyRecord(dailyRecord);
        Long recordId = savedDailyRecord.getRecordId();

        final DefenseSession defenseSession = defenseSessionPort.saveDefenseSession(
                DefenseSession.startSession(member, recordId, dailyDefense.getDefenseType(), tryProblem.keySet(), now, dailyDefense.getEndTime(now)));

        /*
         *  DefenseSession에 관련된 타이머 시작 이벤트 발행
         * */
        publisher.publishEvent(new DefenseStartTimerEvent(defenseSession.getDefenseSessionId(), defenseSession.getStartDateTime(), defenseSession.getEndDateTime()));

        /*
        *  DefenseMessage 연결 이벤트 발행 (현재는 SSE로 구현되어 있습니다.)
        * */
        publisher.publishEvent(new CreateDefenseMessageEvent(defenseSession.getDefenseSessionId()));

        return defenseSession;
    }


}
