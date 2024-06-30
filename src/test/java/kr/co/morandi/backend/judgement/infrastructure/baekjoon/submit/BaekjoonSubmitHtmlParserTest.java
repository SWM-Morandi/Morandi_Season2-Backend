package kr.co.morandi.backend.judgement.infrastructure.baekjoon.submit;

import kr.co.morandi.backend.common.exception.MorandiException;
import kr.co.morandi.backend.judgement.domain.error.SubmitErrorCode;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.*;

class BaekjoonSubmitHtmlParserTest {

    @DisplayName("CSRF 키를 정상적으로 파싱한다.")
    @Test
    void parseCsrfKeyInSubmitPage_validResponse() {
        // given
        String validHtml = "<html><body><input name='csrf_key' value='validCsrfKey' /></body></html>";

        // when
        String csrfKey = BaekjoonSubmitHtmlParser.parseCsrfKeyInSubmitPage(validHtml);

        // then
        assertThat(csrfKey).isEqualTo("validCsrfKey");
    }

    @DisplayName("CSRF 키가 없는 경우 예외를 던진다.")
    @Test
    void parseCsrfKeyInSubmitPage_missingCsrfKey() {
        // given
        String invalidHtml = "<html><body></body></html>";

        // when & then
        assertThatThrownBy(() -> BaekjoonSubmitHtmlParser.parseCsrfKeyInSubmitPage(invalidHtml))
                .isInstanceOf(MorandiException.class)
                .hasMessage(SubmitErrorCode.CSRF_KEY_NOT_FOUND.getMessage());
    }

    @DisplayName("null 응답인 경우 예외를 던진다.")
    @Test
    void parseCsrfKeyInSubmitPage_nullResponse() {
        // given
        String nullResponse = null;

        // when & then
        assertThatThrownBy(() -> BaekjoonSubmitHtmlParser.parseCsrfKeyInSubmitPage(nullResponse))
                .isInstanceOf(MorandiException.class)
                .hasMessage(SubmitErrorCode.BAEKJOON_SUBMIT_PAGE_ERROR.getMessage());
    }

    @DisplayName("솔루션 아이디를 정상적으로 파싱한다.")
    @Test
    void parseSolutionIdFromHtml_validHtml() {
        // given
        String validHtml = """
                <table id="status-table">
                    <tbody>
                        <tr>
                            <td>123456</td>
                        </tr>
                    </tbody>
                </table>
                """;

        // when
        String solutionId = BaekjoonSubmitHtmlParser.parseSolutionIdFromHtml(validHtml);

        // then
        assertThat(solutionId).isEqualTo("123456");
    }

    @DisplayName("솔루션 아이디가 없는 경우 예외를 던진다.")
    @Test
    void parseSolutionIdFromHtml_missingSolutionId() {
        // given
        String invalidHtml = """
                <table id="status-table">
                    <tbody>
                        <tr>
                            <td></td>
                        </tr>
                    </tbody>
                </table>
                """;

        // when & then
        assertThatThrownBy(() -> BaekjoonSubmitHtmlParser.parseSolutionIdFromHtml(invalidHtml))
                .isInstanceOf(MorandiException.class)
                .hasMessage(SubmitErrorCode.CANT_FIND_SOLUTION_ID.getMessage());
    }

    @DisplayName("상태 테이블이 없는 경우 예외를 던진다.")
    @Test
    void parseSolutionIdFromHtml_missingStatusTable() {
        // given
        String invalidHtml = "<html><body></body></html>";

        // when & then
        assertThatThrownBy(() -> BaekjoonSubmitHtmlParser.parseSolutionIdFromHtml(invalidHtml))
                .isInstanceOf(MorandiException.class)
                .hasMessage(SubmitErrorCode.CANT_FIND_SOLUTION_ID.getMessage());
    }

    @DisplayName("첫 번째 행이 없는 경우 예외를 던진다.")
    @Test
    void parseSolutionIdFromHtml_missingFirstRow() {
        // given
        String invalidHtml = """
            <table id="status-table">
                <tbody>
                </tbody>
            </table>
            """;

        // when & then
        assertThatThrownBy(() -> BaekjoonSubmitHtmlParser.parseSolutionIdFromHtml(invalidHtml))
                .isInstanceOf(MorandiException.class)
                .hasMessage(SubmitErrorCode.CANT_FIND_SOLUTION_ID.getMessage());
    }

    @DisplayName("solutionIdElement가 null인 경우 예외를 던진다.")
    @Test
    void parseSolutionIdFromHtml_nullSolutionIdElement() {
        // given
        String invalidHtml = """
            <table id="status-table">
                <tbody>
                    <tr>
                    </tr>
                </tbody>
            </table>
            """;

        // when & then
        assertThatThrownBy(() -> BaekjoonSubmitHtmlParser.parseSolutionIdFromHtml(invalidHtml))
                .isInstanceOf(MorandiException.class)
                .hasMessage(SubmitErrorCode.CANT_FIND_SOLUTION_ID.getMessage());
    }

    @DisplayName("solutionIdElement의 텍스트가 비어있는 경우 예외를 던진다.")
    @Test
    void parseSolutionIdFromHtml_emptySolutionIdElement() {
        // given
        String invalidHtml = """
            <table id="status-table">
                <tbody>
                    <tr>
                        <td></td>
                    </tr>
                </tbody>
            </table>
            """;

        // when & then
        assertThatThrownBy(() -> BaekjoonSubmitHtmlParser.parseSolutionIdFromHtml(invalidHtml))
                .isInstanceOf(MorandiException.class)
                .hasMessage(SubmitErrorCode.CANT_FIND_SOLUTION_ID.getMessage());
    }
}
