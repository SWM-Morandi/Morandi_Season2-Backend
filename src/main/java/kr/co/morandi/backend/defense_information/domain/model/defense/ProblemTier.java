package kr.co.morandi.backend.defense_information.domain.model.defense;

import kr.co.morandi.backend.common.exception.MorandiException;
import kr.co.morandi.backend.problem_information.domain.model.error.ProblemErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;
import java.util.List;

@Getter
@RequiredArgsConstructor
public enum ProblemTier {
    UNRANKED(0),
    B5(1),B4(2),B3(3),B2(4),B1(5),
    S5(6),S4(7),S3(8),S2(9),S1(10),
    G5(11),G4(12),G3(13),G2(14),G1(15),
    P5(16),P4(17),P3(18),P2(19),P1(20),
    D5(21),D4(22),D3(23),D2(24),D1(25),
    R5(26),R4(27),R3(28),R2(29),R1(30);

    private final int tier;

    private static final List<ProblemTier> VALUES = Arrays.asList(values());

    public static List<ProblemTier> tierRangeOf(ProblemTier start, ProblemTier end) {
        if (start.tier > end.tier)
            throw new MorandiException(ProblemErrorCode.PROBLEM_TIER_ERROR);

        return VALUES.stream()
                .filter(tier -> tier.tier >= start.tier && tier.tier <= end.tier)
                .toList();
    }
}
