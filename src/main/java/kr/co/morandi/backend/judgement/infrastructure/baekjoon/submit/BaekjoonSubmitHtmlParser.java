package kr.co.morandi.backend.judgement.infrastructure.baekjoon.submit;

import kr.co.morandi.backend.common.exception.MorandiException;
import kr.co.morandi.backend.judgement.domain.error.SubmitErrorCode;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.springframework.stereotype.Component;

public class BaekjoonSubmitHtmlParser {
    /*
     * 제출 결과 페이지에서 솔루션 아이디를 추출하는 메소드
     * */
    public static String parseSolutionIdFromHtml(String html) {
        // HTML을 파싱합니다.
        Document doc = Jsoup.parse(html);

        // 테이블을 선택합니다.
        Element table = doc.getElementById("status-table");
        if (table == null) {
            throw new MorandiException(SubmitErrorCode.CANT_FIND_SOLUTION_ID);
        }

        // 첫 번째 행을 선택합니다.
        Element firstRow = table.select("tbody tr").first();
        if (firstRow == null) {
            throw new MorandiException(SubmitErrorCode.CANT_FIND_SOLUTION_ID);
        }

        // 첫 번째 행에서 solution-id를 추출합니다.
        Element solutionIdElement = firstRow.select("td").first(); // 첫 번째 열에 있는 것이 solution-id 입니다.

        if (solutionIdElement != null && solutionIdElement.text().isEmpty()) {
            throw new MorandiException(SubmitErrorCode.CANT_FIND_SOLUTION_ID);
        }

        if (solutionIdElement == null) {
            throw new MorandiException(SubmitErrorCode.CANT_FIND_SOLUTION_ID);
        }

        return solutionIdElement.text();

    }

    public static String parseCsrfKeyInSubmitPage(String response) {
        if (response == null) {
            throw new MorandiException(SubmitErrorCode.BAEKJOON_SUBMIT_PAGE_ERROR);
        }
        Document document = Jsoup.parse(response);

        String csrfKey = document.select("input[name=csrf_key]").attr("value");

        if (csrfKey.isEmpty()) {
            throw new MorandiException(SubmitErrorCode.CSRF_KEY_NOT_FOUND);
        }

        return csrfKey;
    }

}
