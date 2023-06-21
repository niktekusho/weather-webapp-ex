package example.weatherwebapp.core;

import java.time.Instant;
import java.util.Objects;

/** Simple weather forecast */
public class WeatherForecast {

	private final Instant timestamp;

	private final double avgTemperature;

	// TODO: those could go into an enum...
	private final String weatherIcon;

	private final String weatherDescription;

	/**
	 * Creates the weather forecast.
	 * 
	 * @param timestamp          Timestamp of the weather forecast.
	 * @param avgTemperature     Average temperature.
	 * @param weatherIcon        Icon of the weather.
	 * @param weatherDescription Description of the weather.
	 */
	public WeatherForecast(Instant timestamp, double avgTemperature, String weatherIcon, String weatherDescription) {
		this.timestamp = timestamp;
		this.avgTemperature = avgTemperature;
		this.weatherIcon = weatherIcon;
		this.weatherDescription = weatherDescription;
	}

	public Instant getTimestamp() {
		return timestamp;
	}

	public double getAvgTemperature() {
		return avgTemperature;
	}

	public String getWeatherIcon() {
		return weatherIcon;
	}

	public String getWeatherDescription() {
		return weatherDescription;
	}

	@Override
	public int hashCode() {
		return Objects.hash(timestamp, avgTemperature, weatherIcon, weatherDescription);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!(obj instanceof WeatherForecast))
			return false;
		WeatherForecast other = (WeatherForecast) obj;
		return Objects.equals(timestamp, other.timestamp)
				&& Double.doubleToLongBits(avgTemperature) == Double.doubleToLongBits(other.avgTemperature)
				&& Objects.equals(weatherIcon, other.weatherIcon)
				&& Objects.equals(weatherDescription, other.weatherDescription);
	}

}
