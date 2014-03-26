package com.jwatson.omnigame;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.SocketException;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;

public class ClientBob extends Thread {
	
	public static int WELCOME = 0x001;
	public static int SET_NAME = 0x002;
	public static int AUTH = 0x003;
	public static int READY = 0x004;
	public static int BROADCAST = 0x005;
	public static int CHAT = 0x006;
	public static int SPAWN = 0x007;
	public static int CHECK_PLAYERS = 0x008;
	public static int SET_POS_MULTI = 0x009;
	public static int GET_POS = 0x010;
	public static int SET_POS = 0x011;
	public static int BROKE_BLOCK = 0x012;
	Vector2 pos;
	
	public String name;
	
	ObjectOutputStream out;
	ObjectInputStream in;
	private boolean active;
	public Socket client;
	
	public Bob controlled;

	
	int ID;
	protected int direction;
	protected SocketAddress sender;

	ClientBob( Socket socket, ObjectOutputStream out, ObjectInputStream in ) throws Exception {
        this.out = out;
        this.in= in;
        ID =  World.CurrentWorld.server.CurrentPlayers;
        pos = new Vector2();
        client = socket;
    }
	
	@Override
    public void run() {
		try {
        	active = true;
        	out.writeInt(WELCOME);
			out.writeInt(ID);
			out.flush();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        while ( active ) {
            listen();
        }
    }

    private void listen() {
        try {
           int msg = in.readInt();
           
           if(msg == ClientBob.AUTH) {
        	   name = in.readUTF();
        	   World world = World.CurrentWorld;
        	   World.CurrentWorld.server.Boadcast(""+name+" has joined.");
        	   pos = new Vector2((world.terrain.chunkWidth*world.terrain.Width)/2,(world.terrain.chunkHeight*world.terrain.Height)/2 + world.terrain.chunkHeight);
        	   World.CurrentWorld.server.Boadcast(ClientBob.SPAWN,ID,new Vector2((world.terrain.chunkWidth*world.terrain.Width)/2,(world.terrain.chunkHeight*world.terrain.Height)/2 + world.terrain.chunkHeight));
        	  
        	   
        	   if(World.CurrentWorld.client.serverBob.ID != ID) {
        		   synchronized (World.CurrentWorld.msginfo) {
					
				
        	   World.CurrentWorld.msginfo.add(new MessageInfo(ID,pos.x,pos.y,true));
        		   }
        	   }
           
           }
           if(msg == ClientBob.CHAT) {
        	   Gdx.app.debug("HMM", "BRO");
        	   String msg2 = in.readUTF();
        	   if(msg2.substring(0, 1).equals("/")) {
        		   String command = msg2.substring(1,msg2.indexOf(" "));
        		  
        		   
        		   if(command.equals("give")) {
        			   String w2 = msg2.substring(msg2.indexOf(" ")+1,msg2.length());
        			   String itemname = w2.substring(0, w2.indexOf(" "));
        			   int amt = Integer.parseInt(w2.substring(w2.indexOf(" ")+1,w2.length()),10);
        			   
        			   if(ID == 0) {
        				   Bob.CurrentBob.inventory.AddToBag(itemname, amt);
        			   }
        			   else {
        				   World.CurrentWorld.ClientBobs.get(ID).inventory.AddToBag(itemname, amt);
        			   }
        			   
        		   }
        	   }
        	   else
        	   World.CurrentWorld.server.Boadcast("<"+name+"> "+msg2);
           }
           if(msg == ClientBob.CHECK_PLAYERS) {
        	   World.CurrentWorld.server.UpdatePositions();
        	   Thread.sleep(10);
        	   out.writeInt(ClientBob.SET_POS_MULTI);
        	   out.writeInt(World.CurrentWorld.server.clients.size());
        	   for(int i=0; i<World.CurrentWorld.server.clients.size(); i++) {
        		   out.writeInt(World.CurrentWorld.server.clients.get(i).ID);
        		   out.writeFloat(World.CurrentWorld.server.clients.get(i).pos.x);
        		   out.writeFloat(World.CurrentWorld.server.clients.get(i).pos.y);
             		
        	   }
        	   out.flush();

           }
           if(msg == ClientBob.SET_POS) {
        	   
        	   float x = in.readFloat();
        	   float y = in.readFloat();
        	   pos.x = x;
        	   pos.y = y;
           }
           if(msg == ClientBob.BROKE_BLOCK) {
        	   int x = in.readInt();
        	   int y = in.readInt();
        	   World.CurrentWorld.server.Boadcast(ClientBob.BROKE_BLOCK, ID, x, y);
           }
           
           
           
        } catch ( Exception e ) {}
    }
    
    public void SendMessage(int type,String msg) {
    	try {
			out.writeInt(type);
	    	out.writeUTF(msg);
	    	out.flush();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

    }

    private int process( int b ) {
        return -1;
    }
    
 
}

