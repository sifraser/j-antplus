package be.glever.ant.message.control;

import be.glever.ant.AntException;
import be.glever.ant.message.AbstractAntMessage;
import be.glever.ant.message.AntBlockingMessage;
import be.glever.ant.message.channel.ChannelEventOrResponseMessage;

public class CloseChannelMessage extends AbstractAntMessage implements AntBlockingMessage {

    private byte[] bytes;

    public CloseChannelMessage() {
    }

    public CloseChannelMessage(byte channelNr) {
        this.bytes = new byte[]{channelNr};
    }

    @Override
    public byte getMessageId() {
        return 0x4c;
    }

    @Override
    public byte[] getMessageContent() {
        return this.bytes;
    }

    @Override
    public void setMessageBytes(byte[] messageContentBytes) throws AntException {
        this.bytes = messageContentBytes;
    }

    public byte getChannelNr() {
        return bytes[0];
    }

    @Override
    public byte getResponseMessageId() {
        return ChannelEventOrResponseMessage.MSG_ID;
    }
}
