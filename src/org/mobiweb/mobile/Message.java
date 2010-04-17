/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.mobiweb.mobile;

import org.mobiweb.mobile.util.Common;
import javax.microedition.lcdui.*;

/**
 *
 * @author Ahmed Maawy
 */
public class Message {

    private String messageContent;
    private String boundary;

    public String getBoundary() {
        return boundary;
    }

    public Message() {
        // Initialize the message content

        messageContent = "";
        boundary = Common.generateBoundary();
    }

    public String getMessageContent() {
        // Getter: Message Content
        
        return messageContent;
    }

    public void addParameter(String paramName, String param) {
        // Adds a parameter to the message

        messageContent +=
            "--" + boundary + "\r\n" +
            "Content-Disposition: form-data; name=\"param_" + paramName + "\"\r\n" +
            param + "\r\n";
    }

    public void addPhoto(String photoName, byte[] photoData) {
        // Adds a photograph to the message

        messageContent += "--" + boundary + "\r\n" +
            "Content-Disposition: form-data; name=\"photo_"+ photoName +"\"; filename=\""+ photoName +"\"\r\n" +
            "Content-Type: image/jpeg\r\n";

        Image image = Image.createImage(photoData, 0,photoData.length);
        String imageString = new String(photoData);
        
        messageContent += imageString;
    }

    public void addFile(String fileName, byte[] fileData) {
        // Adds a file to the message

        messageContent += "--" + boundary + "\r\n" +
            "Content-Disposition: attachment; name=\"file_"+ fileName +"\"; filename=\""+ fileName +"\"\r\n";

        messageContent += fileData.toString();
    }
}