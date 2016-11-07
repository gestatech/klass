package no.ssb.klass.rest.dto.hal;

import java.util.List;
import java.util.stream.Collectors;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import no.ssb.klass.core.model.ClassificationItem;
import no.ssb.klass.core.model.CorrespondenceMap;
import no.ssb.klass.core.model.Language;

@JsonPropertyOrder(value = { "sourceCode", "sourceName", "targetCode", "targetName", })
public class CorrespondenceMapResource {
    private final String sourceCode;
    private final String sourceName;
    private final String targetCode;
    private final String targetName;

    public CorrespondenceMapResource(CorrespondenceMap correspondenceMap, Language language) {
        ClassificationItem sourceItem = correspondenceMap.getSource().orElse(null);
        this.sourceCode = sourceItem == null ? null : sourceItem.getCode();
        this.sourceName = sourceItem == null ? null : sourceItem.getOfficialName(language);

        ClassificationItem targetItem = correspondenceMap.getTarget().orElse(null);
        this.targetCode = targetItem == null ? null : targetItem.getCode();
        this.targetName = targetItem == null ? null : targetItem.getOfficialName(language);
    }

    public String getSourceCode() {
        return sourceCode;
    }

    public String getSourceName() {
        return sourceName;
    }

    public String getTargetCode() {
        return targetCode;
    }

    public String getTargetName() {
        return targetName;
    }

    public static List<CorrespondenceMapResource> convert(List<CorrespondenceMap> correspondenceMaps,
            Language language) {
        return correspondenceMaps.stream().map(correspondenceMap -> new CorrespondenceMapResource(correspondenceMap,
                language)).collect(Collectors.toList());
    }
}
