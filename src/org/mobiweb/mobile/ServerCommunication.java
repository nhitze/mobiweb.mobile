/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.mobiweb.mobile;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import javax.microedition.io.Connector;
import javax.microedition.io.HttpConnection;

import org.mobiweb.mobile.exceptions.ConnectionException;
import org.mobiweb.mobile.util.ServerResponse;

/**
 *
 * @author Ahmed Maawy
 */
public class ServerCommunication {

    private HttpConnection httpConnection = null;
    private String serverUrl;

    public String getServerUrl() {
        return serverUrl;
    }

    public void setServerUrl(String serverUrl) {
        this.serverUrl = serverUrl;
    }

    public ServerCommunication(String url, boolean openConnection) {
        serverUrl = url;
        
        if(openConnection) {
            openConnection();
        }
    }

    public void openConnection() {
        // Open HTTP connection

        try {
            httpConnection = (HttpConnection)Connector.open(serverUrl);
        } catch (IOException ex) {
            httpConnection = null;

            ex.printStackTrace();
        }
    }

    public void closeConnection() {
        // Close HTTP connection

        if(httpConnection != null) {
            try {
                httpConnection.close();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
        
        httpConnection = null;
    }

    private void makeResponseError(ServerResponse serverResponse) {
        serverResponse.setStatusMessage("error");
        serverResponse.setMainMessage(null);
        serverResponse.setResponseMessage(null);
    }
    
    public ServerResponse sendPostMessage(Message message) throws ConnectionException {
        DataInputStream dis;
        DataOutputStream dos;
        ServerResponse serverResponse = new ServerResponse();

        if(httpConnection != null) {
            try {
                httpConnection.setRequestMethod(HttpConnection.POST);
                httpConnection.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + message.getBoundary());

                dos = httpConnection.openDataOutputStream();
                String finalMessage = message.getMessageContent() + "--" + message.getBoundary() + "--\r\n";
                
                byte[] byteStream = finalMessage.getBytes();

                dos.write(byteStream);

                /*
                Another method you can use:
                for(int byteLoop = 0; byteLoop < byteStream.length; byteLoop ++) {
                    dos.writeByte(byteStream[byteLoop]);
                }
                */

                dos.close();

                dis = new DataInputStream(httpConnection.openDataInputStream());
                long len = httpConnection.getLength();

                char ch;

                // Check for content length
                if (len!=-1) {
                    for(int i = 0;i<len;i++) {
                        if((ch = (char) dis.read())!= -1) {
                            serverResponse.getMainMessage().append((char) ch);
                        }
                    }
                }
                else {
                    // if the content-length is not available
                    while ((ch = (char) dis.read()) != -1)
                        serverResponse.getMainMessage().append((char) ch);
                }

                dis.close();

                serverResponse.setResponseMessage(new StringBuffer(httpConnection.getResponseMessage()));

                if(httpConnection.getResponseCode() != HttpConnection.HTTP_OK) {
                    makeResponseError(serverResponse);

                    return serverResponse;
                }

                // Server response header: OK
                serverResponse.setStatusMessage("ok");
            } catch (IOException ex) {
                ex.printStackTrace();

                // Server response header: Error
                makeResponseError(serverResponse);
            }
        }
        else {
            makeResponseError(serverResponse);
            throw new ConnectionException();
        }
        
        return serverResponse;
    }

    public void finalize() {
        // Destructor

        closeConnection();
    }
}
