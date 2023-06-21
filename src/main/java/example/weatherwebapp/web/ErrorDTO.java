package example.weatherwebapp.web;

import java.time.Instant;

public record ErrorDTO(Instant timestamp, String message, String errorCode) {
	
}
