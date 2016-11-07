package no.ssb.klass.rest.dto;

import java.time.LocalDate;
import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import no.ssb.klass.core.service.dto.Code;
import no.ssb.klass.core.util.DateRange;
import no.ssb.klass.core.util.TimeUtil;
import no.ssb.klass.rest.util.CustomLocalDateSerializer;
import no.ssb.klass.rest.util.PresentationNameBuilder;

@JsonPropertyOrder(value = { "code", "level", "name", "shortName", "presentationName" })
public class CodeItem implements Comparable<CodeItem> {
    private final String code;
    private final String name;
    private final String shortName;
    private final String presentationName;
    private final String level;

    public CodeItem(CodeItem codeItem, PresentationNameBuilder builder) {
        this.code = codeItem.getCode();
        this.name = codeItem.getName();
        this.shortName = codeItem.getShortName();
        this.level = codeItem.getLevel();
        this.presentationName = builder.presentationName(codeItem.getCode(), codeItem.getName(), codeItem
                .getShortName());
    }

    public CodeItem(CodeItem codeItem) {
        this.code = codeItem.getCode();
        this.name = codeItem.getName();
        this.shortName = codeItem.getShortName();
        this.level = codeItem.getLevel();
        this.presentationName = codeItem.getPresentationName();
    }

    public CodeItem(Code code) {
        this.code = code.getCode();
        this.name = code.getOfficialName();
        this.shortName = code.getShortName();
        this.level = code.getLevel();
        this.presentationName = "";
    }

    public String getCode() {
        return code;
    }

    public String getShortName() {
        return shortName;
    }

    public String getPresentationName() {
        return presentationName;
    }

    public String getLevel() {
        return level;
    }

    public String getName() {
        return name;
    }

    @Override
    public int hashCode() {
        return Objects.hash(code, name, level);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }

        CodeItem other = (CodeItem) obj;
        return Objects.equals(this.code, other.code) && Objects.equals(this.name, other.name) && Objects.equals(
                this.level, other.level);
    }

    @Override
    public int compareTo(CodeItem other) {
        return code.compareTo(other.code);
    }

    /**
     * A CodeItem that has a valid range
     */
    @JsonPropertyOrder(value = { "code", "level", "name", "shortName", "presentationName", "validFrom", "validTo" })
    public static class RangedCodeItem extends CodeItem {
        private final DateRange dateRange;

        public RangedCodeItem(RangedCodeItem codeItem, DateRange newDateRange) {
            super(codeItem);
            this.dateRange = newDateRange;
        }

        public RangedCodeItem(RangedCodeItem codeItem, PresentationNameBuilder builder) {
            super(codeItem, builder);
            this.dateRange = codeItem.getDateRange();
        }

        public RangedCodeItem(Code code) {
            super(code);
            this.dateRange = code.getDateRange();
        }

        @JsonSerialize(using = CustomLocalDateSerializer.class)
        public LocalDate getValidFrom() {
            if (TimeUtil.isMinDate(dateRange.getFrom())) {
                return null;
            }
            return dateRange.getFrom();
        }

        @JsonSerialize(using = CustomLocalDateSerializer.class)
        public LocalDate getValidTo() {
            if (TimeUtil.isMaxDate(dateRange.getTo())) {
                return null;
            }
            return dateRange.getTo();
        }

        @JsonIgnore
        public DateRange getDateRange() {
            return dateRange;
        }
    }
}