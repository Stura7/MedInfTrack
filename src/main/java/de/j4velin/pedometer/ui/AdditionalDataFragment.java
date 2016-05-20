package de.j4velin.pedometer.ui;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import java.sql.Time;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;

import de.j4velin.pedometer.R;

import static java.util.Timer.*;

/**
 * A placeholder fragment containing a simple view.
 */
public class AdditionalDataFragment extends Fragment {
    public AdditionalDataFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_additional_data, container, false);

        final ListView lvKeyValueMedData = (ListView) rootView.findViewById(R.id.AD_listView);

        Calendar kalender = Calendar.getInstance();
        SimpleDateFormat datumsformat = new SimpleDateFormat("dd.MM.yyyy");
        EditText edDate = (EditText) rootView.findViewById(R.id.AD_date);
        edDate.setText(datumsformat.format(kalender.getTime()));

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

        ArrayList<Map<String, String>> lKeyValue = getMedDataKeyValue();
        final String[] from = { "key", "value", "unit" };
        int[] to = {R.id.ad_list_item_textview, R.id.ad_list_item_editText, R.id.ad_list_item_unit};

        SimpleAdapter adapter = new SimpleAdapter(getActivity(), lKeyValue,
                R.layout.ad_list_item, from, to);

        lvKeyValueMedData.setAdapter(adapter);
        return rootView;
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

}
