# Opensrp Bidan Application
SID Bidan Client Application

Remove
        
    $ adb uninstall package_name
        
Clear 

    $ adb shell pm clear package_name

Check Face Vector

    $ adb shell "run-as org.smartregister.bidan_cloudant cat shared_prefs/HashMap.xml"
    $ adb shell "run-as org.smartregister.bidan_cloudant cat shared_prefs/serialize_deserialize.xml"
