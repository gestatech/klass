package no.ssb.klass.core.service;

import static com.google.common.base.Preconditions.*;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import no.ssb.klass.core.model.ClassificationAccessCounter;
import no.ssb.klass.core.model.ClassificationSeries;
import no.ssb.klass.core.model.ClassificationType;
import no.ssb.klass.core.model.SearchWords;
import no.ssb.klass.core.model.StatisticalUnit;
import no.ssb.klass.core.repository.ClassificationAccessRepository;
import no.ssb.klass.core.repository.ClassificationSeriesRepository;
import no.ssb.klass.core.repository.SearchWordsRepository;
import no.ssb.klass.core.repository.StatisticalUnitRepository;
import no.ssb.klass.core.service.dto.ClassificationReportDto;
import no.ssb.klass.core.service.dto.ClassificationVersionReportDto;
import no.ssb.klass.designer.ClassificationReportData;
import no.ssb.klass.designer.VersionReportData;
import no.ssb.klass.designer.admin.UsageStatisticsData;
import no.ssb.klass.designer.admin.util.ContentUseStatisticRows;
import no.ssb.klass.designer.admin.util.ContentUseStatisticRows.ReportModeChoice;
import no.ssb.klass.designer.admin.util.UsageStatisticsRows;
import no.ssb.klass.designer.admin.util.UsageStatisticsRows.UseStatisticsModeChoice;

@Service
@Transactional(readOnly = true)
public class StatisticsServiceImpl implements StatisticsService {

    private final ClassificationSeriesRepository classificationRepository;
    private final ClassificationAccessRepository classificationAccessRepository;
    private final SearchWordsRepository searchWordsRepository;
    private final StatisticalUnitRepository statisticalUnitRepository;
    private final Boolean MISSED = Boolean.FALSE;

    @Autowired
    public StatisticsServiceImpl(ClassificationSeriesRepository classificationRepository,
            ClassificationAccessRepository statisticalClassificationAccessCounterRepository,
            SearchWordsRepository searchWordsRepository, StatisticalUnitRepository statisticalUnitRepository) {
        this.classificationRepository = classificationRepository;
        this.classificationAccessRepository = statisticalClassificationAccessCounterRepository;
        this.searchWordsRepository = searchWordsRepository;
        this.statisticalUnitRepository = statisticalUnitRepository;
    }

    @Override
    public ContentUseStatisticRows generateContentUseStat(String section, ClassificationType classificationType) {
        int numberOfClassifications = classificationRepository.finNumberOfClassifications(classificationType, section);
        int numberOfPublishedClassifications = classificationRepository.findNumberOfPublishedClassifications(
                classificationType, section);
        int numberOfPublishedVersionsAnyLanguages = classificationRepository.findNumberOfPublishedVersionsAnyLanguages(
                classificationType, section);
        int numberOfPublishedVersionsAllLanguages = classificationRepository.findNumberOfPublishedVersionsAllLanguages(
                classificationType, section);
        return new ContentUseStatisticRows(numberOfClassifications, numberOfPublishedClassifications,
                numberOfClassifications - numberOfPublishedClassifications,
                numberOfPublishedVersionsAnyLanguages - numberOfPublishedVersionsAllLanguages);
    }

    @Override
    public ClassificationReportData<?> getClassificationReport(String section, ClassificationType classificationType,
            ReportModeChoice operation) {
        ClassificationReportData<? extends ClassificationReportDto> report = null;
        List<ClassificationReportDto> resultList = null;
        switch (operation) {
        case TOTAL:
            resultList = classificationRepository.getClassificationReport(classificationType, section);
            report = new ClassificationReportData<ClassificationReportDto>(resultList);
            break;
        case PUBLISHED:
            resultList = classificationRepository.getPublishedClassificationReport(classificationType, section);
            report = new ClassificationReportData<ClassificationReportDto>(resultList);
            break;
        case UNPUBLISHED:
            List<ClassificationReportDto> totalList = classificationRepository.getClassificationReport(classificationType, section);
            List<ClassificationReportDto> publishedList = classificationRepository.getPublishedClassificationReport(classificationType,
                    section);
            report = new ClassificationReportData<ClassificationReportDto>(totalList);
            ClassificationReportData<ClassificationReportDto> published = new ClassificationReportData<ClassificationReportDto>(publishedList);
            report.diff(published);
            break;
        case MISSING_LANG:
            List<ClassificationVersionReportDto> publishedAnyLanguageList = classificationRepository.getPublishedVersionsAnyLanguages(
                    classificationType, section);
            List<ClassificationVersionReportDto> publishedAllLanguageList = classificationRepository.getPublishedVersionsAllLanguages(
                    classificationType, section);
            report = new VersionReportData(publishedAnyLanguageList);
            ClassificationReportData<ClassificationVersionReportDto> publishedAllLanguage = new VersionReportData(publishedAllLanguageList);
            report.diff(publishedAllLanguage);
            break;
        default:
        }
        report.sort();
        return report;
    }

    @Override
    public UsageStatisticsRows getUsageStatistics(LocalDate fromSearchDate, LocalDate toSearchDate) {
        checkNotNull(fromSearchDate);
        checkNotNull(toSearchDate);
        Instant fromDate = fromSearchDate.atStartOfDay(ZoneId.systemDefault()).toInstant();
        Instant toDate = toSearchDate.plusDays(1).atStartOfDay(ZoneId.systemDefault()).toInstant();
        int usageSumC = classificationAccessRepository.getAccessSum(Date.from(fromDate), Date.from(toDate));
        int numberOfMisses = searchWordsRepository.getNumberOfMiss(Date.from(fromDate), Date.from(toDate));
        int numberOfSearchWords = searchWordsRepository.getNumberOfSearchWords(Date.from(fromDate), Date.from(toDate));
        return new UsageStatisticsRows(usageSumC, numberOfMisses, numberOfSearchWords);
    }
    
    @Override
    public void addUseForClassification(ClassificationSeries classificationSeries) {
        ClassificationAccessCounter loggUse = new ClassificationAccessCounter(classificationSeries);
        classificationAccessRepository.save(loggUse);
    }
    
   
    
    @Override
    public void addSearchWord(String searchWord, boolean hit) {
        SearchWords searchWords = new SearchWords(searchWord, hit);
        searchWordsRepository.save(searchWords);
    }
    
    @Override
    public UsageStatisticsData getUsageStatistics(LocalDate fromSearchDate, LocalDate toSearchDate, 
            UseStatisticsModeChoice operation, Pageable pageable) {
        UsageStatisticsData resultList = null;
        Instant fromDate = fromSearchDate.atStartOfDay(ZoneId.systemDefault()).toInstant();
        Instant toDate = toSearchDate.plusDays(1).atStartOfDay(ZoneId.systemDefault()).toInstant();
        switch (operation) {
        case TOTAL_CLASSIFIC:
            resultList = new UsageStatisticsData(classificationAccessRepository
                    .getClassificationsCount(Date.from(fromDate), Date.from(toDate), pageable));
            break;
        case NUMBEROF_SEARCH_RETURNED_NULL:
            resultList = new UsageStatisticsData(searchWordsRepository.getSearchWords(MISSED, 
                    Date.from(fromDate), Date.from(toDate), pageable));            
            break;
        case TOTAL_SEARCH_WORDS:
            resultList = new UsageStatisticsData(searchWordsRepository.getSearchWords(
                    Date.from(fromDate), Date.from(toDate), pageable));
            break;
        default:
        }
        return resultList;
    }

    @Override
    public UsageStatisticsData getStaticalUnitsOverView(Pageable pageable) {        
        return new UsageStatisticsData(statisticalUnitRepository.getStaticalUnitsOverView(pageable));
    }    
    
    @Override
    public ClassificationReportData<ClassificationReportDto> getAllClassificationSeriesForStaticalUnit(StatisticalUnit statisticalUnit) {
        List<ClassificationReportDto> raw = statisticalUnitRepository.getAllClassificationSeriesForStaticalUnit(statisticalUnit);
        ClassificationReportData<ClassificationReportDto> report = new ClassificationReportData<ClassificationReportDto>(raw);
        report.sort();
        return report;
    }
}
