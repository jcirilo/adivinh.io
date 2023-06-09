package server;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import server.handlers.ChatConnectionHandler;

public class Chat extends Thread
{
    private static HashMap<Integer, ArrayList<ChatConnectionHandler>> groups;
    private static ServerSocket server;
    
    public Chat () {
        groups = new HashMap<Integer, ArrayList<ChatConnectionHandler>>();
    }

    @Override
    public void run ()
    {
        try {
            Executor e = Executors.newCachedThreadPool();
            server = new ServerSocket(3000);
            System.out.println("chat rodando! porta 3000");
            while (!server.isClosed())
            {
                e.execute(new ChatConnectionHandler(server.accept()));
            }
        
        } catch (IOException e ) {}
    }

    public static void newChat (int id)
    {
        synchronized (groups) {
            groups.put(id, new ArrayList<ChatConnectionHandler>(100));
        }
    }

    public static void broadcast (int groupID, String msg)
    {
        ArrayList<ChatConnectionHandler> chatGroup = groups.get(groupID);
        
        if (chatGroup != null) {
            synchronized (chatGroup) {
                for (ChatConnectionHandler client : chatGroup) {
                    if (client != null && client.hasBoundPlayer()) 
                        client.receiveMessage(msg);
                }
            }
        }
    }

    public static void endChat (int roomId)
    {
        ArrayList<ChatConnectionHandler> chatGroup = groups.get(roomId);
        synchronized (chatGroup) {
            if (chatGroup != null) {
                for (ChatConnectionHandler hand : chatGroup)
                    if (hand != null && hand.hasBoundPlayer()) hand.shutdown();
                groups.remove(roomId);
            }
        }
    }

    public static void 
    quit (int roomId, ChatConnectionHandler hand )
    {
        ArrayList<ChatConnectionHandler> chatGroup = groups.get(roomId);
        synchronized (chatGroup) {
            chatGroup.remove(hand);
            broadcast(roomId, (hand.getBoundPlayer().getNickname() + " saiu"));
            hand = null;
        }
    }

    public static void join (int roomId, ChatConnectionHandler hand)
    {
        ArrayList<ChatConnectionHandler> chatGroup = groups.get(roomId);
        
        if (chatGroup != null)
        {
            synchronized (chatGroup) {
                chatGroup.add(hand);
            }
            broadcast(roomId, hand.getBoundPlayer().getNickname() + " entrou!");
        }
    }

}