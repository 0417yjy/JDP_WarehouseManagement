import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;
import java.io.IOException;

public class GoogMatrixRequest {

    private static final String API_KEY = "AIzaSyAbImqPR7aE4fUL8jyUBhnloMxnA-6zqEo";

  OkHttpClient client = new OkHttpClient();

  public String run(String url) throws IOException {
    Request request = new Request.Builder()
        .url(url)
        .build();

    Response response = client.newCall(request).execute();
    return response.body().string();
  }

  public static void main(String[] args) throws IOException {
    GoogMatrixRequest request = new GoogMatrixRequest();
    String url_request = "https://maps.googleapis.com/maps/api/distancematrix/json?origins=Vancouver+BC%7CSeattle&destinations=San+Francisco%7CVictoria+BC&mode=bicycling&language=fr-FR&key=" + API_KEY;

    String response = request.run(url_request);
    System.out.println(response);
  }
}