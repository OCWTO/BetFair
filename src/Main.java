import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import model.BetFairGameObject;
import model.ISimpleBetFair;
import model.ProgramOptions;
import model.SimpleBetFairCore;
import betfairUtils.JsonConverter;

import com.google.gson.reflect.TypeToken;

/**
 * Class containing main method which runs the whole program
 * @author Craig Thomson
 *
 */
//TODO convert to javafx
//TODO work on report
//TODO connect front end
public class Main
{
	//TODO test out marketprojection? parameters to see if the maps are necessary, see what data is available whilst getting market books
	//TODO design gui
	//TODO record all markets at every tick and their pricematched
	
	//TODO record all json object requests so reflection testing can be done later
	
	//TODO check that theres a v in the name, to filter out league wide markets I don't care about.
	//print out names are in home vs away, not too relevant but maybe display it in gui
	public static void main(String[] args)
	{
		//TextFrontEnd textUi = new TextFrontEnd(true);
		//textUi.start();
		ISimpleBetFair bf = new SimpleBetFairCore(true);
		bf.login("0ocwto0", "2014Project", "project", new File("certs/client-2048.p12"));
		List<BetFairGameObject> obj = bf.getGameListForSport("1");
		String json = getJson(obj);
		System.out.println("JSON " + json );
		
		TypeToken<List<BetFairGameObject>> token = new TypeToken<List<BetFairGameObject>>() {};
		//List<Animal> animals = gson.fromJson(data, token.getType());
		
		
		List<BetFairGameObject> obj2 = JsonConverter.convertFromJson(json,token.getType());
		System.out.println(obj2.get(0));
		System.out.println(obj2.size());
		System.out.println(obj.get(0));
		System.out.println(obj.size());
		
		
		
		
		
		
		ProgramOptions x1 = new ProgramOptions();
		x1.setEventId("1");
		x1.setEventTypeId("2");
		x1.addMarketIds(Arrays.asList("1","2","3"));
		
		String json2 = JsonConverter.convertToJson(x1);
		ProgramOptions x2 = JsonConverter.convertFromJson(json2, ProgramOptions.class);
		System.out.println(json2);
		System.out.println(x1.getEventTypeId());
		System.out.println(x2.getEventTypeId());
		
		String json3 = getJson(x2);
		System.out.println(json3);
	}
	
	public static String getJson(Object o)
	{
		return JsonConverter.convertToJson(o);
	}
}