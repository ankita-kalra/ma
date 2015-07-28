package com.peacecorps.malaria;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.media.Image;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * Created by Ankita on 6/13/2015.
 */
public class DayFragmentActivity extends FragmentActivity {


    private static String TAGD = DayFragmentActivity.class.getSimpleName();
    private final String[] months = { "January", "February", "March",
            "April", "May", "June", "July", "August", "September",
            "October", "November", "December" };
    private String date_header=""; private String mon="";
    private int day,month, year;
    private String drugPicked="";
    static SharedPreferenceStore mSharedPreferenceStore;
    public static Context mFragmentContext;
    final Context con = this;
    private RadioGroup btnRadGroup;
    private RadioButton btnRadButton;
    private String ch="";
    private int flag=0;
    private long curr_time=0;
    @Override
    public void onCreate(Bundle savedInstanceState) {

        /* setting view for Day Fragment Activity*/
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_day);

        /*declaring variables for accessing Shared Preferences*/
        mSharedPreferenceStore = new SharedPreferenceStore();
        mFragmentContext = DayFragmentActivity.this
                .getApplicationContext();
        mSharedPreferenceStore.getSharedPreferences(this);

        /*declaring variables for accessing Database*/
        final DatabaseSQLiteHelper sqLite= new DatabaseSQLiteHelper(this);

        /*displaying clicked date on the Day Fragment*/
        Intent intent = getIntent();
        String selected_date = intent.getStringExtra(ThirdAnalyticFragment.DATE_TAG);
        TextView dayDB = (TextView)findViewById(R.id.dayDB);
        SimpleDateFormat dateFormatter = new SimpleDateFormat("EEE MMM dd HH:mm:ss zzzz yyyy");
        Date comp_date=Calendar.getInstance().getTime();
        Log.d(TAGD,""+comp_date.getDate());
        Log.d(TAGD,""+comp_date.getMonth());
        Log.d(TAGD,""+comp_date.getYear()+1900);
        try {
           comp_date   = dateFormatter.parse(selected_date);
        } catch (Exception e) {
            e.printStackTrace();
        }
        Calendar cal = Calendar.getInstance();
        cal.setTime(comp_date);
        month=cal.get(Calendar.MONTH);
        day=cal.get(Calendar.DATE);
        mon=months[month];
        year=cal.get(Calendar.YEAR);
        curr_time=cal.getTimeInMillis();

        date_header=String.valueOf(day)+" "+mon+" "+String.valueOf(year);
        Log.d(TAGD, date_header);
        dayDB.setText(date_header);

        /*dispaying chosen drug out of the three*/
        TextView drugID = (TextView)findViewById(R.id.drugID);
        drugPicked=SharedPreferenceStore.mPrefsStore.getString("com.peacecorps.malaria.drugPicked",null);
        drugID.setText(drugPicked);
        //Log.d(TAGD, drugPicked);

        /*fetching information about the first time drug was taken*/
        long firstTime = SharedPreferenceStore.mPrefsStore.getLong("com.peacecorps.malaria.firstRunTime", 0);
        //Log.d(TAGD,String.valueOf(firstTime));
        cal.setTimeInMillis(firstTime);
        int firstDate=cal.get(Calendar.DATE);
        int firstMonth= cal.get(Calendar.MONTH);
        int firstYear= cal.get(Calendar.YEAR);
        Log.d(TAGD,"First Run Time:"+firstDate+" "+firstMonth+" "+firstYear);

        /*Querying the database whether drug was taken on that specific day or not*/
        Log.d(TAGD,"querying  "+day+" "+month+" "+year+" ");
        String data=sqLite.getMedicationData(day, month, year);
        Log.d(TAGD, "++++++" + data + "++++++");

        /*Setting the Indicator according to the Result*/
        final ImageView indicator = (ImageView)findViewById(R.id.medi_indicator);
        if(data.compareTo("yes")==0)
        {
            Toast.makeText(getApplicationContext(),"I took medicine!",Toast.LENGTH_LONG).show();
            indicator.setBackgroundResource(R.drawable.accept_medi_checked_);
        }
        else if(data.compareTo("no")==0) {
            indicator.setBackgroundResource(R.drawable.reject_medi_checked);
            Toast.makeText(getApplicationContext(), "I didn't take medicine!", Toast.LENGTH_LONG).show();
        }
        else
        {   Log.d(TAGD,"Inside Missed Drug Entry");

            selected_date=SharedPreferenceStore.mPrefsStore.getString("com.peacecorps.malaria.checkMediLastTakenTime","");
            SimpleDateFormat dateF = new SimpleDateFormat("dd/MM/yyyy");
            Date cd=Calendar.getInstance().getTime();
            try {
                cd   = dateFormatter.parse(selected_date);
            } catch (Exception e) {
                e.printStackTrace();
            }
            cal.setTime(cd);

            Log.d(TAGD,"Medication Last Taken Time");
            Log.d(TAGD, "" + cal.get(Calendar.DATE));
            Log.d(TAGD, "" + cal.get(Calendar.MONTH));
            Log.d(TAGD, "" + cal.get(Calendar.YEAR));

            long queried_date=comp_date.getTime();
            long last_medication_date = cd.getTime();

            Calendar c= Calendar.getInstance();
            long current_date= c.getTimeInMillis();


            if(queried_date>=firstTime)
            {
                if(queried_date<=current_date)
                {
                    Toast.makeText(getApplicationContext(), "I missed entering medicine!", Toast.LENGTH_LONG).show();
                    sqLite.insertOrUpdateMissedMedicationEntry(day, month, year, 0);
                }
                else
                {
                    Toast.makeText(getApplicationContext(), "This is a future date, you cannot edit it!", Toast.LENGTH_LONG).show();
                    flag=1;
                }
            }
            else {
                //Toast.makeText(getApplicationContext(), "This is a date before medication even started, you can't edit it!", Toast.LENGTH_LONG).show();
                Toast.makeText(getApplicationContext(), "I missed entering medicine!", Toast.LENGTH_LONG).show();
                sqLite.insertOrUpdateMissedMedicationEntry(day, month, year, 0);

            }





        }

        /*Implementing Editing Data Button for a Day Page*/
        ImageButton btnChangeData = (ImageButton)findViewById(R.id.btnChangeData);
        btnChangeData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final Dialog dialog = new Dialog(con);
                dialog.setContentView(R.layout.day_dialog);
                dialog.setTitle("Medicine Consumption");

                ImageView drug = (ImageView) dialog.findViewById(R.id.image);
                drug.setBackgroundResource(R.drawable.drug_normal);

                TextView tv = (TextView) dialog.findViewById(R.id.text);
                tv.setText("Added inaccurate data? Don't worry, you can change here.");
                tv.setTextSize(17);
                tv.setTextColor(getResources().getColor(R.color.golden_brown));

                btnRadGroup = (RadioGroup) dialog.findViewById(R.id.radioGroup);

                Button btnOK = (Button) dialog.findViewById(R.id.dialogButtonOK);
                btnOK.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // get selected radio button from radioGroup
                        int selectedId = btnRadGroup.getCheckedRadioButtonId();

                        // find the radiobutton by returned id
                        btnRadButton = (RadioButton) dialog.findViewById(selectedId);


                        ch = btnRadButton.getText().toString();
                        Log.d(TAGD, "Radio Button Response-" + ch);
                        if (flag == 0) {
                            Log.d(TAGD, "Radio Button Response-" + ch);
                            if (ch.equalsIgnoreCase("yes")) {
                                Toast.makeText(con,
                                        btnRadButton.getText(), Toast.LENGTH_SHORT).show();
                                int accept_count = SharedPreferenceStore.mPrefsStore.getInt("com.peacecorps.malaria.drugAcceptedCount", 0) + 1;
                                Log.d(TAGD, "accept count:" + accept_count);
                                SharedPreferenceStore.mEditor.putInt("com.peacecorps.malaria.drugAcceptedCount", accept_count).apply();
                                long firstTime = sqLite.getFirstTime();
                                Log.d(TAGD, "" + SharedPreferenceStore.mPrefsStore.getLong("com.peacecorps.malaria.firstRunTime", 0));
                                SharedPreferenceStore.mEditor.putLong("com.peacecorps.malaria.firstRunTime", firstTime).apply();
                                double prcntage = computeAdherenceRate(curr_time);
                                Log.d(TAGD, "Adherence when Yes:" + prcntage);
                                sqLite.updateMedicationEntry(day, month, year, "yes", prcntage);
                                int dosesInaRow=sqLite.getDosesInaRowDaily();
                                Log.d(TAGD,"Doses in a Row:"+dosesInaRow);
                                SharedPreferenceStore.mEditor.putInt("com.peacecorps.malaria.dailyDose", dosesInaRow).apply();
                                indicator.setBackgroundResource(R.drawable.accept_medi_checked_);

                            } else if (ch.equalsIgnoreCase("no")) {
                                Toast.makeText(con,
                                        btnRadButton.getText(), Toast.LENGTH_SHORT).show();
                                long firstTime = sqLite.getFirstTime();
                                //Log.d(TAGD, "" + SharedPreferenceStore.mPrefsStore.getLong("com.peacecorps.malaria.firstRunTime", 0));
                                SharedPreferenceStore.mEditor.putLong("com.peacecorps.malaria.firstRunTime", firstTime).apply();
                                String st = sqLite.getStatus(day, month, year);
                                if (st != null && st.equalsIgnoreCase("yes")) {
                                    int accept_count = SharedPreferenceStore.mPrefsStore.getInt("com.peacecorps.malaria.drugAcceptedCount", 0) - 1;
                                    SharedPreferenceStore.mEditor.putInt("com.peacecorps.malaria.drugAcceptedCount", accept_count).apply();
                                }
                                double prcntage = computeAdherenceRate(curr_time);
                                Log.d(TAGD, "Adherence when No:" + prcntage);
                                sqLite.updateMedicationEntry(day, month, year, "no", prcntage);
                                int dosesInaRow = sqLite.getDosesInaRowDaily();
                                Log.d(TAGD, "Doses in a Row:" + dosesInaRow);
                                SharedPreferenceStore.mEditor.putInt("com.peacecorps.malaria.dailyDose", dosesInaRow).apply();
                                indicator.setBackgroundResource(R.drawable.reject_medi_checked);
                            } else
                                dialog.dismiss();
                        } else {
                            Toast.makeText(getApplicationContext(), "You are not allowed to edit!", Toast.LENGTH_LONG).show();
                        }
                        dialog.dismiss();

                    }
                });
                Button btnCancel = (Button) dialog.findViewById(R.id.dialogButtonCancel);
                btnCancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });
                dialog.show();

            }
        });
    }

    public static double computeAdherenceRate(long day_time) {
        long interval = checkDrugTakenTimeInterval("firstRunTime",day_time) + 1;
        int takenCount = SharedPreferenceStore.mPrefsStore.getInt("com.peacecorps.malaria.drugAcceptedCount", 0);
        double adherenceRate = ((double)takenCount /(double) interval) * 100;
        return adherenceRate;
    }

    public static long checkDrugTakenTimeInterval(String time,long day_time) {
        long interval = 0;
        long takenDate = mSharedPreferenceStore.mPrefsStore.getLong("com.peacecorps.malaria."
                + time, 0);
        Log.d(TAGD, time + ":" + takenDate);
        long oneDay = 1000 * 60 * 60 * 24;
        interval = (day_time - takenDate) / oneDay;
        return interval;
    }


    /*public void getSharedPreferences() {

        mSharedPreferenceStore.mPrefsStore = getActivity()
                .getSharedPreferences("com.peacecorps.malaria.storeTimePicked",
                        Context.MODE_PRIVATE);
        mSharedPreferenceStore.mEditor = mSharedPreferenceStore.mPrefsStore
                .edit();
    }

    public static double computeAdherenceRate() {
        long interval = checkDrugTakenTimeInterval("firstRunTime") + 1;
        int takenCount = SharedPreferenceStore.mPrefsStore.getInt("com.peacecorps.malaria.drugAcceptedCount", 0);
        double adherenceRate = (takenCount / interval) * 100;
        return adherenceRate;
    }

    public void saveUsersettings(Boolean state, Boolean isWeekly) {
        if (isWeekly) {
            mSharedPreferenceStore.mEditor.putLong("com.peacecorps.malaria.weeklyDate",
                    new Date().getTime()).commit();
            mSharedPreferenceStore.mEditor.putBoolean(
                    "com.peacecorps.malaria.isWeeklyDrugTaken", state).commit();
        } else {
            mSharedPreferenceStore.mEditor.putLong("com.peacecorps.malaria.dateDrugTaken",
                    new Date().getTime()).commit();
            mSharedPreferenceStore.mEditor.putBoolean("com.peacecorps.malaria.isDrugTaken",
                    state).commit();
        }
        mSharedPreferenceStore.mEditor.putInt("com.peacecorps.malaria.drugRejectedCount",
                drugRejectedCount).commit();
        mSharedPreferenceStore.mEditor.putInt("com.peacecorps.malaria.drugAcceptedCount",
                mDrugAcceptedCount).commit();

    }

    public void getSettings() {
        checkDay = mCalendar.get(Calendar.DAY_OF_WEEK);
        mGetCurrentDate = new SimpleDateFormat("dd/MM/yyyy",
                Locale.getDefault()).format(mCalendar.getTime());
        mDrugAcceptedCount = mSharedPreferenceStore.mPrefsStore.getInt(
                "com.peacecorps.malaria.drugAcceptedCount", 0);
        drugRejectedCount = mSharedPreferenceStore.mPrefsStore.getInt(
                "com.peacecorps.malaria.drugRejectedCount", 0);
        decideDayofWeek(checkDay, mPossibledays);
    }

    public static long checkDrugTakenTimeInterval(String time) {
        long interval = 0;
        long today = new Date().getTime();
        long takenDate = mSharedPreferenceStore.mPrefsStore.getLong("com.peacecorps.malaria."
                + time, 0);
        Log.d(TAGHSF, time + ":" + takenDate);
        long oneDay = 1000 * 60 * 60 * 24;
        interval = (today - takenDate) / oneDay;
        return interval;
    }

    public void storeMediTimeLastChecked() {
        CharSequence lastMedicationCheckedTime = "";
        Calendar c = Calendar.getInstance();
        lastMedicationCheckedTime = new SimpleDateFormat("dd/MM",
                Locale.getDefault()).format(c.getTime());

        mSharedPreferenceStore.mEditor.putString(
                "com.peacecorps.malaria.checkMediLastTakenTime",
                lastMedicationCheckedTime.toString()).commit();
    }

    public void changeWeeklyAlarmTime() {
        int hour = Calendar.getInstance().get(Calendar.HOUR);
        int minute = Calendar.getInstance().get(Calendar.MINUTE) - 1;
        getActivity().startService(
                new Intent(getActivity(), AlarmService.class));
        mSharedPreferenceStore.mEditor.putInt("com.peacecorps.malaria.AlarmHour", hour)
                .commit();
        mSharedPreferenceStore.mEditor.putInt("com.peacecorps.malaria.AlarmMinute", minute)
                .commit();
    }


    public String decideDayofWeek(int checkDay, String possibleDays[]) {
        String currentDayOfWeek = null;
        switch (checkDay) {
            case 1:
                currentDayOfWeek = possibleDays[0];
                break;
            case 2:
                currentDayOfWeek = possibleDays[1];
                break;
            case 3:
                currentDayOfWeek = possibleDays[2];
                break;
            case 4:
                currentDayOfWeek = possibleDays[3];
                break;
            case 5:
                currentDayOfWeek = possibleDays[4];
                break;
            case 6:
                currentDayOfWeek = possibleDays[5];
                break;
            case 7:
                currentDayOfWeek = possibleDays[6];
                break;

        }
        return currentDayOfWeek;
    }*/



}
