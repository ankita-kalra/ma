package com.peacecorps.malariatest;

import android.test.AndroidTestCase;
import android.test.RenamingDelegatingContext;

import com.peacecorps.malaria.DatabaseSQLiteHelper;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by Ankita on 7/7/2015.
 */
public class MalariaDatabaseTest extends AndroidTestCase {

    private DatabaseSQLiteHelper db;

    public void setUp()
    {
        RenamingDelegatingContext context =new RenamingDelegatingContext(getContext(),"test_");
        db=new DatabaseSQLiteHelper(context);
    }

    public void testAddEntryinIOMMEmethod()
    {
        int date=30;
        int month=5;
        int year=2015;

        double percentage=23.00;

        db.insertOrUpdateMissedMedicationEntry(date,month,year,percentage);
        long x=db.getFirstTime();

        String date_header=""+date+"/"+month+"/"+year;
        SimpleDateFormat sdf= new SimpleDateFormat("dd/MM/yyyy");
        Date d = Calendar.getInstance().getTime();

        try{
            d=sdf.parse(date_header);
        }
        catch (ParseException e)
        {
            e.printStackTrace();
        }

        Calendar c=Calendar.getInstance();

        c.setTime(d);

        long y = c.getTimeInMillis();
        assertEquals(x,y);
    }

    public void testDosesInaRowWithNoEntry(){


        int x = db.getDosesInaRowDaily();

        assertEquals(0,x);


    }

    public void testDosesInaRowWithASingleEntry(){

        int date=30;
        int month=5;
        int year=2015;

        double percentage=23.00;

        db.insertOrUpdateMissedMedicationEntry(date, month, year, percentage);

        db.updateMedicationEntry(date, month, year, "yes", percentage);

        int x = db.getDosesInaRowDaily();

        assertEquals(1,x);


    }

    public void testDosesInaRowWithDoubleEntry(){

        int date=30;
        int month=5;
        int year=2015;

        double percentage=23.00;

        db.insertOrUpdateMissedMedicationEntry(date, month, year, percentage);

        db.updateMedicationEntry(date, month, year, "yes", percentage);

        date=29;month=5;year=2015;percentage=28.00;

        db.insertOrUpdateMissedMedicationEntry(date, month, year, percentage);

        db.updateMedicationEntry(date,month,year,"yes",percentage);

        int x = db.getDosesInaRowDaily();

        assertEquals(2,x);

    }

    public void testDosesInaRowWithDiscontinuedYesEntry(){
        int date=30;
        int month=5;
        int year=2015;

        double percentage=23.00;

        db.insertOrUpdateMissedMedicationEntry(date, month, year, percentage);

        db.updateMedicationEntry(date, month, year, "yes", percentage);

        date=29;month=5;year=2015;percentage=28.00;

        db.insertOrUpdateMissedMedicationEntry(date, month, year, percentage);

        db.updateMedicationEntry(date,month,year,"yes",percentage);

        date=28;month=5;year=2015;percentage=28.00;

        db.insertOrUpdateMissedMedicationEntry(date, month, year, percentage);

        db.updateMedicationEntry(date,month,year,"no",percentage);

        date=27;month=5;year=2015;percentage=28.00;

        db.insertOrUpdateMissedMedicationEntry(date, month, year, percentage);

        db.updateMedicationEntry(date, month, year, "yes", percentage);

        int x = db.getDosesInaRowDaily();

        assertEquals(2, x);



    }

    public void testDosesInaRowWithDiscontiuedDateYesEntry(){

        int date=30;
        int month=5;
        int year=2015;

        double percentage=23.00;

        db.insertOrUpdateMissedMedicationEntry(date, month, year, percentage);

        db.updateMedicationEntry(date, month, year, "yes", percentage);

        date=29;month=5;year=2015;percentage=28.00;

        db.insertOrUpdateMissedMedicationEntry(date, month, year, percentage);

        db.updateMedicationEntry(date,month,year,"yes",percentage);

        date=27;month=5;year=2015;percentage=28.00;

        db.insertOrUpdateMissedMedicationEntry(date, month, year, percentage);

        db.updateMedicationEntry(date,month,year,"yes",percentage);

        date=26;month=5;year=2015;percentage=28.00;

        db.insertOrUpdateMissedMedicationEntry(date, month, year, percentage);

        db.updateMedicationEntry(date, month, year, "yes", percentage);

        int x = db.getDosesInaRowDaily();

        assertEquals(2,x);
    }

    public void testDosesInaRowWithDiscontinuedBoundaryDateYesEntry(){

        int date=30;
        int month=5;
        int year=2015;

        double percentage=23.00;

        db.insertOrUpdateMissedMedicationEntry(date, month, year, percentage);

        db.updateMedicationEntry(date, month, year, "yes", percentage);

        date=1;month=6;year=2015;percentage=28.00;

        db.insertOrUpdateMissedMedicationEntry(date, month, year, percentage);

        db.updateMedicationEntry(date,month,year,"yes",percentage);

        date=2;month=6;year=2015;percentage=28.00;

        db.insertOrUpdateMissedMedicationEntry(date, month, year, percentage);

        db.updateMedicationEntry(date,month,year,"yes",percentage);

        date=3;month=6;year=2015;percentage=28.00;

        db.insertOrUpdateMissedMedicationEntry(date, month, year, percentage);

        db.updateMedicationEntry(date, month, year, "yes", percentage);

        int x = db.getDosesInaRowDaily();

        assertEquals(4,x);

    }

    public void testDosesInaRowWeeklyIrregular(){

        int date=24;
        int month=5;
        int year=2015;

        double percentage=23.00;

        db.insertOrUpdateMissedMedicationEntry(date, month, year, percentage);

        db.updateMedicationEntry(date, month, year, "yes", percentage);

        date=17;month=5;year=2015;percentage=28.00;

        db.insertOrUpdateMissedMedicationEntry(date, month, year, percentage);

        db.updateMedicationEntry(date,month,year,"yes",percentage);

        date=9;month=5;year=2015;percentage=28.00;

        db.insertOrUpdateMissedMedicationEntry(date, month, year, percentage);

        db.updateMedicationEntry(date,month,year,"yes",percentage);

        date=3;month=5;year=2015;percentage=28.00;

        db.insertOrUpdateMissedMedicationEntry(date, month, year, percentage);

        db.updateMedicationEntry(date, month, year, "yes", percentage);

        int x = db.getDosesInaRowWeekly();

        assertEquals(4,x);


    }

    public void testDosesInaRowWeeklyDiscontinued(){

        int date=30;
        int month=5;
        int year=2015;

        double percentage=23.00;

        db.insertOrUpdateMissedMedicationEntry(date, month, year, percentage);

        db.updateMedicationEntry(date, month, year, "yes", percentage);

        date=23;month=5;year=2015;percentage=28.00;

        db.insertOrUpdateMissedMedicationEntry(date, month, year, percentage);

        db.updateMedicationEntry(date,month,year,"yes",percentage);

        date=16;month=5;year=2015;percentage=28.00;

        db.insertOrUpdateMissedMedicationEntry(date, month, year, percentage);

        db.updateMedicationEntry(date,month,year,"yes",percentage);

        date=3;month=5;year=2015;percentage=28.00;

        db.insertOrUpdateMissedMedicationEntry(date, month, year, percentage);

        db.updateMedicationEntry(date, month, year, "yes", percentage);

        int x = db.getDosesInaRowWeekly();

        assertEquals(3,x);


    }

    public void testDosesInaRowWeeklyNoEntry(){

        int x = db.getDosesInaRowWeekly();

        assertEquals(0,x);


    }

    public void testDosesInaRowWeeklyIrregularDifferentMonths(){

        int date=24;
        int month=5;
        int year=2015;

        double percentage=23.00;

        db.insertOrUpdateMissedMedicationEntry(date, month, year, percentage);

        db.updateMedicationEntry(date, month, year, "yes", percentage);

        date=17;month=5;year=2015;percentage=28.00;

        db.insertOrUpdateMissedMedicationEntry(date, month, year, percentage);

        db.updateMedicationEntry(date,month,year,"yes",percentage);

        date=9;month=5;year=2015;percentage=28.00;

        db.insertOrUpdateMissedMedicationEntry(date, month, year, percentage);

        db.updateMedicationEntry(date,month,year,"yes",percentage);

        date=31;month=4;year=2015;percentage=28.00;

        db.insertOrUpdateMissedMedicationEntry(date, month, year, percentage);

        db.updateMedicationEntry(date, month, year, "yes", percentage);

        date=28;month=4;year=2015;percentage=28.00;

        db.insertOrUpdateMissedMedicationEntry(date, month, year, percentage);

        db.updateMedicationEntry(date, month, year, "yes", percentage);

        int x = db.getDosesInaRowWeekly();

        assertEquals(5,x);


    }


}
