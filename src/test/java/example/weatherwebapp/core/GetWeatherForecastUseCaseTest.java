package example.weatherwebapp.core;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

import java.time.Instant;
import java.util.Arrays;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class GetWeatherForecastUseCaseTest {

	private static final Coordinates VICENZA_COORDINATES = new Coordinates(45.5, 11.5);
	private static final City VICENZA = new City(VICENZA_COORDINATES, "Vicenza (IT)");

	private static final Instant BASE_TS = Instant.parse("2023-06-20T10:00:00Z");

	WeatherForecastRepository weatherForecastRepository;
	CityGeocodingRepository cityCoordinatesRepository;

	@BeforeEach
	void setup() {
		weatherForecastRepository = mock(WeatherForecastRepository.class);
		cityCoordinatesRepository = mock(CityGeocodingRepository.class);
	}

	@Test
	void whenNoForecastDaysAndCityIsValid_shouldReturnExpectedWeatherForecast() {

		final var vicenza = new CityName("Vicenza");

		when(cityCoordinatesRepository.getCity(vicenza))
				.thenReturn(Optional.of(VICENZA));

		when(weatherForecastRepository.getWeatherForecastForCity(VICENZA_COORDINATES, 5))
				.thenReturn(Arrays.asList(
						createTestWF(BASE_TS),
						createTestWF(previousDay(BASE_TS, 1)),
						createTestWF(previousDay(BASE_TS, 2)),
						createTestWF(previousDay(BASE_TS, 3)),
						createTestWF(previousDay(BASE_TS, 4))));

		final var usecase = new GetWeatherForecastUseCase(weatherForecastRepository, cityCoordinatesRepository);

		final var forecast = usecase.get(vicenza, null).right();

		assertEquals(5, forecast.getForecasts().size());

	}

	@Test
	void when3ForecastDaysAndCityIsValid_shouldReturnExpectedWeatherForecast() {

		final var vicenza = new CityName("Vicenza");

		when(cityCoordinatesRepository.getCity(vicenza))
				.thenReturn(Optional.of(VICENZA));

		when(weatherForecastRepository.getWeatherForecastForCity(VICENZA_COORDINATES, 3))
				.thenReturn(Arrays.asList(
						createTestWF(BASE_TS),
						createTestWF(previousDay(BASE_TS, 1)),
						createTestWF(previousDay(BASE_TS, 2))));

		final var usecase = new GetWeatherForecastUseCase(weatherForecastRepository, cityCoordinatesRepository);

		final var forecast = usecase.get(vicenza, 3).right();

		assertEquals(3, forecast.getForecasts().size());

	}

	@Test
	void whenCityIsNull_shouldThrow() {

		final var usecase = new GetWeatherForecastUseCase(weatherForecastRepository, cityCoordinatesRepository);

		final var e = assertThrows(NullPointerException.class, () -> usecase.get(null, 3));

		assertEquals("City was null.", e.getMessage());

	}

	@Test
	void whenCityIsNotFound_shouldReturnLeftEither() {

		final var vicenza = new CityName("Vicenza");

		when(cityCoordinatesRepository.getCity(vicenza)).thenReturn(Optional.empty());

		final var usecase = new GetWeatherForecastUseCase(weatherForecastRepository, cityCoordinatesRepository);

		final var either = usecase.get(vicenza, null);

		assertEquals(GetWeatherForecastUseCase.FailureCause.CITY_NOT_FOUND, either.left());

	}

	private static WeatherForecast createTestWF(Instant ts) {
		return new WeatherForecast(ts, 25, "test", "Test");
	}

	private static Instant previousDay(Instant input, int previous) {
		return input.minusSeconds(previous * 24 * 60 * 60);
	}

}