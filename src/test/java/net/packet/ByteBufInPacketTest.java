package net.packet;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.awt.*;
import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ByteBufInPacketTest {
    private ByteBuf byteBuf;
    private InPacket inPacket;

    @BeforeEach
    void reset() {
        this.byteBuf = Unpooled.buffer();
        this.inPacket = new ByteBufInPacket(byteBuf);
    }

    @Test
    void readByte() {
        final byte writtenByte = 123;
        byteBuf.writeByte(writtenByte);

        byte readByte = inPacket.readByte();

        assertEquals(writtenByte, readByte);
    }

    @Test
    void readShort() {
        final short writtenShort = 12_345;
        byteBuf.writeShortLE(writtenShort);

        short readShort = inPacket.readShort();

        assertEquals(writtenShort, readShort);
    }

    @Test
    void readInt() {
        final int writtenInt = 1_234_567_890;
        byteBuf.writeIntLE(writtenInt);

        int readInt = inPacket.readInt();

        assertEquals(writtenInt, readInt);
    }

    @Test
    void readLong() {
        final long writtenLong = 9_223_372_036_854_775_807L;
        byteBuf.writeLongLE(writtenLong);

        long readLong = inPacket.readLong();

        assertEquals(writtenLong, readLong);
    }

    @Test
    void readPoint() {
        final Point writtenPoint = new Point(111, 222);
        byteBuf.writeShortLE((short) writtenPoint.getX());
        byteBuf.writeShortLE((short) writtenPoint.getY());

        Point readPoint = inPacket.readPoint();

        assertEquals(writtenPoint, readPoint);
    }

    @Test
    void readString() {
        final String writtenString = "You have gained experience (+3200)";
        byteBuf.writeShortLE(writtenString.length());
        byte[] writtenStringBytes = writtenString.getBytes(StandardCharsets.US_ASCII);
        byteBuf.writeBytes(writtenStringBytes);

        String readString = inPacket.readString();

        assertEquals(writtenString, readString);
    }

    @Test
    void readBytes() {
        byte[] writtenBytes = {10, 11, 12, 13, 14, 15};
        byteBuf.writeBytes(writtenBytes);

        byte[] byteBatch1 = inPacket.readBytes(1);
        assertEquals(1, byteBatch1.length);
        assertEquals(10, byteBatch1[0]);

        byte[] byteBatch2 = inPacket.readBytes(2);
        assertEquals(2, byteBatch2.length);
        assertEquals(11, byteBatch2[0]);
        assertEquals(12, byteBatch2[1]);

        byte[] byteBatch3 = inPacket.readBytes(3);
        assertEquals(3, byteBatch3.length);
        assertEquals(13, byteBatch3[0]);
        assertEquals(14, byteBatch3[1]);
        assertEquals(15, byteBatch3[2]);
    }

    @Test
    void skip() {
        byte[] writtenBytes = {20, 21, 22, 23, 24, 25};
        byteBuf.writeBytes(writtenBytes);

        byte firstByte = inPacket.readByte();
        assertEquals(20, firstByte);

        inPacket.skip(3);

        byte fifthByte = inPacket.readByte();
        assertEquals(24, fifthByte);
    }

    @Test
    void available() {
        byte[] writtenBytes = {30, 31, 32, 33, 34, 35};
        byteBuf.writeBytes(writtenBytes);

        assertEquals(6, inPacket.available());

        inPacket.readByte();
        assertEquals(5, inPacket.available());

        inPacket.readInt();
        assertEquals(1, inPacket.available());
    }

    @Test
    void seek() {
        byte[] writtenBytes = {40, 41, 42, 43, 44, 45};
        byteBuf.writeBytes(writtenBytes);

        inPacket.seek(2);
        assertEquals(4, inPacket.available());
        byte byteAtSeek = inPacket.readByte();
        assertEquals(42, byteAtSeek);

        inPacket.seek(0);
        byte byteAtReset = inPacket.readByte();
        assertEquals(40, byteAtReset);
    }

    @Test
    void getPosition() {
        byte[] writtenBytes = {50, 51, 52, 53, 54, 55, 56, 57, 58, 59, 60};
        byteBuf.writeBytes(writtenBytes);

        assertEquals(0, inPacket.getPosition());

        inPacket.readByte();
        assertEquals(1, inPacket.getPosition());

        inPacket.readShort();
        assertEquals(3, inPacket.getPosition());

        inPacket.readInt();
        assertEquals(7, inPacket.getPosition());

        inPacket.seek(5);
        assertEquals(5, inPacket.getPosition());
    }
}