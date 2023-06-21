package example.weatherwebapp.infrastructure;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import example.weatherwebapp.core.City;
import example.weatherwebapp.core.CityGeocodingRepository;
import example.weatherwebapp.core.CityName;
import example.weatherwebapp.core.Coordinates;
import example.weatherwebapp.core.WeatherForecast;
import example.weatherwebapp.core.WeatherForecastRepository;

@Component
public class OpenWeatherMapWeatherForecastRepository implements WeatherForecastRepository, CityGeocodingRepository {

	private static final Logger LOGGER = LoggerFactory.getLogger(OpenWeatherMapWeatherForecastRepository.class);

	private final RestTemplate restTemplate;

	private final String openWeatherApiKey;

	/* package */ OpenWeatherMapWeatherForecastRepository(
			RestTemplateBuilder restTemplateBuilder,
			@Value("${OPEN_WEATHER_API_KEY:ba98fc083b6ab2fdeadacee9414982eb}") String openWeatherApiKey) {
		restTemplate = restTemplateBuilder.build();
		this.openWeatherApiKey = openWeatherApiKey;
	}

	@Override
	public List<WeatherForecast> getWeatherForecastForCity(Coordinates coordinates, int limit) {
		final var response = restTemplate.getForEntity(
				"https://api.openweathermap.org/data/2.5/forecast?lat={lat}&lon={long}&units=metric&appid={apiKey}",
				OpenWeather5DaysForecastResponseDTO.class,
				Map.of("lat", coordinates.latitude(), "long", coordinates.longitude(), "apiKey", openWeatherApiKey));

		if (response.getStatusCode().is2xxSuccessful() && response.hasBody()) {
			final var body = response.getBody();

			final var weatherDTOs = Optional.ofNullable(body)
					.map(dto -> dto.list)
					.orElseGet(Collections::emptyList);

			return weatherDTOs.stream()
					.map(OpenWeatherMapWeatherForecastRepository::fromDTO)
					.toList();

		}

		LOGGER.error("HTTP call to OpenWeather APIs not successful", response);

		return Collections.emptyList();
	}

	@Override
	public Optional<City> getCity(CityName cityName) {
		final var response = restTemplate.getForEntity(
				"https://api.openweathermap.org/geo/1.0/direct?q={city}&appid={apiKey}",
				OpenWeatherGeocodingResponseDTO[].class,
				Map.of("city", cityName.cityName(), "apiKey", openWeatherApiKey));

		if (response.getStatusCode().is2xxSuccessful() && response.hasBody()) {
			OpenWeatherGeocodingResponseDTO[] body = response.getBody();
			if (body == null || body.length == 0)
				return Optional.empty();

			// TODO: it should not take the first element arbitrarily...
			final var geocoded = body[0];
			final var coordinates = new Coordinates(geocoded.lat, geocoded.lon);
			final var displayName = String.format("%s (%s)", geocoded.name,
					geocoded.country);
			return Optional.of(new City(coordinates, displayName));
		}

		LOGGER.error("HTTP call to OpenWeather APIs not successful", response);

		return Optional.empty();
	}

	private static WeatherForecast fromDTO(OpenWeatherForecastDTO dto) {
		return new WeatherForecast(Instant.ofEpochSecond(dto.dt),
				dto.main.temp,
				// TODO: can a forecast not have the weather list?
				dto.weather.get(0).icon,
				dto.weather.get(0).description);
	}

	static class OpenWeather5DaysForecastResponseDTO {
		public String cod;
		public int message;
		public int cnt;
		public List<OpenWeatherForecastDTO> list = new ArrayList<>();
		public CityDTO city;
	}

	static class OpenWeatherForecastDTO {
		public int dt;
		public OpenWeatherForecastMainDataDTO main;
		public List<OpenWeatherForecastWeatherDTO> weather = new ArrayList<>();
		public OpenWeatherCloudsDTO clouds;
		public OpenWeatherWindDTO wind;
		public int visibility;
		public OpenWeatherRainDTO rain;
	}

	static class OpenWeatherForecastMainDataDTO {
		public double temp;
		public double feels_like;
		public double temp_min;
		public double temp_max;
		public int pressure;
		public int sea_level;
		public int grnd_level;
		public int humidity;
		public double temp_kf;
	}

	static class CityDTO {
		public int id;
		public String name;
		public OpenWeatherCoordDTO coord;
		public String country;
		public int population;
		public int timezone;
		public int sunrise;
		public int sunset;
	}

	static class OpenWeatherCloudsDTO {
		public int all;
	}

	static class OpenWeatherCoordDTO {
		public double lat;
		public double lon;
	}

	static class OpenWeatherRainDTO {
		@JsonProperty("3h")
		public double _3h;
	}

	static class OpenWeatherForecastWeatherDTO {
		public int id;
		public String main;
		public String description;
		public String icon;
	}

	static class OpenWeatherWindDTO {
		public double speed;
		public int deg;
		public double gust;
	}

	static class OpenWeatherGeocodingResponseDTO {
		public String name;

		@JsonProperty("local_names")
		public Map<String, String> localNames = new HashMap<>();
		public double lat;
		public double lon;
		public String country;
		public String state;

	}

}