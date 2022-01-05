package Networking;

import org.json.JSONObject;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Scanner;


public class WeatherData {

    private JSONObject WeatherDataJSON;

    private URL APIURL;

    private URL buildAPICall() throws MalformedURLException {
        return new URL(String.format("http://api.openweathermap.org/data/2.5/weather?q=Vienna&units=metric&appid=%s", APIKey.get()));
    }

    public WeatherData() {
        getDataFromTestFile("Resources/TestData/PlaceholderAPIResponse.txt");
    }

    private void getApiData() {
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

        WeatherDataJSON = new JSONObject(rawData);
    }

    public JSONObject getWeatherDataJSON() {
        return this.WeatherDataJSON;
    }

    public String getWeatherString() {
        return this.WeatherDataJSON.getJSONArray("weather").getJSONObject(0).getString("main");
    }

    public double getTemp() {
        return this.WeatherDataJSON.getJSONObject("main").getDouble("temp");
    }
}
