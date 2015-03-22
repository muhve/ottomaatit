package felixl.ottoautomaatit;

import android.content.Context;
import android.content.res.AssetManager;
import android.os.AsyncTask;

import com.google.android.gms.maps.model.LatLng;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Scanner;

public class FileReader extends AsyncTask<Void, Void, ArrayList<LatLng>> {
    private BufferedReader br;

    public FileReader(Context context) {

        AssetManager am = context.getAssets();

        try {
            InputStream is = am.open("input.txt");
            InputStreamReader isr = new InputStreamReader(is);
            br = new BufferedReader(isr);
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

    public ArrayList<LatLng> read() {
        ArrayList<LatLng> places = new ArrayList<LatLng>();

        while (true) {
            try {
                String[] line = br.readLine().split(" ");
                if (line[0].equals("-1")) break;
                String lat = line[0];
                String lon = line[1];

                LatLng place = new LatLng(Double.parseDouble(lat), Double.parseDouble(lon));
                places.add(place);
            } catch (IOException e) {
                break;
            }
        }
        return places;
    }

    @Override
    protected ArrayList<LatLng> doInBackground(Void... params) {
        return read();
    }
}
