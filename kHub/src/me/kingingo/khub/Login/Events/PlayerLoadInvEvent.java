package me.kingingo.khub.Login.Events;

import lombok.Getter;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class PlayerLoadInvEvent extends Event
{
  private static final HandlerList handlers = new HandlerList();
  @Getter
  private Player player;

  public PlayerLoadInvEvent(Player player)
  {
    this.player=player;
  }

  public HandlerList getHandlers()
  {
    return handlers;
  }

  public static HandlerList getHandlerList()
  {
    return handlers;
  }
}
