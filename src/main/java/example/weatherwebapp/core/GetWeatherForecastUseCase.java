package example.weatherwebapp.core;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import org.springframework.stereotype.Component;

import example.weatherwebapp.shared.Either;

@Component
public class GetWeatherForecastUseCase {
	private static final int DEFAULT_FORECAST_DAYS = 5;

	private final WeatherForecastRepository weatherForecastRepository;

	private final CityGeocodingRepository cityGeocodingRepository;

	/**
	 * Create the use case.
	 * 
	 * @param weatherForecastRepository Repository of the weather forecasts.
	 * @param cityCoordinatesRepository Repository of the city to coordinates
	 *                                  mapping.
	 */
	public GetWeatherForecastUseCase(
			WeatherForecastRepository weatherForecastRepository,
			CityGeocodingRepository cityCoordinatesRepository) {
		this.weatherForecastRepository = weatherForecastRepository;
		this.cityGeocodingRepository = cityCoordinatesRepository;
	}

	/**
	 * Get the weather forecasts (aka "execute" the use case).
	 * @param city Name of the city to search.
	 * @param forecastDays Number of days to forecast.
	 * @return Either with a failure cause or a list of forecasts.
	 */
	public Either<FailureCause, WeatherReport> get(CityName cityName, Integer forecastDays) {
		Objects.requireNonNull(cityName, "City was null.");
		
		final var maybeCity = cityGeocodingRepository.getCity(cityName);

		if (maybeCity.isEmpty()) return Either.left(FailureCause.CITY_NOT_FOUND);

		final var city = maybeCity.orElseThrow();
	
		final int limit = Optional.ofNullable(forecastDays)
			.filter(i -> i > 0)
			.orElse(DEFAULT_FORECAST_DAYS);

		try {
			final var cityCoordinates = city.coordinates();
			final var forecasts = weatherForecastRepository.getWeatherForecastForCity(cityCoordinates, limit);

			// get only the first <limit> forecasts of each day
			// final var filteredForecasts = extractLimitDaysOnly(forecasts, limit);


			final var report = new WeatherReport(forecasts, city);
			return Either.right(report);
		} catch (Exception e) {
			// TODO: logging
			System.err.println(e);
			return Either.left(FailureCause.INTERNAL_ERROR);
		}
	}

	private static List<WeatherForecast> extractLimitDaysOnly(List<WeatherForecast> weatherForecasts, int limit) {

		final List<WeatherForecast> result = new ArrayList<>();

        LocalDate currentDate = null;

        for (WeatherForecast wf : weatherForecasts) {
            LocalDate date = wf.getTimestamp().atZone(ZoneOffset.UTC).toLocalDate();

            if (!date.equals(currentDate)) {
                result.add(wf);
                currentDate = date;
            }
        }

		return result;
	}

	/**
	 * Failure cause enum.
	 */
	public enum FailureCause {
		/**
		 * City not found failure.
		 */
		CITY_NOT_FOUND,
		/**
		 * Server error failure.
		 */
		INTERNAL_ERROR;
	}

}
