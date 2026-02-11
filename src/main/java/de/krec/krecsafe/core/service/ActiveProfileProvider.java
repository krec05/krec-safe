package de.krec.krecsafe.core.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

@Service
public class ActiveProfileProvider {

	private final String activeProfile;

	@Autowired
	public ActiveProfileProvider(Environment environment) {
		String[] activeProfiles = environment.getActiveProfiles();

		if (activeProfiles.length != 1) {
			throw new IllegalStateException(
					"Exact one active Spring profile is required, but got: "
					+ String.join(",", activeProfiles));
		}

		this.activeProfile = activeProfiles[0];
	}

	public String getActiveProfile() {
		return activeProfile;
	}

}
