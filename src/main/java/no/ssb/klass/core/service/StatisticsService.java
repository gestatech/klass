package no.ssb.klass.core.service;

import java.time.LocalDate;

import org.springframework.data.domain.Pageable;

import no.ssb.klass.core.model.ClassificationSeries;
import no.ssb.klass.core.model.ClassificationType;
import no.ssb.klass.core.model.StatisticalUnit;
import no.ssb.klass.core.service.dto.ClassificationReportDto;
import no.ssb.klass.designer.ClassificationReportData;
import no.ssb.klass.designer.admin.UsageStatisticsData;
import no.ssb.klass.designer.admin.util.ContentUseStatisticRows;
import no.ssb.klass.designer.admin.util.ContentUseStatisticRows.ReportModeChoice;
import no.ssb.klass.designer.admin.util.UsageStatisticsRows;
import no.ssb.klass.designer.admin.util.UsageStatisticsRows.UseStatisticsModeChoice;

public interface StatisticsService {
    ContentUseStatisticRows generateContentUseStat(String section, ClassificationType classificationType);
    ClassificationReportData<?> getClassificationReport(String section, ClassificationType classificationType, ReportModeChoice operation);
    UsageStatisticsRows getUsageStatistics(LocalDate fromSearchDate, LocalDate toSearchDate);
    void addSearchWord(String searchWord, boolean hit);
    void addUseForClassification(ClassificationSeries classificationSeries);
    UsageStatisticsData getUsageStatistics(LocalDate fromSearchDate, LocalDate toSearchDate,
            UseStatisticsModeChoice operation, Pageable pageable);
    UsageStatisticsData getStaticalUnitsOverView(Pageable pageable);
    ClassificationReportData<ClassificationReportDto> getAllClassificationSeriesForStaticalUnit(StatisticalUnit statisticalUnit);
}
