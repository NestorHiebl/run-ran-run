package Networking;

import org.json.JSONObject;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Scanner;


public class WeatherData {

    private JSONObject WeatherDataJSON;

    private URL buildAPICall() throws MalformedURLException {
        return new URL(String.format("http://api.openweathermap.org/data/2.5/weather?q=Vienna&units=metric&appid=%s", APIKey.get()));
    }

    public WeatherData() {
        getApiData();
        //getDataFromTestFile("Resources/TestData/PlaceholderAPIResponse.txt");
    }

    private void getApiData() {
        try {
            URL APIURL = buildAPICall();
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

                this.WeatherDataJSON = new JSONObject(APIResponse.toString());
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void getDataFromTestFile(String filePath) {
        String rawData = null;

        try (BufferedReader br = new BufferedReader(new FileReader(filePath))){
            StringBuilder stringBuilder = new StringBuilder();

            String line = br.readLine();

            while (line != null) {
                stringBuilder.append(line);
                stringBuilder.append(System.lineSeparator());
                line = br.readLine();
            }

            rawData = stringBuilder.toString();
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (rawData == null) {
            throw new RuntimeException("Could not read data from test file - aborting startup");
        }

        this.WeatherDataJSON = new JSONObject(rawData);
    }

    public static String[] getPossibleWeatherStringValues() {
        return new String[]{
                "Clear",
                "Thunderstorm",
                "Drizzle",
                "Rain",
                "Snow",
                "Mist",
                "Smoke",
                "Haze",
                "Dust",
                "Fog",
                "Sand",
                "Ash",
                "Squall",
                "Tornado",
                "Clouds"
        };
    }

    public String getWeatherString() {
        return this.WeatherDataJSON.getJSONArray("weather").getJSONObject(0).getString("main");
    }

    public double getTemp() {
        return this.WeatherDataJSON.getJSONObject("main").getDouble("temp");
    }

    public int getHumidity() {
        return this.WeatherDataJSON.getJSONObject("main").getInt("humidity");
    }

    public double getWindSpeed() {
        return this.WeatherDataJSON.getJSONObject("wind").getDouble("speed");
    }
}
