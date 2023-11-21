/*
 * Ini adalah program utama dari aplikasi Cuacamu
 * Versi 1.0.0-test
 * 
 * @author Adhiansyah M. Pradana F.
 * @author Akmal Adicandra
 * @author Arzario Irsyad AF
 * 
 */

package Cuacamu;

import java.awt.Font;
import java.awt.GridLayout;
import java.io.IOException;
import java.util.HashMap;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;

import org.apache.http.HttpEntity;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import org.json.*;

public class App extends JFrame {
    private JLabel namaKotaLabel;
    private JLabel cuacaLabel = new JLabel("Keadaan cuaca", SwingConstants.CENTER);
    private JLabel suhuLabel = new JLabel("Suhu", SwingConstants.CENTER);
    private JLabel arahAnginLabel = new JLabel("Arah angin", SwingConstants.CENTER);
    private JLabel kecepatanAnginLabel = new JLabel("Kecepatan angin", SwingConstants.CENTER);
    // private JLabel tekananUdaraLabel = new JLabel("Tekanan udara", SwingConstants.CENTER);

    private String dataSuhu;
    private String dataKecepatanAngin = String.format("%s km/jam", "80");

    private JTextField keadaanCuaca;
    private JTextField suhu;
    private JTextField arahAngin;
    private JTextField kecepatanAngin;

    private static final HashMap<Integer, String> dataKodeCuaca = new HashMap<>();
    private static final HashMap<String, String> dataArahAngin = new HashMap<>();


    private App() {
        this.setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        JPanel paddedContainer = new JPanel();
        paddedContainer.setBorder(new EmptyBorder(3, 12, 12, 12));
        this.setContentPane(paddedContainer);

        this.setTitle("Cuacamu - Penampil cuaca");
        this.setSize(500, 210);
        this.setResizable(false);
        this.setLayout(new GridLayout(5, 2, 3, 3));

        try {
            final JSONObject objekData = new JSONObject(DataJsonCuaca());

            // Akses data berlapis
            final JSONObject row = objekData.getJSONObject("row");
            final JSONObject data = row.getJSONObject("data");
            final JSONObject forecast = data.getJSONObject("forecast");
            final JSONArray area = forecast.getJSONArray("area");
            final JSONObject pwt = area.getJSONObject(21);
            final JSONArray parameter = pwt.getJSONArray("parameter");

            // Dapatkan nama kota
            final String namaPwt = pwt.getString("@description");

            // Dapatkan keadaan cuaca
            final JSONObject cuaca = parameter.getJSONObject(6);
            final JSONArray timerangeCuaca = cuaca.getJSONArray("timerange");
            final JSONObject timerangeCuaca2 = timerangeCuaca.getJSONObject(2);
            final JSONObject valueCuaca = timerangeCuaca2.getJSONObject("value");

            final String kodeKeadaanCuaca = valueCuaca.getString("#text");

            // Dapatkan suhu
            final JSONObject temperatur = parameter.getJSONObject(5);
            final JSONArray timerangeTemperatur = temperatur.getJSONArray("timerange");
            final JSONObject timerangeTemperatur2 = timerangeTemperatur.getJSONObject(2);
            final JSONArray valueTemperatur = timerangeTemperatur2.getJSONArray("value");
            final JSONObject valueTemperatur0 = valueTemperatur.getJSONObject(0);

            String derajatSuhu = valueTemperatur0.getString("#text"); 

            // Dapatkan arah angin
            final JSONObject wd = parameter.getJSONObject(7);
            final JSONArray timerangeWd = wd.getJSONArray("timerange");
            final JSONObject timerangeWd2 = timerangeWd.getJSONObject(2);
            final JSONArray valueWd = timerangeWd2.getJSONArray("value");
            final JSONObject valueWd2 = valueWd.getJSONObject(1);

            String arahKardinalAngin = valueWd2.getString("#text");

            // Dapatkan kecepatan angin
            final JSONObject ws = parameter.getJSONObject(8);
            final JSONArray timerangeWs = ws.getJSONArray("timerange");
            final JSONObject timerangeWs2 = timerangeWs.getJSONObject(2);
            final JSONArray valueWs = timerangeWs2.getJSONArray("value");
            final JSONObject valueWs2 = valueWs.getJSONObject(2);

            String nilaiKecepatanAngin = valueWs2.getString("#text");

            namaKotaLabel = new JLabel(namaPwt);
            suhu = new JTextField(String.format("%s \u00B0" + "C", derajatSuhu));
            keadaanCuaca = new JTextField(KonversiKodeCuaca(kodeKeadaanCuaca));
            arahAngin = new JTextField(KonversiKodeAngin(arahKardinalAngin));
            kecepatanAngin = new JTextField(String.format("%s " + "km/jam", nilaiKecepatanAngin));

        } catch (IOException e) {
            e.printStackTrace();
        }

        namaKotaLabel.setFont(new Font("Sans Serif", Font.BOLD, 24));
        keadaanCuaca.setEditable(false);
        keadaanCuaca.setHorizontalAlignment(JTextField.CENTER);
        suhu.setEditable(false);
        suhu.setHorizontalAlignment(JTextField.CENTER);
        arahAngin.setEditable(false);
        arahAngin.setHorizontalAlignment(JTextField.CENTER);
        kecepatanAngin.setEditable(false);
        kecepatanAngin.setHorizontalAlignment(JTextField.CENTER);

        this.add(namaKotaLabel); this.add(new JLabel(""));
        this.add(cuacaLabel); this.add(keadaanCuaca);
        this.add(suhuLabel); this.add(suhu);
        this.add(arahAnginLabel); this.add(arahAngin);
        this.add(kecepatanAnginLabel); this.add(kecepatanAngin);

        this.setVisible(true);

    }

    private String KonversiKodeCuaca(String kode) {
        dataKodeCuaca.put(0, "Cerah / Clear Skies");
        dataKodeCuaca.put(1, "Cerah Berawan / Partly Cloudy");
        dataKodeCuaca.put(2, "Cerah Berawan / Partly Cloudy");
        dataKodeCuaca.put(3, "Berawan / Mostly Cloudy");
        dataKodeCuaca.put(4, "Berawan Tebal / Overcast");
        dataKodeCuaca.put(5, "Udara Kabur / Haze");
        dataKodeCuaca.put(10, "Asap / Smoke");
        dataKodeCuaca.put(45, "Kabut / Fog");
        dataKodeCuaca.put(60, "Hujan Ringan / Light Rain");
        dataKodeCuaca.put(61, "Hujan Sedang / Rain");
        dataKodeCuaca.put(63, "Hujan Lebat / Heavy Rain");
        dataKodeCuaca.put(80, "Hujan Lokal / Isolated Shower");
        dataKodeCuaca.put(95, "Hujan Petir / Severe Thunderstorm");
        dataKodeCuaca.put(97, "Hujan Petir / Severe Thunderstorm");

        return dataKodeCuaca.get(Integer.parseInt(kode));
    }

    private String KonversiKodeAngin(String kode) {
        dataArahAngin.put("N", "Utara");
        dataArahAngin.put("NNE", "Utara-Timur Laut");
        dataArahAngin.put("NE", "Timur Laut");
        dataArahAngin.put("ENE", "Timur-Utara Timur");
        dataArahAngin.put("E", "Timur");
        dataArahAngin.put("ESE", "Timur-Selatan Timur");
        dataArahAngin.put("SE", "Tenggara");
        dataArahAngin.put("SSE", "Selatan-Tenggara");
        dataArahAngin.put("S", "Selatan");
        dataArahAngin.put("SSW", "Selatan-Barat Daya");
        dataArahAngin.put("SW", "Barat Daya");
        dataArahAngin.put("WSW", "Barat-Barat Daya");
        dataArahAngin.put("W", "Barat");
        dataArahAngin.put("WNW", "Barat-Utara Barat");
        dataArahAngin.put("NW", "Utara Barat");
        dataArahAngin.put("NNW", "Utara-Utara Barat");
        dataArahAngin.put("VARIABLE", "Berubah-ubah");

        return dataArahAngin.get(kode);
    }

    private String DataJsonCuaca() throws IOException {
        try (CloseableHttpClient httpclient = HttpClients.createDefault()) {

            HttpGet httpget = new HttpGet("https://cuaca.umkt.ac.id/api/cuaca/DigitalForecast-JawaTengah.xml?format=json");
            System.out.println("Menjalankan permintaan " + httpget.getRequestLine());

            ResponseHandler<String> responseHandler = HandlerDataCuaca();
            String responseBody = httpclient.execute(httpget, responseHandler);

            return responseBody;
        }
    }

    private ResponseHandler<String> HandlerDataCuaca() {
        return response -> {
            int status = response.getStatusLine().getStatusCode();
            if (status >= 200 && status < 300) {
                HttpEntity entity = response.getEntity();
                return entity != null ? EntityUtils.toString(entity) : null;
            } else {
                throw new ClientProtocolException("Status error: " + status);
            }
        };
    }

    public static void main(String[] args) {
        new App();
    }
}
