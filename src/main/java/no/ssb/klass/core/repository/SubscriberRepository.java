package no.ssb.klass.core.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import no.ssb.klass.core.model.ClassificationSeries;
import no.ssb.klass.core.model.Subscriber;

@Repository
public interface SubscriberRepository extends JpaRepository<Subscriber, Long> {
    Optional<Subscriber> findOneByEmail(String email);

    @Query("select s from Subscriber s join s.subscriptions as su where su.classification = ?1 and su.verification = 'VALID'")
    List<Subscriber> findVerifiedSubscribersOfClassification(ClassificationSeries classification);
}
