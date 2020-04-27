package com.drofff.palindrome.service;

public interface FileService {

    void saveFile(String filename, String content);

    String loadFileByName(String filename);

}
