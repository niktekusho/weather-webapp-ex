package example.weatherwebapp.web;

import java.time.Instant;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import example.weatherwebapp.core.CityName;
import example.weatherwebapp.core.GetWeatherForecastUseCase;

/**
 * Webapp HTTP Apis.
 */
@RestController
@RequestMapping("/weather")
public class WeatherWebApis {

	private final GetWeatherForecastUseCase getWeatherForecastUseCase;

	WeatherWebApis(GetWeatherForecastUseCase getWeatherForecastUseCase) {
		this.getWeatherForecastUseCase = getWeatherForecastUseCase;
	}

	/**
	 * GET HTTP request that queries for weather forecasts of a specific city.
	 * 
	 * @param city City to look for weather forecasts.
	 * @return response.
	 */
	@GetMapping("{city}")
	@ResponseBody
	public ResponseEntity<Object> getWeatherForecast(
			@PathVariable String city, @RequestParam(required = false) Integer forecaseDays) {

		final var cityEither = CityName.tryCreate(city);

		if (cityEither.isLeft()) {
			// TODO: If there were other reasons for this, then you can catch it here
			final var body = new ErrorDTO(Instant.now(), "City can not be null or blank", null);
			return ResponseEntity.badRequest().body(body);
		}

		final var weatherForecasts = getWeatherForecastUseCase.get(cityEither.right(), forecaseDays);

		if (weatherForecasts.isLeft()) {
			final var body = new ErrorDTO(Instant.now(), weatherForecasts.left().toString(), null);
			return ResponseEntity.internalServerError().body(body);
		}

		return ResponseEntity.ok(weatherForecasts.right());

	}

}
