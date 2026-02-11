package de.krec.krecsafe.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "de.krec.monitoring")
public class MonitoringProperties {

	private String csvProtocolPath;

	public String getCsvProtocolPath() {
		return csvProtocolPath;
	}

	public void setCsvProtocolPath(String csvProtocolPath) {
		this.csvProtocolPath = csvProtocolPath;
	}
}
