package Networking;

import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Scanner;

public class WeatherPuller {

    private URL APIURL;

    private URL buildAPICall() throws MalformedURLException {
        return new URL(String.format("http://api.openweathermap.org/data/2.5/weather?q=Vienna&units=metric&appid=%s", APIKey.get()));
    }

    public WeatherPuller() {
        try {
            this.APIURL = buildAPICall();
            HttpURLConnection APIConnection = (HttpURLConnection) APIURL.openConnection();

            APIConnection.setRequestMethod("GET");

            APIConnection.connect();

            int responseCode = APIConnection.getResponseCode();

            if (responseCode != 200) {
                throw new RuntimeException("Response code: " + responseCode);
            } else {

                StringBuilder APIResponse = new StringBuilder();

                Scanner scanner = new Scanner(APIURL.openStream());

                while (scanner.hasNext()) {
                    APIResponse.append(scanner.nextLine());
                }

                scanner.close();

                System.out.println(APIResponse);
            }


        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}
