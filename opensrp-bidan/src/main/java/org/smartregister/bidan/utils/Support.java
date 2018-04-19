package org.smartregister.bidan.utils;

import android.app.Activity;
import android.content.Context;
import android.widget.ImageView;
import android.widget.TextView;

import org.apache.commons.lang3.StringUtils;
import org.joda.time.LocalDate;
import org.joda.time.Months;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.smartregister.bidan.R;
import org.smartregister.commonregistry.CommonPersonObject;
import org.smartregister.commonregistry.CommonPersonObjectClient;
import org.smartregister.util.Log;
import org.smartregister.view.activity.DrishtiApplication;

import java.io.File;

import static org.joda.time.LocalDateTime.parse;

/**
 * Created by al on 30/05/2017
 */
public class Support {

    public static boolean ONSYNC = false;

    public static String[] replace(String[] data, String target, String replacement) {
        for (int i = 0; i < data.length; i++) {
            if (data[i].equals(target))
                data[i] = replacement;
        }
        return data;
    }

    public static String[] split(String data) {
        String myData = data;
        if (myData == null)
            myData = "";
        if (!myData.contains(":"))
            return new String[]{"0", "0"};

        String[] temp = myData.split(",");
        String[] result = {"", ""};
        for (String aTemp : temp) {
            result[0] = result[0] + "," + aTemp.split(":")[0];
            result[1] = result[1] + "," + aTemp.split(":")[1];
        }
        result[0] = result[0].substring(1, result[0].length());
        result[1] = result[1].substring(1, result[1].length());
        return result;
    }

    public static String getColumnmaps(CommonPersonObjectClient person, String values) {
        if (person.getColumnmaps().get(values) != null && person.getColumnmaps().get(values).length() > 0)
                return person.getColumnmaps().get(values);
        return "-";
    }

    public static String getColumnmaps(CommonPersonObject person, String values) {
        if (person.getColumnmaps().get(values) != null && person.getColumnmaps().get(values).length() > 0)
                return person.getColumnmaps().get(values);
        return "-";
    }

    public static String getDetails(CommonPersonObjectClient person, String values) {
        if (person.getDetails().get(values) != null) {
            if (person.getDetails().get(values).length() > 0)
                return person.getDetails().get(values);
        }
        return "-";
    }

    public static String getDetails(CommonPersonObject person, String values) {
        if (person.getDetails().get(values) != null) {
            if (person.getDetails().get(values).length() > 0)
                return person.getDetails().get(values);
        }
        return "-";
    }

//    public static String[] insertionSort(String data) {
//        String[] temp = data.split(",");
//        for (int i = 0; i < temp.length; i++) {
//            for (int j = temp.length - 1; j > i; j--) {
//                if (getAge(temp[j]) < getAge(temp[j - 1])) {
//                    String a = temp[j];
//                    temp[j] = temp[j - 1];
//                    temp[j - 1] = a;
//                }
//            }
//        }
//
//        return temp;
//    }

//    public static int getAge(String data) {
//        if (data.contains(":"))
//            return Integer.parseInt(data.split(":")[0]);
//        return 0;
//    }
//
//    public static String combine(String[] data, String separator) {
//        String result = "";
//        for (int i = 0; i < data.length; i++) {
//            result = result + separator + data[i];
//        }
//        return result.substring(1, result.length());
//    }

//    public static String fixHistory(String data) {
//        if (data == null)
//            return null;
//        return combine(insertionSort(data), ",");
//    }
//
//    public static String findDate(String startDate, int dayAge) {
//        int[] dayLength = {31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31};
//        int startYear = Integer.parseInt(startDate.substring(0, 4));
//        int startMonth = Integer.parseInt(startDate.substring(5, 7));
//        int startDay = Integer.parseInt(startDate.substring(8, 10));
//
//        dayLength[1] = startYear % 4 == 0 ? 29 : 28;
//        while (dayAge > dayLength[startMonth - 1]) {
//            dayAge = dayAge - dayLength[startMonth - 1];
//            startMonth++;
//            if (startMonth > 12) {
//                startYear++;
//                startMonth = 1;
//                dayLength[1] = startYear % 4 == 0 ? 29 : 28;
//            }
//        }
//        startDay += dayAge;
//        if (startDay > dayLength[startMonth - 1]) {
//            startDay = startDay - dayLength[startMonth - 1];
//            startMonth++;
//        }
//        if (startMonth > 12) {
//            startYear++;
//            startMonth = 1;
//        }
//
//        String m = "" + (startMonth < 10 ? "0" + startMonth : Integer.toString(startMonth));
//        String d = "" + (startDay < 10 ? "0" + startDay : Integer.toString(startDay));
//        return Integer.toString(startYear) + "-" + m + "-" + d;
//    }

    public static void setImagetoHolderFromUri(Activity activity, String file, ImageView view, int placeholder) {
//        String path = DrishtiApplication.getAppDir() + File.separator + ".thumbs" + File.separator + "th_";
        String path = DrishtiApplication.getAppDir();
        String fullPath = path + File.separator + file + ".jpg";
//        android.util.Log.e(TAG, "setImagetoHolderFromUri: " + fullPath);
        view.setImageDrawable(activity.getResources().getDrawable(placeholder));
        File externalFile = new File(fullPath);

        if (!externalFile.exists()) {
//            externalFile = new File(fullPath.replace(".JPEG", ".jpg"));
            externalFile = new File(fullPath);
        }

        if (externalFile.exists()) {

//            Uri external = Uri.fromFile(externalFile);
//            view.setImageURI(external);

            view.setImageBitmap(Tools.getThumbnailBitmap(externalFile.getPath(), 100));

//            android.util.Log.e(TAG, "setImagetoHolderFromUri: " + external);

        } else {
            Log.logError(Support.class.getName(), String.format("image %s doesn't exist", file));
        }
    }

//    public static int monthAges(String lastVisitDate, String currentDate) {
//        int tahun = Integer.parseInt(currentDate.substring(0, 4)) - Integer.parseInt(lastVisitDate.substring(0, 4));
//        int bulan = Integer.parseInt(currentDate.substring(5, 7)) - Integer.parseInt(lastVisitDate.substring(5, 7));
//        int hari = Integer.parseInt(currentDate.substring(8)) - Integer.parseInt(lastVisitDate.substring(8));
//        return (tahun * 12 + bulan + (int) (hari / 30));
//    }

    public static void checkMonth(Context context, String htp, TextView TextMonth) {
        String edd = htp;
        String _edd = edd;
        String _dueEdd = "";
        DateTimeFormatter formatter = DateTimeFormat.forPattern("yyyy-MM-dd");

        if (StringUtils.isNotBlank(htp) && !"delivered".equals(htp)) {

            LocalDate date = parse(_edd, formatter).toLocalDate();
            LocalDate dateNow = LocalDate.now();
            date = date.withDayOfMonth(1);
            dateNow = dateNow.withDayOfMonth(1);
            int months = Months.monthsBetween(dateNow, date).getMonths();
            if (months >= 1) {
                TextMonth.setTextColor(context.getResources().getColor(R.color.alert_in_progress_blue));
                _dueEdd = "" + months + " " + context.getString(R.string.months_away);
            } else if (months == 0) {
                TextMonth.setTextColor(context.getResources().getColor(R.color.light_blue));
                _dueEdd = context.getString(R.string.this_month);
            } else if (months < 0) {
                TextMonth.setTextColor(context.getResources().getColor(R.color.alert_urgent_red));
                _dueEdd = context.getString(R.string.edd_passed);
            }
            TextMonth.setText(_dueEdd);
        }
        /*else if(htp.equals("delivered")){
            TextMonth.setTextColor(context.getResources().getColor(R.color.alert_complete_green));
            _dueEdd = context.getString(R.string.delivered);
            TextMonth.setText(_dueEdd);
        }*/
        else {
            TextMonth.setText("-");
        }

    }


//    public void checkLastVisit(Context context, String date, String visitNumber, String Status, TextView visitStatus, TextView visitDate, TextView VisitNumber) {
//        String visit_date = date != null ? context.getString(R.string.date_visit_title) + " " + date : "";
//
//        VisitNumber.setText(visitNumber);
//        visitDate.setText(visit_date);
//        visitStatus.setText(Status);
//    }

}