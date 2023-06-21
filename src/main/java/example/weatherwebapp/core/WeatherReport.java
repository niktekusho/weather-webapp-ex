package example.weatherwebapp.core;

import java.util.ArrayList;
import java.util.List;

public class WeatherReport {
	private List<WeatherForecast> forecasts = new ArrayList<>();

	private City city;

	public WeatherReport(List<WeatherForecast> forecasts, City city) {
		this.forecasts = forecasts;
		this.city = city;
	}

	public List<WeatherForecast> getForecasts() {
		return forecasts;
	}

	public City getCity() {
		return city;
	}

}
