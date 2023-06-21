package example.weatherwebapp.core;

import example.weatherwebapp.shared.Either;

/**
 * Value Object around the city name.
 */
public record CityName(String cityName) {

	/**
	 * Create the value object.
	 * Throws if invalid name. Use {@link CityName.tryCreate} to handle failures.
	 * @param cityName Name of the city.
	 */
	public CityName {
		if (isInvalidString(cityName)) {
			throw new IllegalArgumentException("City name cannot be null or blank. Check for validity using CityName.tryCreate.");
		}
	}

	private static boolean isInvalidString(String cityName) {
		return cityName == null || cityName.isBlank();
	}

	/**
	 * Create a city name if the string is valid, or return a left Either if the city name is invalid.
	 * @param city Name of the city.
	 * @return Either with a failure reason or the city name.
	 */
	public static Either<String, CityName> tryCreate(String city) {
		if (isInvalidString(city)) return Either.left("blank");
		
		return Either.right(new CityName(city.trim()));
	}

}
