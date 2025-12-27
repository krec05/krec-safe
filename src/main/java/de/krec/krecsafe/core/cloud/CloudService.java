package de.krec.krecsafe.core.cloud;

import de.krec.krecsafe.config.BackupPathProperties;
import de.krec.krecsafe.core.security.EncryptionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.crypto.NoSuchPaddingException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.time.LocalDateTime;

@Service
public class CloudService {

	private final CloudClient       cloudClient;
	private final EncryptionService encryptionService;

	@Autowired
	public CloudService(CloudClient cloudClient, EncryptionService encryptionService) {
		this.cloudClient = cloudClient;
		this.encryptionService = encryptionService;
	}

	public void backupFile(BackupPathProperties.PathEntry pathEntry, Path hostFile)
	throws InvalidAlgorithmParameterException, NoSuchPaddingException,
		   NoSuchAlgorithmException, InvalidKeySpecException, IOException,
		   InvalidKeyException {
		Path cloudFile = determineCloudPath(pathEntry, hostFile);
		Path encryptedFile = determineEncryptedPath(hostFile);
		encryptionService.encryptFile(hostFile, encryptedFile);
		cloudClient.backupFile(encryptedFile, cloudFile);
	}

	private Path determineEncryptedPath(Path hostFile) throws IOException {
		String fileName = hostFile.getFileName().toString();

		int dot = fileName.lastIndexOf(".");
		String prefix;
		String suffix;
		if (dot == -1) {
			prefix = fileName;
			suffix = ".tmp";
		} else {
			prefix = fileName.substring(0, dot);
			suffix = fileName.substring(dot);
		}

		// The prefix must have at least 3 characters.
		if (prefix.length() < 3) {
			prefix = (prefix + "___").substring(0, 3);
		}

		Path response = Files.createTempFile(prefix, suffix);
		response.toFile().deleteOnExit();
		return response;
	}

	public LocalDateTime getUploadDate(BackupPathProperties.PathEntry pathEntry,
									   Path hostFile) {
		Path cloudFile = determineCloudPath(pathEntry, hostFile);
		return cloudClient.getLastBackupTime(cloudFile);
	}

	private Path determineCloudPath(BackupPathProperties.PathEntry pathEntry,
									Path hostFile) {
		return Path.of(pathEntry.getClouddir())
				   .resolve(pathEntry.getPath().relativize(hostFile));
	}
}
