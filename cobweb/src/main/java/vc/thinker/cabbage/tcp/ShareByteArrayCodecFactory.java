package vc.thinker.cabbage.tcp;

import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.CumulativeProtocolDecoder;
import org.apache.mina.filter.codec.ProtocolCodecFactory;
import org.apache.mina.filter.codec.ProtocolDecoder;
import org.apache.mina.filter.codec.ProtocolDecoderOutput;
import org.apache.mina.filter.codec.ProtocolEncoder;
import org.apache.mina.filter.codec.ProtocolEncoderAdapter;
import org.apache.mina.filter.codec.ProtocolEncoderOutput;
import org.slf4j.Logger;

import vc.thinker.cabbage.util.HexUtils;



/**
 * 
 * @description MyWebConfig.java
 *
 * @author ZhangGaoXiang
 * @date 2020年12月23日 上午11:41:17
 */
public class ShareByteArrayCodecFactory implements ProtocolCodecFactory {

	
	public static final Logger LOGGER = org.slf4j.LoggerFactory.getLogger(ShareByteArrayCodecFactory.class);
	
	private ProtocolEncoder encoder;
	private ProtocolDecoder decoder;

	public ShareByteArrayCodecFactory() {
		encoder = new ByteArrayEncoder();
		decoder = new ByteArrayDecoder();
	}

	public class ByteArrayEncoder extends ProtocolEncoderAdapter {

		@Override
		public void encode(IoSession session, Object message, ProtocolEncoderOutput out) throws Exception {
			byte[] bytes = (byte[]) message;
			IoBuffer buffer = IoBuffer.allocate(bytes.length);

			buffer.put(bytes);
			buffer.flip();
			out.write(buffer);
		}
	}
	
	public static void main(String[] args) {
		byte[] b = new byte[] { 0, 32 };
		String hexString = HexUtils.toHexString(b);
		int parseInt = Integer.parseInt(hexString, 16);
		System.out.println(parseInt);
	}

	
	
	public class ByteArrayDecoder extends CumulativeProtocolDecoder {

		@Override
		protected boolean doDecode(IoSession session, IoBuffer in, ProtocolDecoderOutput out) throws Exception {

			in.mark();
			byte[] buf = in.array();
			int len = in.limit();

			byte[] packArr = new byte[len];
			in.get(packArr, 0, len);
			
			if (0x00 == packArr[packArr.length-1]) {
				
				out.write(packArr);

				if (in.remaining() > 0) {
					return false;
				}
				return true;
			} else {
				in.reset();
				return false;
			}
		}
	}

	@Override
	public ProtocolEncoder getEncoder(IoSession session) throws Exception {
		return encoder;
	}

	@Override
	public ProtocolDecoder getDecoder(IoSession session) throws Exception {
		return decoder;
	}
	
}
