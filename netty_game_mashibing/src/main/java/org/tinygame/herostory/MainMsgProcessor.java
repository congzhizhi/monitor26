package org.tinygame.herostory;

import com.google.protobuf.GeneratedMessageV3;
import io.netty.channel.ChannelHandlerContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tinygame.herostory.cmdhandler.CmdHandlerFactory;
import org.tinygame.herostory.cmdhandler.ICmdHandler;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 主消息处理器
 */
public final class MainMsgProcessor {
    /**
     * 日志对象
     */
    static private final Logger LOGGER = LoggerFactory.getLogger(MainMsgProcessor.class);

    /**
     * 单例对象
     */
    static private final MainMsgProcessor _instance = new MainMsgProcessor();

    /**
     * 创建一个单线程的线程池
     */
    private final ExecutorService _es = Executors.newSingleThreadExecutor((newRunnable) -> {
        Thread newThread = new Thread(newRunnable);
        newThread.setName("MainMsgProcessor");
        return newThread;
    });

    /**
     * 私有化类默认构造器
     */
    private MainMsgProcessor() {
    }

    /**
     * 获取单例对象
     *
     * @return 单例对象
     */
    static public MainMsgProcessor getInstance() {
        return _instance;
    }

    /**
     * 处理消息
     *
     * @param ctx
     * @param msg
     */
    public void process(ChannelHandlerContext ctx, Object msg) {
        if (null == ctx ||
            null == msg) {
            return;
        }

        final Class<?> msgClazz = msg.getClass();

        LOGGER.info(
            "收到客户端消息, msgClazz = {}, msgObj = {}",
            msgClazz.getSimpleName(),
            msg
        );

        _es.submit(() -> {
            try {
                // 获取命令处理器
                ICmdHandler<? extends GeneratedMessageV3> cmdHandler = CmdHandlerFactory.create(msgClazz);

                if (null != cmdHandler) {
                    cmdHandler.handle(ctx, cast(msg));
                }
            } catch (Exception ex) {
                // 记录错误日志
                LOGGER.error(ex.getMessage(), ex);
            }
        });
    }

    /**
     * 转型为命令对象
     *
     * @param msg    消息对象
     * @param <TCmd> 消息类
     * @return 命令对象
     */
    @SuppressWarnings("unchecked")
    static private <TCmd extends GeneratedMessageV3> TCmd cast(Object msg) {
        if (!(msg instanceof GeneratedMessageV3)) {
            return null;
        } else {
            return (TCmd) msg;
        }
    }
}
