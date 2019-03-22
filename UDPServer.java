import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.util.Iterator;

public class UDPServer {

	DatagramChannel channel;
	 
	Selector selector;
 
	public void work()
	{
		try
		{
			// ��һ��UDP Channel
			channel = DatagramChannel.open();
 
			// �趨Ϊ������ͨ��
			channel.configureBlocking(false);
			// �󶨶˿�
			channel.socket().bind(new InetSocketAddress(18071));
 
			// ��һ��ѡ����
			selector = Selector.open();
			channel.register(selector, SelectionKey.OP_READ);
		} catch (Exception e)
		{
			e.printStackTrace();
		}
 
		ByteBuffer byteBuffer = ByteBuffer.allocate(65536);
		
//		while (true)
//		{
			try
			{
				// ����ѡ��
				int n = selector.select();
				if (n > 0)
				{
					// ��ȡ��ѡ��ļ��ļ���
					Iterator iterator = selector.selectedKeys().iterator();
 
					while (iterator.hasNext())
					{
						SelectionKey key = (SelectionKey) iterator.next();
 
						// �����ֶ�ɾ��
						iterator.remove();
 
						if (key.isReadable())
						{
							DatagramChannel datagramChannel = (DatagramChannel) key
									.channel();
 
							byteBuffer.clear();
							// ��ȡ
							InetSocketAddress address = (InetSocketAddress) datagramChannel
									.receive(byteBuffer);
 
							System.out.println(new String(byteBuffer.array()));
 
							// ɾ���������е�����
							byteBuffer.clear();
 
							String message = "cmd:\"query_cast_status\"\tSenderIp:\"192.168.9.21\"\tSessionType:1";
 
							String stop = "cmd:\"stop_cast\"\tSenderIp:\"192.168.9.21\"\tSessionType:0";
							
							byteBuffer.put(message.getBytes());
 
							byteBuffer.flip();
 
							// ��������
							datagramChannel.send(byteBuffer, address);
						}
					}
				}
			} catch (Exception e)
			{
				e.printStackTrace();
			}
		}
 
//	}
	public static void main(String[] args) {
		// TODO Auto-generated method stub

		new UDPServer().work();
	}

}
