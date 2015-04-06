package betfairUtils;

import betfairGSONClasses.ISO8601DateTypeAdapter;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.lang.reflect.Type;
import java.util.Date;

/**
 * This class is used to convert to and from JSON.
 * CLASS TAKEN FROM BETFAIR source: https://github.com/betfair/API-NG-sample-code/tree/master/java/ng
 * @author Betfair
 */
public class JsonConverter
{
	/**
	 * We needed to override the adapter for the Date class as Betfair's API-NG
	 * requires all dates to be serialized in ISO8601 UTC Just formatting the
	 * string to the ISO format does not adjust by the timezone on the Date
	 * instance during serialization.
	 */
	private static final Gson gson = new GsonBuilder().registerTypeAdapter(
			Date.class, new ISO8601DateTypeAdapter()).create();
	private static StringBuilder notationBuilder = new StringBuilder();

	/**
	 * This method deserializes the specified Json into an object of the
	 * specified class.
	 *
	 */
	public static <T> T convertFromJson(String toConvert, Class<T> clazz)
	{
		return gson.fromJson(toConvert, clazz);
	}

	/**
	 * This method deserializes the specified Json into an object of the
	 * specified Type.
	 *
	 */
	public static <T> T convertFromJson(String toConvert, Type typeOfT)
	{
		return gson.fromJson(toConvert, typeOfT);
	}

	/**
	 * This method serializes the specified object into its equivalent Json
	 * representation.
	 */
	public static String convertToJson(Object toConvert)
	{
		return gson.toJson(toConvert);
	}
}