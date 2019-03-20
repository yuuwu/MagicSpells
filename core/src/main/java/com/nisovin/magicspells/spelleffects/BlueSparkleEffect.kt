package com.nisovin.magicspells.spelleffects

import com.nisovin.magicspells.util.displayParticle
import org.bukkit.Location
import org.bukkit.Particle
import org.bukkit.configuration.ConfigurationSection

class BlueSparkleEffect : SpellEffect() {

    private val effect = Particle.SPELL_WITCH

    private val range = 32.0
    private val spreadHoriz = .2f
    private val spreadVert = .2f
    private val speed = .1f
    private val count = 20
    private val xOffset = 0f
    private val yOffset = 2f
    private val zOffset = 0f

    override fun loadFromString(string: String) {
        // lol nope
    }

    public override fun loadFromConfig(config: ConfigurationSection) {
        // TODO make a config loading schema
    }

    public override fun playEffectLocation(location: Location): Runnable? {
        //MagicSpells.getVolatileCodeHandler().playParticleEffect(location, "witchMagic", .2F, .2F, .1F, 20, 32, 2F);
        //Location location, String name, float spreadHoriz, float spreadVert, float speed, int count, int radius, float yOffset

        //effect.display(null, location.clone().add(xOffset.toDouble(), yOffset.toDouble(), zOffset.toDouble()), null, range, spreadHoriz, spreadVert, spreadHoriz, speed, count)
        displayParticle(
                particle = effect,
                center = location.clone().add(0.0, yOffset.toDouble(), 0.0),
                offsetX = spreadHoriz,
                offsetY = spreadVert,
                offsetZ = spreadHoriz,
                speed = speed,
                amount = count,
                range = range
        )

        //ParticleData data, Location center, Color color, double range, float offsetX, float offsetY, float offsetZ, float speed, int amount
        return null
    }

}
