package com.dvr.mel.dronevoicerecognition;

import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.text.Normalizer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by leo on 16/11/16.
 */

public class AppInfo implements Serializable{
    public static String serializedFileName = "appInfoSaved";
    public static File baseDir, corpusGlobalDir;
    public static Set<String> referencesCorpora = new LinkedHashSet<>();
    public static Set<String> usersCorpora = new LinkedHashSet<>();
    public static List<String> commands = new ArrayList<>();
    public static Map<String, Corpus> corpusMap = new HashMap<>();
    public static int SENSITIVITY = 10; // Set the sensibility threshold of the mic
    public static int BUFFER_SIZE_MULTIPLICATOR = 10; // Set the size of the streamBuffer Analysed in WavStreamHandler

    public String _serializedFileName;
    public File _baseDir, _corpusGlobalDir;
    public Set<String> _referencesCorpora = new LinkedHashSet<>();
    public Set<String> _usersCorpora = new LinkedHashSet<>();
    public List<String> _commands = new ArrayList<>();
    public Map<String, Corpus> _corpusMap = new HashMap<>();
    public int _SENSITIVITY;
    public int _BUFFER_SIZE_MULTIPLICATOR;


    public AppInfo() {    }

    /**
     * Delete all the files and directories related to one corpus from the phone memory.
     * @param corpusName
     */
    public static void clean(String corpusName) {
        // Delete all files related to the corpus designed by corpusName
        File corpusToDelete = new File(corpusGlobalDir, corpusName);
        deleteDirectory(corpusToDelete);
    }

    private static void deleteDirectory(File directory) {
        if (directory.isDirectory())
            for (File f : directory.listFiles())
                deleteDirectory(f);

        directory.delete();
    }

    /**
     * Update some static variables from the AppInfo class if a corpus has been add.
     * @param name
     * @param corpus
     */
    public static void addCorpus(String name, Corpus corpus) {
        usersCorpora.add(name);
        corpusMap.put(name, corpus);
    }

    /**
     * Since the user can call his corpus the way he want, we need to be sure that the name will
     * word under the android system. So we "clean" the name by replacing space by '_' or removing
     * accent. both name are kept so the real name will appear in the corpus list.
     * @param name
     * @return
     */
    public static String sanitarizeName(String name){
        name = name.replace(' ', '_').replace('*', '_');
        name = name.toLowerCase();

        StringBuilder sb = new StringBuilder(name.length());
        name = Normalizer.normalize(name, Normalizer.Form.NFD);
        for (char c : name.toCharArray()) {
            if (c <= '\u007F') sb.append(c);
        }
        return sb.toString();
    }

    /**
     * Will update the variables from the instance which is calling the method with the value from
     * the static variables
     */
    public void updateFromStaticVariables() {
        this._serializedFileName = serializedFileName;
        this._baseDir = new File(baseDir.getAbsolutePath());
        this._corpusGlobalDir = new File(corpusGlobalDir.getAbsolutePath());
        this._referencesCorpora = new LinkedHashSet<>(referencesCorpora);
        this._usersCorpora = new LinkedHashSet<>(usersCorpora);
        this._commands = new ArrayList<>(commands);
        this._corpusMap = new HashMap<>(corpusMap);
        this._SENSITIVITY = SENSITIVITY;
        this._BUFFER_SIZE_MULTIPLICATOR = BUFFER_SIZE_MULTIPLICATOR;
    }

    /**
     * Will update the static variables of the class with the value of the variables from the instan
     * ce which is calling the method
     */
    public void updateToStaticVariables() {
        serializedFileName = _serializedFileName;
        baseDir = new File(this._baseDir.getAbsolutePath());
        corpusGlobalDir = new File(this._corpusGlobalDir.getAbsolutePath());
        referencesCorpora = new LinkedHashSet<>(this._referencesCorpora);
        usersCorpora = new LinkedHashSet<>(this._usersCorpora);
        commands = new ArrayList<>(this._commands);
        corpusMap = new HashMap<>(this._corpusMap);
        SENSITIVITY = _SENSITIVITY;
        BUFFER_SIZE_MULTIPLICATOR = _BUFFER_SIZE_MULTIPLICATOR;
    }


    /**
     * Will save the AppInfo class into a serialized file.
     * In order to be sure that the static variables are correctly updates before writing down the
     * file, an instance need to be created and the method updateFromStaticVariables called.
     */
    public static void saveToSerializedFile() {
        File appInfoSaved = new File(AppInfo.baseDir, serializedFileName);

        try{
            AppInfo ci = new AppInfo();
            ci.updateFromStaticVariables();

            FileOutputStream fileOut = new FileOutputStream(appInfoSaved.getAbsolutePath());
            ObjectOutputStream out = new ObjectOutputStream(fileOut);

            out.writeObject(ci);

            out.close();
            fileOut.close();

        } catch (FileNotFoundException e) {
            e.printStackTrace();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    /**
     * Will load the AppInfo class from his saved which is a serialized file.
     * In order to be sure that the static variables are correctly updates, an instance need to be
     * created and the method updateToStaticVariables called.
     */
    public static void loadFromSerializedFile() {
        File appInfoSaved = new File(AppInfo.baseDir, serializedFileName);

        try {
            AppInfo ci = new AppInfo();

            FileInputStream fileIn = new FileInputStream(appInfoSaved.getAbsolutePath());
            ObjectInputStream in = new ObjectInputStream(fileIn);

            ci = (AppInfo) in.readObject();
            ci.updateToStaticVariables();

            in.close();
            fileIn.close();

        } catch (FileNotFoundException e) {
            e.printStackTrace();

        } catch (IOException e) {
            e.printStackTrace();

        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
}
