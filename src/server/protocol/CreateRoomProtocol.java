package server.protocol;

import server.Server;
import server.room.User;

public class CreateRoomProtocol implements Protocol {
    public static String process (String[] request, User p)
    {
        if (p.getRoom() != null)
            return ALREADY_IN_A_ROOM_STRING;
        if (request.length != 3)
            return FORBBIDEN_REQUEST_STRING;

        String roomName = request[1];
        String roomCategory = request[2];
        Server.addRoom(roomName, p, roomCategory);
        String response = String.format("%s,%s", JOINED_IN_A_ROOM_STRING, p.getRoom().getInfo());
        return response;
    }
}
