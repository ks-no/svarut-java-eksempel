package no.ks.svarut.eksempel;

import com.sun.istack.ByteArrayDataSource;
import no.ks.svarut.services.*;
import org.apache.commons.io.IOUtils;

import javax.activation.DataHandler;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;

public class ForsendelseMock {

	public static Forsendelse createUkryptert(){
		Forsendelse forsendelse = new Forsendelse();
		forsendelse.setTittel("Tittel");
		forsendelse.setAvgivendeSystem("Avgivende system");
		forsendelse.getDokumenter().add(createDokument());
		forsendelse.setMottaker(createPrivatPerson());
		forsendelse.setPrintkonfigurasjon(createPrintkonfigurasjon());
		forsendelse.setKrevNiva4Innlogging(false);
		forsendelse.setKryptert(false);
		return forsendelse;
	}

	public static Forsendelse createKryptert() throws IOException, CertificateException {
		Forsendelse forsendelse = new Forsendelse();
		forsendelse.setTittel("Tittel");
		forsendelse.setAvgivendeSystem("Avgivende system");
		forsendelse.getDokumenter().add(createKryptertDokument());
		forsendelse.setMottaker(createPrivatPerson());
		forsendelse.setPrintkonfigurasjon(createPrintkonfigurasjon());
		forsendelse.setKrevNiva4Innlogging(false);
		forsendelse.setKryptert(true);
		return forsendelse;
	}

	private static Printkonfigurasjon createPrintkonfigurasjon() {
		Printkonfigurasjon printkonfigurasjon = new Printkonfigurasjon();
		printkonfigurasjon.setBrevtype(Brevtype.APOST);
		return printkonfigurasjon;
	}

	private static PrivatPerson createPrivatPerson() {
		PrivatPerson privatPerson = new PrivatPerson();
		privatPerson.setAdresse1("Adresse1");
		privatPerson.setNavn("Navn");
		privatPerson.setPostnr("1234");
		privatPerson.setPoststed("Poststed");
		privatPerson.setFodselsnr("12345678901");
		return privatPerson;
	}

	private static Dokument createDokument() {
		Dokument dokument = new Dokument();
		dokument.setData(loadPdfFromClasspath("test.pdf"));
		dokument.setMimetype("application/pdf");
		dokument.setFilnavn("test.pdf");
		return dokument;
	}

	private static Dokument createKryptertDokument() throws IOException, CertificateException {
		Dokument dokument = new Dokument();
		dokument.setMimetype("application/pdf");
		dokument.setFilnavn("test.pdf");

		X509Certificate sertifikat = hentX509Certificate();
		krypterDokumentMedSertifikat(dokument, sertifikat);
		return dokument;
	}

	private static X509Certificate hentX509Certificate() throws IOException, CertificateException {
		byte[] sertifikatBytes = IOUtils.toByteArray(getInputStreamForFileFromClasspath("svarut_public.pem"));
		return (X509Certificate) CertificateFactory.getInstance("X509").generateCertificate(new ByteArrayInputStream(sertifikatBytes));
	}

	private static void krypterDokumentMedSertifikat(Dokument dokument, X509Certificate sertifikat) throws IOException {
		CMSDataKryptering kryptering = new CMSDataKryptering();
		byte[] kryptertData = kryptering.krypterData(IOUtils.toByteArray(getInputStreamForFileFromClasspath("test.pdf")), sertifikat);
		dokument.setData(new DataHandler(new ByteArrayDataSource(kryptertData, dokument.getMimetype())));
	}

	private static DataHandler loadPdfFromClasspath(String resource){
		URL url = Thread.currentThread().getContextClassLoader().getResource(resource);
		return new DataHandler(url);
	}

	public static InputStream getInputStreamForFileFromClasspath(String resource){
		return Thread.currentThread().getContextClassLoader().getResourceAsStream(resource);
	}

}
