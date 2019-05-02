package com.example.jill.firsttry.Utils;


import com.example.jill.firsttry.model.LocalRecord;
import com.example.jill.firsttry.model.Song;

import java.io.File;
import java.util.ArrayList;

public class GetCompanyFromDir {

    public static ArrayList<Song> getComp(String filePath){
        File file = new File(filePath);//File类型可以是文件也可以是文件夹
        File[] fileList = file.listFiles();//将该目录下的所有文件放置在一个File类型的数组中
        ArrayList<String> fileNameList=new ArrayList<>();//存放文件名
        ArrayList<Song> companyKeys=new ArrayList<>();//存放从文件名解析好的伴奏相关信息
        ArrayList<File> wjList = new ArrayList<File>();//新建一个文件集合
        for (File aFileList : fileList) {
            if (aFileList.isFile()&&(aFileList.getName().lastIndexOf(".mp3")==(aFileList.getName().length()-4))){//判断是否为文件
                wjList.add(aFileList);
            }
        }
        //获取fileName列表
        for(File f:wjList){
            //在文件名中去掉".mp3"后缀，replace本身不会对String进行改变，他只是返回了一个替换后的值
            String fAfterStrip=f.getName().replace(".mp3","");
            fileNameList.add(fAfterStrip);
        }
        //获得伴奏信息Keys列表
        for(String fileName:fileNameList){
            String[] fileAfterSplitStringList=fileName.split("-",4);
            Song companyKey=new Song();
            companyKey.setSname(fileAfterSplitStringList[0]);
            companyKey.setSingerName(fileAfterSplitStringList[1]);
            companyKey.setAlbum(fileAfterSplitStringList[2]);
            companyKey.setSid(new Integer(fileAfterSplitStringList[3]));
            companyKeys.add(companyKey);
        }
        return companyKeys;
    }

    public static ArrayList<LocalRecord> getSong(String filePath){
        File file = new File(filePath);//File类型可以是文件也可以是文件夹
        File[] fileList = file.listFiles();//将该目录下的所有文件放置在一个File类型的数组中
        ArrayList<String> fileNameList=new ArrayList<>();//存放文件名
        ArrayList<LocalRecord> localRecords=new ArrayList<>();//存放从文件名解析好的伴奏相关信息
        ArrayList<File> wjList = new ArrayList<File>();//新建一个文件集合
        for (File aFileList : fileList) {
            if (aFileList.isFile()&&(aFileList.getName().lastIndexOf(".mp3")==(aFileList.getName().length()-4))){//判断是否为文件
                wjList.add(aFileList);
            }
        }
        //获取fileName列表
        for(File f:wjList){
            //在文件名中去掉".mp3"后缀，replace本身不会对String进行改变，他只是返回了一个替换后的值
            String fAfterStrip=f.getName().replace(".mp3","");
            fileNameList.add(fAfterStrip);
        }
        //获得伴奏信息Keys列表
        for(String fileName:fileNameList){
            String[] fileAfterSplitStringList=fileName.split("-");
            LocalRecord localRecord=new LocalRecord();
            localRecord.setSname(fileAfterSplitStringList[0]);
            localRecord.setSingerName(fileAfterSplitStringList[1]);
            localRecord.setAlbum(fileAfterSplitStringList[2]);
            localRecord.setSid(Integer.valueOf(fileAfterSplitStringList[3]));
            localRecord.setRecordTime(fileAfterSplitStringList[4]);
            localRecord.setRid(Integer.valueOf(fileAfterSplitStringList[5]));
            localRecords.add(localRecord);
        }
        return localRecords;
    }
}
