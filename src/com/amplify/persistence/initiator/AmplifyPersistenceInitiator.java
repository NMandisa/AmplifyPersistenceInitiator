/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.amplify.persistence.initiator;

import com.amplify.persistence.initiator.model.*;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 *
 * @author Noxolo.Mkhungo
 */
public class AmplifyPersistenceInitiator {

    /**
     * @param args the command line arguments
     */
    //Delimiter used in CSV file
    private static final String COMMA_DELIMITER = ";";
    private static final String NEW_LINE_SEPARATOR = "\n";
    private static final String[] MEDIA_FORMAT_STRING = {"1200Wx1200H", "515Wx515H", "300Wx300H", "96Wx96H", "65Wx65H", "30Wx30H"};
    private static final String[] HEADER_STRINGS = {"INSERT_UPDATE MediaFolder;qualifier[unique=true];path[unique=true];", "INSERT_UPDATE Media;mediaFormat(qualifier);code[unique=true];$media;mime[default=’image/jpg’];$catalogVersion;folder(qualifier);realfilename;", "INSERT_UPDATE MediaContainer;qualifier[unique=true];$medias;$catalogVersion;", "INSERT_UPDATE Product;code[unique=true];$catalogVersion;$galleryImages;$picture;$thumbnail;"};

    public static void main(String[] args) throws IOException {
        // TODO code application logic 
        generateMedia();
    }

    public static void generateMedia() throws IOException {
        File file = new File(System.getProperty("user.dir") + "/resource/media.csv");
        FileWriter fw = new FileWriter(file,true);
        BufferedWriter bw = new BufferedWriter(fw);
        try {
            

            if (!file.exists()) {

                file.createNewFile();
            }

            //Media Values 
            List mediaValuesList = new ArrayList<>();

            //  Populating the values for the Impex
            String filename = "HelloImAFile.jpg";
            String mediasConcatenate = "";//the Initailizing the concatenation of the file 
            //Expected Outcome --> /1200Wx1200H/HelloWorldIamAFile.jpg,/300Wx300H/HelloWorldIamAFile.jpg,/96Wx96H/HelloWorldIamAFile.jpg,/515Wx515H/HelloWorldIamAFile.jpg,/65Wx65H/HelloWorldIamAFile.jpg,/30Wx30H/HelloWorldIamAFile.jpg
            ProductValue productValueObject = new ProductValue();
            MediaFolderValue mfv = new MediaFolderValue();
            mfv.setPath("images");
            mfv.setQualifier("images");
            //Expected Output --> ;images;images;
            bw.write("#MEDIA");
            bw.write(NEW_LINE_SEPARATOR);
            bw.write(HEADER_STRINGS[0]);
            bw.write(NEW_LINE_SEPARATOR);
            String mfvStringLine = COMMA_DELIMITER + mfv.getQualifier() + COMMA_DELIMITER + mfv.getPath() +COMMA_DELIMITER;
            bw.write(mfvStringLine);
            bw.write(NEW_LINE_SEPARATOR);
            System.out.println("#MEDIA");
            System.out.println(HEADER_STRINGS[0]);
            
            System.out.println("Media Folder Value Line : " + mfvStringLine);
            for (int i = 0; i < MEDIA_FORMAT_STRING.length; i++) {

                MediaValue tempMediaValueObject = new MediaValue();// MediaValue Obeject is being reset
                MediaContainerValue tempMediaContainerValueObject = new MediaContainerValue();// Media Container Value Obeject is being reset

                productValueObject.setCode(filename);
                productValueObject.setGalleryImages(filename);

                if (i == MEDIA_FORMAT_STRING.length - 4) {
                    //expected Outcome --> /300Wx300H/HelloImAFile.jpg
                    String picture = "/" + MEDIA_FORMAT_STRING[i] + "/" + filename;
                    productValueObject.setPicture(picture);
                }
                if (i == MEDIA_FORMAT_STRING.length - 3) {
                    //expected Outcome --> //96Wx96H/EOH_029_1200.jpg
                    productValueObject.setThumbnail("/" + MEDIA_FORMAT_STRING[i] + "/" + filename);
                }

                tempMediaContainerValueObject.setQualifier(filename);
                mediasConcatenate += "images/" + MEDIA_FORMAT_STRING[i] + "/" + filename + ",";

                if (i == MEDIA_FORMAT_STRING.length - 1) {//Populate Media Container Values
                    tempMediaContainerValueObject.setMedias(mediasConcatenate.substring(0, mediasConcatenate.length() - 1));//Removing the last string which is not needed
                    //Expected Outcome Example Below--
                    //1200Wx1200H/HelloWorldImAFile.jpg,/300Wx300H/HelloWorldImAFile.jpg,/96Wx96H/HelloWorldImAFile.jpg,/515Wx515H/HelloWorldImAFile.jpg,/65Wx65H/HelloWorldImAFile.jpg,/30Wx30H/HelloWorldImAFile.jpg
                    String mediaContainerValueLine = COMMA_DELIMITER + tempMediaContainerValueObject.getQualifier() + COMMA_DELIMITER + tempMediaContainerValueObject.getMedias() + COMMA_DELIMITER + " " + COMMA_DELIMITER;
                    System.out.println("# Create Media Containers");
                    bw.write("# Create Media Containers"); bw.write(NEW_LINE_SEPARATOR);
                    System.out.println(HEADER_STRINGS[2]);
                    bw.write(HEADER_STRINGS[2]);bw.write(NEW_LINE_SEPARATOR);
                    System.out.println("Media Container Value : " + mediaContainerValueLine);
                    bw.write(mediaContainerValueLine); bw.write(NEW_LINE_SEPARATOR);
                    //Expected Outcome Example Below--
                    //;HelloWorldImAFile;;HelloWorldImAFile.jpg;/300Wx300H/HelloWorldImAFile.jpg;/96Wx96H/HelloWorldImAFile.jpg;
                    String productValueLine = COMMA_DELIMITER + "\t" + productValueObject.getCode() + COMMA_DELIMITER + " " + COMMA_DELIMITER + productValueObject.getGalleryImages() + COMMA_DELIMITER + " " + productValueObject.getPicture() + " " + COMMA_DELIMITER + " " + productValueObject.getThumbnail() + COMMA_DELIMITER;
                    bw.write("# Update Products with Media and Media Containers");bw.write(NEW_LINE_SEPARATOR);
                    System.out.println("# Update Products with Media and Media Containers");
                    bw.write(HEADER_STRINGS[3]);bw.write(NEW_LINE_SEPARATOR);
                    System.out.println(HEADER_STRINGS[3]);
                    bw.write(productValueLine);bw.write(NEW_LINE_SEPARATOR);
                    System.out.println("Product Value Line : " + productValueLine);
                }

                tempMediaValueObject.setMediaFormat(MEDIA_FORMAT_STRING[i]);
                tempMediaValueObject.setCode("/" + MEDIA_FORMAT_STRING[i] + "/" + filename);
                tempMediaValueObject.setMedia("images/" + MEDIA_FORMAT_STRING[i] + "/" + filename);
                tempMediaValueObject.setMime(" ");
                tempMediaValueObject.setCatalogVersion(" ");
                tempMediaValueObject.setFolder("images");
                tempMediaValueObject.setRealFilename(filename);
                mediaValuesList.add(tempMediaValueObject);

            }

            Iterator iteratorMediaValues = mediaValuesList.iterator();
            MediaValue mediaValue = new MediaValue();
            System.out.println("# Create Media");
           
            System.out.println(HEADER_STRINGS[1]);
            bw.write("# Create Media");
            bw.write(NEW_LINE_SEPARATOR);
            bw.write(HEADER_STRINGS[1]);
            bw.write(NEW_LINE_SEPARATOR);
            while (iteratorMediaValues.hasNext()) {//the Object
                mediaValue = (MediaValue) iteratorMediaValues.next();
                String valueLine = COMMA_DELIMITER + mediaValue.getMediaFormat() + COMMA_DELIMITER + mediaValue.getCode() + " " + COMMA_DELIMITER + " " + mediaValue.getMedia() + " " + COMMA_DELIMITER + mediaValue.getMime() + COMMA_DELIMITER + mediaValue.getCatalogVersion() + " " + COMMA_DELIMITER + mediaValue.getFolder() + " " + COMMA_DELIMITER + mediaValue.getRealFilename() + COMMA_DELIMITER;
                System.out.println("Media Value Line : " + valueLine);
                bw.write(valueLine);
                bw.write(NEW_LINE_SEPARATOR);

            }
        } catch (IOException ex) {

            ex.printStackTrace();
        } finally {
            try {
                if (bw != null) {
                    bw.close();
                } else {
                    System.out.println("Buffer has not been initialized!");
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }

}
