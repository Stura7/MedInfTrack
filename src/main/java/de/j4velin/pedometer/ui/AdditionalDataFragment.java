package de.j4velin.pedometer.ui;

import android.app.AlertDialog;
import android.app.Fragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Environment;
import android.util.Xml;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Time;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.io.StringReader;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;


import de.j4velin.pedometer.R;

import static java.util.Timer.*;

/**
 * A placeholder fragment containing a simple view.
 */
public class AdditionalDataFragment extends Fragment implements View.OnClickListener {
    private static final String SZ_NAMESPACE = null;
    private SimpleAdapter adapterMedFragment =  null;
    private ListView lvKeyValueMedData = null;

    public AdditionalDataFragment() {
    }

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_additional_data, container, false);

        Button btnSubmit = (Button) rootView.findViewById(R.id.AD_Submit);
        btnSubmit.setOnClickListener(this);

        this.lvKeyValueMedData = (ListView) rootView.findViewById(R.id.AD_listView);
        EditText edDate = (EditText) rootView.findViewById(R.id.AD_date);
        edDate.setText(this.getSimpleDate());

/*
        lvKeyValueMedData.setAdapter(adapterListViewKey);
        lvKeyValueMedData.setAdapter(adapterListViewValue);

        lvKeyValueMedData.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, final View view,
                                    int position, long id) {
                final String item = (String) parent.getItemAtPosition(position);
                view.animate().setDuration(2000).alpha(0)
                        .withEndAction(new Runnable() {
                            @Override
                            public void run() {
                                lValue.remove(item);
                                adapterListViewValue.notifyDataSetChanged();
                                view.setAlpha(1);
                            }
                        });
                Toast.makeText(getActivity(), "Hier sollen die Input werde importiert oder definiert werden",
                        Toast.LENGTH_SHORT).show();
            }
        });
*/

        ArrayList<Map<String, String>> lKeyValue = this.leseXml();
        final String[] from = {"key", "value", "unit"};
        int[] to = {R.id.ad_list_item_textview, R.id.ad_list_item_editText, R.id.ad_list_item_unit};

        this.adapterMedFragment = new SimpleAdapter(getActivity(), lKeyValue,
                R.layout.ad_list_item, from, to);

        this.lvKeyValueMedData.setAdapter(this.adapterMedFragment);

        return rootView;
    }

    @Override
    public void onClick(final View v) {
        Toast.makeText(getActivity(), "" + this.adapterMedFragment.getItem(2),
                Toast.LENGTH_SHORT).show();
        Toast.makeText(getActivity(), "Do Export",
                 Toast.LENGTH_SHORT).show();
    }

    private String getSimpleDate() {
        Calendar calender = Calendar.getInstance();
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy");
        return dateFormat.format(calender.getTime());
    }

    private HashMap<String, String[]> getMedData(String key, String value, String unit) {
        HashMap<String, String[]> item = new HashMap<String, String[]>();
        item.put(key, new String[]{value, unit});
        return item;
    }

    private HashMap<String, String[]> getMedData() {
        HashMap<String, String[]> item = new HashMap<String, String[]>();
        item.put("Gewicht", new String[]{"65", "kg"});
        item.put("Größe", new String[]{"178", "cm"});
        item.put("Blutzucker", new String[]{"N/A", ""});
        item.put("Wert 1", new String[]{"52.53", ""});
        item.put("Wert 2", new String[]{"27", "Jahre"});
        item.put("Wert 3", new String[]{"positiv", ""});
        item.put("Wert 4", new String[]{"rund", ""});
        return item;
    }

    private ArrayList<Map<String, String>> getMedDataKeyValue() {
        ArrayList<Map<String, String>> list = new ArrayList<Map<String, String>>();
        list.add(putData("Gewicht", "65", "kg"));
        list.add(putData("Größe", "178", "cm"));
        list.add(putData("Blutzucker", "N/A", ""));
        list.add(putData("Wert 1", "52.53", ""));
        list.add(putData("Wert 2", "27", ""));
        list.add(putData("Wert 3", "positiv", ""));
        list.add(putData("Wert 4", "rund", ""));
        return list;
    }

    private HashMap<String, String> putData(String key, String value, String unit) {
        HashMap<String, String> item = new HashMap<String, String>();
        item.put("key", key);
        item.put("value", value);
        item.put("unit", unit);
        return item;
    }

    private ArrayList<Map<String, String>> leseXml() {
        //Toast.makeText(getActivity(), "leseXmlAktiendatenAus", Toast.LENGTH_SHORT).show();

        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            File f = new File(Environment.getExternalStorageDirectory(), "valuetypes.xml");
            if (!f.exists() || !f.canRead()) {
                new AlertDialog.Builder(getActivity())
                        .setMessage(getString(R.string.file_cant_read, f.getAbsolutePath()))
                        .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        }).create().show();
                return null;
            }
            try {
                InputStream inStream = new FileInputStream(f.toString());
                return parse(inStream);
            } catch (Exception e) {
                return null;
            }
        }
        return null;
    }

    public ArrayList<Map<String, String>> parse(InputStream in) throws XmlPullParserException, IOException {
        try {
            XmlPullParser parser = Xml.newPullParser();
            parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
            parser.setInput(in, null);
            parser.nextTag();
            parser.require(XmlPullParser.START_TAG, SZ_NAMESPACE, "MedInfTrack");
            return readMedConfig(parser);
        } finally {
            in.close();
        }
    }


    private  ArrayList<Map<String, String>> readMedConfig(XmlPullParser parser) throws XmlPullParserException, IOException {
        ArrayList<Map<String, String>> list = new ArrayList<Map<String, String>>();
        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }
            String name = parser.getName().toLowerCase();
            // Starts by looking for the entry tag
            if (name.equals("meddataset")) {
                //Toast.makeText(getActivity(), "Med", Toast.LENGTH_SHORT).show();
                String[] keyunit = readKeyUnit(parser);
                if (!keyunit[0].isEmpty() && !keyunit[1].isEmpty())
                    list.add(putData(keyunit[0], " ", keyunit[1]));
            } else {
                skip(parser);
            }
        }
        return list;
    }

    private String[] readKeyUnit(XmlPullParser parser) throws XmlPullParserException, IOException {

        String[] keyunit = new String[2];
        keyunit[0] = "";
        keyunit[1] = "";
        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }
            String name = parser.getName().toLowerCase();
            // Starts by looking for the entry tag
            switch (name) {
                case "key":
                    keyunit[0] = skip(parser);
                    break;
                case "unit":
                    keyunit[1] = skip(parser);
                    //Toast.makeText(getActivity(),keyunit[0] + " " + keyunit[1], Toast.LENGTH_SHORT).show();
                    break;
                default:
                    skip(parser);
            }

        }
        return keyunit;
    }

    private String skip(XmlPullParser parser) throws XmlPullParserException, IOException {
        if (parser.getEventType() != XmlPullParser.START_TAG) {
            throw new IllegalStateException();
        }
        String szText = null;
        int depth = 1;
        while (depth != 0) {
            switch (parser.next()) {
                case XmlPullParser.END_TAG:
                     depth--;
                    break;
                case XmlPullParser.START_TAG:
                    depth++;
                    break;
                default:
            }
            if (parser.getText() != null) {
                szText = parser.getText();
            }
        }
        return szText;
    }
}
