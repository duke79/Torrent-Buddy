package com.baliyaan.android.torrents;

/**
 * Created by Pulkit Singh on 2/5/2017.
 */

public enum VideoQuality {

    NA(-1,"NA"),

    Cam(0,"CAM"),

    TS(1,"TS"),
    pDVD(1,"PDVD"),

    TC(2,"TC"),
    WEBRip(2,"WEBRip"),

    SCR(3,"SCR"),

    R5(4,"R5"),
    DVDRip(4,"DVDRip"),
    TVRip(4,"TVRip"),
    WEBDL(4,"WEBDL"),

    BDRip(5,"BDRip"),
    BRRip(5,"BRRip"),
    BluRay(5,"BluRay");

    private int mStarsOutOfFive;
    private String mName;

    private VideoQuality(int starsOutOfFive, String name){
        this.mStarsOutOfFive = starsOutOfFive;
        mName = name;
    }

    public int getStars(){
        return mStarsOutOfFive;
    }

    public String getName(){
        return mName;
    }

    public static VideoQuality parseQuality(String videoName){

        String[] type = new String[]{"Cam","CamRip","Camp-Rip"};
        if(hasQualityOf(type,videoName))
            return Cam;

        type = new String[]{"TELESYNC","TS"};
        if(hasQualityOf(type,videoName))
            return TS;

        type = new String[]{"pDVD","pDVDRip","DesiScr","PreDVDRip","Pre-DVDRip"};
        if(hasQualityOf(type,videoName))
            return pDVD;

        type = new String[]{"TC","Telecine"};
        if(hasQualityOf(type,videoName))
            return TC;

        type = new String[]{"WebRip","Web-Rip"};
        if(hasQualityOf(type,videoName))
            return WEBRip;

        type = new String[]{"DVDScr","BDScr","SCR"};
        if(hasQualityOf(type,videoName))
            return SCR;

        type = new String[]{"DVDScr","BDScr","SCR"};
        if(hasQualityOf(type,videoName))
            return SCR;

        type = new String[]{"R5","R-5"};
        if(hasQualityOf(type,videoName))
            return R5;

        type = new String[]{"R5","R-5"};
        if(hasQualityOf(type,videoName))
            return R5;

        type = new String[]{"DVDRip","DVDR","DVD-Rip","DVD-R"};
        if(hasQualityOf(type,videoName))
            return DVDRip;

        type = new String[]{"TVRip","TVR","TVRip"};
        if(hasQualityOf(type,videoName))
            return TVRip;

        type = new String[]{"WebDL","WEB-DL"};
        if(hasQualityOf(type,videoName))
            return WEBDL;

        type = new String[]{"BDRip","BD-Rip"};
        if(hasQualityOf(type,videoName))
            return BDRip;

        type = new String[]{"BRRip","BR-Rip"};
        if(hasQualityOf(type,videoName))
            return BRRip;

        type = new String[]{"BluRay"};
        if(hasQualityOf(type,videoName))
            return BluRay;

        return NA;
    }

    private static boolean hasQualityOf(String[] type, String videoName) {
        videoName = videoName.toLowerCase();
        for (int i=0;i<type.length;i++)
        {
            type[i] = type[i].toLowerCase();
            if(videoName.contains(type[i]+" "))
                return true;
        }
        return false;
    }

}
