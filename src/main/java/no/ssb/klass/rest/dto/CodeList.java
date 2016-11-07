package no.ssb.klass.rest.dto;

import static java.util.stream.Collectors.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import no.ssb.klass.core.service.dto.Code;
import no.ssb.klass.core.util.DateRange;
import no.ssb.klass.core.util.TimeUtil;
import no.ssb.klass.rest.dto.CodeItem.RangedCodeItem;
import no.ssb.klass.rest.util.PresentationNameBuilder;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import com.google.common.base.Strings;

@JacksonXmlRootElement(localName = "codeList")
public class CodeList {
    private static final String RANGE_REGEX = "\\s*(?<from>[^,\\s]*)\\s*\\-\\s*(?<to>[^,\\s]*)";
    private final char csvSeparator;
    private final boolean displayWithValidRange;
    private final List<RangedCodeItem> codeItems;

    public CodeList(String csvSeparator, boolean displayWithValidRange) {
        if (csvSeparator.toCharArray().length != 1) {
            throw new IllegalArgumentException("Separator must be a single character");
        }
        this.csvSeparator = csvSeparator.charAt(0);
        this.displayWithValidRange = displayWithValidRange;
        this.codeItems = new ArrayList<>();
    }

    public CodeList(char csvSeparator, boolean displayWithValidRange, List<RangedCodeItem> codes) {
        this.csvSeparator = csvSeparator;
        this.displayWithValidRange = displayWithValidRange;
        this.codeItems = codes;
    }

    @JacksonXmlElementWrapper(useWrapping = false)
    @JacksonXmlProperty(localName = "codeItem")
    public List<? extends CodeItem> getCodes() {
        if (displayWithValidRange) {
            return codeItems;
        }
        return codeItems.stream().map(i -> new CodeItem(i)).collect(toList());
    }

    @JsonIgnore
    public char getCsvSeparator() {
        return csvSeparator;
    }

    public CodeList merge(CodeList other) {
        List<RangedCodeItem> combined = new ArrayList<>(codeItems);
        combined.addAll(other.codeItems);
        return newCodeList(combined);
    }

    public CodeList compress() {
        Map<RangedCodeItem, List<RangedCodeItem>> grouped = codeItems.stream().collect(groupingBy(
                codeItem -> codeItem));
        return newCodeList(grouped.entrySet().stream().map(entry -> combineCodeItems(entry.getKey(), entry.getValue()))
                .collect(toList()));
    }

    private RangedCodeItem combineCodeItems(RangedCodeItem base, List<RangedCodeItem> codeItems) {
        // TODO kmgv need to check dateRanges of codeItems, and group those that are back to back.
        DateRange dateRange = DateRange.create(minValidFrom(codeItems), maxValidTo(codeItems));
        return new RangedCodeItem(base, dateRange);
    }

    public CodeList limit(DateRange dateRange) {
        return newCodeList(codeItems.stream().map(codeItem -> new RangedCodeItem(codeItem, codeItem.getDateRange()
                .subRange(dateRange))).collect(toList()));
    }

    public CodeList convert(List<Code> codes) {
        List<RangedCodeItem> result = new ArrayList<>();
        for (Code code : codes) {
            result.add(new RangedCodeItem(code));
        }
        return newCodeList(result);
    }

    public CodeList filterOnLevel(String level) {
        if (Strings.isNullOrEmpty(level)) {
            return this;
        }
        return newCodeList(codeItems.stream().filter(codeItem -> level.equals(codeItem.getLevel())).collect(toList()));
    }

    public CodeList filterOnCodes(String selectCodes) {
        if (Strings.isNullOrEmpty(selectCodes)) {
            return this;
        }

        Map<String, String> ranges = findRanges(selectCodes);
        String pattern = selectCodes
                .replaceAll(RANGE_REGEX, "") // remove ranges (stored in map above(findRanges))
                .replace(" ", "")// remove whitespace
                .replace(',', '|') // remove commas and insert OR operator
                .replace("*", "\\w*"); // replace * with any word character(any amount)
        return newCodeList(codeItems.stream()
                .filter(codeItem ->
                        Pattern.matches(pattern, codeItem.getCode())
                                || compareRanges(codeItem.getCode(), ranges))
                .collect(toList()));
    }

    private Map<String, String> findRanges(String input) {
        Map<String, String> ranges = new HashMap<>();
        Pattern p = Pattern.compile(RANGE_REGEX);
        Matcher m = p.matcher(input);
        if (m.find()) {
            String from = m.group("from").replaceAll("\\*", "");
            String to = m.group("to").replaceAll("\\*", String.valueOf(Character.MAX_VALUE));
            ranges.put(from, to);
        }
        return ranges;
    }

    private Boolean compareRanges(String value, Map<String, String> ranges) {
        for (Map.Entry<String, String> entry : ranges.entrySet()) {
            if (entry.getKey().compareTo(value) <= 0 && value.compareTo(entry.getValue()) <= 0) {
                return true;
            }
        }
        return false;
    }

    public CodeList sort() {
        Collections.sort(codeItems);
        return this;
    }

    public CodeList presentationNames(String presentationNamePattern) {
        PresentationNameBuilder presentationNameBuilder = new PresentationNameBuilder(presentationNamePattern);
        return newCodeList(codeItems.stream().map(codeItem -> new RangedCodeItem(codeItem, presentationNameBuilder))
                .collect(toList()));
    }

    private LocalDate maxValidTo(List<RangedCodeItem> codeItems) {
        return TimeUtil.max(codeItems.stream().map(codeItem -> codeItem.getDateRange().getTo()).collect(toList()));
    }

    private LocalDate minValidFrom(List<RangedCodeItem> codeItems) {
        return TimeUtil.min(codeItems.stream().map(codeItem -> codeItem.getDateRange().getFrom()).collect(toList()));
    }

    private CodeList newCodeList(List<RangedCodeItem> codeItems) {
        return new CodeList(csvSeparator, displayWithValidRange, codeItems);
    }

    public Class<?> codeItemsJavaType() {
        return displayWithValidRange ? RangedCodeItem.class : CodeItem.class;
    }
}
