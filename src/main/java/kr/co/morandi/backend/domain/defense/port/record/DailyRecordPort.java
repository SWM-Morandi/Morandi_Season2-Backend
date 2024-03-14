package kr.co.morandi.backend.domain.defense.port.record;

import kr.co.morandi.backend.domain.member.Member;
import kr.co.morandi.backend.domain.record.dailydefense.DailyRecord;

import java.time.LocalDate;
import java.util.Optional;

public interface DailyRecordPort {
    DailyRecord saveDailyRecord(DailyRecord dailyRecord);
    Optional<DailyRecord> findDailyRecord(Member member, LocalDate date);
}
