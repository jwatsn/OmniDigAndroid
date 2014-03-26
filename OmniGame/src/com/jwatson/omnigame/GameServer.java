package com.jwatson.omnigame;



import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.LinkedBlockingQueue;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;



public class GameServer implements Runnable {
	
	
	
	static int STATE_JUSTMOVED = 0x001;
	static int STATE_MOVING = 0x002;
	static int STATE_STOPPED = 0x003;
	static int STATE_JUMPED = 0x004;
	static int STATE_BLOCK_BROKE = 0x005;
	
	public boolean isRunning;
	ServerSocket Server;
	List<ClientBob> clients;
	private ByteBuffer ExtraInfo;
	ObjectOutputStream out;
	ObjectInputStream in;
	BlockingQueue<ByteBuffer> extraInfo;
	int CurrentPlayers = 0;
	List<SocketAddress> UDPsenders;
	HashMap<Integer,Vector2> lastUpdatePos;
	Object sync;
	
	
	public GameServer() {
		
		isRunning = true;
		extraInfo = new LinkedBlockingQueue<ByteBuffer>();
		clients = new ArrayList<ClientBob>();
		UDPsenders = new ArrayList<SocketAddress>();
		sync = new Object();
		ExtraInfo = ByteBuffer.allocate(128);
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub
		
		try {
			Server = new ServerSocket(9999);
			SetUpUDPThread();
			//get local
			
			
			while(isRunning) {
			
			Gdx.app.debug("LISTENING", "");
			ClientBob bob;
			try {
				Socket client = Server.accept();
				
				
				out = new ObjectOutputStream(client.getOutputStream());
		        in = new ObjectInputStream(client.getInputStream());
				
				String k = in.readUTF();
				if(!k.equals("32u4v09238m40c9209ewd0cjqwjmec90132i9ec01k091i2c09d3k")) {
					Gdx.app.debug("ClientBob", "Auth fail");
					client.close();
					continue;
				}
					
				if(k.equals("ping")) {
					
					long time = in.readLong();
					out.writeLong(System.currentTimeMillis() - time);
					client.close();
					continue;
					
				}
				
				
				
				bob = new ClientBob(client,out,in);
				clients.add(bob);
				bob.start();
				CurrentPlayers++;
				
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			}

			Gdx.app.debug("GOTCHA", "");

			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		
	}
	
	public synchronized ByteBuffer getExtraBuffer() {
		return ExtraInfo;
	}
	
	public void Boadcast(String msg) {
		for(ClientBob cb : clients) {
			try {
				cb.out.writeInt(ClientBob.BROADCAST);
				cb.out.writeUTF(msg);
				cb.out.flush();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}
		World.CurrentWorld.chatBox.AddLine(msg);
	}
	
	public void Boadcast(int type, int id, int x, int y) {
		
		for(ClientBob cb : clients) {
			try {				
				cb.out.writeInt(type);
				cb.out.writeInt(id);
				cb.out.writeInt(x);
				cb.out.writeInt(y);
				cb.out.flush();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}
		
	}

	public void Boadcast(int type, int id, Vector2 vec) {
		for(ClientBob cb : clients) {
			try {
				cb.out.writeInt(type);
				cb.out.writeInt(id);
				cb.out.writeFloat(vec.x);
				cb.out.writeFloat(vec.y);
				cb.out.flush();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}
	}
	
	public void UpdatePositions() throws InterruptedException {
		synchronized (sync) {
			

		
		for(ClientBob cb : clients) {
			try {
				cb.out.writeInt(ClientBob.GET_POS);
				cb.out.flush();
				
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}
		
		}
	}
	
	   void SetUpUDPThread() throws NullPointerException {
	    	

		   Runnable udp_run2 = new Runnable() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				long lastUpdate = 0;
				String sentence = "";
				try {
					   DatagramChannel channel = DatagramChannel.open();
					   channel.socket().bind(new InetSocketAddress(9999));
					   
				       
				       
				        ByteBuffer buf = ByteBuffer.allocate(1024);
		            	buf.clear();
		            	
		            	ByteBuffer buf2 = ByteBuffer.allocate(1024);
		            	buf2.clear();
		            	
		            	channel.configureBlocking(false);

				       
		            
			            while(true)
			               {
			            		buf.clear();
				            	SocketAddress sender = channel.receive(buf);
				            	if(sender != null) {
				            		
				            		
				                    buf.flip();
				            		
				                  	int id = buf.get();				                  	
				                  	int state = buf.get();
				                  	float x = buf.getFloat();
				                  	float y = buf.getFloat();
				                  	int direction = buf.getInt();
				                  	int clientpacket = buf.getInt();
				                  	//float vely = buf.getFloat();
				                  					                 

				                  	if(clientpacket == AIBob.PACKET_UPDATE) {
					                  	if(state == Bob.SPAWN) {
					                  		state = Bob.IDLE;

					                  		getClientByID(id).sender = sender;
				                  	BobState bstate = new BobState();				                  	
				                  	bstate.id = id;
				                  
				                  	bstate.state = state; 
				                 	
					                
				                  	
				                  	bstate.direction = direction;
				                  	bstate.x = x;
				                  	bstate.y = y;
				                  	//bstate.velx = velx;
				                  	//bstate.vely = vely;
				                  	World.CurrentWorld.bobState.add(bstate);
					                  	}
				                  	}
				                  	if(clientpacket == AIBob.PACKET_ONUSE) {
					                	int sel = buf.getInt();
					                	float angle = buf.getFloat();
					                	int x2 = buf.getInt();
					                	int y2 = buf.getInt();
					                	int firstUse = buf.getInt();
					                	
					                  	BobState bstate = new BobState();				                  	
					                  	bstate.id = id;

					                  	bstate.state = state; 
					                 	
						                
					                  	
					                  	bstate.direction = direction;
					                  	bstate.x = x;
					                  	bstate.y = y;
					                  	//bstate.velx = velx;
					                  	//bstate.vely = vely;
					                  	
					                	bstate.used = sel;
					                	bstate.angle = angle;
					                	bstate.UsedX = x2;
					                	bstate.UsedY = y2;
					                	bstate.firstUsed = firstUse;
					                	World.CurrentWorld.bobState.add(bstate);
					                }
				                  	if(clientpacket == AIBob.PACKET_INVMOVE) {
				                  		int held = buf.getInt();
				                  		int bag = buf.getInt();
				                  		
				                  		World.CurrentWorld.ClientBobs.get(id).MoveInv(held,bag);
				                  	}
				                  	if(clientpacket == AIBob.PACKET_MOVELEFT) {
				                  		
				                  		TryMoveLeft(id);
				                  		
				                  	}
				                  	if(clientpacket == AIBob.PACKET_MOVERIGHT) {
				                  		TryMoveRight(id);
				                  	}
				                  	if(clientpacket == AIBob.PACKET_MOVEDOWN) {
				                  		TryMoveDown(id);
				                  	}
				                  	if(clientpacket == AIBob.PACKET_JUMP) {
				                  		TryJump(id);
				                  	}
				                  	if(clientpacket == AIBob.PACKET_STOP) {
				                  		TryStop(id);
				                  	}
				                  	
				            	}
				            	
				            	if(System.currentTimeMillis() - lastUpdate >= 66) {
				            		
				            		buf2.clear();
				            		buf2.put(ServerBob.PACKET_UPDATEBLK);
				            		buf2.putInt(World.CurrentWorld.recentdamagedBlocks.size());
				            		for(Vector3 desblk : World.CurrentWorld.recentdamagedBlocks.values()) {
				            			buf2.putInt((int)desblk.x);
				            			buf2.putInt((int)desblk.y);
				            			buf2.putFloat((int)desblk.z);
				            		}
				            		//Gdx.app.debug("", ""+World.CurrentWorld.recentdamagedBlocks.size());
				            		World.CurrentWorld.recentdamagedBlocks.clear();
				            				
				            		//generate snapshot
				            		buf2.put(ServerBob.PACKET_POS); //allows next players info to be read
				            		Bob bob = Bob.CurrentBob;				      		            		
			            			buf2.put((byte) 0);
				            		buf2.put((byte)bob.state);
				            		buf2.putFloat(bob.pos.x);
				            		buf2.putFloat(bob.pos.y);
				            		buf2.putInt(bob.dir);			            		
				            		//buf2.putFloat(bob.vel.y);
				            		
				            		int b2 = 0;
					            	for(Bob b : World.CurrentWorld.ClientBobs.values()) {
					            		
					            		if(b.needsupdate) {
					            			b.needsupdate = false;
					            		buf2.put(ServerBob.PACKET_POS);
					            		buf2.put((byte) b.ID);				            	
					            		buf2.put((byte) b.state);
					            		buf2.putFloat(b.bounds.x);
					            		buf2.putFloat(b.bounds.y);
					            		buf2.putInt(b.dir);
					            		//buf2.putFloat(b.vel.y);		
					            		
					            		b.LastPos.x = b.bounds.x;
					            		b.LastPos.y = b.bounds.y;
					            		
					            		buf2.put(ServerBob.PACKET_UPDATEINV);
					            		buf2.put((byte)b.ID);
					            		UpdateClientInventory(buf2,b);
					            		}
					            	}
					         
					            		ByteBuffer xtra = extraInfo.poll();
					            		while(xtra != null) {
					            			buf2.put(xtra);
					            			xtra = extraInfo.poll();
					            		}
					
					            		buf2.put((byte)0);
					            	
					            	

					            	for(ClientBob cls : clients) {
					            			if(cls.sender == null)
					            				continue;
					            			buf2.flip();
						            		channel.send(buf2, cls.sender);
						            	
					            	}
				            		
				            		
				            		lastUpdate = System.currentTimeMillis();
				            	}
				            	Thread.sleep(1);
			               }
				}
		        catch(IOException e) {
		        	Gdx.app.debug("", ""+e.toString());
		        } catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}

			private void UpdateClientInventory(ByteBuffer buf2, Bob b) {
				
	       		for(int y=0; y<Inventory.CurrentInventory.BagHeight; y++)
        		{
        			for(int x=0; x<Inventory.CurrentInventory.BagWidth; x++) {
        				int i = x + (y*Inventory.CurrentInventory.BagWidth);
        				
        				buf2.put((byte)b.inventory.BagItem[i][0]);
        				buf2.put((byte)b.inventory.BagItem[i][1]);
        			}
        		}
				
			}
		};
		   
	    	Runnable udp_run = new Runnable()  {
				
				@Override
				public void run() {
					long lastUpdate = 0;
					String sentence = "";
					try {
						   DatagramChannel channel = DatagramChannel.open();
						   channel.socket().bind(new InetSocketAddress(9999));
						   
					       
					       
					       ByteBuffer buf = ByteBuffer.allocate(1024);
			            	buf.clear();
			            	
			            	ByteBuffer buf2 = ByteBuffer.allocate(1024);
			            	buf2.clear();
			            	
			            	channel.configureBlocking(false);

					       
			            
			            while(true)
			               { 	  
			            	SocketAddress sender = channel.receive(buf);
			            	if(sender != null) {
			            		if(!UDPsenders.contains(sender))
			            			UDPsenders.add(sender);
			                    buf.flip();
			                  	int id = buf.getInt();
			                  	int state = buf.getInt();
			                  	ClientBob cl = getClientByID(id);
			                  	cl.sender = sender;
			                  	
			                  	if(state == GameServer.STATE_JUSTMOVED) {
			                  		cl.direction = buf.getInt();
			                  		
				            		buf2.putInt(id);
				            		buf2.putInt(GameServer.STATE_JUSTMOVED);
				            		buf2.putInt(cl.direction);
				            		

			                  	}
			                  	
				                  	if(state == GameServer.STATE_MOVING) {
				            		float x = buf.getFloat();
				            		float y = buf.getFloat();
		
				            		cl.pos.x = x;
				            		cl.pos.y = y;
				            		
				            		buf2.putInt(id);
				            		buf2.putInt(GameServer.STATE_MOVING);
				            		buf2.putFloat(cl.pos.x);
				            		buf2.putFloat(cl.pos.y);
				            		

			                  	}
				                  	
				                if(state == GameServer.STATE_JUMPED) {
				            		float x = buf.getFloat();
				            		float y = buf.getFloat();
				            		int dir = buf.getInt();
		
				            		cl.pos.x = x;
				            		cl.pos.y = y;
				            		
				            		buf2.putInt(id);
				            		buf2.putInt(GameServer.STATE_JUMPED);
				            		buf2.putFloat(cl.pos.x);
				            		buf2.putFloat(cl.pos.y);
				            		buf2.putInt(dir);
				                }
				                  	
				                if(state == GameServer.STATE_STOPPED) {
				            		float x = buf.getFloat();
				            		float y = buf.getFloat();
		
				            		cl.pos.x = x;
				            		cl.pos.y = y;
				            		
				            		buf2.putInt(id);
				            		buf2.putInt(GameServer.STATE_STOPPED);
				            		buf2.putFloat(cl.pos.x);
				            		buf2.putFloat(cl.pos.y);
				                }
			            		
					            	int maxplayers = World.CurrentWorld.server.clients.size();
					            	for(int i=0; i<maxplayers; i++) {
					            		
					            		ClientBob abob = World.CurrentWorld.server.clients.get(i);
					            		
					            		//if(abob.client == null)
					            		//	continue;
					            		//if(abob.ID == id)
					            		//	continue;
					            		

					            		
					            		
					            	buf2.flip();
					            	SocketAddress addres = abob.sender;
					            	if(addres != null)
					            	{
					            	
					            	channel.send(buf2,addres);
					            	}
					            	

			            	}
					            	buf2.clear();
				                  	
					                  buf.clear();
			            	if(sender != null) {

			            	
			            	}
			            	
			            	
			            	//lastUpdate = System.currentTimeMillis();
			            	
			            	
			            	}

			            	Thread.sleep(1);
			            	}
					}
					catch(Exception e) {
						Gdx.app.debug("WTF2", e.toString());
			            }
					
				}
			};
	    	Thread udpthread = new Thread(udp_run2);
	    	udpthread.start();
	    	
	    }
	   
	   protected void TryStop(int id) {
		// TODO Auto-generated method stub
		   Bob bob = World.CurrentWorld.ClientBobs.get(id);
		   if(Terrain.CurrentTerrain.GetTile(bob.pos.x+0.5f, bob.pos.y+0.5f) == Terrain.CurrentTerrain.light.LadderID)
			   bob.vel.y = 0;
		   bob.isMoving = false;
		   bob.isClimbing = false;
		   bob.needsupdate = true;
	}

	protected void TryJump(int id) {
		// TODO Auto-generated method stub
		Bob bob = World.CurrentWorld.ClientBobs.get(id);
		bob.Move(Bob.JUMP);
		bob.needsupdate = true;
		bob.isClimbing = true;
	}

	protected void TryMoveDown(int id) {
		Bob bob = World.CurrentWorld.ClientBobs.get(id);
		if(Terrain.CurrentTerrain.GetTile(bob.pos.x+0.5f, bob.pos.y+0.5f) == Terrain.CurrentTerrain.light.LadderID) {
				bob.isClimbing = true;
			   bob.vel.y = -5;
		}
	}
	
	protected void TryMoveRight(int id) {
		// TODO Auto-generated method stub
		Bob bob = World.CurrentWorld.ClientBobs.get(id);
		bob.Move(Bob.RIGHT);
		bob.needsupdate = true;
	}

	protected void TryMoveLeft(int id) {
		// TODO Auto-generated method stub
		Bob bob = World.CurrentWorld.ClientBobs.get(id);
		bob.Move(Bob.LEFT);
		bob.needsupdate = true;
	}

	void SetPos(int ID, float x, float y) {
		   
	   }
	
	   ClientBob getClientByID(int id) {
		   
		   for(int i=0; i<clients.size(); i++) {
			   if(id == clients.get(i).ID)
				   return clients.get(i);
		   }
		   
		   return null;
	   }







}
