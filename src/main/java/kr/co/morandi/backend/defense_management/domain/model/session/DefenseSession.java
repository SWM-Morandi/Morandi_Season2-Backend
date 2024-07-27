package kr.co.morandi.backend.defense_management.domain.model.session;

import jakarta.persistence.*;
import kr.co.morandi.backend.common.exception.MorandiException;
import kr.co.morandi.backend.common.model.BaseEntity;
import kr.co.morandi.backend.defense_information.domain.model.defense.DefenseType;
import kr.co.morandi.backend.defense_management.domain.error.SessionErrorCode;
import kr.co.morandi.backend.defense_management.domain.model.tempcode.model.Language;
import kr.co.morandi.backend.member_management.domain.model.member.Member;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class DefenseSession extends BaseEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long defenseSessionId;

    @ManyToOne(fetch = FetchType.LAZY)
    private Member member;

    private Long recordId;

    @OneToMany(mappedBy = "defenseSession", cascade = CascadeType.ALL)
    private List<SessionDetail> sessionDetails = new ArrayList<>();

    private LocalDateTime startDateTime;

    private LocalDateTime endDateTime;

    private Long lastAccessProblemNumber;

    private LocalDateTime lastAccessDateTime;

    @Enumerated(EnumType.STRING)
    private DefenseType defenseType;

    @Enumerated(EnumType.STRING)
    private ExamStatus examStatus;

    private static final Long INITIAL_ACCESS_PROBLEM_NUMBER = 1L;

    public void validateSessionOwner(Long memberId) {
        if (!this.member.getMemberId().equals(memberId)) {
            throw new MorandiException(SessionErrorCode.INVALID_SESSION_OWNER);
        }
    }

    public void updateTempCode(Long problemNumber, Language language, String code) {
        if(isTerminated()) {
            throw new MorandiException(SessionErrorCode.SESSION_ALREADY_ENDED);
        }
        SessionDetail sessionDetail = getSessionDetail(problemNumber);
        sessionDetail.updateTempCode(language, code);
    }

    public boolean isTerminated() {
        return examStatus == ExamStatus.COMPLETED;
    }

    public boolean terminateSession() {
        if(examStatus == ExamStatus.COMPLETED) {
            throw new MorandiException(SessionErrorCode.SESSION_ALREADY_ENDED);
        }
        examStatus = ExamStatus.COMPLETED;
        return true;
    }

    public SessionDetail getSessionDetail(Long problemNumber) {
        return getSessionDetails().stream()
                .filter(detail -> Objects.equals(detail.getProblemNumber(), problemNumber))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("해당 문제가 없습니다."));
    }
    public boolean hasTriedProblem(Long problemNumber) {
        return getSessionDetails().stream()
                .anyMatch(detail -> Objects.equals(detail.getProblemNumber(), problemNumber));
    }
    public void tryMoreProblem(Long problemNumber, LocalDateTime accessDateTime) {
        if (examStatus == ExamStatus.COMPLETED || accessDateTime.isAfter(endDateTime)) {
            throw new IllegalStateException("이미 종료된 시험입니다.");
        }
        // 이미 있는 시험이라면
        if (sessionDetails.stream()
                .anyMatch(sessionDetail -> sessionDetail.getProblemNumber().equals(problemNumber))) {
            return ;
        }
        sessionDetails.add(SessionDetail.create(this, problemNumber));
        lastAccessProblemNumber = problemNumber;
        lastAccessDateTime = accessDateTime;

    }

    public static DefenseSession startSession(Member member, Long recordId, DefenseType defenseType, Set<Long> problemNumbers,
                                              LocalDateTime startDateTime, LocalDateTime endDateTime) {
        return new DefenseSession(member, recordId, defenseType, problemNumbers, startDateTime, endDateTime);
    }

    @Builder
    private DefenseSession(Member member, Long recordId, DefenseType defenseType, Set<Long> problemNumbers,
                           LocalDateTime startDateTime, LocalDateTime endDateTime) {
        if(problemNumbers==null || problemNumbers.isEmpty())
            throw new IllegalArgumentException("문제 번호가 없습니다.");
        this.member = member;
        this.recordId = recordId;
        this.defenseType = defenseType;
        this.sessionDetails = problemNumbers.stream()
                .map(problemNumber -> SessionDetail.create(this, problemNumber))
                .collect(Collectors.toCollection(ArrayList::new));
        this.startDateTime = startDateTime;
        this.endDateTime = endDateTime;
        this.examStatus = ExamStatus.IN_PROGRESS;
        this.lastAccessDateTime = startDateTime;
        this.lastAccessProblemNumber = problemNumbers.stream()
                .findFirst()
                .orElse(INITIAL_ACCESS_PROBLEM_NUMBER);
    }
}
