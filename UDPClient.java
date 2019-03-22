import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.charset.Charset;
import java.util.Iterator;
import java.util.regex.Pattern;

public class UDPClient {


	DatagramChannel channel;
	Selector selector;
 
	public void work()
	{
 
		try
		{
			// 开启一个通道
			channel = DatagramChannel.open();
 
			channel.configureBlocking(false);
 
			SocketAddress sa = new InetSocketAddress("localhost", 18071);
 
			channel.connect(sa);
		} catch (Exception e)
		{
			e.printStackTrace();
		}
 
		try
		{
			selector = Selector.open();
			channel.register(selector, SelectionKey.OP_READ);
			channel.write(Charset.defaultCharset().encode("data come from client"));
		} catch (Exception e)
		{
			e.printStackTrace();
		}
 
		ByteBuffer byteBuffer = ByteBuffer.allocate(100);
		while (true)
		{
			try
			{
				int n = selector.select();
				if (n > 0)
				{
 
					Iterator iterator = selector.selectedKeys().iterator();
 
					while (iterator.hasNext())
					{
						SelectionKey key = (SelectionKey) iterator.next();
						iterator.remove();
						if (key.isReadable())
						{
							channel = (DatagramChannel) key.channel();
							channel.read(byteBuffer);
 

							System.out.println("发射端发送 : " + new String(byteBuffer.array()));
							
							String tempString = new String(byteBuffer.array());
							String[] saStrings = new String(tempString).split("\t");
							
							String pattern = ".*stop_cast.*";
							String outputString = "";
							if(Pattern.matches(pattern, saStrings[0])) {
								System.out.println("接受端什么都不做");
							} else {
								outputString = "接受端发送\tcmd:\"query_cast_status_rsp\"\tSenderIp:\"192.168.9.21\"\tSessionType:1\tstatus:0";
							}
							byteBuffer.clear();
							
							channel.write(Charset.defaultCharset().encode(
									outputString));
						}
					}
				}
			} catch (Exception e)
			{
				e.printStackTrace();
			}
		}
 
	}
	public static void main(String[] args) {
		// TODO Auto-generated method stub

		new UDPClient().work();
	}

}
