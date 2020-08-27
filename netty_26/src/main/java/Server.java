import accept.FrameAcceptHandler;
import accept.FrameAnalyseHandler;
import accept.HeartBeatHandler;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.timeout.IdleStateHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class Server {
	static private final Logger LOGGER = LoggerFactory.getLogger(Server.class);

	public void bind(int port) throws Exception {
		EventLoopGroup bossGroup = new NioEventLoopGroup();
		EventLoopGroup workerGroup = new NioEventLoopGroup();
		try{
		ServerBootstrap b = new ServerBootstrap();
		b.group(bossGroup, workerGroup)
		.channel(NioServerSocketChannel.class)
		.option(ChannelOption.SO_BACKLOG, 100)
		.option(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT)
				/**
				 * 设置接收缓冲区大小啊啊
				 */
		.childOption(ChannelOption.SO_RCVBUF, 83886080*2*4*2)
				/**
				 * 设置接收缓冲池总共大小
				 */
		.childOption(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT)
		.childOption(ChannelOption.RCVBUF_ALLOCATOR, new FixedRecvByteBufAllocator(65536))

		.handler(new LoggingHandler(LogLevel.INFO))
		.childHandler(new ChannelInitializer<SocketChannel>() {

			@Override
			protected void initChannel(SocketChannel ch) throws Exception {
				// TODO Auto-generated method stub
				//针对客户端，如果1分钟之内没有向服务器发送读写心跳，则主动断开
				ch.pipeline().addLast(new IdleStateHandler(5,5,5));
				ch.pipeline().addLast(new HeartBeatHandler());
				ch.pipeline().addLast(new FrameAcceptHandler());
				ch.pipeline().addLast(new FrameAnalyseHandler());
			}


		});
		
		//bind port
		ChannelFuture f = b.bind(port).sync();
		if (f.isSuccess()){
			LOGGER.info("服务器启动成功");
		}
		//wait
		f.channel().closeFuture().sync();
		}finally{
			bossGroup.shutdownGracefully();
			workerGroup.shutdownGracefully();
		}
	}
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		int port = 8080;
		try {
			if(args != null && args.length > 0){
				port = Integer.parseInt(args[0]);
			}
			new Server().bind(port);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

}
