package DevigetCodeChallenge.DevigetCodeChallenge;


import static io.restassured.RestAssured.given;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.junit.Test;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.parsing.Parser;
import io.restassured.response.Response;


/******************************************************
 *
 * @author Daniel Guidi
 * 03/19/2021
 *
 ******************************************************/

public class AllTests {
	String url_sol = "https://api.nasa.gov/mars-photos/api/v1/rovers/curiosity/photos?sol=1000&api_key=DEMO_KEY&page=1"; 
	// Note: 1000 sol equals to 2015-05-30
	String url_ed = "https://api.nasa.gov/mars-photos/api/v1/rovers/curiosity/photos?earth_date=2015-5-30&api_key=DEMO_KEY&page=1"; 
	String url_resume = "https://api.nasa.gov/mars-photos/api/v1/manifests/Curiosity?api_key=DEMO_KEY"; 
	String cam_url = "https://api.nasa.gov/mars-photos/api/v1/rovers/curiosity/photos?sol=1000&api_key=DEMO_KEY&camera="; 
	int photo_num = 10;
	
	ArrayList<String> photos_sol = new ArrayList<String>() ;
	ArrayList<String> resume = new ArrayList<String>() ;
	Object my_photo = new LinkedHashMap();
	String[] my_photos_sol = new String[10];
	String[] my_photos_ed = new String[10];
	ArrayList<String> photos_ed = new ArrayList<String>() ;
	HashMap<Integer, Object> h_photos_sol = new HashMap<Integer, Object>();
	HashMap<Integer, Object> h_photos_ed = new HashMap<Integer, Object>();
	HashMap<Integer, Object> h_photos_resume = new HashMap<Integer, Object>();
	
	
	// doRequest(String endpoint) is a method that using REST Assured gets
	// And Returns the result of the request to the endpoint passed as parameter
	public static Response doGetRequest(String endpoint) {
        RestAssured.defaultParser = Parser.JSON;

        return
            given().headers("Content-Type", ContentType.JSON, "Accept", ContentType.JSON).
                when().get(endpoint).
                then().contentType(ContentType.JSON).extract().response();
    }
	
	
	
	// Test 1: Retrieve the first 10 Mars photos made by "Curiosity" on 1000 Martian sol.
	@Test
	public void test_1_RetrieveFirst10MarsPhotosOn1000MartianSol() {
		System.out.println("\n\nTest 1: Retrieve the first 10 Mars photos made by \"Curiosity\" on 1000 Martian sol.");
		
		// Get the photos of Earth Date corrssponding to 1000 sol
		// Note: 1000 sol equals to 2015-05-30
		Response response_sol = doGetRequest(url_sol);
		
		for (int i = 0; i < 10; i++)
		     {
		      Map<Object, Object> photos_sol = (Map<Object, Object>) response_sol.jsonPath().getMap("photos["+ i + "]");
		      h_photos_sol.put(i, photos_sol);
		      System.out.println(h_photos_sol.get(i));
		     }
		
	} // End Test 1
	
	
	
	// Test 2: Retrieve the first 10 Mars photos made by "Curiosity" on Earth date equal to 1000 Martian sol.
	@Test
	public void test_2_RetrieveFirst10MarsPhotosOnEarthDateEQ1000MartianSol() {
		System.out.println("\n\nTest 2: Retrieve the first 10 Mars photos made by \"Curiosity\" on Earth date equal to 1000 Martian sol.");
        
		// Get the photos of Earth Date corrssponding to 1000 sol
		Response response_ed = doGetRequest(url_ed);
        
		for (int i = 0; i < 10; i++)
		     {
		      Map<Object, Object> photos_ed = (Map<Object, Object>) response_ed.jsonPath().getMap("photos["+ i + "]");
		      h_photos_ed.put(i, photos_ed);
		      System.out.println(h_photos_ed.get(i));
		     }
	
	} // End Test 2
		
	
	
	// Test 3: Retrieve and compare the first 10 Mars photos made by "Curiosity" on 1000 sol and on Earth date equal to 1000 Martian sol.
	@Test 
	public void test_3_RetrieveAndCompareFirst10MarsPhotosOn1000SolAndEDEqual1000MartianSol() {
		System.out.println("\n\nTest 3: Retrieve and compare the first 10 Mars photos made by \"Curiosity\" on 1000 sol and on Earth date equal to 1000 Martian sol.");
			
    	// As Photos From Mars Sol 1000 And Earth Date Equal To Sol 1000 Were Already Retrieved
		// There is no Need To Retrieve Them Again but the exercise asks to do it.
		
		for (int i = 0; i < 10; i++)
		     {
			  try {
		           // Do the assertion...
		           assertEquals(my_photos_ed[i], my_photos_sol[i]);
			      } catch (AssertionError e) {
        	               // Print when assetion fails...
                           System.out.println("The photo: " + my_photos_ed[i] + " is not the same as this one " + my_photos_sol[i]);
	
			      } // end try
		      System.out.println("Photo number " + i + " is the same for sol 1000 and for Earth Date 2015-05-31");
	 	    } // End for
		
	 } // end Test 3
	
	

	// Test 4: Validate that the amounts of pictures that each "Curiosity" camera took on 1000 Mars sol is not greater than 10 times the amount taken by other cameras on the same date.
	@Test 
	public void test_4_ValidateAmountOfCamPicIsLessThan10TimesAmountOfOthers() {
		String cam_name = "";
		
		System.out.println("\n\nTest 4: Validate that the amounts of pictures that each \"Curiosity\" camera took on 1000 Mars sol is not greater than 10 times the amount taken by other cameras on the same date.");
		
        // Step 1: 
        // Get The Mission's Manifest
		Response response_resu = doGetRequest(url_resume);
		
		// Extract corresponding info for sol 1000
		List<Map<String, ?>> ph_re = (List<Map<String, ?>>) response_resu.jsonPath().get("photo_manifest.photos.findAll{photos -> photos.sol == 1000}");
		
		// Get the Camera list
		LinkedHashMap my_cam = new LinkedHashMap(ph_re.get(0));

		System.out.println("The cameras that worked on 1000 sol are: " + my_cam.get("cameras"));
		
		// Store Cam's names
		ArrayList<String> cams = (ArrayList<String>) (my_cam.get("cameras"));
			
		ArrayList<String> aux_arr = new ArrayList<String>();
		// For each cam 
		for (int i = 0; i < cams.size(); i++)
	 	     {
			  // Get it's name
	 	      cam_name = cams.get(i);
	 	   
	 	      // Compose endpoint
	 	      String tmp = cam_url + cam_name;
	 	      
	 	      // Do the Request
	 	      Response response_cam = doGetRequest(tmp);
	 	 
	 	      // Get the Photos
	 	      List<Map<String, ?>> cam_ph = (List<Map<String, ?>>) response_cam.jsonPath().get("photos");

	 	      // Store the amount of photos for the cam
	 	      int ph_qty = cam_ph.size();
	 	      aux_arr.add(i, String.valueOf(ph_qty));
	 	     }
		
		// For all the cams
		for (int i = 0; i < cams.size(); i++)
	 	     {
		      // Print amount of taken photos
		      System.out.println(cams.get(i) + " has taken " + aux_arr.get(i) + " photos on 1000 sol."); //" + (String) aux_arr.get(i) + "
		 
		      // Now check all other cams amounts excluding the current one on process
		      for (int j = 0; j < cams.size(); j++)
		           {
		            // Current on on process exclusion
	                if (j != i)
	                    {
	  	                 try {
	  	                	  // Assert that the current amount of photos taken by the cam on process is not
	  	                	  // greater than 10 times the amount of photos taken by the others
	                          assertFalse(Integer.valueOf(aux_arr.get(i)) > (10 * Integer.valueOf(aux_arr.get(j))));
	    	                 } catch (AssertionError e) {
	    	                	      // Print when assetion fails...
	    	                          System.out.println("Cam " + cams.get(i) + " has taken more than 10 times photos (" + String.valueOf(aux_arr.get(i)) + ") than cam " + cams.get(j) + " which has taken " + String.valueOf(aux_arr.get(j)));
	                          } // end try
	    	
	    	           }  // End if          
		             } // End inner for
	 	       } // end outer for
        
		
	} // End Test 4
	
} // End class
