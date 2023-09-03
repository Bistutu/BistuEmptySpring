package com.thinkstu.helper;

import net.sourceforge.tess4j.*;

import java.io.*;

// OCR 工具类
public class TesseractOCRHelper {
    public static final int MAX_TRY_TIMES = 20;

    public static String doOcr(String path) throws TesseractException, FileNotFoundException {
        ITesseract instance = new Tesseract();
        String     result   = instance.doOCR(new File(path));
        result = result.replaceAll("\\s+", "");
        return result;
    }
}
