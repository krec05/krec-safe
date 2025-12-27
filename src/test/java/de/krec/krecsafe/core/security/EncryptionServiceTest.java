package de.krec.krecsafe.core.security;

import de.krec.krecsafe.config.EncryptionProperties;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.crypto.NoSuchPaddingException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.Arrays;

@ExtendWith(MockitoExtension.class)
class EncryptionServiceTest {

	@InjectMocks EncryptionService encryptionService;

	@Mock EncryptionProperties encryptionPropertiesMock;
	private Path startFile;
	private Path encryptedFile;
	private Path decryptedFile;

	@BeforeEach
	public void initMock() {
		Mockito.when(encryptionPropertiesMock.getPassword())
			   .thenReturn("encryptionSecretPassword");
	}

	@BeforeEach
	public void createFile() throws IOException {
		startFile = Files.createTempFile("startFile", "txt");
		startFile.toFile().deleteOnExit();
		Files.write(startFile,
					"Lorem ipsum dolor sit amet, consetetur sadipscing elitr, sed diam nonumy eirmod tempor invidunt ut labore et dolore magna aliquyam erat".getBytes());

		encryptedFile = Files.createTempFile("encryptedFile", "txt");
		encryptedFile.toFile().deleteOnExit();
		decryptedFile = Files.createTempFile("decryptedFile", "txt");
		decryptedFile.toFile().deleteOnExit();
	}

	@Test
	public void encryptDecryptTest()
	throws IOException, InvalidAlgorithmParameterException, NoSuchPaddingException,
		   NoSuchAlgorithmException, InvalidKeySpecException, InvalidKeyException {
		encryptionService.encryptFile(startFile, encryptedFile);

		byte[] startContent = Files.readAllBytes(startFile);
		byte[] encryptedContent = Files.readAllBytes(encryptedFile);

		Assertions.assertFalse(Arrays.equals(startContent, encryptedContent), "Encrypted content must not correspond to the plaintext.");

		encryptionService.decryptFile(encryptedFile, decryptedFile);

		byte[] decryptedContent = Files.readAllBytes(decryptedFile);

		Assertions.assertArrayEquals(startContent, decryptedContent, "The decoded content must match the original.");
	}

}