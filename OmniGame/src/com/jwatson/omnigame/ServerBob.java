package com.jwatson.omnigame;

import java.io.IOException;


import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;

import java.net.BindException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.nio.BufferUnderflowException;
import java.nio.ByteBuffer;
import java.nio.channels.ClosedByInterruptException;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.DatagramChannel;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Timer;
import com.badlogic.gdx.utils.Timer.Task;



public class ServerBob {
	
	static final byte PACKET_POS = 101;
	static final byte PACKET_UPDATEBLK = 102;
	static final byte PACKET_UPDATEINV = 103;
	static final byte PACKET_ONUSE = 104;
	static final byte PACKET_CBLOCK = 105;
	
	private ObjectOutputStream out;
	private ObjectInputStream in;
	
	BufferedReader inFromUser;
	DatagramSocket clientSocket;
	
	final InetAddress addr;
	
	final float X = 0;
	final float Y = 0;
	
	long lastUpdate = 0;
	
	boolean hasSent;
	boolean hasRecieved;
	boolean Spawned;
	Timer timer;
	Task check;
	
	boolean active;
	boolean isMoving;
	int direction;
	
	private Socket client;
	Runnable udp_runnable;
	Thread udpthread;
	int ID;
	String name;
	World world;
	
	//UDP
	DatagramChannel channel;
	ByteBuffer buf;
	ByteBuffer buf2;
	ByteBuffer buf3;
	
	boolean hasUpdated = false;
	private Runnable tcp_run;
	private Thread tcp_thread;
	private boolean beenUpdated;
	
	int[][] bag = new int[Inventory.CurrentInventory.BagWidth][Inventory.CurrentInventory.BagHeight];
	InetSocketAddress sadder;
	
	public boolean HasUpdated() {		if(hasUpdated)			return true;		else			return false;	}
	public void setUpdate(boolean flag) { if(flag) hasUpdated = true; else hasUpdated = false;	}
	ServerBob( Socket socket, ObjectOutputStream out, ObjectInputStream in, String name, int ID, World world) throws Exception {

        this.out = out;
        this.in = in;
        this.name = name;
        this.ID = ID;
        this.world = world;
        this.client = socket;
        
        addr = socket.getInetAddress();
        sadder = new InetSocketAddress(addr, 9999);
    }
	
	public void run() {
		
        active = true;
        try {
			out.writeInt(ClientBob.AUTH);
	        out.writeUTF(name);
	        out.flush();
	        
	        
	        try {
				 	channel = DatagramChannel.open();
				 	//channel.socket().connect(new InetSocketAddress("localhost", 9999));
				}
				catch(BindException e) {
					Gdx.app.debug("ruh roh", ""+e.toString());
				}
	        
	        buf = ByteBuffer.allocate(1024);
        	buf.clear();
        	
        	buf2 = ByteBuffer.allocate(1024);
        	buf2.clear();
        
        	
        	channel.configureBlocking(false);

        	long lastUpdated = 0;
        	boolean beenUpdated = false;
	        
        
	        startTCPThread();
		}
        catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	

        public void update(float deltaTime) {
        
        
		      if(World.CurrentWorld.server == null)
		    	  try {
		    		  
		    		  if(System.currentTimeMillis() - lastUpdate > 100) {
		    		  SendUpdate();
		    		  lastUpdate = System.currentTimeMillis();
		    		  }
		    		  
		    		  buf2.clear();
		    		  if(channel.receive(buf2) != null) {
		    			  	buf2.flip();
		    			  	byte packet = 1;
		    			  	while(packet != 0) {
		    			  		packet = buf2.get();
		    			  		
		    			  		if(packet == 0) {
		    			  			continue;
		    			  		}

		    			  		
		    			  		if(packet == PACKET_UPDATEBLK) {
		    			  			
		    			  			UpdateBlocks();
		    			  		}
		    			  		if(packet == PACKET_POS) {
		    			  			UpdateInfo();
		    			  		}
		    			  		if(packet == PACKET_UPDATEINV) {
		    			  			UpdateInv();
		    			  			
		    			  		}
		    			  		if(packet == PACKET_ONUSE) {
		    			  			UpdateOnUse();
		    			  		}
		    			  		
		    			  	
		    			  	}
		    			  	
		    			  	
		    		  }
		    	  }
		    		  catch(IOException e) {
		    			  Gdx.app.debug("ServerBob", e.toString());
		    		  }
		    			  	
		    			  	
		    			  	//server always sends first

		                  	


		          
		    	  
		
	}
    
        void UpdateInfo() {
          	int id = buf2.get();
          	int state = buf2.get();
          	float x = buf2.getFloat();
          	float y = buf2.getFloat();
          	int direction = buf2.getInt();
          	
          	BobState bstate = new BobState();
          	
          	bstate.id = id;
          	bstate.state = state;
          	bstate.direction = direction;
          	bstate.x = x;
          	bstate.y = y;

          
         	
          	World.CurrentWorld.bobState.add(bstate);
        }
        
    void UpdateOnUse() {
    	int id = buf2.getInt();
    	int wep = buf2.getInt();
    	int firstused = buf2.getInt();
    	float x = buf2.getFloat();
    	float y = buf2.getFloat();
    	float angle = buf2.getFloat();
    	if(id != World.CurrentWorld.client.serverBob.ID)
    	world.ClientBobs.get(id).onClientUse(wep,angle,firstused,x,y);
    	
    	
    }
        
    void UpdateInv() {
    	BobState bstate = new BobState();
    	int id = (byte)buf2.get();
		bstate.inv = true;
		bstate.id = id;
      	for(int y2=0; y2<Inventory.CurrentInventory.BagHeight; y2++)
		{
			for(int x2=0; x2<Inventory.CurrentInventory.BagWidth; x2++) {
				int i = x2 + (y2*Inventory.CurrentInventory.BagWidth);

				bstate.bag[i][0] = buf2.get();
				bstate.bag[i][1] = buf2.get();			
			}
		}
      	World.CurrentWorld.bobState.add(bstate);
      	
      	
    }
        
    void UpdateBlocks() {
    	int destroyed = 0;
    	int recdmg = buf2.getInt();
	  	for(int i=0; i<recdmg; i++) {
	  		int x = buf2.getInt();
	  		int y = buf2.getInt();

	  		float hp = buf2.getFloat();
	  		if(hp <= 0) {
		  		int X = x/Terrain.CurrentTerrain.chunkWidth;
		  		int Y = y/Terrain.CurrentTerrain.chunkHeight;
		  		int X2 = x%Terrain.CurrentTerrain.chunkWidth;
		  		int Y2 = y%Terrain.CurrentTerrain.chunkHeight;
				TerrainChunk tc = Terrain.CurrentTerrain.GetChunkByID(X, Y);
				
				tc.parent.map.world.damagedBlocks.remove(x + (y*tc.Width*tc.parent.Width));
				tc.TerrainMap[X2+(Y2*tc.Width)] = 0;
				destroyed++;
	  		}
	  		else
	  		World.CurrentWorld.damagedBlocks.put(x+(y*Terrain.CurrentTerrain.chunkWidth*Terrain.CurrentTerrain.Width), new Vector3(x,y,hp));
	  		
	  	//	if(destroyed > 0)
//				for(TerrainChunk ch : Terrain.CurrentTerrain.ActiveChunks.values()) {
//					
//					ch.UpdateCache(MapRenderer.CurrentRenderer.cache);
//				}
//	  			Terrain.CurrentTerrain.sky = -1;
	  	}
    }
	
	void SendUpdate() {
	
	SendUpdate(AIBob.PACKET_UPDATE);
  	  
	}
	
	void SendUpdate(int packet) {
		
	if(World.CurrentWorld.server != null)
		return;
	
	
	  Bob bob = Bob.CurrentBob;
      buf.clear();
  	  buf.put((byte) ID);
  	  buf.put((byte) bob.state);
  	  buf.putFloat(bob.bounds.x);
  	  buf.putFloat(bob.bounds.y);
  	  buf.putInt(bob.dir);
  	  buf.putInt(packet);
  	  //buf.putFloat(bob.vel.y);
  	  buf.flip();
  	  
  	  
  	  try {
		channel.send(buf, sadder);
	} catch (IOException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
  	  
	}
	
	void remoteOnUse(int x, int y,float angle) {
		  Bob bob = Bob.CurrentBob;
	      buf.clear();
	  	  buf.put((byte) ID);
	  	  buf.put((byte) bob.state);
	  	  buf.putFloat(bob.bounds.x);
	  	  buf.putFloat(bob.bounds.y);
	  	  buf.putInt(bob.dir);
	  	  buf.putInt(AIBob.PACKET_ONUSE);
	  	  buf.putInt(bob.inventory.currSelected);
	  	  buf.putFloat(angle);
	  	  buf.putInt(x);
	  	  buf.putInt(y);
	  	  int firstTouch = 0;
	  	  if(Gdx.input.justTouched())
	  		  firstTouch = 1;
	  	  else
	  		  firstTouch = 0;
	  	
	  	  buf.putInt(firstTouch);
	  	  //buf.putFloat(bob.vel.y);
	  	  buf.flip();
	  	
	  	try {
			channel.send(buf, sadder);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void MoveBag(int held, int bag) {
		  Bob bob = Bob.CurrentBob;
	      buf.clear();
	  	  buf.put((byte) ID);
	  	  buf.put((byte) bob.state);
	  	  buf.putFloat(bob.bounds.x);
	  	  buf.putFloat(bob.bounds.y);
	  	  buf.putInt(bob.dir);
	  	  buf.putInt(AIBob.PACKET_INVMOVE);
	  	  buf.putInt(held);
	  	  buf.putInt(bag);
	  	  //buf.putFloat(bob.vel.y);
	  	  buf.flip();
	  	
	  	try {
			channel.send(buf, sadder);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
    private void runOld() {
        active = true;
        try {
			out.writeInt(ClientBob.AUTH);
	        out.writeUTF(name);
	        out.flush();
	        
	        
	        try {
				 	channel = DatagramChannel.open();
				 	channel.socket().bind(new InetSocketAddress(9999));
				}
				catch(BindException e) {
					
				}
	        
	        buf = ByteBuffer.allocate(1024);
        	buf.clear();
        	
        	buf2 = ByteBuffer.allocate(1024);
        	buf2.clear();
        	
        	buf3 = ByteBuffer.allocate(1024);
        	buf3.clear();
        	
        	channel.configureBlocking(false);

        	long lastUpdated = 0;
        	boolean beenUpdated = false;
	        
	        
	        startTCPThread();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

        long lastUpdate = 0; 		    
		      
        if(World.CurrentWorld.server == null)
		      while(active) {
		    	  		    	  
		    	  
		    	  
		    	  try {
		    	  
		    	  
		    	  if(!beenUpdated) {
		    		  
			    	  buf.putInt(ID);
			    	 
			    	  buf.putInt(GameServer.STATE_MOVING);
			    	  buf.putFloat(Bob.CurrentBob.bounds.x);
			    	  buf.putFloat(Bob.CurrentBob.bounds.y);
			    	  buf.flip();
			    	  channel.send(buf, sadder);
			    	  beenUpdated = true;
			    	  buf.clear();
		    	  }
		    	  
		    	  float vel = Bob.CurrentBob.vel.x;
		    	  if(vel < 0)vel *=-1;
		    	  if((int)vel > 0) {
		    		  if(System.currentTimeMillis() - lastUpdate > 45) {
				    	  buf.clear();
				    	  buf.putInt(ID);
				    	  buf.putInt(GameServer.STATE_MOVING);
				    	  buf.putFloat(Bob.CurrentBob.bounds.x);
				    	  buf.putFloat(Bob.CurrentBob.bounds.y);
				    	  buf.flip();
				    	  channel.send(buf,  sadder);
				    	  lastUpdate = System.currentTimeMillis();
		    		  }
				    	  
		    	  }
		    	  
		    	  if(channel.receive(buf2) != null) {
		    		  buf2.flip();
		    			  int id = buf2.getInt();
		    			  int state = buf2.getInt();
		    			  if(state == GameServer.STATE_MOVING) {
			    			  float x = buf2.getFloat();
			    			  float y = buf2.getFloat();
			    			  //Gdx.app.debug(""+x+ " "+y, ""+id);
			    			  
			    			  if(id != ID)
			    			  World.CurrentWorld.msginfo.add(new MessageInfo(id, x, y, false));
		    			  }
		    			  if(state == GameServer.STATE_JUSTMOVED) {
		    				 //Gdx.app.debug("test", "yo");
		    				  if(id != ID) {
		    				  int direction = buf2.getInt();
		    				  World.CurrentWorld.ClientBobs.get(id).Move(direction);
		    				  }
		    			  }
		    			  if(state == GameServer.STATE_STOPPED) {
			    				 
		    				  if(id != ID) {
		    				  float x = buf2.getFloat();
		    				  float y = buf2.getFloat();
		    				  Bob cl = World.CurrentWorld.ClientBobs.get(id);
		    				  cl.isMoving = false;
		    				  cl.bounds.x = x;
		    				  cl.bounds.y = y;
		    				  }
		    			  }
		    			  if(state == GameServer.STATE_JUMPED) {
		    				  float x = buf2.getFloat();
		    				  float y = buf2.getFloat();
		    				  int dir = buf2.getInt();
		    				  
		    				  Bob cl = World.CurrentWorld.ClientBobs.get(id);			    				  
		    				  cl.bounds.x = x;
		    				  cl.bounds.y = y;
		    				  cl.dir = dir;
		    				  cl.Jump();
		    				  
		    			  }
		    		  buf2.clear();
		    		  
		    	  }
		    	  
		    	  
		    	  }catch(Exception e) {
		    		  
		    	  }
		    	  
		    	  
		    	  
		    	  
		    	  
		    	  
		    	  }
		    	  
		    	  

        	
    }

    

    private int process( int b ) {
        return -1;
    }
    
    public void Chat(String msg) {
    	try {
    		Gdx.app.debug("", "WHY");
			out.writeInt(ClientBob.CHAT);
	    	out.writeUTF(msg);
	    	out.flush();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

    }
    
    public void BreakBlock(int x, int y) {
    	try {
    	out.writeInt(ClientBob.BROKE_BLOCK);
    	out.writeInt(x);
    	out.writeInt(y);
    	out.flush();
    	}
    	catch(IOException e) {
    		
    	}
    }
    void LoadOtherPlayers() {
    	try {
			out.writeInt(ClientBob.CHECK_PLAYERS);
			out.flush();
	
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	
    	
    }
    
    void setMoving(boolean flag) {
    	isMoving = flag;
    	try {
	    	if(flag) {
				  buf3.clear();
		    	  buf3.putInt(ID);
		    	  buf3.putInt(GameServer.STATE_JUSTMOVED);
		    	  buf3.putInt(direction);
		    	  buf3.flip();
		    	  channel.send(buf3, sadder);
	    	}
	    	else {
				  buf3.clear();
		    	  buf3.putInt(ID);
		    	  buf3.putInt(GameServer.STATE_STOPPED);
		    	  buf3.putFloat(Bob.CurrentBob.bounds.x);
		    	  buf3.putFloat(Bob.CurrentBob.bounds.y);
		    	  buf3.flip();
		    	  channel.send(buf3,  sadder);	    		
	    	}
    	}
    	catch(Exception e) {
    		
    	}
    }
    
    void setJumping(boolean flag) {
    	try {
    	if(flag) {
			  buf3.clear();
	    	  buf3.putInt(ID);
	    	  buf3.putInt(GameServer.STATE_JUMPED);
	    	  buf3.putFloat(Bob.CurrentBob.bounds.x);
	    	  buf3.putFloat(Bob.CurrentBob.bounds.y);
	    	  buf3.putInt(direction);
	    	  buf3.flip();
	    	  channel.send(buf3,  sadder);    		
    	}
    	else {
    		
    	}
    	}
    	catch(Exception e) {
    		
    	}
    }
    
    void startTCPThread() {
    	
    	tcp_run = new Runnable() {
			
			@Override
			public void run() {
				
				while(active)
			        try {

			            
			           int msg = in.readInt();
			           if(msg == ClientBob.BROADCAST) {
			        	   String msg2 = in.readUTF();
			        	   World.CurrentWorld.chatBox.AddLine(msg2);
			           }
			           if(msg == ClientBob.SPAWN) {
			        	   final int id = in.readInt();
			        	   final float x = in.readFloat();
			        	   final float y = in.readFloat();
				        	   if(id == ID) { // Spawning is be me me me me
				        		   world.RespawnFlag = true;
				        		   world.tsX = x;
				        		   world.tsY = y;
				        		   LoadOtherPlayers();
				        		   Spawned = true;
				        		   if(id != 0) { //client
				        			   world.isClient = true;
				        		   }
				        		   
				        	   }
				        	   else {
				        		   if(World.CurrentWorld.server == null)
				        		   World.CurrentWorld.msginfo.add(new MessageInfo(id,x,y,true));	        			   	        		     
				        		   else
				        			   World.CurrentWorld.msginfo.add(new MessageInfo(id,x,y,false));	    
				        	   }
				    
			        	   }
			           if(msg == ClientBob.SET_POS_MULTI) {
			       		int num = in.readInt();

						for(int i=0; i<num; i++) {
							int id = in.readInt();
							float x = in.readFloat();
							float y = in.readFloat();
							if(id != ID) {
								synchronized (World.CurrentWorld.msginfo) {
									
								
								World.CurrentWorld.msginfo.add(new MessageInfo(id, x, y, true));
								}
							}
						}
			           }
			           if(msg == ClientBob.GET_POS) {
			        	   out.writeInt(ClientBob.SET_POS);
			        	   out.writeFloat(Bob.CurrentBob.pos.x);
			        	   out.writeFloat(Bob.CurrentBob.pos.y);
			        	   out.flush();
			           }
			           if(msg == ClientBob.BROKE_BLOCK) {
			        	   int id = in.readInt();
			        	   int x = in.readInt();
			        	   int y = in.readInt();
			        	   
			        	   Vector2 destroyedblock = new Vector2(x,y);
			        	   Terrain.CurrentTerrain.toBeDestroyed.push(destroyedblock);
			           }
			           
			           
			        } catch ( Exception e ) {
			        	
			        }
			    }
		};
		
		tcp_thread = new Thread(tcp_run);
		
		tcp_thread.start();
		
    	
    }

}

