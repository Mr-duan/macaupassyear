package com.org.socket.impl;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.org.exception.SSocketException;
import com.org.socket.ISocket;

/**
 * 系统阻塞的客户端 
 * 
 * @since 2011-09-06 13:43:43
 *
 */
public class IOSocket implements ISocket {

    private static Log log = LogFactory.getLog(IOSocket.class);

    protected Socket socket;

    protected InputStream ins = null;

    protected OutputStream out = null;

    private int bufferSize = 512;

    public IOSocket() {
    }

    public IOSocket(Socket s) {
        this.socket = s;
    }

    @Override
    public void close() {
        try {
            if (this.socket != null && !isClose()) {

                if (this.out != null) {
                    try {
                        this.out.close();
                    } catch (IOException ie) {
                        ie.printStackTrace();
                    }
                }

                if (this.ins != null) {
                    try {
                        this.ins.close();
                    } catch (IOException ie) {
                        ie.printStackTrace();
                    }
                }
                this.socket.close();
                this.socket = null;
            }
        } catch (IOException e) {
            e = new SSocketException(e.getMessage());
            e.printStackTrace();
            log.info("Socket中关闭通讯通道失败");
        }
    }

    @Override
    public void connect(String hostName, int port, int timeoutSeconds) {
        try {
            if (this.socket == null) {
                this.socket = new Socket();
            }
            this.socket.connect(new InetSocketAddress(hostName, port));
            this.socket.setTcpNoDelay(true);
            this.socket.setSoTimeout(timeoutSeconds * 1000);
            log.info("连接[" + hostName + ":" + port + "]成功");
        } catch (IOException ioe) {
            ioe.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean isClose() {
        return this.socket.isClosed();
    }

    @Override
    public byte[] read() {
        if (this.socket != null && !isClose()) {
            try {
                ByteArrayOutputStream bout = new ByteArrayOutputStream();
                this.ins = this.socket.getInputStream();
                byte[] b = new byte[this.bufferSize];
                int fb = this.ins.read();
                bout.write(fb);
                while ((this.ins.available()) > 0) {
                    int n = this.ins.read(b);
                    bout.write(b, 0, n);
                }
                bout.flush();
                return bout.toByteArray();
            } catch (IOException e) {

                log.info("socket 读取数据出现异常");
                if (this.ins != null) {
                    try {
                        this.ins.close();
                    } catch (IOException ie) {
                        ie.printStackTrace();
                    }
                }
                e.printStackTrace();
            }
        }
        return null;
    }

    @Override
    public void write(byte[] data, boolean flush) {
        if (this.socket != null && !this.socket.isClosed()) {
            try {
                this.out = this.socket.getOutputStream();
                this.out.write(data, 0, data.length);
                if (flush) {
                    this.out.flush();
                }
            } catch (IOException e) {
                log.info("socket 读取数据出现异常");
                if (this.out != null) {
                    try {
                        this.out.close();
                    } catch (IOException ie) {
                        ie.printStackTrace();
                    }
                }
                e.printStackTrace();
            }
        }
    }

    public static void main(String[] args) {

        log.info("Socket中关闭通讯通道失败");

        ISocket so = null;
        try {
            so = new IOLenthSocket();

            so.connect("172.28.250.111", 3737, 20);

            byte[] data = "{\"version\":\"ver1.0.0\",\"charset\":\"UTF-8\",\"accessChannelNo\":\"00000003\",\"accessType\":\"0001\",\"commType\":\"0001\",\"deviceType\":\"\",\"smpType\":\"M0025\",\"dType\":\"01\",\"deviceInfo\":{},\"tranAttribute\":\"\",\"monitorFiled\":{},\"routeFiled\":\"\",\"traceNo\":\"\",\"securityInfo\":{},\"clientInfo\":{},\"memId\":\"2396\"}"
                    .getBytes("utf-8");
            so.write(data, true);
            System.out.println("data send ok");
            byte[] bb = so.read();
            System.out.println(new String(bb, "utf-8"));
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (so != null) {
                try {
                    so.close();
                } catch (SSocketException e) {
                    e.printStackTrace();
                }
            }
        }
    }

}
