package org.smartregister.bidan_cloudant.face.camera.utils;

import org.smartregister.domain.ProfileImage;

/**
 * Created by sid-tech on 11/22/17.
 */

public class ProfileImageFR extends ProfileImage {

    public String fileVector;
    private String filevector;

    private String imageid;
    private String anmId;
    private String entityID;
    private String contenttype;
    private String filepath;
    private String syncStatus;
    private String filecategory;
    private String vectorid;
    private String fFaceVectorApi;

    public ProfileImageFR(String imageid, String anmId, String entityID, String contenttype, String filepath, String syncStatus, String filecategory, String filevector) {
        this.imageid = imageid;
        this.entityID = entityID;
        this.anmId = anmId;
        this.contenttype = contenttype;
        this.filepath = filepath;
        this.syncStatus = syncStatus;
        this.filecategory = filecategory;
        this.filevector = filevector;
    }

    public ProfileImageFR(String imageid, String anmId, String entityID, String contenttype, String filepath, String syncStatus, String filecategory) {
        this.imageid = imageid;
        this.entityID = entityID;
        this.anmId = anmId;
        this.contenttype = contenttype;
        this.filepath = filepath;
        this.syncStatus = syncStatus;
        this.filecategory = filecategory;
    }

    public ProfileImageFR(String vectorid,String entityID, String syncStatus) {
        this.vectorid = vectorid;
        this.entityID = entityID;
        this.syncStatus = syncStatus;
    }

    public ProfileImageFR() {

    }

    public String getFileVector() {
        return fileVector;
    }

    public void setFileVector(String fileVector) {
        this.fileVector = fileVector;
    }

    public void setFilevector(String filevector) {
        this.filevector = filevector;
    }

    public String getFilevector() {
        return filevector;
    }
}
