package com.drofff.palindrome.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class StreamUtils {

    private StreamUtils() {}

    public static String readAllAsString(InputStream inputStream) throws IOException {
        InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
        BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
        String response = readStrFromReader(bufferedReader);
        bufferedReader.close();
        return response;
    }

    private static String readStrFromReader(BufferedReader reader) throws IOException {
        StringBuilder accumulator = new StringBuilder();
        return readAllAsStringRecursively(reader, accumulator).toString();
    }

    private static StringBuilder readAllAsStringRecursively(BufferedReader reader, StringBuilder accumulator) throws IOException {
        String nextLine = reader.readLine();
        if(nextLine == null) {
            return accumulator;
        }
        accumulator.append(nextLine);
        return readAllAsStringRecursively(reader, accumulator);
    }

}
