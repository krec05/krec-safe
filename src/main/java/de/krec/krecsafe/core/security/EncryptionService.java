package de.krec.krecsafe.core.security;

import de.krec.krecsafe.config.EncryptionProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.CipherOutputStream;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;

@Service
public class EncryptionService {

	private final EncryptionProperties encryptionProperties;

	@Autowired
	public EncryptionService(EncryptionProperties encryptionProperties) {
		this.encryptionProperties = encryptionProperties;
	}

	public void encryptFile(Path decryptedPath, Path encryptedPath)
	throws NoSuchAlgorithmException, InvalidKeySpecException, NoSuchPaddingException,
		   InvalidAlgorithmParameterException, InvalidKeyException, IOException {
		SecureRandom secureRandom = new SecureRandom();

		byte[] salt = new byte[16];
		byte[] iv = new byte[12];

		secureRandom.nextBytes(salt);
		secureRandom.nextBytes(iv);

		SecretKey secretKey = deriveKeyFromPassword(salt);

		Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
		GCMParameterSpec gcmParameterSpec = new GCMParameterSpec(128, iv);
		cipher.init(Cipher.ENCRYPT_MODE, secretKey, gcmParameterSpec);

		try (FileOutputStream fos = new FileOutputStream(encryptedPath.toFile());
			 DataOutputStream dos = new DataOutputStream(fos);
			 CipherOutputStream cos = new CipherOutputStream(dos, cipher);
			 FileInputStream fis = new FileInputStream(decryptedPath.toFile())) {
			// header
			dos.writeInt(salt.length);
			dos.write(salt);
			dos.writeInt(iv.length);
			dos.write(iv);

			// data
			fis.transferTo(cos);
		}

	}

	public void decryptFile(Path encryptedPath, Path decryptedPath)
	throws IOException, NoSuchAlgorithmException, InvalidKeySpecException,
		   NoSuchPaddingException, InvalidAlgorithmParameterException,
		   InvalidKeyException {
		try (FileInputStream fis = new FileInputStream(encryptedPath.toFile());
			 DataInputStream dis = new DataInputStream(fis)) {
			byte[] salt = dis.readNBytes(dis.readInt());
			byte[] iv = dis.readNBytes(dis.readInt());

			SecretKey secretKey = deriveKeyFromPassword(salt);

			Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
			cipher.init(Cipher.DECRYPT_MODE, secretKey, new GCMParameterSpec(128, iv));

			try (CipherInputStream cis = new CipherInputStream(dis, cipher);
			FileOutputStream fos = new FileOutputStream(decryptedPath.toFile())) {
				cis.transferTo(fos);
			}
		}
	}

	private SecretKey deriveKeyFromPassword(byte[] salt)
	throws NoSuchAlgorithmException, InvalidKeySpecException {
		SecretKeyFactory secretKeyFactory =
				SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
		KeySpec keySpec =
				new PBEKeySpec(encryptionProperties.getPassword().toCharArray(), salt,
							   200000, 256);
		return new SecretKeySpec(secretKeyFactory.generateSecret(keySpec).getEncoded(),
								 "AES");
	}
}
