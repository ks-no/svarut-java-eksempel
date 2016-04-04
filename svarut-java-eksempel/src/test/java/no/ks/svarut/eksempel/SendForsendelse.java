package no.ks.svarut.eksempel;

import no.ks.svarut.services.Forsendelse;
import no.ks.svarut.services.ForsendelseStatus;
import no.ks.svarut.services.ForsendelsesServiceV4;
import org.junit.Test;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class SendForsendelse {

    @Test
    public void testSendEksempelUkryptertForsendelseV4() {
        ForsendelsesServiceV4 service = ForsendelseService.create();
        Forsendelse forsendelse = ForsendelseMock.createUkryptert();

        try {
            String forsendelsesid = service.sendForsendelse(forsendelse);
            System.out.println(forsendelsesid);
            assertNotNull("SendForsendelse skal returnere forsendelses id", forsendelsesid);

            ForsendelseStatus status = service.retrieveForsendelseStatus(forsendelsesid);
            System.out.println(status);
            assertNotNull("RetrieveForsendelseStatus skal returnere status", status);
        } catch (Exception e) {
            System.out.println(e.getMessage());
            assertTrue("En feil oppstod", false);
        }
    }

    @Test
    public void testSendEksempelKryptertForsendelseV4() {
        ForsendelsesServiceV4 service = ForsendelseService.create();

        try {
            Forsendelse forsendelse = ForsendelseMock.createKryptert();
            String forsendelsesid = service.sendForsendelse(forsendelse);
            System.out.println(forsendelsesid);
            assertNotNull("SendForsendelse skal returnere forsendelses id", forsendelsesid);

            ForsendelseStatus status = service.retrieveForsendelseStatus(forsendelsesid);
            System.out.println(status);
            assertNotNull("RetrieveForsendelseStatus skal returnere status", status);
        } catch (Exception e) {
            System.out.println(e.getMessage());
            assertTrue("En feil oppstod", false);
        }
    }
}
