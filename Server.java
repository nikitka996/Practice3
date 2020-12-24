package com.company;

import java.net.*;
import java.io.*;
import java.util.*;
import java.nio.*;
import java.nio.channels.*;


public class Server {
    int port;

    public Server() {
        this.port = 8000;
        InetSocketAddress isa = new InetSocketAddress("local", port);

        try (Selector channel = Selector.open();
             ServerSocketChannel socket = ServerSocketChannel.open()){
            socket.bind(isa);
            socket.configureBlocking(false);

            ByteBuffer buffer = ByteBuffer.allocate(256);

            while (true) {
                channel.select();
                Set<SelectionKey> keys = channel.selectedKeys();
                Iterator<SelectionKey> it = keys.iterator();

                while (it.hasNext()) {
                    try {
                        SelectionKey key = it.next();

                        if (key.isAcceptable()) {
                            SocketChannel client = socket.accept();
                            client.configureBlocking(false);
                            client.register(channel, SelectionKey.OP_READ);
                        }

                        if (key.isReadable()) {
                            try {
                                SocketChannel client = (SocketChannel) key.channel();
                                client.read(buffer);
                                buffer.flip();

                                String request = new String(buffer.array()).trim();
                                buffer.clear();

                                String response = response(request);
                                ByteBuffer out = ByteBuffer.allocate(256);
                                out.put(response.getBytes());
                                out.flip();
                                client.write(out);
                                out.clear();
                            }
                            catch (IOException e) {
                                key.cancel();
                            }
                        }
                    }
                    catch(Exception e) {
                        System.out.println(e);
                    }
                    finally {
                        it.remove();
                    }
                }
            }
        }
        catch(Exception e) {
            System.out.println(e);
        }
    }

    public static String response(String request) {
        String firstNum = "";
        String secondNum = "";
        char sign = '+';
        String operations = "+-*/";

        try {
            for (int i = 0; i < request.length(); i++) {
                if (operations.indexOf(request.charAt(i)) == -1) {
                    if (request.charAt(i) != ' ')
                        firstNum += request.charAt(i);
                }
                else {
                    sign = request.charAt(i);
                    for (int j  = i+1; j < request.length(); j++) {
                        if (request.charAt(j) != ' ')
                            secondNum += request.charAt(j);
                    }
                    break;
                }
            }

            int a = Integer.parseInt(firstNum);
            int b = Integer.parseInt(secondNum);
            int res = 0;

            switch(sign){
                case '+':
                    res = a + b;
                    break;
                case '-':
                    res = a - b;
                    break;
                case '*':
                    res = a * b;
                    break;
                case '/':
                    res = a / b;
                    break;
            }

            return Integer.toString(res);
        }
        catch (Exception e) {
            System.out.println(e.getMessage());
            return "Wrong expression. Use two numbers and one sign!";
        }
    }

    public static void main(String[] args){
        new Server();
    }
}
