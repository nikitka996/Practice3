package com.company;

import java.io.*;
import java.nio.*;
import java.nio.channels.*;
import java.net.*;

public class Client {
    public static void main(String args[]) throws IOException {
        String hostName = "127.0.0.1";
        int portNumber = 8000;
        InetSocketAddress isa = new InetSocketAddress(hostName, portNumber);

        try (
            BufferedReader stdIn = new BufferedReader(new InputStreamReader(System.in));
            SocketChannel socket = SocketChannel.open(isa);
        )
        {
            while(true) {
                System.out.print("Your expression : ");
                String input = stdIn.readLine();

                ByteBuffer buffer = ByteBuffer.wrap(input.getBytes());
                socket.write(buffer);
                buffer.clear();

                ByteBuffer out = ByteBuffer.allocate(256);
                socket.read(out);
                String response = new String(out.array()).trim();
                out.clear();

                System.out.println("Result = " + response);
            }
        }
    }
}
