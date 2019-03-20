package com.nisovin.magicspells.util

import com.nisovin.magicspells.MagicSpells
import org.bukkit.Color
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.Particle
import org.bukkit.entity.Player

fun displayParticle(
        particle: Particle,
        center: Location,
        offsetX: Float,
        offsetY: Float,
        offsetZ: Float,
        speed: Float,
        amount: Int,
        size: Float = 1F,
        color: Color? = null,
        material: Material? = null,
        materialData: Byte = 0,
        range: Double,
        targetPlayers: List<Player>? = null
        ) {
    val display = MagicSpells.getInstance().effectManager
    display.display(particle, center, offsetX, offsetY, offsetZ, speed, amount, size, color, material, materialData, range, targetPlayers)
}
