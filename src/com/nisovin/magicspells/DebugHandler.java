package com.nisovin.magicspells;

import java.util.logging.Level;

public class DebugHandler {
	
	public static void debugNull(Throwable t) {
		if (MagicSpells.plugin.debugNull) {
			MagicSpells.plugin.getLogger().log(Level.WARNING, t.toString() + "\n" + throwableToString(t));
		}
	}
	
	private static String throwableToString(Throwable t) {
		String s = "";
		for (StackTraceElement e: t.getStackTrace()) {
			s+= e.toString() +"\n";
		}
		return s;
	}
	
	public static void debugNumberFormat(Throwable t) {
		if (MagicSpells.plugin.debugNumberFormat) {
			MagicSpells.plugin.getLogger().log(Level.WARNING, t.toString() + "\n" + throwableToString(t));
		}
	}
	
	public static void debugIllegalState(Throwable t) {
		if (MagicSpells.plugin.debugNumberFormat) { //TODO setuo a different config node
			MagicSpells.plugin.getLogger().log(Level.WARNING, t.toString() + "\n" + throwableToString(t));
		}
	}
	
	public static void debugGeneral(Throwable t) {
		if (MagicSpells.plugin.debugNumberFormat) { //TODO setuo a different config node
			MagicSpells.plugin.getLogger().log(Level.WARNING, t.toString() + "\n" + throwableToString(t));
		}
	}
	
	public static void debugNoClassDefFoundError(Throwable t) {
		if (MagicSpells.plugin.debugNumberFormat) { //TODO setuo a different config node
			MagicSpells.plugin.getLogger().log(Level.WARNING, t.toString() + "\n" + throwableToString(t));
		}
	}
	
	public static void debugIOException(Throwable t) {
		if (MagicSpells.plugin.debugNumberFormat) { //TODO setuo a different config node
			MagicSpells.plugin.getLogger().log(Level.WARNING, t.toString() + "\n" + throwableToString(t));
		}
	}
	
	public static void debugFileNotFoundException(Throwable t) {
		if (MagicSpells.plugin.debugNumberFormat) { //TODO setuo a different config node
			MagicSpells.plugin.getLogger().log(Level.WARNING, t.toString() + "\n" + throwableToString(t));
		}
	}
	
	public static void debugIllegalStateException(Throwable t) {
		if (MagicSpells.plugin.debugNumberFormat) { //TODO setuo a different config node
			MagicSpells.plugin.getLogger().log(Level.WARNING, t.toString() + "\n" + throwableToString(t));
		}
	}
	
	public static void debugIllegalArgumentException(Throwable t) {
		if (MagicSpells.plugin.debugNumberFormat) { //TODO setuo a different config node
			MagicSpells.plugin.getLogger().log(Level.WARNING, t.toString() + "\n" + throwableToString(t));
		}
	}
	
	public static void debugIllegalAccessException(Throwable t) {
		if (MagicSpells.plugin.debugNumberFormat) { //TODO setuo a different config node
			MagicSpells.plugin.getLogger().log(Level.WARNING, t.toString() + "\n" + throwableToString(t));
		}
	}
	
	public static void debugInvocationTargetException(Throwable t) {
		if (MagicSpells.plugin.debugNumberFormat) { //TODO setuo a different config node
			MagicSpells.plugin.getLogger().log(Level.WARNING, t.toString() + "\n" + throwableToString(t));
		}
	}
	
	public static void debugNoSuchMethodException(Throwable t) {
		if (MagicSpells.plugin.debugNumberFormat) { //TODO setuo a different config node
			MagicSpells.plugin.getLogger().log(Level.WARNING, t.toString() + "\n" + throwableToString(t));
		}
	}
	
	public static void debugClassNotFoundException(Throwable t) {
		if (MagicSpells.plugin.debugNumberFormat) { //TODO setuo a different config node
			MagicSpells.plugin.getLogger().log(Level.WARNING, t.toString() + "\n" + throwableToString(t));
		}
	}
	
	public static void debugSecurityException(Throwable t) {
		if (MagicSpells.plugin.debugNumberFormat) { //TODO setuo a different config node
			MagicSpells.plugin.getLogger().log(Level.WARNING, t.toString() + "\n" + throwableToString(t));
		}
	}
	
	public static void debugNoSuchFieldException(Throwable t) {
		if (MagicSpells.plugin.debugNumberFormat) { //TODO setuo a different config node
			MagicSpells.plugin.getLogger().log(Level.WARNING, t.toString() + "\n" + throwableToString(t));
		}
	}
	
	public static void debug(Throwable t) {
		if (MagicSpells.plugin.debugNumberFormat) { //TODO setuo a different config node
			MagicSpells.plugin.getLogger().log(Level.WARNING, t.toString() + "\n" + throwableToString(t));
		}
	}
	
	public static void nullCheck(String s) {
		if (MagicSpells.plugin.debug) {
			MagicSpells.plugin.getLogger().warning(s);
		}
	}
	
	public static void nullCheck(Throwable t) {
		nullCheck(t.toString() + "\n" + throwableToString(t));
	}
	
	public static boolean isNullCheckEnabled() {
		return MagicSpells.plugin.debug;
	}
	
	public static boolean isSpellPreImpactEventCheckEnabled() {
		return MagicSpells.plugin.debug;
	}
}
