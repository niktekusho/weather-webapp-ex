package example.weatherwebapp.core;

import java.util.Optional;

/**
 * Repository interface to check whether a CityName exists.
 */
public interface CityGeocodingRepository {
	/**
	 * Get the city details if the city name is found.
	 * @param city Name of the city to search.
	 * @return Optional with details if city is found, empty Optional otherwise.
	 */
	Optional<City> getCity(CityName city);
}
