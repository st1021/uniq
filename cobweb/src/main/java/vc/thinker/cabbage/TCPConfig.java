package vc.thinker.cabbage;

import java.io.IOException;
import java.net.InetSocketAddress;

import org.apache.mina.filter.codec.ProtocolCodecFactory;
import org.apache.mina.filter.codec.ProtocolCodecFilter;
import org.apache.mina.transport.socket.SocketAcceptor;
import org.apache.mina.transport.socket.nio.NioSocketAcceptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import akka.actor.ActorSystem;
import vc.thinker.cabbage.cmd.FutejiaTCPCommandPush;
import vc.thinker.cabbage.cmd.ShareTcpCommonPush;
import vc.thinker.cabbage.cmd.TcpCommandPush;
import vc.thinker.cabbage.cmd.YunchongbaiTCPCommandPush;
import vc.thinker.cabbage.extension.SpringExtension;
import vc.thinker.cabbage.se.BoxConstants;
import vc.thinker.cabbage.tcp.ConductorHandler;
import vc.thinker.cabbage.tcp.FutiejiaByteArrayCodecFactory;
import vc.thinker.cabbage.tcp.SessionStoreManager;
import vc.thinker.cabbage.tcp.ShareByteArrayCodecFactory;
import vc.thinker.cabbage.tcp.YunchongbaiByteArrayCodecFactory;

/**
 * 
 * @author james
 *
 */
@Configuration
@ConditionalOnExpression("${tcp.run:true}")
public class TCPConfig {
	
	
	private static final Logger log=LoggerFactory.getLogger(TCPConfig.class);

	@Value("${tcp.port}")
	private int PORT = 7002;
	
	@Value("${tcp.akka.receive}")
	private String tcpAkkaReceive;
	
	@Autowired
	private SpringExtension springExtension;
	
	@Autowired
	private ActorSystem actorSystem;
	
	@Bean
	public SessionStoreManager sessionStoreManager(){
		return new SessionStoreManager();
	}
	
	@Bean
	public TcpCommandPush shareTcpCommonPush() {
		switch (tcpAkkaReceive) {
		case BoxConstants.BOX_TYPE_SHARE:
			return new ShareTcpCommonPush();
		case BoxConstants.BOX_TYPE_FUTEJIA:
			return new FutejiaTCPCommandPush();
		default:
			return new YunchongbaiTCPCommandPush();
		}
	}
	
	@Bean
	public SocketAcceptor acceptor() throws IOException {

		ProtocolCodecFactory protocolCodecFactory = null;

		switch (tcpAkkaReceive) {
		case BoxConstants.BOX_TYPE_SHARE:
			protocolCodecFactory = new ShareByteArrayCodecFactory();
			break;
		case BoxConstants.BOX_TYPE_FUTEJIA:
			protocolCodecFactory = new FutiejiaByteArrayCodecFactory();
			break;
		default:
			protocolCodecFactory = new YunchongbaiByteArrayCodecFactory();
			break;
		}
		SocketAcceptor acceptor = new NioSocketAcceptor();
		acceptor.setReuseAddress(true);
		acceptor.getFilterChain().addLast("codec", new ProtocolCodecFilter(protocolCodecFactory));
		acceptor.setHandler(new ConductorHandler(springExtension, actorSystem, sessionStoreManager(), tcpAkkaReceive));
		acceptor.bind(new InetSocketAddress(PORT));
		log.info("bind port {}", PORT);
		return acceptor;
	}
}
