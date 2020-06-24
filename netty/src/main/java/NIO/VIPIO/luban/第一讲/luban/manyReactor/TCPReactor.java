    // Reactor線程 （该类与单线程的处理基本无变动） 
    package NIO.VIPIO.luban.第一讲.luban.manyReactor;
      
    import java.io.IOException;  
    import java.net.InetSocketAddress;  
    import java.nio.channels.SelectionKey;  
    import java.nio.channels.Selector;  
    import java.nio.channels.ServerSocketChannel;
    import java.nio.channels.spi.SelectorProvider;
    import java.util.Iterator;  
    import java.util.Set;  
      
    public class TCPReactor implements Runnable {  
      
        private final ServerSocketChannel ssc;  
        private final Selector selector;  
      
        public TCPReactor(int port) throws IOException {
            selector = Selector.open();  
            ssc = ServerSocketChannel.open();  
            InetSocketAddress addr = new InetSocketAddress(port);  
            ssc.socket().bind(addr); // 在ServerSocketChannel綁定監聽端口  
            ssc.configureBlocking(false); // 設置ServerSocketChannel為非阻塞  
            SelectionKey sk = ssc.register(selector, SelectionKey.OP_ACCEPT); // ServerSocketChannel向selector註冊一個OP_ACCEPT事件，然後返回該通道的key  
            sk.attach(new Acceptor(selector, ssc)); // 給定key一個附加的Acceptor對象  
        }  
      
        @Override  
        public void run() {  
            while (!Thread.interrupted()) { // 在線程被中斷前持續運行  
                System.out.println("Waiting for new event on port: " + ssc.socket().getLocalPort() + "...");  
                try {  
                    if (selector.select() == 0) // 若沒有事件就緒則不往下執行  
                        continue;  
                } catch (IOException e) {  
                    // TODO Auto-generated catch block  
                    e.printStackTrace();  
                }  
                Set<SelectionKey> selectedKeys = selector.selectedKeys(); // 取得所有已就緒事件的key集合  
                Iterator<SelectionKey> it = selectedKeys.iterator();  
                while (it.hasNext()) {  
                    dispatch((SelectionKey) (it.next())); // 根據事件的key進行調度  
                    it.remove();
                }  
            }  
        }  
      
        /* 
         * name: dispatch(SelectionKey key) 
         * description: 調度方法，根據事件綁定的對象開新線程 
         */  
        private void dispatch(SelectionKey key) {  
            Runnable r = (Runnable) (key.attachment()); // 根據事件之key綁定的對象開新線程  
            if (r != null)  
                r.run();  
        }  
      
    }  