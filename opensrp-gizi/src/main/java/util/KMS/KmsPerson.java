package util.KMS;

import org.ei.opensrp.commonregistry.CommonPersonObjectClient;

import util.formula.Support;

/**
 * Created by Iq on 02/05/16.
 */
public class KmsPerson {

    private String name;
    private boolean isMale;
    private int age;
    private String dateOfBirth;
    private double weight;
    private double previousWeight;
    private double secondLastWeight;
    private String lastVisitDate;
    private String secondLastVisitDate;
    private String thirdLastVisitDate;

    //ditanya
    public String StatusBeratBadan;         // value : naik, tidak, ukur pertama
    public boolean BGM;
    public boolean GarisKuning;
    public boolean Tidak2Kali;


    //constructor
    public KmsPerson( boolean isMale,String dateOfBirth,double weight,double previousWeight,
                      String lastVisitDate,double secondLastWeight,String secondLastVisitDate,
                      String thirdLastVisitDate){

        this.setParameters(isMale, dateOfBirth, weight, previousWeight, lastVisitDate, secondLastWeight,
                secondLastVisitDate, thirdLastVisitDate
        );
    }

    public KmsPerson(CommonPersonObjectClient client){
        String[]history = Support.split(Support.fixHistory(Support.getDetails(client,"history_berat")));
        String[]historyUmur = Support.replace(history[0].split(","), "", "0");
        String[]historyBerat = Support.replace(history[1].split(","), "", "0");
        String dob = Support.getDetails(client, "tanggalLahirAnak").substring(0, 10);

        this.setParameters(
                !Support.getDetails(client, "gender").toLowerCase().contains("em"),
                dob,
                historyBerat.length > 0 ? Double.parseDouble(historyBerat[historyBerat.length - 1]) : 0,
                historyBerat.length > 1 ? Double.parseDouble(historyBerat[historyBerat.length - 2]) : 0,
                historyUmur.length > 0 ? Support.findDate(dob, Integer.parseInt(historyUmur[historyUmur.length - 1])) : "",
                historyBerat.length > 2 ? Double.parseDouble(historyBerat[historyBerat.length - 3]) : 0,
                historyUmur.length > 1
                        ? historyUmur[historyUmur.length - 2].equals("0")
                            ? "0"
                            : Support.findDate(dob, Integer.parseInt(historyUmur[historyUmur.length - 2]))
                        : "",
                historyUmur.length > 2
                        ? historyUmur[historyUmur.length - 3].equals("0")
                            ? "0"
                            : Support.findDate(dob, Integer.parseInt(historyUmur[historyUmur.length - 3]))
                        :""
        );
    }

    // mutators
    private void setParameters(boolean isMale,String dateOfBirth,double weight,double previousWeight,
            String lastVisitDate,double secondLastWeight,String secondLastVisitDate,String thirdLastVisitDate){
        this.isMale = isMale;
        this.dateOfBirth = dateOfBirth;
        this.age = Support.monthAges(dateOfBirth,lastVisitDate);
        this.weight = weight;
        this.previousWeight = previousWeight;
        this.lastVisitDate = lastVisitDate;
        this.secondLastWeight = secondLastWeight;
        this.secondLastVisitDate = secondLastVisitDate;
        this.thirdLastVisitDate = thirdLastVisitDate;
    }

    // accessors
    public boolean isMale(){return isMale;}
    public int getAge(){return age;}
    public String getDateOfBirth(){return dateOfBirth;}
    public double getWeight(){return weight;}
    public double getPreviousWeight(){return previousWeight;}
    public String getLastVisitDate(){return lastVisitDate;}
    public double getSecondLastWeight(){return secondLastWeight;}
    public String getSecondLastVisitDate(){return secondLastVisitDate;}
    public String getThirdLastVisitDate() {return thirdLastVisitDate;}
}
