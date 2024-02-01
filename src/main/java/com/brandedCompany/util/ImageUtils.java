package com.brandedCompany.util;

import com.brandedCompany.util.DomainUtils.TABLE;
import com.google.common.collect.Table;
import org.apache.commons.codec.binary.Base64;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.*;
import java.math.BigInteger;
import java.util.Locale;

@Component
public class ImageUtils
{
    private final long MAX_FILE_SIZE = 209_715_20L;

    public String getNoImageCode(TABLE table) throws IOException
    {

        String path = getImageRootPath(table).append("\\noImg.txt")
                                     .toString();
        StringBuffer buffer = new StringBuffer();
        try(FileReader reader = new FileReader(path);
         BufferedReader bufferedReader = new BufferedReader(reader);)
        {
            String str = "";
            while (true)
            {
                str = bufferedReader.readLine();
                if (str == null)
                    break;
                buffer.append(str);
            }
        }
        return buffer.toString();
    }

    public String getBase64ImageCode(TABLE table, BigInteger id) throws IOException
    {
        StringBuilder IMG_PATH = getImageRootPath(table);
        String NO_IMG_PATH = getImageRootPath(table).append("\\noImg.txt").toString();
        File file = new File(IMG_PATH.toString() + "\\" + id + ".txt");
        File noImgFile = new File(NO_IMG_PATH);
        StringBuffer buffer = new StringBuffer();
        FileReader reader = null;
        String str = "";
        if (file.exists() && file.isFile())
            reader = new FileReader(file);
        else
            reader = new FileReader(noImgFile);
        BufferedReader bufferedReader = new BufferedReader(reader);
        while (true)
        {
            str = bufferedReader.readLine();
            if (str == null)
                break;
            buffer.append(str);
        }
        bufferedReader.close();
        reader.close();
        return buffer.toString();
    }

    public StringBuilder getImageRootPath(TABLE table)
    {
        StringBuilder IMG_PATH = new StringBuilder();
        switch (table)
        {
            case CUSTOMERS:
                IMG_PATH.append(getImageRootPath().append("\\customer"));
                break;
            case EMPLOYEES:
                IMG_PATH.append(getImageRootPath().append("\\employee"));
                break;
        }
        return IMG_PATH;
    }

    public StringBuilder getImageRootPath()
    {
//       ec2 instance noImg.txt 프로젝트 경로
        return new StringBuilder().append("C:\\apache-tomcat-9.0.52\\webapps\\brandedCompany\\resources\\base64");
//        내 컴퓨터 noImg.txt 프로젝트 경로
//        return new StringBuilder().append("C:\\Users\\cis94\\ideaProjects\\brandedCompany\\src\\main\\webapp\\resources\\base64");
    }

    public void uploadImage(TABLE table, BigInteger id, MultipartFile uploadFile, RedirectAttributes rattr) throws IOException
    {
        StringBuilder imageRootPath = getImageRootPath(table);
        File file = new File(imageRootPath.toString() + "\\" + id + ".txt");
        try (FileWriter writer = new FileWriter(file, true); BufferedWriter bufferedWriter = new BufferedWriter(writer);)
        {
            if (addFlashMessage(uploadFile, rattr, file, MAX_FILE_SIZE, table))
                return;
            String s = Base64.encodeBase64String(uploadFile.getBytes());
            new FileWriter(file).write("");
            final int LENGTH = 3000;
            int n = s.length() / LENGTH;
            for (int i = 0, j = 1; i <= n; i++, j++)
            {
                if (i == n)
                {
                    String s1 = s.substring(LENGTH * i);
                    bufferedWriter.write(s1);
                }
                else
                {
                    String s1 = s.substring(LENGTH * i, LENGTH * j)
                                 .toString();
                    bufferedWriter.write(s1);
                    bufferedWriter.newLine();
                }
                bufferedWriter.flush();
            }
        }
        catch (IOException e)
        {
            //            e.printStackTrace();
            new FileWriter(file).write("");
        }
    }

    private boolean addFlashMessage(MultipartFile uploadFile, RedirectAttributes rattr, File file, long MAX_FILE_SIZE, TABLE table) throws IOException
    {
        String domainType = getDomainTypeToString(table);
        String originalFilename = uploadFile.getOriginalFilename();
        if (!(originalFilename.contains(".jpg") || originalFilename.contains(".JPG") || originalFilename.contains(".JPEG") || originalFilename.contains(".jpeg") || originalFilename.contains(".PNG") || originalFilename.contains(".png")))
        {
            rattr.addFlashAttribute("msg", domainType + ".image.invalidExtension");
            return true;
        }
        else if (MAX_FILE_SIZE < uploadFile.getSize())
        {
            rattr.addFlashAttribute("msg", domainType + ".image.overSize");
            return true;
        }
        else if (!file.exists())
            if (!file.createNewFile())
            {
                rattr.addAttribute("msg", domainType + ".image.uploadFailed");
                return true;
            }
        return false;

    }

    private String getDomainTypeToString(TABLE table)
    {
        switch (table)
        {
            case CUSTOMERS:return "customer";
            case EMPLOYEES: return"employee";
        }
        return "";
    }

    public boolean removeImg(TABLE table, BigInteger id) throws InterruptedException
    {
        StringBuilder imageRootPath = getImageRootPath(table);
        String imgPath = imageRootPath.append("\\" + id + ".txt")
                                      .toString();
        File imgFile = new File(imgPath);
        if (imgFile.exists() && imgFile.isFile())
        {
            System.gc();
            return imgFile.delete();
        }
        return false;

    }


}
