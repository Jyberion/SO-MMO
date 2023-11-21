import java.io.IOException;
import java.net.Socket;

public class Client {
    

    private Socket socket;

    public Client(String host, int port) throws IOException {
        socket = new Socket(host, port);
    }

    public void sendMoveMessage(int x, int y) throws IOException {
        Message message = new Message(Message.TYPE_PLAYER_MOVE, new byte[]{(byte) x, (byte) y});
        message.send(socket.getOutputStream());
    }

    public void sendAttackMessage(int targetId) throws IOException {
        Message message = new Message(Message.TYPE_PLAYER_ATTACK, new byte[]{(byte) targetId});
        message.send(socket.getOutputStream());
    }

    public void sendChatMessage(String message) throws IOException {
        Message message = new Message(Message.TYPE_CHAT_MESSAGE, message.getBytes());
        message.send(socket.getOutputStream());
    }

    // Define the Message class within the Client class
    private class Message {

        private byte type;
        private byte[] payload;

        public Message(byte type, byte[] payload) {
            this.type = type;
            this.payload = payload;
        }

        public byte getType() {
            return type;
        }

        public byte[] getPayload() {
            return payload;
        }

        public void send(OutputStream outputStream) throws IOException {
            outputStream.write(type);
            outputStream.write(payload.length);
            outputStream.write(payload);
        }
    }

    public static void main(String[] args) throws IOException {
        Client client = new Client("localhost", 8080);

        client.sendMoveMessage(100, 100);
        client.sendAttackMessage(2);
        client.sendChatMessage("Hello, world!");
    }
}