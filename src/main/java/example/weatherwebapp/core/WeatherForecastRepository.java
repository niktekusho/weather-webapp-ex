package example.weatherwebapp.core;

import java.util.List;

public interface WeatherForecastRepository {

	List<WeatherForecast> getWeatherForecastForCity(Coordinates coordinates, int limit);

}
