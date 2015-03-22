/*
 *
 * Hands-On code of the book Introduction to Reliable Distributed Programming
 * by Christian Cachin, Rachid Guerraoui and Luis Rodrigues
 * Copyright (C) 2005-2011 Luis Rodrigues
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA
 *
 * Contact
 * 	Address:
 *		Rua Alves Redol 9, Office 605
 *		1000-029 Lisboa
 *		PORTUGAL
 * 	Email:
 * 		ler@ist.utl.pt
 * 	Web:
 *		http://homepages.gsd.inesc-id.pt/~ler/
 * 
 */

package list1.session;

import list1.event.ConfirmEvent;
import list1.event.SubmitEvent;
import net.sf.appia.core.AppiaEventException;
import net.sf.appia.core.Direction;
import net.sf.appia.core.Event;
import net.sf.appia.core.Layer;
import net.sf.appia.core.Session;
import net.sf.appia.core.events.channel.ChannelInit;


/**
 * Receives submit and prints them on screen.
 * 
 * @author carlos.mestre
 */
public class JobHandlerSession extends Session {

  public JobHandlerSession(Layer layer) {
    super(layer);
  }

  public void handle(Event event) {
    if (event instanceof ChannelInit)
      handleChannelInit((ChannelInit) event);
    else if (event instanceof SubmitEvent) {
      handleSubmit((SubmitEvent) event);
    }
  }

  private void handleChannelInit(ChannelInit init) {
    try {
      init.go();
    } catch (AppiaEventException e) {
      e.printStackTrace();
    }
  }

  private void handleSubmit(SubmitEvent submit) {
    try {
      ConfirmEvent ack = new ConfirmEvent();

      System.out.println();
      System.out.println("[Print] " + submit.getString());
      submit.go();

      ack.setChannel(submit.getChannel()); // set the Appia channel where the event will travel
      ack.setDir(Direction.UP); // set events direction
      ack.setSourceSession(this); // set the session that created the event
      ack.setId(submit.getId()); // set the request ID
      // initializes the event, defining all internal structures,
      // for instance the path the event will take (sessions to visit)
      ack.init();
      ack.go(); // send the event
    } catch (AppiaEventException e) {
      e.printStackTrace();
    }
  }
}
