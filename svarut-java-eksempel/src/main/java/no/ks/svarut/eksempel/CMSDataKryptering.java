package no.ks.svarut.eksempel;

import org.apache.commons.io.IOUtils;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.DERNull;
import org.bouncycastle.asn1.DEROctetString;
import org.bouncycastle.asn1.nist.NISTObjectIdentifiers;
import org.bouncycastle.asn1.pkcs.PKCSObjectIdentifiers;
import org.bouncycastle.asn1.pkcs.RSAESOAEPparams;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.cms.*;
import org.bouncycastle.cms.jcajce.JceCMSContentEncryptorBuilder;
import org.bouncycastle.cms.jcajce.JceKeyTransRecipientInfoGenerator;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.operator.OutputEncryptor;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.Provider;
import java.security.Security;
import java.security.cert.CertificateEncodingException;
import java.security.cert.X509Certificate;

public class CMSDataKryptering {

    private final ASN1ObjectIdentifier cmsEncryptionAlgorithm;
    private final AlgorithmIdentifier keyEncryptionScheme;

    public CMSDataKryptering() {
        Security.addProvider(new BouncyCastleProvider());
        this.keyEncryptionScheme = this.rsaesOaepIdentifier();
        this.cmsEncryptionAlgorithm = CMSAlgorithm.AES256_CBC;
    }

    private AlgorithmIdentifier rsaesOaepIdentifier() {
        AlgorithmIdentifier hash = new AlgorithmIdentifier(NISTObjectIdentifiers.id_sha256, DERNull.INSTANCE);
        AlgorithmIdentifier mask = new AlgorithmIdentifier(PKCSObjectIdentifiers.id_mgf1, hash);
        AlgorithmIdentifier pSource = new AlgorithmIdentifier(PKCSObjectIdentifiers.id_pSpecified, new DEROctetString(new byte[0]));
        RSAESOAEPparams parameters = new RSAESOAEPparams(hash, mask, pSource);
        return new AlgorithmIdentifier(PKCSObjectIdentifiers.id_RSAES_OAEP, parameters);
    }
    public byte[] krypterData(byte[] bytes, X509Certificate sertifikat, Provider p) {
        try {
            JceKeyTransRecipientInfoGenerator e = (new JceKeyTransRecipientInfoGenerator(sertifikat, this.keyEncryptionScheme)).setProvider(p);
            CMSEnvelopedDataGenerator envelopedDataGenerator = new CMSEnvelopedDataGenerator();
            envelopedDataGenerator.addRecipientInfoGenerator(e);
            OutputEncryptor contentEncryptor = (new JceCMSContentEncryptorBuilder(this.cmsEncryptionAlgorithm)).build();
            CMSEnvelopedData cmsData = envelopedDataGenerator.generate(new CMSProcessableByteArray(bytes), contentEncryptor);
            return cmsData.getEncoded();
        } catch (CertificateEncodingException var7) {
            throw new RuntimeException("Feil med mottakers sertifikat", var7);
        } catch (CMSException var8) {
            throw new RuntimeException("Kunne ikke generere Cryptographic Message Syntax for dokumentpakke", var8);
        } catch (IOException var9) {
            throw new RuntimeException(var9);
        }
    }

    public byte[] krypterData(byte[] bytes, X509Certificate sertifikat) {
        try {
            JceKeyTransRecipientInfoGenerator e = (new JceKeyTransRecipientInfoGenerator(sertifikat, this.keyEncryptionScheme)).setProvider("BC");
            CMSEnvelopedDataGenerator envelopedDataGenerator = new CMSEnvelopedDataGenerator();
            envelopedDataGenerator.addRecipientInfoGenerator(e);
            OutputEncryptor contentEncryptor = (new JceCMSContentEncryptorBuilder(this.cmsEncryptionAlgorithm)).build();

            CMSEnvelopedData cmsData = envelopedDataGenerator.generate(new CMSProcessableByteArray(bytes), contentEncryptor);
            return cmsData.getEncoded();
        } catch (CertificateEncodingException var7) {
            throw new RuntimeException("Feil med mottakers sertifikat", var7);
        } catch (CMSException var8) {
            throw new RuntimeException("Kunne ikke generere Cryptographic Message Syntax for dokumentpakke", var8);
        } catch (IOException var9) {
            throw new RuntimeException(var9);
        }
    }

    public void krypterData(OutputStream encryptedOutputStream, InputStream inputStream, X509Certificate sertifikat, Provider p) {
        try {
            JceKeyTransRecipientInfoGenerator e = (new JceKeyTransRecipientInfoGenerator(sertifikat, this.keyEncryptionScheme)).setProvider(p);
            CMSEnvelopedDataStreamGenerator envelopedDataGenerator = new CMSEnvelopedDataStreamGenerator();

            envelopedDataGenerator.addRecipientInfoGenerator(e);
            OutputEncryptor contentEncryptor = (new JceCMSContentEncryptorBuilder(this.cmsEncryptionAlgorithm)).build();
            try (final OutputStream outputStream = envelopedDataGenerator.open(encryptedOutputStream, contentEncryptor)) {
                IOUtils.copy(inputStream, outputStream);
            }

        } catch (CertificateEncodingException var7) {
            throw new RuntimeException("Feil med mottakers sertifikat", var7);
        } catch (CMSException var8) {
            throw new RuntimeException("Kunne ikke generere Cryptographic Message Syntax for dokumentpakke", var8);
        } catch (IOException var9) {
            throw new RuntimeException(var9);
        }
    }
}
