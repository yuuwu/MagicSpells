package com.nisovin.magicspells.util;

public @interface SpellTypesAllowed {

	SpellTypes[] value() default {SpellTypes.ALL};
}
