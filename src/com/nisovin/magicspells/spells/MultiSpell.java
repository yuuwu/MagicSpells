package com.nisovin.magicspells.spells;

import com.nisovin.magicspells.MagicSpells;
import com.nisovin.magicspells.Spell;
import com.nisovin.magicspells.Spell.PostCastAction;
import com.nisovin.magicspells.Spell.SpellCastState;
import com.nisovin.magicspells.Subspell;
import com.nisovin.magicspells.spelleffects.EffectPosition;
import com.nisovin.magicspells.util.MagicConfig;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitScheduler;

public final class MultiSpell
  extends InstantSpell
{
  private boolean castWithItem;
  private boolean castByCommand;
  private boolean checkIndividualCooldowns;
  private boolean castRandomSpellInstead;
  private boolean customSpellCastChance;
  private boolean enableIndividualChances;
  private List<String> spellList;
  private List<ActionChance> actions;
  private Random random = new Random();
  
  public MultiSpell(MagicConfig config, String spellName)
  {
    super(config, spellName);
    
    this.castWithItem = getConfigBoolean("can-cast-with-item", true);
    this.castByCommand = getConfigBoolean("can-cast-by-command", true);
    this.checkIndividualCooldowns = getConfigBoolean("check-individual-cooldowns", false);
    this.castRandomSpellInstead = getConfigBoolean("cast-random-spell-instead", false);
    this.customSpellCastChance = getConfigBoolean("enable-custom-spell-cast-chance", false);
    this.enableIndividualChances = getConfigBoolean("enable-individual-chances", false);
    
    this.actions = new ArrayList();
    this.spellList = getConfigStringList("spells", null);
  }
  
  public void initialize()
  {
    super.initialize();
    if (this.spellList != null) {
      for (String s : this.spellList)
      {
        String[] parts = s.split(":");
        double chance = parts.length == 2 ? Double.parseDouble(parts[1]) : 0.0D;
        s = parts[0];
        if (s.matches("DELAY [0-9]+"))
        {
          int delay = Integer.parseInt(s.split(" ")[1]);
          this.actions.add(new ActionChance(new Action(delay), chance));
        }
        else
        {
          Subspell spell = new Subspell(s);
          if (spell.process()) {
            this.actions.add(new ActionChance(new Action(spell), chance));
          } else {
            MagicSpells.error("No such spell '" + s + "' for multi-spell '" + this.internalName + "'");
          }
        }
      }
    }
    this.spellList = null;
  }
  
  public Spell.PostCastAction castSpell(Player player, Spell.SpellCastState state, float power, String[] args)
  {
    if (state == Spell.SpellCastState.NORMAL)
    {
      ActionChance actionChance;
      if (!this.castRandomSpellInstead)
      {
        if (this.checkIndividualCooldowns) {
          for (ActionChance actionChance1 : this.actions)
          {
            Action action = actionChance1.getAction();
            if ((action.isSpell()) && 
              (action.getSpell().getSpell().onCooldown(player)))
            {
              sendMessage(player, this.strOnCooldown);
              return Spell.PostCastAction.ALREADY_HANDLED;
            }
          }
        }
        int delay = 0;
        for (Iterator localIterator3 = this.actions.iterator(); localIterator3.hasNext();)
        {
          actionChance = (ActionChance)localIterator3.next();
          Action action = actionChance.getAction();
          if (action.isDelay())
          {
            delay += action.getDelay();
          }
          else if (action.isSpell())
          {
            Subspell spell = action.getSpell();
            if (delay == 0) {
              spell.cast(player, power);
            } else {
              Bukkit.getScheduler().scheduleSyncDelayedTask(MagicSpells.plugin, new DelayedSpell(spell, player, power), delay);
            }
          }
        }
      }
      else
      {
        int index;
        if (this.customSpellCastChance)
        {
          int total = 0;
          for (ActionChance actionChance2 : this.actions) {
            total = (int)Math.round(total + actionChance2.getChance());
          }
          index = this.random.nextInt(total);
          int s = 0;
          int i = 0;
          while (s < index) {
            s = (int)Math.round(s + ((ActionChance)this.actions.get(i++)).getChance());
          }
          Action action = ((ActionChance)this.actions.get(Math.max(0, i - 1))).getAction();
          if (action.isSpell())
          {
            if ((this.checkIndividualCooldowns) && (action.getSpell().getSpell().onCooldown(player)))
            {
              sendMessage(player, this.strOnCooldown);
              return Spell.PostCastAction.ALREADY_HANDLED;
            }
            action.getSpell().cast(player, power);
          }
        }
        else if (this.enableIndividualChances)
        {
          for (ActionChance actionChance3 : this.actions)
          {
            double chance = Math.random();
            if ((actionChance3.getChance() / 100.0D > chance) && (actionChance3.getAction().isSpell()))
            {
              Action action = actionChance3.getAction();
              if ((this.checkIndividualCooldowns) && (action.getSpell().getSpell().onCooldown(player)))
              {
                sendMessage(player, this.strOnCooldown);
                return Spell.PostCastAction.ALREADY_HANDLED;
              }
              action.getSpell().cast(player, power);
            }
          }
        }
        else
        {
          Action action = ((ActionChance)this.actions.get(this.random.nextInt(this.actions.size()))).getAction();
          if ((this.checkIndividualCooldowns) && (action.getSpell().getSpell().onCooldown(player)))
          {
            sendMessage(player, this.strOnCooldown);
            return Spell.PostCastAction.ALREADY_HANDLED;
          }
          action.getSpell().cast(player, power);
        }
      }
      playSpellEffects(EffectPosition.CASTER, player);
    }
    return Spell.PostCastAction.HANDLE_NORMALLY;
  }
  
  public boolean castFromConsole(final CommandSender sender, final String[] args)
  {
    if (!this.castRandomSpellInstead)
    {
      int delay = 0;
      for (ActionChance actionChance : this.actions)
      {
        Action action = actionChance.getAction();
        if (action.isSpell())
        {
          if (delay == 0)
          {
            action.getSpell().getSpell().castFromConsole(sender, args);
          }
          else
          {
            final Spell spell = action.getSpell().getSpell();
            MagicSpells.scheduleDelayedTask(new Runnable()
            {
              public void run()
              {
                spell.castFromConsole(sender, args);
              }
            }, delay);
          }
        }
        else if (action.isDelay()) {
          delay += action.getDelay();
        }
      }
    }
    else
    {
      int index;
      if (this.customSpellCastChance)
      {
        int total = 0;
        for (ActionChance actionChance : this.actions) {
          total = (int)Math.round(total + actionChance.getChance());
        }
        index = this.random.nextInt(total);
        int s = 0;
        int i = 0;
        while (s < index) {
          s = (int)Math.round(s + ((ActionChance)this.actions.get(i++)).getChance());
        }
        Action action = ((ActionChance)this.actions.get(Math.max(0, i - 1))).getAction();
        if (action.isSpell()) {
          action.getSpell().getSpell().castFromConsole(sender, args);
        }
      }
      else if (this.enableIndividualChances)
      {
        for (ActionChance actionChance : this.actions)
        {
          double chance = Math.random();
          if ((actionChance.getChance() / 100.0D > chance) && (actionChance.getAction().isSpell())) {
            actionChance.getAction().getSpell().getSpell().castFromConsole(sender, args);
          }
        }
      }
      else
      {
        Action action = ((ActionChance)this.actions.get(this.random.nextInt(this.actions.size()))).getAction();
        if (action.isSpell()) {
          action.getSpell().getSpell().castFromConsole(sender, args);
        }
      }
    }
    return true;
  }
  
  public boolean canCastWithItem()
  {
    return this.castWithItem;
  }
  
  public boolean canCastByCommand()
  {
    return this.castByCommand;
  }
  
  private class Action
  {
    private Subspell spell;
    private int delay;
    
    public Action(Subspell spell)
    {
      this.spell = spell;
      this.delay = 0;
    }
    
    public Action(int delay)
    {
      this.delay = delay;
      this.spell = null;
    }
    
    public boolean isSpell()
    {
      return this.spell != null;
    }
    
    public Subspell getSpell()
    {
      return this.spell;
    }
    
    public boolean isDelay()
    {
      return this.delay > 0;
    }
    
    public int getDelay()
    {
      return this.delay;
    }
  }
  
  private class DelayedSpell
    implements Runnable
  {
    private Subspell spell;
    private String playerName;
    private float power;
    
    public DelayedSpell(Subspell spell, Player player, float power)
    {
      this.spell = spell;
      this.playerName = player.getName();
      this.power = power;
    }
    
    public void run()
    {
      Player player = Bukkit.getPlayerExact(this.playerName);
      if ((player != null) && (player.isValid())) {
        this.spell.cast(player, this.power);
      }
    }
  }
  
  private class ActionChance
  {
    private MultiSpell.Action action;
    private double chance;
    
    public ActionChance(MultiSpell.Action action, double chance)
    {
      this.action = action;
      this.chance = chance;
    }
    
    public MultiSpell.Action getAction()
    {
      return this.action;
    }
    
    public double getChance()
    {
      return this.chance;
    }
  }
}