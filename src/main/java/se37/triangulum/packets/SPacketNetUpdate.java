package se37.triangulum.packets;

import scala.actors.threadpool.Arrays;
import se37.triangulum.core.OctahedronLogic;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IThreadListener;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class SPacketNetUpdate implements IMessage {
	
	private static final int VARINT_BYTES = 5;

	private float voltage;
	private float[] currents;
	private BlockPos pos;

	public SPacketNetUpdate() {
		this(0, null, null);
	}

	public SPacketNetUpdate(float voltage, float[] currents, BlockPos pos) {
		this.voltage = voltage;
		this.currents = currents;
		this.pos = pos;
	}

	public float getVoltage() {
		return voltage;
	}

	public float[] getCurrents() {
		return currents;
	}

	public BlockPos getPos() {
		return pos;
	}

	/**
	 * Reads the voltage and currents from the given ByteBuf. The currents are
	 * stored in an array just long enough to hold all of them.
	 */
	@Override
	public void fromBytes(ByteBuf buf) {
		voltage = buf.readFloat();

		int x = ByteBufUtils.readVarInt(buf, VARINT_BYTES);
		int y = ByteBufUtils.readVarInt(buf, VARINT_BYTES);
		int z = ByteBufUtils.readVarInt(buf, VARINT_BYTES);

		pos = new BlockPos(x, y, z);
		
		byte flags = buf.readByte();
		currents = new float[8];
		for (int i = 0; i < currents.length; i++) {
			if (getFlag(flags, i)) {
				currents[i] = buf.readFloat();
			} else {
				currents[i] = 0;
			}
		}
	}

	private boolean getFlag(int flags, int index) {
		return (flags >>> index) % 2 == 1;
	}
	
	private int setFlag(int flags, int index) {
		return flags | (1 << index);
	}

	/**
	 * Writes the packet's voltage and the first eight floats in currents to the
	 * given ByteBuf. If there are less than eight floats in currents, the rest
	 * are filled in with 0s without taking up any more space.
	 */
	@Override
	public void toBytes(ByteBuf buf) {
		buf.writeFloat(voltage);

		ByteBufUtils.writeVarInt(buf, pos.getX(), VARINT_BYTES);
		ByteBufUtils.writeVarInt(buf, pos.getY(), VARINT_BYTES);
		ByteBufUtils.writeVarInt(buf, pos.getZ(), VARINT_BYTES);
		
		byte flags = 0;
		for (int i = 0; i < 8 && i < currents.length; i++) {
			if (currents[i] != 0) {
				flags = (byte) setFlag(flags, i);
			}
		}
		buf.writeByte(flags);
		for (int i = 0; i < 8 && i < currents.length; i++) {
			if (currents[i] != 0) {
				buf.writeFloat(currents[i]);
			}
		}
	}
	
	public String toString() {
		return "V: " + voltage + ", I: " + Arrays.toString(currents) + ", " + pos;
	}
	
	public static class Handler implements
			IMessageHandler<SPacketNetUpdate, IMessage> {

		@Override
		public IMessage onMessage(final SPacketNetUpdate message, MessageContext ctx) {
			IThreadListener clientThread = Minecraft.getMinecraft();
			clientThread.addScheduledTask(new Runnable() {
				@Override
				public void run() {
					TileEntity te = Minecraft.getMinecraft().theWorld.getTileEntity(message.getPos());
					if(te instanceof OctahedronLogic) {
						OctahedronLogic ol = (OctahedronLogic) te;
						ol.onDataPacket(message);
					}
				}
			});
			return null;
		}
	}
}
