package com.nisovin.magicspells.volatilecode

import com.nisovin.magicspells.volatilecode.v1_12_R1.VolatileCode1_12_R1

object ManagerVolatile {

    fun constructVolatileCodeHandler(): VolatileCodeHandle {
        try {
            return VolatileCode1_12_R1()
        } catch (ex: Exception) {
            //
        }
        return VolatileCodeDisabled()
    }
}