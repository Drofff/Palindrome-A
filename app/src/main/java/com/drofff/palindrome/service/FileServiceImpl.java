package com.drofff.palindrome.service;

import android.app.Activity;

import com.drofff.palindrome.exception.PalindromeException;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import static com.drofff.palindrome.utils.FileUtils.writeTextToFileAtPath;
import static com.drofff.palindrome.utils.IOUtils.readAllAsString;
import static com.drofff.palindrome.utils.ValidationUtils.validateNotNull;

public class FileServiceImpl implements FileService {

    private final File tokensDir;

    public FileServiceImpl(Activity contextActivity) {
        this.tokensDir = contextActivity.getFilesDir();
    }

    @Override
    public void saveFile(String filename, String content) {
        validateNotNull(filename, "File should obtain a name");
        validateNotNull(content, "File content should not be null");
        String filePath = pathToFileWithName(filename);
        writeTextToFileAtPath(content, filePath);
    }

    @Override
    public String loadFileByName(String filename) {
        String filePath = pathToFileWithName(filename);
        return loadFileAtPath(filePath);
    }

    private String pathToFileWithName(String filename) {
        return tokensDir.getAbsolutePath() + "/" + filename;
    }

    private String loadFileAtPath(String path) {
        try {
            return loadFile(path);
        } catch(IOException e) {
            throw new PalindromeException(e.getMessage());
        }
    }

    private String loadFile(String filePath) throws IOException {
        FileInputStream fileInputStream = new FileInputStream(filePath);
        return readAllAsString(fileInputStream);
    }

}
