/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.mobiweb.mobile;

import org.mobiweb.mobile.util.Common;
import javax.microedition.lcdui.*;
import org.mobiweb.mobile.exceptions.MessageException;

/**
 *
 * @author Ahmed Maawy
 */
public class Message {

    private String messageContent;
    private String boundary;

    private boolean messageClosed = false;

    public String getBoundary() {
        return boundary;
    }

    public Message() {
        // Initialize the message content

        messageContent = "";
        boundary = Common.generateBoundary();
        messageClosed = false;
    }

    public boolean isMessageClosed() {
        return messageClosed;
    }

    public String getMessageContent() {
        // Getter: Message Content
        
        return messageContent;
    }

    public void addParameter(String paramName, String param) throws MessageException {
        // Adds a parameter to the message

        if(messageClosed) {
            throw new MessageException();
        }

        messageContent +=
            "--" + boundary + "\r\n" +
            "Content-Disposition: form-data; name=\"param_" + paramName + "\"\r\n\r\n" +
            param + "\r\n";
    }

    public void addPhoto(String photoName, byte[] photoData) throws MessageException {
        // Adds a photograph to the message

        if(messageClosed) {
            throw new MessageException();
        }

        messageContent += "--" + boundary + "\r\n" +
            "Content-Disposition: form-data; name=\"photo_"+ photoName +"\"; filename=\""+ photoName +"\"\r\n" +
            "Content-Type: image/jpeg\r\n\r\n";

        Image image = Image.createImage(photoData, 0,photoData.length);
        String imageString = new String(photoData);
        
        messageContent += imageString;
    }

    public void addFile(String fileName, byte[] fileData) throws MessageException {
        // Adds a file to the message

        if(messageClosed) {
            throw new MessageException();
        }

        messageContent += "--" + boundary + "\r\n" +
            "Content-Disposition: attachment; name=\"file_"+ fileName +"\"; filename=\""+ fileName +"\"\r\n\r\n";

        messageContent += new String(fileData);
    }

    public void closeMessage() {
        // Close the entire message
        
        messageContent += "\r\n--" + boundary + "--\r\n";
        messageClosed = true;
    }
}