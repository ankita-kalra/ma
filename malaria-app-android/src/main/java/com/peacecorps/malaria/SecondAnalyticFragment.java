
package com.peacecorps.malaria;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import com.jjoe64.graphview.CustomLabelFormatter;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.GraphView.GraphViewData;
import com.jjoe64.graphview.GraphViewSeries;
import com.jjoe64.graphview.LineGraphView;

import java.util.Calendar;
import java.util.Locale;

public class SecondAnalyticFragment extends Fragment {

    private TextView firstMonthProgressLabel, secondMonthProgressLabel, thirdMonthProgressLabel, fourthMonthProgressLabel;
    private TextView firstMonthProgressPercent, secondMonthProgressPercent, thirdMonthProgressPercent, fourthMonthProgressPercent;
    private ProgressBar firstMonthProgressBar, secondMonthProgressBar, thirdMonthProgressBar, fourthMonthProgressBar;
    private Button mSettingsButton;
    private View rootView;
    public final static String MONTH_REQ = "com.peacecorps.malaria.secondanalyticfragment.MONTHREQ";

    static SharedPreferenceStore mSharedPreferenceStore;


    private static final String DATABASE_NAME = "MalariaDatabase";
    private static final String userMedicationChoiceTable = "userSettings";
    private final int[] daysOfMonth = {31, 28, 31, 30, 31, 30, 31, 31, 30,
            31, 30, 31};
    private String TAGSAF = "SecondAnalyticFragment";

    GraphViewSeries drugGraphSeries;
    private GraphViewData[] graphViewData;
    private int date;
    private String choice;
    private Dialog dialog = null;

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_second_analytic_screen,
                null);

        mSettingsButton = (Button) rootView.findViewById(R.id.fragment_second_screen_settings_button);

        firstMonthProgressLabel = (TextView) rootView.findViewById(R.id.firstMonthProgressLabel);
        secondMonthProgressLabel = (TextView) rootView.findViewById(R.id.secondMonthProgressLabel);
        thirdMonthProgressLabel = (TextView) rootView.findViewById(R.id.thirdMonthProgressLabel);
        fourthMonthProgressLabel = (TextView) rootView.findViewById(R.id.fourthMonthProgressLabel);

        firstMonthProgressPercent = (TextView) rootView.findViewById(R.id.firstMonthProgressPercent);
        secondMonthProgressPercent = (TextView) rootView.findViewById(R.id.secondMonthProgressPercent);
        thirdMonthProgressPercent = (TextView) rootView.findViewById(R.id.thirdMonthProgressPercent);
        fourthMonthProgressPercent = (TextView) rootView.findViewById(R.id.fourthMonthProgressPercent);

        firstMonthProgressBar = (ProgressBar) rootView.findViewById(R.id.firstMonthProgressBar);
        secondMonthProgressBar = (ProgressBar) rootView.findViewById(R.id.secondMonthProgressBar);
        thirdMonthProgressBar = (ProgressBar) rootView.findViewById(R.id.thirdMonthProgressBar);
        fourthMonthProgressBar = (ProgressBar) rootView.findViewById(R.id.fourthMonthProgressBar);


        Calendar cal = Calendar.getInstance();

        date = Calendar.getInstance().get(Calendar.MONTH);
        //choice;
        if (mSharedPreferenceStore.mPrefsStore.getBoolean(
                "com.peacecorps.malaria.isWeekly", false)) {
            choice = "weekly";
        } else {
            choice = "daily";
        }

        updateUI(choice, date);


        return rootView;
    }


    @Override
    public void onResume() {
        super.onResume();
        updateUI(choice, date);

    }

    int mdate;
    int myear;

    public String getMonth(int date) {
        String month[] = getResources().getStringArray(R.array.month);
        if (date == 0) {
            date = 12;
            myear = Calendar.getInstance().get(Calendar.YEAR) - 1;
        } else if (date == -1) {
            date = 11;
            myear = Calendar.getInstance().get(Calendar.YEAR) - 1;
        } else if (date == -2) {
            date = 10;
            myear = Calendar.getInstance().get(Calendar.YEAR) - 1;
        }
        myear = Calendar.getInstance().get(Calendar.YEAR);
        mdate = date;
        return month[date];
    }

    public void addButtonListeners() {
        mSettingsButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                mSharedPreferenceStore.mEditor.putBoolean(
                        "com.peacecorps.malaria.hasUserSetPreference", false).commit();
                addDialog();

            }
        });

        firstMonthProgressBar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(getActivity(), ThirdAnalyticFragment.class);
                String mon = firstMonthProgressLabel.getText().toString();
                intent.putExtra(MONTH_REQ, mon); //transfering the month Information for displaying Calendar of Specific Month
                startActivity(intent);
                Toast.makeText(getActivity(), "First progress", Toast.LENGTH_SHORT).show();
            }
        });

        secondMonthProgressBar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(getActivity(), ThirdAnalyticFragment.class);
                String mon = secondMonthProgressLabel.getText().toString();
                intent.putExtra(MONTH_REQ, mon); //transfering the month Information for displaying Calendar of Specific Month
                startActivity(intent);
                Toast.makeText(getActivity(), "Second progress", Toast.LENGTH_SHORT).show();
            }
        });

        thirdMonthProgressBar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(getActivity(), ThirdAnalyticFragment.class);
                String mon = thirdMonthProgressLabel.getText().toString();
                intent.putExtra(MONTH_REQ, mon); //transfering the month Information for displaying Calendar of Specific Month
                startActivity(intent);
                Toast.makeText(getActivity(), "Third progress", Toast.LENGTH_SHORT).show();
            }
        });

        fourthMonthProgressBar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), ThirdAnalyticFragment.class);
                String mon = fourthMonthProgressLabel.getText().toString();
                intent.putExtra(MONTH_REQ, mon); //transfering the month Information for displaying Calendar of Specific Month
                startActivity(intent);
                Toast.makeText(getActivity(), "Fourth progress", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void getSharedPreferences() {

        mSharedPreferenceStore.mPrefsStore = getActivity()
                .getSharedPreferences("com.peacecorps.malaria.storeTimePicked",
                        Context.MODE_PRIVATE);
        mSharedPreferenceStore.mEditor = mSharedPreferenceStore.mPrefsStore
                .edit();
    }

    public int getNumberOfDaysInMonth(int month) {
        return daysOfMonth[month];
    }

    public void updateProgressBar(String choice, int date) {
        DatabaseSQLiteHelper sqLH = new DatabaseSQLiteHelper(getActivity());
        firstMonthProgressLabel.setText(getMonth(date - 3));
        int progress = sqLH.getData(mdate, myear, choice);
        float progressp = 0;
        if (choice.equalsIgnoreCase("daily"))
            progressp = (float) progress / getNumberOfDaysInMonth(mdate) * 100;
        else
            progressp = progress * 25;
        firstMonthProgressBar.setProgress((int) progressp);
        firstMonthProgressPercent.setText("" + (int) progressp + "%");

        secondMonthProgressLabel.setText(getMonth(date - 2));
        progress = sqLH.getData(mdate, myear, choice);
        if (choice.equalsIgnoreCase("daily"))
            progressp = (float) progress / getNumberOfDaysInMonth(mdate) * 100;
        else
            progressp = progress * 25;
        secondMonthProgressBar.setProgress((int) progressp);
        secondMonthProgressPercent.setText("" + (int) progressp + "%");

        thirdMonthProgressLabel.setText(getMonth(date - 1));
        progress = sqLH.getData(mdate, myear, choice);
        if (choice.equalsIgnoreCase("daily"))
            progressp = (float) progress / getNumberOfDaysInMonth(mdate) * 100;
        else
            progressp = progress * 25;
        thirdMonthProgressBar.setProgress((int) progressp);
        thirdMonthProgressPercent.setText("" + (int) progressp + "%");

        fourthMonthProgressLabel.setText(getMonth(date));
        progress = sqLH.getData(mdate, myear, choice);
        Log.d(TAGSAF, "Query Return: " + progress);
        if (choice.equalsIgnoreCase("daily"))
            progressp = (float) progress / getNumberOfDaysInMonth(mdate) * 100;
        else
            progressp = progress * 25;
        Log.d(TAGSAF, "" + getNumberOfDaysInMonth(mdate));
        Log.d(TAGSAF, "" + progress);
        Log.d(TAGSAF, "" + progressp);
        fourthMonthProgressBar.setProgress((int) progressp);
        fourthMonthProgressPercent.setText("" + (int) progressp + "%");
    }

    public void updateUI(String choice, int date) {

        updateProgressBar(choice, date);
        SetupAndShowGraph();
        getSharedPreferences();
        addButtonListeners();

    }

    public void SetupAndShowGraph() {
        GraphViewData graphViewData[] = new GraphViewData[DatabaseSQLiteHelper.date.size()];

        for (int index = 0; index < DatabaseSQLiteHelper.percentage.size(); index++) {

            graphViewData[index] = new GraphViewData(DatabaseSQLiteHelper.date.get(index), Double.parseDouble("" + DatabaseSQLiteHelper.percentage.get(index)));
        }
        drugGraphSeries = new GraphViewSeries(graphViewData);

        GraphView lineGraphView = new LineGraphView(getActivity(), "");

        lineGraphView.getGraphViewStyle().setGridColor(getResources().getColor(R.color.golden_brown));
        lineGraphView.getGraphViewStyle().setHorizontalLabelsColor(getResources().getColor(R.color.golden_brown));
        lineGraphView.getGraphViewStyle().setVerticalLabelsColor(getResources().getColor(R.color.golden_brown));
        lineGraphView.getGraphViewStyle().setTextSize(15.0F);
        lineGraphView.setScrollable(true);
        lineGraphView.setScalable(true);

        lineGraphView.setCustomLabelFormatter(new CustomLabelFormatter() {
            @Override
            public String formatLabel(double value, boolean isXAxis) {


                return null;
            }
        });

        ((LineGraphView) lineGraphView).setDrawBackground(true);
        lineGraphView.addSeries(drugGraphSeries);

        LinearLayout linearLayout = (LinearLayout) rootView.findViewById(R.id.graphView);
        linearLayout.addView(lineGraphView);


    }

    public void addDialog()
    {
        dialog = new Dialog(this.getActivity());
        dialog.setContentView(R.layout.resetdata_dialog);
        dialog.setTitle("Reset Data");

        final RadioGroup btnRadGroup = (RadioGroup) dialog.findViewById(R.id.radioGroupReset);
        Button btnOK = (Button) dialog.findViewById(R.id.dialogButtonOKReset);

        btnOK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // get selected radio button from radioGroup
                int selectedId = btnRadGroup.getCheckedRadioButtonId();

                // find the radiobutton by returned id
                RadioButton btnRadButton = (RadioButton) dialog.findViewById(selectedId);

                String ch = btnRadButton.getText().toString();

                if (ch.equalsIgnoreCase("yes")) {
                    DatabaseSQLiteHelper sqLite = new DatabaseSQLiteHelper(getActivity());
                    sqLite.resetDatabase();
                    mSharedPreferenceStore.mEditor.clear().commit();
                    startActivity(new Intent(getActivity(),
                            UserMedicineSettingsFragmentActivity.class));
                    getActivity().finish();
                } else {
                    dialog.dismiss();
                }

            }
        });

        Button btnCancel = (Button) dialog.findViewById(R.id.dialogButtonCancelReset);
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.show();

    }

}
