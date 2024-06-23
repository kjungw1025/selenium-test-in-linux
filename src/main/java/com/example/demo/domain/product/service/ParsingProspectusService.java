package com.example.demo.domain.product.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
@RequiredArgsConstructor
@Slf4j
public class ParsingProspectusService {


    // 기초자산가격 변동성


    // 자동조기상환평가일
    public List<String> findEarlyRepaymentEvaluationDates(String targetProductSession, String url) throws IOException {
        if (targetProductSession != null && url != null) {

            Document document = fetchDocument(url);

            // 투자 설명서에서 해당 회차 상품이 몇 번째인지
            int number = findLocationOfProduct(targetProductSession, document);

            // 투자 설명서에서 모든 자동조기상환평가일 파싱
            List<List<String>> earlyRepaymentEvaluationDateList = findEarlyRepaymentEvaluationDatesList(document);

            // 해당 회차 상품의 자동조기상환평가일
            return earlyRepaymentEvaluationDateList.get(number - 1);
        } else {
            return null;
        }
    }

    private List<List<String>> findEarlyRepaymentEvaluationDatesList(Document doc) {

        List<List<String>> result = new ArrayList<>();

        List<String> keywords = List.of("자동조기상환평가일");
        List<String> optionalKeywords = List.of("차수", "차 수      ", "차 수", "상환금액", "상환금액(USD)(세전)", "상환금액(세전)");
        String midKeyword = "중간기준가격 결정일";

        Elements tables = doc.select("table");

        for (Element table : tables) {
            List<List<String>> tableData = new ArrayList<>();
            Elements rows = table.select("tr");

            for (Element row : rows) {
                List<String> rowData = new ArrayList<>();
                Elements cells = row.select("th, td");

                for (Element cell : cells) {
                    rowData.add(cell.text().strip());
                }

                tableData.add(rowData);
            }

            if (!tableData.isEmpty()) {
                List<String> header = tableData.get(0);
                boolean hasAllKeywords = new HashSet<>(header).containsAll(keywords);
                boolean hasAnyOptionalKeywords = optionalKeywords.stream().anyMatch(header::contains);

                if (hasAllKeywords && hasAnyOptionalKeywords) {
                    List<String> formattedRow = new ArrayList<>();
                    for (int j = 1; j < tableData.size(); j++) {
                        List<String> row = tableData.get(j);
                        if (row.size() < 2 || !(row.get(1).contains("년") && row.get(1).contains("월") && row.get(1).contains("일"))) continue;
                        // 동일한 날짜의 형태인 1-1차 2-2차에 대해서 우선은 문자열 통일
                        // todo : 추후 어떻게 보여줄 지 구체적인 논의 필요
                        if (Arrays.stream(row.get(0).split("-")).count() == 2) {
                            formattedRow.add(row.get(0).split("-")[0] + "차" + ": " + row.get(1));
                        } else {
                            formattedRow.add(row.get(0) + ": " + row.get(1));
                        }
                    }
                    result.add(formattedRow);
                } else {
                    for (List<String> row : tableData) {
                        String title = row.get(0);
                        if (title.contains(midKeyword) && row.size() == 2) {
                            String body = row.get(1);
                            Matcher matcher = Pattern.compile("\\d+차: \\d{4}년 \\d{2}월 \\d{2}일").matcher(body);
                            List<String> dates = new ArrayList<>();

                            if (title.contains("월수익 중간기준가격 결정일")) {
                                // 월지급식의 자동조기상환가격 결정일
                                int idx = 1, turn = 1;
                                while (matcher.find()) {
                                    if (idx % 6 == 0) {
                                        String originalMatch = matcher.group();
                                        String updatedMatch = originalMatch.replaceFirst("\\d+차", turn + "차");
                                        dates.add(updatedMatch);
                                        turn++;
                                    }
                                    idx++;
                                }
                            } else {
                                while (matcher.find()) {
                                    dates.add(matcher.group());
                                }
                            }
                            result.add(dates);
                            break;
                        }
                    }
                }
            }
        }

        return result;
    }

    private int findLocationOfProduct(String targetProductSession, Document doc) {

        int result = 0;

        Elements trElements = doc.select("tr:has(td:matchesOwn(주식회사|증권|증\\s권|주\\s식\\s회\\s사))");

        if (!trElements.isEmpty()) {

            // 찾은 tr 태그의 인덱스를 구함
            int indexOfTargetTr = Objects.requireNonNull(trElements.first()).elementSiblingIndex();
            // 바로 다음 tr 태그를 가져옴
            Element nextTr = doc.select("tr").get(indexOfTargetTr + 1);

            String innerHtml = nextTr.html();
            String[] parts = innerHtml.split("<br>");

            List<String> productSessionList = new ArrayList<>();
            for (String part : parts) {
                Matcher matcher = Pattern.compile("(?<=\\s|제|회|호|^)\\d+(?=\\s|제|회|호|$)").matcher(part);

                while (matcher.find()) {
                    productSessionList.add(matcher.group());
                }
            }
            if (productSessionList.isEmpty()) {
                // 제29589-29598회 형태
                Matcher matcher = Pattern.compile("\\d+-\\d+").matcher(nextTr.text());
                if (matcher.find()) {
                    String found = matcher.group();
                    String[] foundList = found.split("-");
                    int number1 = Integer.parseInt(foundList[0]);

                    result = Integer.parseInt(targetProductSession) - number1 + 1;
                }
            } else {
                result = productSessionList.indexOf(targetProductSession) + 1;
            }


        } else {
            log.error("findLocationOfProduct : 해당 조건을 만족하는 tr 태그를 찾을 수 없습니다.");
        }

        return result;
    }

    private Document fetchDocument(String url) throws IOException {
        return Jsoup.connect(url)
                .userAgent("Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/605.1.15 (KHTML, like Gecko) Version/14.1.1 Safari/605.1.15")
                .timeout(10000)
                .maxBodySize(0)
                .get();
    }
}
