package org.smartregister.gizi.utils;

import org.smartregister.domain.form.FormSubmission;
import org.smartregister.repository.DetailsRepository;
import org.smartregister.service.formsubmissionhandler.FormSubmissionHandler;

import util.formula.Support;
import util.kms.KmsCalc;
import util.kms.KmsPerson;
import util.zscore.ZScoreSystemCalculation;

/**
 * Created by Iq on 15/09/16.
 * This class is used to create preload (prepopulate) data which used for next visit registration,
 * and other calculation that cannot conduct by Form, such as Nutrition status etc.
 */

public class KmsHandler implements FormSubmissionHandler {
//    static String bindobject = "anak";
//    private GiziClientProcessor clientProcessor;
//    private  org.ei.opensrp.Context context;

//    public KmsHandler() {
//    }

    @Override
    public void handle(FormSubmission submission) {
        String entityID = submission.entityId();
//        AllCommonsRepository childRepository = org.smartregister.Context.getInstance().allCommonsRepositoryobjects("ec_anak");
//        CommonPersonObject childobject = childRepository.findByCaseID(entityID);
        Long tsLong = System.currentTimeMillis() / 1000;

        DetailsRepository detailsRepository = org.smartregister.Context.getInstance().detailsRepository();

        String[] history = submission.getFieldValue("history_berat") != null ? split(Support.fixHistory(submission.getFieldValue("history_berat"))) : new String[]{"0", "0"};
        String[] history2 = submission.getFieldValue("history_tinggi") != null ? split(Support.fixHistory(submission.getFieldValue("history_tinggi"))) : new String[]{"0", "0"};
        String berats = history[1];
        String[] history_berat = berats.split(",");
        double berat_sebelum = Double.parseDouble((history_berat.length) >= 3 ? (history_berat[(history_berat.length) - 3]) : "0");
        String umurs = history[0];
        String[] history_umur = umurs.split(",");
        String tinggi = submission.getFieldValue("history_tinggi") != null ? submission.getFieldValue("history_tinggi") : "0#0";
        String lastVisitDate = submission.getFieldValue("tanggalPenimbangan") != null
                ? !"nan".equals(submission.getFieldValue("tanggalPenimbangan").toLowerCase())
                ? submission.getFieldValue("tanggalPenimbangan")
                : "-"
                : "-";
        String gender = submission.getFieldValue("gender") != null ? submission.getFieldValue("gender") : "-";
        String tgllahir = submission.getFieldValue("tanggalLahirAnak") != null
                ? submission.getFieldValue("tanggalLahirAnak").substring(0, 10)
                : "-";
        String dateOfBirth = tgllahir.substring(0, 10);

        detailsRepository.add(entityID, "preload_umur", umurs, tsLong);
        detailsRepository.add(entityID, "berat_preload", submission.getFieldValue("history_berat") != null ? submission.getFieldValue("history_berat") : "0:0", tsLong);
//        detailsRepository.add(entityID, "history_umur", umurs, tsLong);

        // detailsRepository.add(entityID, "preload_history_tinggi", submission.getFieldValue("history_tinggi")!= null ? submission.getFieldValue("history_tinggi") :"0#0", tsLong);
        detailsRepository.add(entityID, "preload_history_tinggi", tinggi, tsLong);
        detailsRepository.add(entityID, "kunjunganSebelumnya", lastVisitDate, tsLong);
        ZScoreSystemCalculation zScore = new ZScoreSystemCalculation();
        if ((submission.getFieldValue("tanggalPenimbangan") != null && !submission.getFieldValue("tanggalPenimbangan").toLowerCase().equals("")) &&
            (zScore.dailyUnitCalculationOf(dateOfBirth, lastVisitDate) < 1857)) {
                String[] tempAgeW = history[0].split(",");
                String[] tempAgeH = history2[0].split(",");
                String[] tempWeight = history[1].split(",");
                String[] tempHeight = history2[1].split(",");
                int weightAge = Integer.parseInt(tempAgeW[tempAgeW.length - 1]);
                double weight = Double.parseDouble(tempWeight[tempWeight.length - 1]);
                int lengthAge = Integer.parseInt(tempAgeH[tempAgeH.length - 1]);
                double length = tempHeight.length > 0 ? Double.parseDouble(tempHeight[tempHeight.length - 1]) : 0;

                double weight_for_age = zScore.countWFA(!gender.toLowerCase().contains("em"), weightAge, weight);
                String wfaStatus = zScore.getWFAZScoreClassification(weight_for_age);
                if (length != 0) {
                    double heigh_for_age = zScore.countHFA(!gender.toLowerCase().contains("em"), lengthAge, length);
                    String hfaStatus = zScore.getHFAZScoreClassification(heigh_for_age);

                    double wight_for_lenght;
                    String wflStatus;
                    if (zScore.dailyUnitCalculationOf(dateOfBirth, lastVisitDate) < 730) {
                        wight_for_lenght = zScore.countWFL(gender, weight, length);
                    } else {
                        wight_for_lenght = zScore.countWFH(gender, weight, length);
                    }
                    wflStatus = zScore.getWFLZScoreClassification(wight_for_lenght);

                    detailsRepository.add(entityID, "underweight", wfaStatus, tsLong);
                    detailsRepository.add(entityID, "stunting", hfaStatus, tsLong);
                    detailsRepository.add(entityID, "wasting", wflStatus, tsLong);

                } else {
                    detailsRepository.add(entityID, "underweight", wfaStatus, tsLong);
                    detailsRepository.add(entityID, "stunting", "-", tsLong);
                    detailsRepository.add(entityID, "wasting", "-", tsLong);


                }
        }
        //
        // kms calculation
        // NOTE - Need a better way to handle z-score data to sqllite
        //
        if ((submission.getFieldValue("history_berat") != null && submission.getFieldValue("tanggalLahirAnak") != null) &&
                (submission.getFieldValue("tanggalPenimbangan") != null && !submission.getFieldValue("tanggalPenimbangan").toLowerCase().equals(""))) {

            String[] historyBerat = Support.insertionSort(submission.getFieldValue("history_berat"));
            String latestDate = Support.findDate(submission.getFieldValue("tanggalLahirAnak"), Integer.parseInt(historyBerat[historyBerat.length - 1].split(":")[0]));
            double berat;
            double beraSebelum;
            String tanggal_sebelumnya;
            if (submission.getFieldValue("tanggalPenimbangan").equalsIgnoreCase(latestDate.toLowerCase()) && submission.getFieldValue("kunjunganSebelumnya") != null) {
                berat = Double.parseDouble(submission.getFieldValue("beratBadan") != null ? submission.getFieldValue("beratBadan") : "0");
                beraSebelum = Double.parseDouble((history_berat.length) >= 2 ? (history_berat[(history_berat.length) - 2]) : "0");
                tanggal_sebelumnya = (submission.getFieldValue("kunjunganSebelumnya") != null ? submission.getFieldValue("kunjunganSebelumnya") : "0");
            } else {
                berat = Double.parseDouble(historyBerat[historyBerat.length - 1].split(":")[1]);
                beraSebelum = historyBerat.length > 2 ? Double.parseDouble(historyBerat[historyBerat.length - 2].split(":")[1]) : 0.0;
                tanggal_sebelumnya = historyBerat.length > 2 ? Support.findDate(submission.getFieldValue("tanggalLahirAnak"), Integer.parseInt(historyBerat[historyBerat.length - 2].split(":")[0])) : "";
            }
            String tanggal2sblmnya = historyBerat.length > 3 ? Support.findDate(submission.getFieldValue("tanggalLahirAnak"), Integer.parseInt(historyBerat[historyBerat.length - 3].split(":")[0])) : "";

            //KMS calculation lastVisitDate
            KmsPerson data = new KmsPerson(!gender.toLowerCase().contains("em"), dateOfBirth, berat, beraSebelum, lastVisitDate, berat_sebelum, tanggal_sebelumnya, tanggal2sblmnya);
            KmsCalc calculator = new KmsCalc();
            ////System.out.println("tanggal penimbangan = "+submission.getFieldValue("tanggalPenimbangan")+", "+lastVisitDate);

            String duat = history_berat.length <= 2 ? "-" : (Integer.parseInt(history_umur[history_umur.length - 1]) / 30) - (Integer.parseInt(history_umur[history_umur.length - 2]) / 30) >= 2 ? "-" : calculator.cek2T(data);
            String status = history_berat.length <= 2 ? "No" : calculator.cekWeightStatus(data);

            detailsRepository.add(entityID, "bgm", calculator.cekBGM(data), tsLong);
            detailsRepository.add(entityID, "dua_t", duat, tsLong);
            detailsRepository.add(entityID, "garis_kuning", calculator.cekBawahKuning(data), tsLong);
            detailsRepository.add(entityID, "nutrition_status", status, tsLong);

            if (submission.getFieldValue("vitA") != null) {
                if (submission.getFieldValue("vitA").equalsIgnoreCase("yes") || submission.getFieldValue("vitA").equalsIgnoreCase("ya")) {
                    detailsRepository.add(entityID, "lastVitA", submission.getFieldValue("tanggalPenimbangan"), tsLong);
                }
            } else {
                detailsRepository.add(entityID, "lastVitA", submission.getFieldValue("lastVitA"), tsLong);
            }
            if (submission.getFieldValue("obatcacing") != null) {
                if (submission.getFieldValue("obatcacing").equalsIgnoreCase("yes") || submission.getFieldValue("obatcacing").equalsIgnoreCase("ya")) {
                    detailsRepository.add(entityID, "lastAnthelmintic", submission.getFieldValue("tanggalPenimbangan"), tsLong);
                }
            } else {
                detailsRepository.add(entityID, "lastAnthelmintic", submission.getFieldValue("lastAnthelmintic"), tsLong);
            }
//            }
        }
    }

    private String[] split(String data) {
        if (!data.contains(":"))
            return new String[]{"0", "0"};
        String[] temp = data.split(",");
        String[] result = {"", ""};
        for (String aTemp : temp) {
            result[0] = result[0] + "," + aTemp.split(":")[0];
            result[1] = result[1] + "," + (aTemp.split(":").length > 1 ? aTemp.split(":")[1] : "");
        }

        result[0] = result[0].substring(1, result[0].length());
        result[1] = result[1].substring(1, result[1].length());

        if (result[0].length() > 2 && result[1].length() > 2) {
            result[0] = result[0].substring(0, 2).equals("0,") ? result[0].substring(2, result[0].length()) : result[0];
            result[1] = result[1].substring(0, 2).equals("0,") ? result[1].substring(2, result[1].length()) : result[1];
        }

        return result;
    }
}
