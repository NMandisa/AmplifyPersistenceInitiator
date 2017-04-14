/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.amplify.persistence.initiator;

import java.util.AbstractList;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 *
 * @author Noxolo.Mkhungo
 */
public class ImpexScriptGenerator {

    /**
     * @param args the command line arguments
     */
    //Delimiter used in CSV file
    private static final String commaDelimiter = ";";
    private static final String newLineSeparator = "\n";

    public static void main(String[] args) {
        // TODO code application logic 
        generateMedia();
    }
    private static final String[] mediaFormatArray = {"1200Wx1200H", "515Wx515H", "300Wx300H", "96Wx96H", "65Wx65H", "30Wx30H"};
    
    public static void generateMedia() {

        
        //Write the CSV file header
        String mediaFolderModeType = "INSERT_UPDATE";
        String madiaFolderItemType = "MediaFolder";

        List<String> mediaAttributeList = new ArrayList<>();
        List mediaValuesList = new ArrayList<>();
        mediaAttributeList.add("mediaFormat(qualifier)");
        mediaAttributeList.add("code[unique=true]");
        mediaAttributeList.add("$media");
        mediaAttributeList.add("mediaFormat(qualifier)");
        mediaAttributeList.add("mime[default=’image/jpg’]");
        mediaAttributeList.add("$catalogVersion");
        mediaAttributeList.add("folder(qualifier)");
        mediaAttributeList.add("realfilename");
        String mediaHeader = mediaFolderModeType + " " + madiaFolderItemType + commaDelimiter;

        for (int i = 0; i < mediaAttributeList.size(); i++) {//Attributes Contented in the Headers
            mediaHeader += " " + mediaAttributeList.get(i).toString();
            mediaHeader += " " + commaDelimiter;
        }
        System.out.println("Header : " + mediaHeader);

        //  Populating the values for the Impex
        String filename = "HelloImAFile.jpg";
        String mediasConcatenate = "";//the Initailizing the concatenation of the file 
        //Expected Outcome --> /1200Wx1200H/EOH_089_1200.jpg,/300Wx300H/EOH_089_1200.jpg,/96Wx96H/EOH_089_1200.jpg,/515Wx515H/EOH_089_1200.jpg,/65Wx65H/EOH_089_1200.jpg,/30Wx30H/EOH_089_1200.jpg
        ProductValue productValueObject = new ProductValue();
        for (int i = 0; i < mediaFormatArray.length; i++) {

            MediaValue tempMediaValueObject = new MediaValue();// MediaValue Obeject is being reset
            MediaContainerValue tempMediaContainerValueObject = new MediaContainerValue();// Media Container Value Obeject is being reset

            productValueObject.setCode(filename);
            productValueObject.setGalleryImages(filename);

            if (i == mediaFormatArray.length - 4) {
                //expected Outcome --> /300Wx300H/HelloImAFile.jpg
                String picture = "/" + mediaFormatArray[i] + "/" + filename;
                productValueObject.setPicture(picture);
            }
            if (i == mediaFormatArray.length - 3) {
                //expected Outcome --> //96Wx96H/EOH_029_1200.jpg
                productValueObject.setThumbnail("/" + mediaFormatArray[i] + "/" + filename);
            }

            tempMediaContainerValueObject.setQualifier(filename);
            mediasConcatenate += "images/" + mediaFormatArray[i] + "/" + filename + ",";

            if (i == mediaFormatArray.length - 1) {//Populate Media Container Values
                tempMediaContainerValueObject.setMedias(mediasConcatenate.substring(0, mediasConcatenate.length() - 1));//Removing the last string which is not needed
                //Expected Outcome Example Below--
                //1200Wx1200H/HelloWorldImAFile.jpg,/300Wx300H/HelloWorldImAFile.jpg,/96Wx96H/HelloWorldImAFile.jpg,/515Wx515H/HelloWorldImAFile.jpg,/65Wx65H/HelloWorldImAFile.jpg,/30Wx30H/HelloWorldImAFile.jpg
                String mediaContainerValueLine = commaDelimiter+ tempMediaContainerValueObject.getQualifier() + commaDelimiter + tempMediaContainerValueObject.getMedias() + commaDelimiter + " " + commaDelimiter;
                System.out.println("# Create Media Containers");
                System.out.println("Media Container Value : " + mediaContainerValueLine);
                //Expected Outcome Example Below--
                //;HelloWorldImAFile;;HelloWorldImAFile.jpg;/300Wx300H/HelloWorldImAFile.jpg;/96Wx96H/HelloWorldImAFile.jpg;
                String productValueLine = commaDelimiter +"\t"+ productValueObject.getCode() + commaDelimiter + " " + commaDelimiter + productValueObject.getGalleryImages() + commaDelimiter + " " + productValueObject.getPicture() + " " + commaDelimiter + " " + productValueObject.getThumbnail();
                System.out.println("# Update Products with Media and Media Containers");
                System.out.println("Product Value Line : " + productValueLine);
            }

            tempMediaValueObject.setMediaFormat(mediaFormatArray[i]);
            tempMediaValueObject.setCode("/" + mediaFormatArray[i] + "/" + filename);
            tempMediaValueObject.setMedia("images/" + mediaFormatArray[i] + "/" + filename);
            tempMediaValueObject.setMime(" ");
            tempMediaValueObject.setCatalogVersion(" ");
            tempMediaValueObject.setFolder("images");
            tempMediaValueObject.setRealFilename(filename);
            mediaValuesList.add(tempMediaValueObject);

        }

        Iterator iteratorMediaValues = mediaValuesList.iterator();
        MediaValue mediaValue = null;
        System.out.println("# Create Media");
        while (iteratorMediaValues.hasNext()) {//the Object
            mediaValue = (MediaValue) iteratorMediaValues.next();
            String valueLine = commaDelimiter+ mediaValue.getMediaFormat() + commaDelimiter + mediaValue.getCode() + " " + commaDelimiter + " " + mediaValue.getMedia() + " " + commaDelimiter + mediaValue.getMime() + commaDelimiter + mediaValue.getCatalogVersion() + " " + commaDelimiter + mediaValue.getFolder() + " " + commaDelimiter + mediaValue.getRealFilename();
            
            System.out.println("Media Value Line : " + valueLine);

        }

    }

}
