/*
 *filename : GoogMatrixRequest.java
 *author : team Tic Toc
 *since : 2016.11.11
 *purpose/function : Use Google map API to find routes and distance.
 *
 */
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;
import java.io.IOException;

public class GoogMatrixRequest {

	private static final String API_KEY = "AIzaSyAbImqPR7aE4fUL8jyUBhnloMxnA-6zqEo";
	private String startAddress; // String address of start point
	private String targetAddress; // String address of target point
	// double latitude, longitude of 2 points
	private double startLatitude, startLongitude, targetLatitude, targetLongitude;
	private int mode; // if mode = 1 , calculate with two address. else if mode
						// = 2, calculate with latitude and longitude

	public GoogMatrixRequest(String startAR, String targetAR) { // Address mode
																// constructor
		startAddress = startAR;
		targetAddress = targetAR;
		mode = 1;
	}

	public GoogMatrixRequest(double stLa, double stLo, double taLa, double taLo) { // Point
																					// mode
																					// constructor
		startLatitude = stLa;
		startLongitude = stLo;
		targetLatitude = taLa;
		targetLongitude = taLo;
		mode = 2;
	}

	OkHttpClient client = new OkHttpClient();

	public String run(String url) throws IOException {
		Request request = new Request.Builder().url(url).build();
		Response response = client.newCall(request).execute();
		return response.body().string();
	}

	public void calculate() throws IOException {
		GoogMatrixRequest request = this;
		String url_request = null;
		if (mode == 1)
			url_request = "https://maps.googleapis.com/maps/api/distancematrix/json?origins=" + startAddress
					+ "&destinations=" + targetAddress + "&key=" + API_KEY;
		else if (mode == 2)
			url_request = "https://maps.googleapis.com/maps/api/distancematrix/json?origins=" + startLatitude + ","
					+ startLongitude + "&destinations=" + targetLatitude + "," + targetLongitude + "&key=" + API_KEY;

		String response = request.run(url_request);
		System.out.println(response);
	}
}