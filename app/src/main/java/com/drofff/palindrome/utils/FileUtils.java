package com.drofff.palindrome.utils;

import com.drofff.palindrome.exception.PalindromeException;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class FileUtils {

    public static void createFileIfNotExists(String filePath) {
        try {
            File file = new File(filePath);
            if(notExists(file)) {
                file.createNewFile();
            }
        } catch(IOException e) {
            throw new PalindromeException(e.getMessage());
        }
    }

    private static boolean notExists(File file) {
        return !file.exists();
    }

    public static void writeTextToFileAtPath(String text, String filePath) {
        try(FileOutputStream fileOutputStream = new FileOutputStream(filePath)) {
            byte[] textBytes = text.getBytes();
            fileOutputStream.write(textBytes);
        } catch(IOException e) {
            throw new PalindromeException(e.getMessage());
        }
    }

}
