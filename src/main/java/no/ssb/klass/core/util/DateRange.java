package no.ssb.klass.core.util;

import static com.google.common.base.Preconditions.*;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

import com.google.common.collect.Lists;

/**
 * Represents a date range. From is inclusive and to is exclusive |-->.
 *
 */
public final class DateRange {
    private final LocalDate from;
    private final LocalDate to;
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ISO_LOCAL_DATE;

    private DateRange(LocalDate from, LocalDate to) {
        checkNotNull(from);
        checkNotNull(to);
        if (from.equals(to) || from.isAfter(to)) {
            throw new IllegalArgumentException("From is equal or after to. From: " + from + " To: " + to);
        }
        this.from = from;
        this.to = to;
    }

    public boolean overlaps(DateRange other) {
        if (other.to.isAfter(from)) {
            if (other.from.isBefore(to)) {
                return true;
            }
        }
        return false;
    }

    public LocalDate getFrom() {
        return from;
    }

    public LocalDate getTo() {
        return to;
    }

    public DateRange subRange(DateRange other) {
        if (!overlaps(other)) {
            throw new IllegalArgumentException("dateRanges do not overlap. This: " + this + ". Other: " + other);
        }
        LocalDate highestFrom = TimeUtil.max(Lists.newArrayList(from, other.getFrom()));
        LocalDate lowestTo = TimeUtil.min(Lists.newArrayList(to, other.getTo()));
        return new DateRange(highestFrom, lowestTo);
    }

    public boolean contains(LocalDate date) {
        if (from.isBefore(date) || from.equals(date)) {
            if (to.isAfter(date)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public int hashCode() {
        return Objects.hash(from, to);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }

        DateRange other = (DateRange) obj;
        return Objects.equals(this.from, other.from) && Objects.equals(this.to, other.to);
    }

    @Override
    public String toString() {
        String fromString = TimeUtil.isMinDate(from) ? "min" : from.format(DATE_FORMATTER);
        String toString = TimeUtil.isMaxDate(to) ? "max" : to.format(DATE_FORMATTER);
        return "[from=" + fromString + ", to=" + toString + "]";
    }

    public static DateRange create(LocalDate from, LocalDate to) {
        if (from == null) {
            from = TimeUtil.createMinDate();
        }
        if (to == null) {
            to = TimeUtil.createMaxDate();
        }

        return new DateRange(from, to);
    }

    /**
     * From and to are specified in format yyyy-MM-dd
     */
    public static DateRange create(String from, String to) {
        return create(TimeUtil.createDate(from), TimeUtil.createDate(to));
    }
}
