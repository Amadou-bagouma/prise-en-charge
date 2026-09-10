package com.mycompany.myapp.domain;

import static com.mycompany.myapp.domain.DemandePriseEnChargeTestSamples.*;
import static com.mycompany.myapp.domain.NotificationTestSamples.*;
import static com.mycompany.myapp.domain.TacheTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.mycompany.myapp.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class NotificationTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Notification.class);
        Notification notification1 = getNotificationSample1();
        Notification notification2 = new Notification();
        assertThat(notification1).isNotEqualTo(notification2);

        notification2.setId(notification1.getId());
        assertThat(notification1).isEqualTo(notification2);

        notification2 = getNotificationSample2();
        assertThat(notification1).isNotEqualTo(notification2);
    }

    @Test
    void demandeTest() {
        Notification notification = getNotificationRandomSampleGenerator();
        DemandePriseEnCharge demandePriseEnChargeBack = getDemandePriseEnChargeRandomSampleGenerator();

        notification.setDemande(demandePriseEnChargeBack);
        assertThat(notification.getDemande()).isEqualTo(demandePriseEnChargeBack);

        notification.demande(null);
        assertThat(notification.getDemande()).isNull();
    }

    @Test
    void tacheTest() {
        Notification notification = getNotificationRandomSampleGenerator();
        Tache tacheBack = getTacheRandomSampleGenerator();

        notification.setTache(tacheBack);
        assertThat(notification.getTache()).isEqualTo(tacheBack);

        notification.tache(null);
        assertThat(notification.getTache()).isNull();
    }
}
