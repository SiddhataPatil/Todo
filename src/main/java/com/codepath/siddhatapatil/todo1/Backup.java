package com.codepath.siddhatapatil.todo1;

import android.os.Environment;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

import static com.codepath.siddhatapatil.todo1.MainActivity.*;

import com.codepath.siddhatapatil.todo1.R;


public class Backup {

    public static final String NOTES_FILE_NAME = "lists.json"; // Local notes file name
    public static final String NOTES_ARRAY_NAME = "list"; // Root object name

    public static final String BACKUP_FOLDER_PATH = "/Siddhata"; // Backup folder path
    public static final String BACKUP_FILE_NAME = "siddhata.json"; // Backup file name

    public static final int NEW_NOTE_REQUEST = 60000;
    public static final String NOTE_REQUEST_CODE = "requestCode";
    public static final String NOTE_TITLE = "title";
    public static final String NOTE_BODY = "body";
    public static final String NOTE_COLOUR = "colour";
    public static final String NOTE_FAVOURED = "favoured";
    public static final String NOTE_FONT_SIZE = "fontSize";
    public static final String NOTE_HIDE_BODY = "hideBody";


    public static boolean saveData(File toFile, JSONArray notes) {
        Boolean successful = false;

        JSONObject root = new JSONObject();

        if (notes != null) {
            try {
                root.put(NOTES_ARRAY_NAME, notes);

            } catch (JSONException e) {
                e.printStackTrace();
                return false;
            }
        }

        else
            return false;

        if (toFile == getBackupPath()) {
            if (isExternalStorageReadable() && isExternalStorageWritable()) {
                if (!toFile.exists()) {
                    try {
                        Boolean created = toFile.createNewFile();

                        if (!created)
                            return false;

                    } catch (IOException e) {
                        e.printStackTrace();
                        return false; 
                    }
                }
            }
            else
                return false;
        }

        else if (toFile == getLocalPath() && !toFile.exists()) {
            try {
                Boolean created = toFile.createNewFile();

                if (!created)
                    return false;

            } catch (IOException e) {
                e.printStackTrace();
                return false; 
            }
        }


        BufferedWriter bufferedWriter = null;

        try {

            bufferedWriter = new BufferedWriter(new FileWriter(toFile));
            bufferedWriter.write(root.toString());


            successful = true;

        } catch (IOException e) {
            successful = false;
            e.printStackTrace();

        } finally {

            if (bufferedWriter != null) {
                try {
                    bufferedWriter.flush();
                    bufferedWriter.close();

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        return successful;
    }

    public static JSONArray retrieveData(File fromFile) {
        JSONArray notes = null;

        if (fromFile == getBackupPath()) {
            if (isExternalStorageReadable() && !fromFile.exists()) {
                return null;
            }
        }

        else if (fromFile == getLocalPath() && !fromFile.exists()) {
            notes = new JSONArray();

            Boolean successfulSaveToLocal = saveData(fromFile, notes);

            if (successfulSaveToLocal) {
                return notes;
            }

 
            return null;
        }


        JSONObject root = null;
        BufferedReader bufferedReader = null;

        try {
          
            bufferedReader = new BufferedReader(new FileReader(fromFile));

            StringBuilder text = new StringBuilder();
            String line;

            while ((line = bufferedReader.readLine()) != null) {
                text.append(line);
            }

            root = new JSONObject(text.toString());

        } catch (IOException | JSONException e) {
            e.printStackTrace();

        } finally {
         
            if (bufferedReader != null) {
                try {
                    bufferedReader.close();

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        if (root != null) {
            try {
                notes = root.getJSONArray(NOTES_ARRAY_NAME);

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return notes;
    }


    public static JSONArray deleteNotes(JSONArray from, ArrayList<Integer> selectedNotes) {
        JSONArray newNotes = new JSONArray();
        for (int i = 0; i < from.length(); i++) {
           
            if (!selectedNotes.contains(i)) {
                try {
                    newNotes.put(from.get(i));

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }

        return newNotes;
    }


    public static boolean isExternalStorageWritable() {
        return Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState());
    }

  
    public static boolean isExternalStorageReadable() {
        String state = Environment.getExternalStorageState();

        return Environment.MEDIA_MOUNTED.equals(state) ||
                Environment.MEDIA_MOUNTED_READ_ONLY.equals(state);
    }
}
